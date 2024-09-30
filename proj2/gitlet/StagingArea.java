package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static gitlet.MyUtils.rm;
import static gitlet.Utils.readObject;
import static gitlet.Utils.writeObject;

/** The staging area representation. */
public class StagingArea implements Serializable {

    /**
     * The tracked files Map with file path as key and SHA1 id as value.
     * After git add, the file will be tracked.
     * */
    private Map<String, String> tracked;

    /** The added files Map in the staging area. */
    private final Map<String, String> added = new HashMap<>();

    /** The removed files Set with file path as key. */
    private final Set<String> removed = new HashSet<>();

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

        // Check if the file be tracked
        String trackedBlobId = tracked.get(filePath);
        if (trackedBlobId != null) {
            // If the file be tracked and file content unchanged, remove it from added and removed.
            if (trackedBlobId.equals(blobId)) {
                if (added.remove(filePath) != null) {
                    return true;
                }
                return removed.remove(filePath);
            }
        }

        // put method return old value if filePath exist
        // return null if filePath doesn't exist
        String oldBlobId = added.put(filePath, blobId);
        if (oldBlobId != null && oldBlobId.equals(blobId)) {
            return false;
        }

        if (!blob.getFile().exists()) {
            blob.save();
        }
        return true;
    }


    /** Get added files Map */
    public Map<String, String> getAdded() {
        return added;
    }

    /** Get removed files Set. */
    public Set<String> getRemoved() {
        return removed;
    }

    /** Check whether staging area is clean. */
    public boolean isClean() {
        return added.isEmpty() && removed.isEmpty();
    }

    /** Clear the staging area.*/
    public void clear() {
        added.clear();
        removed.clear();
    }

    /** Perform a commit. Return tracked files Map after commit. */
    public Map<String, String> commit() {
        tracked.putAll(added);
        for (String filePath : removed) {
            tracked.remove(filePath);
        }
        clear();
        return tracked;
    }

    /** Remove file. */
    public boolean remove(File file) {
        String filePath = file.getPath();

        String addedBlobId = added.remove(filePath);
        if (addedBlobId != null) {
            return true;
        }

        if (tracked.get(filePath) != null) {
            if (file.exists()) {
               rm(file);
            }
            return removed.add(filePath);
        }
        return false;
    }
}
