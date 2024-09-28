package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

/** The staging area representation. */
public class StagingArea implements Serializable {

    /** The tracked files Map with file path as key and SHA1 id as value. */
    private Map<String, String> tracked;

    /** The added files Map in the staging area. */
    private final Map<String, String> added = new HashMap<>();

    /** Get a StagingArea instance from the INDEX file. */
    public static StagingArea fromFile() {
        return readObject(Repository.INDEX, StagingArea.class);
    }

    /** Save this instance to the file INDEX. */
    public void save() {
        writeObject(Repository.INDEX, this);
    }

    /** Set tracked files. */
    public void setTracked(Map<String, String> fileMap) {
        tracked = fileMap;
    }

    /** add file to staging area. */
    public boolean add(File file) {
        String filePath = file.getPath();

        Blob blob = new Blob(file);
        String blobId = blob.getId();

        String oldBlobId = added.put(filePath, blobId);
        if (oldBlobId != null && oldBlobId.equals(blobId)) {
            return false;
        }

        if (!blob.getFile().exists()) {
            blob.save();
        }
        return true;
    }

    /** Get added Map */
    public Map<String, String> getAdded() {
        return added;
    }
}
