package gitlet;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;

/**
 * Represents files to store contents of tracked files.
 * @author Advtn
 */
public class Blob implements Serializable {

    /**
     * The source file from constructor.
     */
    private final File source;

    /** The content of the source file. */
    private final byte[] content;

    /**
     * The Blob id.
     */
    private final String id;

    /** The Blob file */
    private final File file;

    public Blob(File sourceFile) {
        source = sourceFile;
        content = readContents(sourceFile);
        id = generateId(sourceFile);
        file = getObjectFile(id);
    }

    /** Get a Blob instance from file. */
    public static Blob fromFile(String id) {
        return readObject(getObjectFile(id), Blob.class);
    }

    /** Save this instance to the file. */
    public void save() {
        saveObjectFile(file, this);
    }

    /** Get the SHA1 id. */
    public String getId() {
        return id;
    }

    /** Get the blob file. */
    public File getFile() {
        return file;
    }

    /**
     * Write the file content back to the source file.
     */
    public void writeContentToSource() {
        writeContents(source, content);
    }

    /** Generate Blob id by sha1 */
    public static String generateId(File sourceFile) {
        String filePath = sourceFile.getPath();
        byte[] fileContent = readContents(sourceFile);
        return sha1(filePath, fileContent);
    }

    /** get content as string .*/
    public String getContentAsString() {
        return new String(content, StandardCharsets.UTF_8);
    }
}
