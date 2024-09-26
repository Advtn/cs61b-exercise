package gitlet;

import java.io.File;
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
    private static final String DEFAULT_HEAD_REF_PREFIX = "ref: refs/heads/";

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
     *  */
    private static final File index = join(GITLET_DIR, "index");

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
        mkdir(HEADS_DIR);
        setCurrentBranch(DEFAULT_BRANCH_NAME);
        createInitialCommit();
    }

    /**
     * set current branch.
     * @param branchName Name of branch
     */
    private static void setCurrentBranch(String branchName) {
        writeContents(HEAD, DEFAULT_HEAD_REF_PREFIX + branchName);
    }

    /**
     * Set the branch.
     * write the commit id to the branch file.
     * */
    private static void setBranch(String branchName, String commitId) {
        File branchFile = getBranchFile(branchName);
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
    private static Commit getBranch(String branchName) {
        File branchFile = getBranchFile(branchName);
        String branchCommitId = readContentsAsString(branchFile);
        return Commit.fromFile(branchCommitId);
    }

    /** Creates initial commit. */
    private static void createInitialCommit() {
        Commit initialCommit = new Commit();
        initialCommit.save();
        setBranch(DEFAULT_BRANCH_NAME, initialCommit.getId());
    }

    public static void checkout(String branchToCheckout) {
        setCurrentBranch(branchToCheckout);
    }
}
