package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author fuchaoma
 */
public class MyHashMap<K, V> implements Map61B<K, V>, Iterable<K> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.75;
    private final double maxLoadFactor;
    private Collection<Node>[] buckets;
    private int size;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_MAX_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_MAX_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        maxLoadFactor = maxLoad;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  3. Iterate through items (`iterator` method)
     *  2. Remove items (`remove` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }
    
    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    private Node getNode(K key) {
        int bucketIndex = getBucketIndex(key);
        return getNode(key, bucketIndex);
    }
    private Node getNode(K key, int bucketIndex) {
        for (Node node : buckets[bucketIndex]) {
            if (key.equals(node.key)) {
                return node;
            }
        }
        return null;
    }
    @Override
    public V get(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;
    }

    @Override
    public int size() {
        return size;
    }
    private int getBucketIndex(K key) {
        return Math.floorMod(key.hashCode(), buckets.length);
    }
    @Override
    public void put(K key, V value) {
        int bucketIndex = getBucketIndex(key);
        Node node = getNode(key, bucketIndex);
        if (node != null) {
            node.value = value;
            return;
        }
        node = createNode(key, value);
        buckets[bucketIndex].add(node);
        size++;
    }

    @Override
    public Set<K> keySet() {
        HashSet<K> hs = new HashSet<>();
        for (K key : this) {
            hs.add(key);
        }
        return hs;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }
    private class MyHashMapIterator implements Iterator<K> {
        private Iterator<Node> currentListIterator;
        private int currentBucketIndex;
        private Iterator<Node> getCurrentListIterator() {
            return buckets[currentBucketIndex].iterator();
        }
        MyHashMapIterator() {
            currentBucketIndex = 0;
            currentListIterator = getCurrentListIterator();
        }
        @Override
        public boolean hasNext() {
            while (currentListIterator == null || !currentListIterator.hasNext()) {
                if (currentBucketIndex < buckets.length - 1) {
                    currentBucketIndex++;
                    currentListIterator = getCurrentListIterator();
                } else {
                    return false;
                }
            }
            return true;
        }

        @Override
        public K next() {
            if (!hasNext()) {
                return null;
            }
            return currentListIterator.next().key;
        }
    }
    public static void main(String[] args) {
        MyHashMap<String, Integer> h = new MyHashMap<>();
        h.put("hello",2);
        h.put("Hash",3);
        h.put("Map",1);
        h.put("Hi",7);
        h.put("My",6);
        h.put("Friend",5);
        System.out.println(h.keySet());
    }
}
