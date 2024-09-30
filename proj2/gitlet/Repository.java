package gitlet;

import jh61b.junit.In;

import java.io.File;
import java.net.StandardSocketOptions;
import java.nio.file.Paths;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

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
    private static final File HEADS_DIR = join(REFS_DIR, "heads");

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
    private static final Lazy<File[]> currentFiles = lazy(() -> CWD.listFiles(File::isFile));

    /** The current branch name. */
    private static final Lazy<String> currentBranch = lazy(() -> {
        String HEADFileContent = readContentsAsString(HEAD);
        return HEADFileContent.replace(HEAD_BRANCH_REF_PREFIX,"");
    });

    /** The commit that HEAD points to. */
    private final Lazy<Commit> HEADCommit = lazy(() -> getBranchCommit(currentBranch.get()));

    /** The staging area. */
    private final Lazy<StagingArea> stagingArea = lazy(() -> {
       StagingArea s = INDEX.exists()
           ? StagingArea.fromFile()
           : new StagingArea();
       s.setTracked(HEADCommit.get().getTracked());
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
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();
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
    private static void setBranchPointer(String branchName, String commitId) {
        File branchFile = getBranchFile(branchName);
        writeContents(branchFile, commitId);
    }

    private static void setBranchPointer(File branchFile, String commitId) {
        writeContents(branchFile, commitId);
    }

    /**
     * Get branch file.
     * generate branch file in the heads directory.
     * */
    private static File getBranchFile(String branchName) {
        return join(HEADS_DIR, branchName);
    }

    /** Get commit that branch pointed. */
    private static Commit getBranchCommit(String branchName) {
        File branchFile = getBranchFile(branchName);
        String branchCommitId = readContentsAsString(branchFile);
        return Commit.fromFile(branchCommitId);
    }

    /** Creates initial commit. */
    private static void createInitialCommit() {
        Commit initialCommit = new Commit();
        initialCommit.save();
        setBranchPointer(DEFAULT_BRANCH_NAME, initialCommit.getId());
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
        for (File file : currentFiles.get()) {
            String filePath = file.getPath();
            String blobId = Blob.generateId(file);
            filesMap.put(filePath, blobId);
        }
        return filesMap;
    }

    /** Print logs of the current branch. */
    public void log() {
        StringBuilder logBuilder = new StringBuilder();
        Commit currentCommit = HEADCommit.get();
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
        statusBuilder.append("=== Branches ===").append("\n").append("\n");
        statusBuilder.append("*").append(currentBranch.get()).append("\n\n");
        String[] branchNames = HEADS_DIR.list((dir, name) -> !name.equals(currentBranch.get()));
        if (branchNames != null) {
            Arrays.sort(branchNames);
            for (String branchName : branchNames) {
                statusBuilder.append(branchName).append("\n");
            }
        }
        statusBuilder.append("\n\n");

        Map<String, String> addedFilesMap = stagingArea.get().getAdded();
        Set<String> removedFilesMap = stagingArea.get().getRemoved();

        // Staged Files
        statusBuilder.append("=== Staged Files ===").append("\n").append("\n");
        appendFileNamesInOrder(statusBuilder, addedFilesMap.keySet());
        statusBuilder.append("\n\n");

        // Removed Files
        statusBuilder.append("=== Removed Files ===").append("\n").append("\n");
        appendFileNamesInOrder(statusBuilder, removedFilesMap);
        statusBuilder.append("\n\n");

        // Modifications Not Staged For Commit
        statusBuilder.append("=== Modifications Not Staged For Commit ===").append("\n").append("\n");
        List<String> modifiedNotStageFilePaths = new ArrayList<>();
        Set<String> deletedNotStageFilesPaths = new HashSet<>();

        Map<String, String> currentFilesMap = getCurrentFilesMap();
        Map<String, String> trackedFilesMap = HEADCommit.get().getTracked();

        trackedFilesMap.putAll(addedFilesMap);
        for (String filePath : removedFilesMap) {
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
                deletedNotStageFilesPaths.add(filePath);
            }
        }

        modifiedNotStageFilePaths.sort(String::compareTo);

        for (String filePath : modifiedNotStageFilePaths) {
            String fileName = Paths.get(filePath).getFileName().toString();
            statusBuilder.append(fileName);
            if (deletedNotStageFilesPaths.contains(filePath)) {
                statusBuilder.append(" ").append("(deleted)");
            } else {
                statusBuilder.append(" ").append("(modified)");
            }
            statusBuilder.append("\n\n");
        }
        statusBuilder.append("\n\n");

        // Untracked Files
        statusBuilder.append("=== Untracked Files ===").append("\n").append("\n");
        appendFileNamesInOrder(statusBuilder, currentFilesMap.keySet());
        statusBuilder.append("\n");


        System.out.print(statusBuilder);
    }

    /** Append lines of file name in order from files paths Set to StringBuilder.*/
    private static void appendFileNamesInOrder(StringBuilder stringBuilder, Collection<String> filePathCollection) {
        List<String> filesPathList = new ArrayList<>(filePathCollection);
        appendFileNamesInOrder(stringBuilder, filesPathList);
    }

    /** Append lines of file name in order from files paths Set to StringBuilder.*/
    private static void appendFileNamesInOrder(StringBuilder stringBuilder, List<String> filePathList) {
        filePathList.sort(String::compareTo);
        for (String filePath : filePathList) {
            String fileName = Paths.get(filePath).getFileName().toString();
            stringBuilder.append(fileName).append("\n").append("\n");
        }
    }

    /** The commit command. */
    public void commit(String message) {
        if (stagingArea.get().isClean()) {
            exit("No changes added to the commit.");
        }
        Map<String, String> newTrackedFilesMap = stagingArea.get().commit();
        stagingArea.get().save();
        List<String> parents = new ArrayList<>();
        parents.add(HEADCommit.get().getId());
        Commit newCommit = new Commit(message, parents, newTrackedFilesMap);
        newCommit.save();
        setBranchPointer(currentBranch.get(), newCommit.getId());
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

    /** The commit command. */
    public void find(String commitMessage) {

    }

    /** Creates a new branch with the given name. */
    public void branch(String newBranchName) {
        File newBranchFile = getBranchFile(newBranchName);
        if (newBranchFile.exists()) {
            exit("A branch with that name already exists.");
        }
        setBranchPointer(newBranchFile, HEADCommit.get().getId());
    }
}
