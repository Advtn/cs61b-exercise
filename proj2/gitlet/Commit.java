package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.MyUtils.getObjectFile;
import static gitlet.MyUtils.saveObjectFile;
import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author Advtn
 */
public class Commit implements Serializable {


    /** The message of this Commit. */
    private final String message;

    /**
     * The date of this Commit be created.
     */
    private final Date date;

    /**
     * The commit id that sha1 generated.
     */
    private final String id;

    /**
     * The parents commit sha1 id.
     */
    private final List<String> parents;

    /**
     * The tracked files Map with file path as key and SHA1 id as value.
     */
    private final Map<String, String> tracked;

    /**
     * commit object file stored in the objects folder.
     * path generated by SHA1 id that first 2 is dir name and the rest is file name.
     */
    private final File file;

    /**
     * Initial commit
     */
    public Commit() {
        date = new Date(0);
        message = "initial commit";
        parents = new ArrayList<>();
        tracked = new HashMap<>();
        id = generateId();
        file = getObjectFile(id);
    }

    public Commit(String message, List<String> parents, Map<String, String> tracked) {
        date = new Date();
        this.message = message;
        this.parents = parents;
        this.tracked = tracked;
        id = generateId();
        file = getObjectFile(id);
    }

    /**
     * Get the timestamp.
     * @return Date and time
     */
    public String getTimestamp() {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return dateFormat.format(date);
    }

    /** generate SHA-1 hash code.  */
    private String generateId() {
        return sha1(getTimestamp(), message, parents.toString(), tracked.toString());
    }

    /**
     * Get the commit message.
     * @return String of commit message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the date when the commit be created.
     */
    public Date getDate() {
        return date;
    }

    /** Get the sha1 id. */
    public String getId() {
        return id;
    }

    /** Get the tracked .*/
    public Map<String, String> getTracked() {
        return tracked;
    }

    /**
     * Get the parents commit id.
     */
    public List<String> getParents() {
        return parents;
    }

    /** Serializes and saves a commit to a file. */
    public void save() {
        saveObjectFile(file, this);
    }

    /** Reads in and deserializes a commit from a file. */
    public static Commit fromFile(String id) {
        return readObject(getObjectFile(id), Commit.class);
    }

    /** Get commit log. */
    public void getLog() {
        System.out.print("commit" + " " + id);
        System.out.print("Date:" + "   " + getTimestamp());
    }
}
