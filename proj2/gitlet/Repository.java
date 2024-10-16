package gitlet;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *
 *  @author Advtn
 */
public class Repository {

    /**
     * Default branch name.
     */
    private static final String DEFAULT_BRANCH_NAME = "master";

    /**
     * HEAD ref prefix.
     */
    private static final String HEAD_BRANCH_REF_PREFIX = "ref: refs\\heads\\";

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The objects directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /** The refs directory. */
    private static final File REFS_DIR = join(GITLET_DIR, "refs");

    /** The heads directory, saved the branches.*/
    private static final File BRANCH_HEADS_DIR = join(REFS_DIR, "heads");

    /**
     * The HEAD file.
     * points to the current working branch
     * */
    private static final File HEAD = join(GITLET_DIR, "HEAD");

    /**
     * The index file.
     *  staging area
     */
    public static final File INDEX = join(GITLET_DIR, "index");

    /** Files in the current working directory. */
    private static final Lazy<File[]> CURRENT_FILES = lazy(() -> CWD.listFiles(File::isFile));

    /** The current branch name. */
    private static final Lazy<String> CURRENT_BRANCH = lazy(() -> {
        String headFileContent = readContentsAsString(HEAD);
        return headFileContent.replace(HEAD_BRANCH_REF_PREFIX, "");
    });

    /** The commit that HEAD points to. */
    private final Lazy<Commit> headCommit = lazy(() -> getBranchHeadCommit(CURRENT_BRANCH.get()));

    /** The staging area. */
    private final Lazy<StagingArea> stagingArea = lazy(() -> {
        StagingArea s = INDEX.exists()
            ? StagingArea.fromFile()
            : new StagingArea();
        s.setTracked(headCommit.get().getTracked());
        return s;
    });

    /** Exit if the repository at the current working directory is initialized. */
    public static void checkWorkingDir() {
        if (!GITLET_DIR.exists()) {
            exit("Not in an initialized Gitlet directory.");
        }
    }

    /**
     * Initialize a repository at the current working directory.
     *
     * <pre>
     * .gitlet
     * ├── HEAD
     * ├── objects
     * └── refs
     *     └── heads
     * </pre>
     */
    public static void init() {
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }
        mkdir(GITLET_DIR);
        mkdir(OBJECTS_DIR);
        mkdir(REFS_DIR);
        mkdir(BRANCH_HEADS_DIR);
        setCurrentBranch(DEFAULT_BRANCH_NAME);
        createInitialCommit();
    }

    /**
     * set current branch.
     * @param branchName Name of branch
     */
    private static void setCurrentBranch(String branchName) {
        writeContents(HEAD, HEAD_BRANCH_REF_PREFIX + branchName);
    }

    /**
     * Set the branch point to new commit.
     * write the commit id to the branch file.
     * */
    private static void setBranchHeadCommit(String branchName, String commitId) {
        File branchFile = getBranchHeadFile(branchName);
        writeContents(branchFile, commitId);
    }

    private static void setBranchHeadCommit(File branchFile, String commitId) {
        writeContents(branchFile, commitId);
    }

    /**
     * Get branch file.
     * generate branch file in the heads directory.
     * */
    private static File getBranchHeadFile(String branchName) {
        return join(BRANCH_HEADS_DIR, branchName);
    }

    /** Get commit by branch name. */
    private static Commit getBranchHeadCommit(String branchName) {
        File branchFile = getBranchHeadFile(branchName);
        return getBranchHeadCommit(branchFile);
    }

    /** Get commit by branch file. */
    private static Commit getBranchHeadCommit(File branchHeadFile) {
        String headCommitId = readContentsAsString(branchHeadFile);
        return Commit.fromFile(headCommitId);
    }

    /** Creates initial commit. */
    private static void createInitialCommit() {
        Commit initialCommit = new Commit();
        initialCommit.save();
        setBranchHeadCommit(DEFAULT_BRANCH_NAME, initialCommit.getId());
    }

    /** Add file to the staging area. */
    public void add(String fileName) {
        File file = getFileFromCWD(fileName);
        if (!file.exists()) {
            exit("File does not exist.");
        }
        if (stagingArea.get().add(file)) {
            stagingArea.get().save();
        }
    }

    /** Get file from CWD by the file name. */
    private static File getFileFromCWD(String fileName) {
        return Paths.get(fileName).isAbsolute()
            ? new File(fileName)
            : join(CWD, fileName);
    }

    /** Get a Map of file paths and their SHA1 id from CWD. */
    private static Map<String, String> getCurrentFilesMap() {
        Map<String, String> filesMap = new HashMap<>();
        for (File file : CURRENT_FILES.get()) {
            String filePath = file.getPath();
            String blobId = Blob.generateId(file);
            filesMap.put(filePath, blobId);
        }
        return filesMap;
    }

    /** Print logs of the current branch. */
    public void log() {
        StringBuilder logBuilder = new StringBuilder();
        Commit currentCommit = headCommit.get();
        while (true) {
            logBuilder.append(currentCommit.getLog()).append("\n");
            List<String> parentCommitIds = currentCommit.getParents();
            if (parentCommitIds.isEmpty()) {
                break;
            }
            String firstParentCommitId = parentCommitIds.get(0);
            currentCommit = Commit.fromFile(firstParentCommitId);
        }

        System.out.print(logBuilder);
    }

    /** Print current status. */
    public void status() {
        StringBuilder statusBuilder = new StringBuilder();
        // Branches
        statusBuilder.append("=== Branches ===").append("\n");
        statusBuilder.append("*").append(CURRENT_BRANCH.get()).append("\n");
        FilenameFilter branchFilter = (dir, name) -> !name.equals(CURRENT_BRANCH.get());
        String[] branchNames = BRANCH_HEADS_DIR.list(branchFilter);
        if (branchNames != null) {
            Arrays.sort(branchNames);
            for (String branchName : branchNames) {
                statusBuilder.append(branchName).append("\n");
            }
        }
        statusBuilder.append("\n");

        Map<String, String> addedFilesMap = stagingArea.get().getAdded();
        Set<String> removedFilePathsSet = stagingArea.get().getRemoved();

        // Staged Files
        statusBuilder.append("=== Staged Files ===").append("\n");
        appendFileNamesInOrder(statusBuilder, addedFilesMap.keySet());
        statusBuilder.append("\n");
        // Removed Files
        statusBuilder.append("=== Removed Files ===").append("\n");
        appendFileNamesInOrder(statusBuilder, removedFilePathsSet);
        statusBuilder.append("\n");
        // Modifications Not Staged For Commit
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n");

        List<String> modifiedNotStageFilePaths = new ArrayList<>();
        Set<String> deletedNotStageFilePaths = new HashSet<>();
        Map<String, String> currentFilesMap = getCurrentFilesMap();
        Map<String, String> trackedFilesMap = headCommit.get().getTracked();
        trackedFilesMap.putAll(addedFilesMap);

        for (String filePath : removedFilePathsSet) {
            trackedFilesMap.remove(filePath);
        }
        for (Map.Entry<String, String> entry : trackedFilesMap.entrySet()) {
            String filePath = entry.getKey();
            String blobId = entry.getValue();
            String currentFileBlobId = currentFilesMap.get(filePath);
            if (currentFileBlobId != null) {
                // Current file be tracked but content changed but not staged
                if (!currentFileBlobId.equals(blobId)) {
                    modifiedNotStageFilePaths.add(filePath);
                }
                // remove it from untracked files
                currentFilesMap.remove(filePath);
            } else {
                // Current file does not be tracked
                modifiedNotStageFilePaths.add(filePath);
                deletedNotStageFilePaths.add(filePath);
            }
        }
        modifiedNotStageFilePaths.sort(String::compareTo);
        for (String filePath : modifiedNotStageFilePaths) {
            String fileName = Paths.get(filePath).getFileName().toString();
            statusBuilder.append(fileName);
            if (deletedNotStageFilePaths.contains(filePath)) {
                statusBuilder.append(" ").append("(deleted)");
            } else {
                statusBuilder.append(" ").append("(modified)");
            }
            statusBuilder.append("\n");
        }
        statusBuilder.append("\n");
        // Untracked Files
        statusBuilder.append("=== Untracked Files ===").append("\n");
        appendFileNamesInOrder(statusBuilder, currentFilesMap.keySet());
        statusBuilder.append("\n");

        System.out.print(statusBuilder);
    }

    /** Append lines of file name in order from files paths Set to StringBuilder.*/
    private static void appendFileNamesInOrder(StringBuilder sb, Collection<String> filePathColl) {
        List<String> filesPathList = new ArrayList<>(filePathColl);
        appendFileNamesInOrder(sb, filesPathList);
    }

    /** Append lines of file name in order from files paths Set to StringBuilder.*/
    private static void appendFileNamesInOrder(StringBuilder sb, List<String> filePathList) {
        filePathList.sort(String::compareTo);
        for (String filePath : filePathList) {
            String fileName = Paths.get(filePath).getFileName().toString();
            sb.append(fileName).append("\n");
        }
    }

    /** The commit command. */
    public void commit(String message) {
        commit(message, null);
    }

    /** Perform a commit with message and two parents. */
    private void commit(String message, String secondParent) {
        if (stagingArea.get().isClean()) {
            exit("No changes added to the commit.");
        }
        Map<String, String> newTrackedFilesMap = stagingArea.get().commit();
        stagingArea.get().save();
        List<String> parents = new ArrayList<>();
        parents.add(headCommit.get().getId());
        if (secondParent != null) {
            parents.add(secondParent);
        }
        Commit newCommit = new Commit(message, parents, newTrackedFilesMap);
        newCommit.save();
        setBranchHeadCommit(CURRENT_BRANCH.get(), newCommit.getId());
    }

    /** The remove command. */
    public void remove(String fileName) {
        File file = getFileFromCWD(fileName);
        if (stagingArea.get().remove(file)) {
            stagingArea.get().save();
        } else {
            exit("No reason to remove the file.");
        }
    }

    /** Creates a new branch with the given name. */
    public void branch(String newBranchName) {
        File newBranchFile = getBranchHeadFile(newBranchName);
        if (newBranchFile.exists()) {
            exit("A branch with that name already exists.");
        }
        setBranchHeadCommit(newBranchFile, headCommit.get().getId());
    }

    /** Deletes the branch with the given name. */
    public void removeBranch(String branchName) {
        File branchFile = getBranchHeadFile(branchName);
        if (!branchFile.exists()) {
            exit("A branch with that name does not exist.");
        }
        if (branchName.equals(CURRENT_BRANCH.get())) {
            exit("Cannot remove the current branch.");
        }
        rm(branchFile);
    }

    /** Checkout file from HEAD commit. */
    public void checkout(String fileName) {
        String filePath = getFileFromCWD(fileName).getPath();
        if (!headCommit.get().restoreTracked(filePath)) {
            exit("File does not exist in that commit.");
        }
    }

    /** Checkout file from the given commit id. */
    public void checkout(String commitId, String fileName) {
        commitId = getActualCommitId(commitId);
        String filePath = getFileFromCWD(fileName).getPath();
        if (!Commit.fromFile(commitId).restoreTracked(filePath)) {
            exit("File does not exist in that commit.");
        }
    }

    /** Checkout branch from the given branch name. */
    public void checkoutBranch(String targetBranchName) {
        File targetBranchFile = getBranchHeadFile(targetBranchName);
        if (!targetBranchFile.exists()) {
            exit("No such branch exists.");
        }
        if (targetBranchName.equals(CURRENT_BRANCH.get())) {
            exit("No need to checkout the current branch.");
        }
        Commit targetBranchCommit = getBranchHeadCommit(targetBranchFile);
        checkUntracked(targetBranchCommit);
        checkoutCommit(targetBranchCommit);
        setCurrentBranch(targetBranchName);
    }

    /** Checkout to specific commit. */
    private void checkoutCommit(Commit targetCommit) {
        stagingArea.get().clear();
        stagingArea.get().save();
        for (File file : CURRENT_FILES.get()) {
            rm(file);
        }
        targetCommit.restoreAllTracked();
    }

    /** Exit with message if target commit would overwrite the untracked files. */
    private void checkUntracked(Commit targetCommit) {
        Map<String, String> currentFilesMap = getCurrentFilesMap();
        Map<String, String> trackedFilesMap = headCommit.get().getTracked();
        Map<String, String> addedFilesMap = stagingArea.get().getAdded();
        Set<String> removedFilesPathSet = stagingArea.get().getRemoved();

        List<String> untrackedFilePaths = new ArrayList<>();

        // 遍历 CWD，检查当前头提交是否跟踪该文件，
        for (String filePath : currentFilesMap.keySet()) {
            if (trackedFilesMap.containsKey(filePath)) {
                // 如果跟踪并且暂存区显示已被移除，加入未跟踪列表
                if (removedFilesPathSet.contains(filePath)) {
                    untrackedFilePaths.add(filePath);
                }
                // 如果未跟踪并且不在暂存区，加入未跟踪列表
            } else {
                if (!addedFilesMap.containsKey(filePath)) {
                    untrackedFilePaths.add(filePath);
                }
            }
        }

        Map<String, String> targetCommitTrackedFilesMap = targetCommit.getTracked();
        String mg = "There is an untracked file in the way; delete it, or add and commit it first.";

        // 检查未跟踪文件是否改变，如果改变则打印消息并退出程序
        for (String filePath : untrackedFilePaths) {
            String blobId = currentFilesMap.get(filePath);
            String targetBlobId = targetCommitTrackedFilesMap.get(filePath);
            if (!blobId.equals(targetBlobId)) {
                exit(mg);
            }
        }
    }

    /** Get whole commit id. Exit with message if it does not exit. */
    @SuppressWarnings("ConstantConditions")
    private static String getActualCommitId(String commitId) {
        if (commitId.length() < UID_LENGTH) {
            if (commitId.length() < 4) {
                exit("Commit id should contain at least 4 characters.");
            }
            String objectDirName = getObjectDirName(commitId);
            File objectDir = join(OBJECTS_DIR, objectDirName);
            if (!objectDir.exists()) {
                exit("No commit with that id exists.");
            }

            boolean isFound = false;
            String objectFileNamePrefix = getObjectFileName(commitId);

            for (File objectFile : objectDir.listFiles()) {
                String objectFileName = objectFile.getName();
                if (objectFileName.startsWith(objectFileNamePrefix)) {
                    if (isFileInstanceOf(objectFile, Commit.class)) {
                        if (isFound) {
                            exit("More than 1 commit has the same id prefix.");
                        }
                        commitId = objectDirName + objectFileName;
                        isFound = true;
                    }
                }
            }
            if (!isFound) {
                exit("No commit with that id exists.");
            }
        } else {
            if (!getObjectFile(commitId).exists()) {
                exit("No commit with that id exists.");
            }
        }
        return commitId;
    }

    /** Checks out all the files tracked by the given commit.
     *  set current branch head to point to the given commit.
     * */
    public void reset(String commitId) {
        commitId = getActualCommitId(commitId);
        Commit targetCommit = Commit.fromFile(commitId);
        checkUntracked(targetCommit);
        checkoutCommit(targetCommit);
        setBranchHeadCommit(CURRENT_BRANCH.get(), commitId);
    }

    /** The find command. */
    public static void find(String commitMessage) {
        StringBuilder resultBuilder = new StringBuilder();
        forEachCommit(commit -> {
            if (commit.getMessage().equals(commitMessage)) {
                resultBuilder.append(commit.getId()).append("\n");
            }
        });
        if (resultBuilder.length() == 0) {
            exit("Found no commit with that message.");
        }
        System.out.print(resultBuilder);
    }

    /** Print all commit logs ever made. */
    public static void globalLog() {
        StringBuilder logBuilder = new StringBuilder();
        forEachCommitInOrder(commit -> logBuilder.append(commit.getLog()).append("\n"));
        System.out.print(logBuilder);
    }

    /** Get priority queue with comparator by date. */
    private static PriorityQueue<Commit> getPriorityQueue() {
        Comparator<Commit> commitComparator = Comparator.comparing(Commit::getDate).reversed();
        return new PriorityQueue<>(commitComparator);
    }

    /** Iterate all commits in the order of created date */
    private static void forEachCommitInOrder(Consumer<Commit> cb) {
        Queue<Commit> commitsPriorityQueue = getPriorityQueue();
        forEachCommit(cb, commitsPriorityQueue);
    }

    /** Iterate all commits and execute callback function on each of them.*/
    private static void forEachCommit(Consumer<Commit> cb) {
        Queue<Commit> commitsQueue = new ArrayDeque<>();
        forEachCommit(cb, commitsQueue);
    }

    /** Helper method to iterate all commit. */
    @SuppressWarnings("ConstantConditions")
    private static void forEachCommit(Consumer<Commit> cb, Queue<Commit> queueToHoldCommits) {
        Set<String> checkedCommitIds = new HashSet<>();
        Set<String> checkedOrphanCommitIds = new HashSet<>();

        Queue<Commit> allCommitsQueue = getPriorityQueue();

        File[] branchHeadFiles = BRANCH_HEADS_DIR.listFiles();
        Arrays.sort(branchHeadFiles, Comparator.comparing(File::getName));

        //遍历所有分支，读取分支头提交ID，加入checkedCommitIds集合
        //根据分支头提交ID，得到Commit实例对象，加入queueToHoldCommits 队列
        for (File branchHeadFile : branchHeadFiles) {
            String branchHeadCommitId = readContentsAsString(branchHeadFile);
            // if commit has been in checkedCommitIds, do not join the queue.
            if (checkedCommitIds.contains(branchHeadCommitId)) {
                continue;
            }
            checkedCommitIds.add(branchHeadCommitId);
            Commit branchHeadCommit = Commit.fromFile(branchHeadCommitId);
            queueToHoldCommits.add(branchHeadCommit);
        }

        //处理所有的 parent commit
        while (true) {
            Commit nextCommit = queueToHoldCommits.poll();
            allCommitsQueue.add(nextCommit);
            checkedOrphanCommitIds.add(nextCommit.getId());
            List<String> parentCommitIds = nextCommit.getParents();
            if (parentCommitIds.isEmpty()) {
                break;
            }
            for (String parentCommitId : parentCommitIds) {
                if (checkedCommitIds.contains(parentCommitId)) {
                    continue;
                }
                checkedCommitIds.add(parentCommitId);
                Commit parentCommit = Commit.fromFile(parentCommitId);
                queueToHoldCommits.add(parentCommit);
            }
        }

        // 处理孤立commit
        File[] objects = OBJECTS_DIR.listFiles();
        for (File objectDir : objects) {
            for (File objectFile : objectDir.listFiles()) {
                String objectId = objectDir.getName() + objectFile.getName();
                if (checkedOrphanCommitIds.contains(objectId)) {
                    continue;
                }
                if (isFileInstanceOf(objectFile, Commit.class)) {
                    allCommitsQueue.add(Commit.fromFile(objectId));
                }
            }
        }

        while (!allCommitsQueue.isEmpty()) {
            Commit commit = allCommitsQueue.poll();
            cb.accept(commit);
        }
    }

    /** Get Bread First Path . */
    @SuppressWarnings("ConstantConditions")
    private static List<String> getBreadFirstPath(Commit commit) {
        Queue<Commit> commitQueue = new ArrayDeque<>();
        List<String> commitsInOrder = new ArrayList<>();
        Set<String> checkedCommits = new HashSet<>();
        commitQueue.add(commit);

        while (!commitQueue.isEmpty()) {
            Commit latestCommit = commitQueue.poll();
            String latestCommitId = latestCommit.getId();
            if (checkedCommits.contains(latestCommitId)) {
                continue;
            }
            for (String commitId : latestCommit.getParents()) {
                commitQueue.add(Commit.fromFile(commitId));
            }
            commitsInOrder.add(latestCommitId);
            checkedCommits.add(latestCommitId);
        }
        return commitsInOrder;
    }

    /** Get the latest common ancestor commit of the two commits. */
    @SuppressWarnings("ConstantConditions")
    private static Commit getLatestCommonAncestorCommit(Commit commitA, Commit commitB) {
        List<String> commitsA = getBreadFirstPath(commitA);
        List<String> commitsB = getBreadFirstPath(commitB);

        for (String commitId : commitsA) {
            if (commitsB.contains(commitId)) {
                return Commit.fromFile(commitId);
            }
        }
        return null;
    }

    /** Merge the conflicted blob content and return a new String.*/
    private static String getConflictContent(String currentBlobId, String targetBlobId) {
        StringBuilder contentBuilder = new StringBuilder();
        contentBuilder.append("<<<<<<< HEAD").append("\n");
        if (currentBlobId != null) {
            Blob currentBlob = Blob.fromFile(currentBlobId);
            String content = currentBlob.getContentAsString();
            if (content.charAt(content.length() - 1) == '\n') {
                contentBuilder.append(content);
            } else {
                contentBuilder.append(content).append("\n");
            }
        }
        contentBuilder.append("=======").append("\n");
        if (targetBlobId != null) {
            Blob targetBlob = Blob.fromFile(targetBlobId);
            String content = targetBlob.getContentAsString();
            if (content.charAt(content.length() - 1) == '\n') {
                contentBuilder.append(content);
            } else {
                contentBuilder.append(content).append("\n");
            }
        }
        contentBuilder.append(">>>>>>>");
        return contentBuilder.toString();
    }

    /** check if exit. */
    @SuppressWarnings("ConstantConditions")
    private void checkExit(String targetBranchName) {
        File targetBranchHeadFile = getBranchHeadFile(targetBranchName);
        if (!targetBranchHeadFile.exists()) {
            exit("A branch with that name does not exist.");
        }
        if (targetBranchName.equals(CURRENT_BRANCH.get())) {
            exit("Cannot merge a branch with itself.");
        }
        if (!stagingArea.get().isClean()) {
            exit("You have uncommitted changes.");
        }
        // 获得目标分支头提交
        Commit targetBranchHeadCommit = getBranchHeadCommit(targetBranchHeadFile);
        checkUntracked(targetBranchHeadCommit);

        // 获得最近的公共父提交
        Commit lcaCommit = getLatestCommonAncestorCommit(headCommit.get(), targetBranchHeadCommit);
        String lcaCommitId = lcaCommit.getId();

        if (lcaCommitId.equals(targetBranchHeadCommit.getId())) {
            exit("Given branch is an ancestor of the current branch.");
        }
        if (lcaCommitId.equals(headCommit.get().getId())) {
            checkoutCommit(targetBranchHeadCommit);
            setCurrentBranch(targetBranchName);
            exit("Current branch fast-forwarded.");
        }
    }

    /** Write conflict content to file and stage it. */
    private void writeAndStage(String headCmtBId, String targetCmtBId, File file) {
        String confCnt = getConflictContent(headCmtBId, targetCmtBId);
        writeContents(file, confCnt);
        stagingArea.get().add(file);
    }

    /** Merge current branch with the given branch. */
    @SuppressWarnings("ConstantConditions")
    public void merge(String targetBranchName) {
        // 检查是否退出
        checkExit(targetBranchName);
        // 获得目标分支头提交
        Commit targetBranchHeadCommit = getBranchHeadCommit(targetBranchName);
        checkUntracked(targetBranchHeadCommit);
        // 获得最近的公共父提交
        Commit lcaCommit = getLatestCommonAncestorCommit(headCommit.get(), targetBranchHeadCommit);

        boolean hasConflict = false;
        Map<String, String> headCmtTkedFilesMap = new HashMap<>(headCommit.get().getTracked());
        Map<String, String> targetCmtTkedFilesMap = targetBranchHeadCommit.getTracked();
        Map<String, String> lcaCmtTkedFilesMap = lcaCommit.getTracked();

        // 遍历 lcaCommit 跟踪的文件
        for (Map.Entry<String, String> entry : lcaCmtTkedFilesMap.entrySet()) {
            String filePath = entry.getKey();
            File file = new File(filePath);
            String blobId = entry.getValue();

            String headCmtBId = headCmtTkedFilesMap.get(filePath);
            String targetCmtBId = targetCmtTkedFilesMap.get(filePath);

            if (targetCmtBId != null) { // 在目标分支上
                if (!targetCmtBId.equals(blobId)) { // 在目标分支上修改
                    if (headCmtBId != null) { //在当前分支上
                        if (headCmtBId.equals(blobId)) { // 当前分支未修改
                            Blob.fromFile(targetCmtBId).writeContentToSource();
                            stagingArea.get().add(file);
                        } else { // 在当前分支修改
                            if (!headCmtBId.equals(targetCmtBId)) { // modified in different ways
                                hasConflict = true;
                                writeAndStage(headCmtBId, targetCmtBId, file);
                            }  // modified in the same ways
                        }
                    } else { // deleted in current branch
                        hasConflict = true;
                        writeAndStage(null, targetCmtBId, file);
                    }
                }  // else not modified in the target branch
            } else { // deleted in the target branch
                if (headCmtBId != null) { // exists in the current branch
                    if (headCmtBId.equals(blobId)) { // not modified in the current branch
                        stagingArea.get().remove(file);
                    } else { // modified in the current branch
                        hasConflict = true;
                        writeAndStage(headCmtBId, null, file);
                    }
                } // deleted in the current branch
            }
            headCmtTkedFilesMap.remove(filePath);
            targetCmtTkedFilesMap.remove(filePath);
        }

        // 遍历 target
        for (Map.Entry<String, String> entry :targetCmtTkedFilesMap.entrySet()) {
            String filePath = entry.getKey();
            File file = new File(filePath);
            String targetCmtBId = entry.getValue();
            String headCmtBId = headCmtTkedFilesMap.get(filePath);

            if (headCmtBId != null) { // added in both branches
                if (!headCmtBId.equals(targetCmtBId)) {
                    hasConflict = true;
                    writeAndStage(headCmtBId, targetCmtBId, file);
                } // modified in the same ways
            } else { // only added in the target branch
                Blob.fromFile(targetCmtBId).writeContentToSource();
                stagingArea.get().add(file);
            }
        }

        String newMsg = "Merged " + targetBranchName + " into " + CURRENT_BRANCH.get() + ".";
        commit(newMsg, targetBranchHeadCommit.getId());

        if (hasConflict) {
            message("Encountered a merge conflict.");
        }
    }
}
