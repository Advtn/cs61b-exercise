package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.function.Supplier;

import static gitlet.Utils.*;

public class MyUtils {

    /**
     * Get a lazy initialized value.
     *
     * @param delegate Function to get the value
     * @param <T>      Type of the value
     * @return Lazy instance
     */
    public static <T> Lazy<T> lazy(Supplier<T> delegate) {
        return new Lazy<>(delegate);
    }

    /**
     * Print error message and exit the program.
     */
    public static void exit(String message, Object... args) {
        message(message, args);
        System.exit(0);
    }

    /**
     *  Mkdir method, print the error message if directory already exists.
     */
    public static void mkdir(File fileDir) {
        if (!fileDir.mkdir()) {
            throw new IllegalArgumentException(String.format("mkdir: %s: Failed to create.", fileDir.getPath()));
        }
    }

    /**
     * Delete the file.
     *
     * @param file File instance
     */
    public static void rm(File file) {
        if (!file.delete()) {
            throw new IllegalArgumentException(String.format("rm: %s: Failed to delete.", file.getPath()));
        }
    }

    /**
     * Get a File instance with the path generated from SHA1 id in the objects folder.
     * @param id SHA1 id
     * @return File instance
     */
    public static File getObjectFile(String id) {
        String dirName = getObjectDirName(id);
        String fileName = getObjectName(id);
        return join(Repository.OBJECTS_DIR, dirName, fileName);
    }

    /** Get the object directory file name. */
    public static String getObjectDirName(String id) {
        return id.substring(0, 2);
    }

    /** Get the object file name. */
     public static String getObjectName(String id) {
        return id.substring(2);
     }

    /**
     * save object file by object's sha1 id.
     * @param file where object will be stored
     * @param obj object will be stored
     */
    public static void saveObjectFile(File file, Serializable obj) {
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdir();
        }
        writeObject(file, obj);
    }
}
