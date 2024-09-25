package gitlet;


import java.io.File;

import static gitlet.Utils.*;

public class MyUtils {

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
        fileDir.mkdir();
    }
}
