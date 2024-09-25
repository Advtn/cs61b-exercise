package gitlet;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *
 *  @author Advtn
 */
public class Commit {


    /** The message of this Commit. */
    private final String message;

    /**
     * The date of this Commit be created.
     */
    private final Date date;

    /**
     * The commit id that sha1 generated.
     */
    private String id;

    /**
     * Initial commit
     */
    public Commit() {
        date = new Date(0);
        message = "initial commit";
    }

    public Commit(String message) {
        date = new Date();
        this.message = message;
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
        return sha1();
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

    public static void main(String[] args) {
        Commit commit = new Commit("test");
        System.out.println(commit.getTimestamp());
    }
}
