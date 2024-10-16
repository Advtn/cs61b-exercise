package gitlet;


import java.io.*;
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
            System.out.printf("mkdir: %s: Failed to create.%n", fileDir.getPath());
        }
    }

    /**
     * Delete the file.
     *
     * @param file File instance
     */
    public static void rm(File file) {
        if (!file.delete()) {
            System.out.printf("rm: %s: Failed to delete.%n", file.getPath());
        }
    }

    /**
     * Get a File instance with the path generated from SHA1 id in the objects folder.
     * @param id SHA1 id
     * @return File instance
     */
    public static File getObjectFile(String id) {
        String dirName = getObjectDirName(id);
        String fileName = getObjectFileName(id);
        return join(Repository.OBJECTS_DIR, dirName, fileName);
    }

    /** Get the object directory file name. */
    public static String getObjectDirName(String id) {
        return id.substring(0, 2);
    }

    /** Get the object file name. */
    public static String getObjectFileName(String id) {
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
            mkdir(dir);
        }
        writeObject(file, obj);
    }

    /**
     * Tells if the deserialized object instance of given class.
     *
     * @param file File instance
     * @param c    Target class
     * @return true if is instance
     */
    public static boolean isFileInstanceOf(File file, Class<?> c) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return c.isInstance(in.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }
}
