package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private class BSTNode {
        private K key;
        private V value;
        private BSTNode left;
        private BSTNode right;
        BSTNode(K k, V v) {
            key = k;
            value = v;
        }
    }
    private BSTNode root;
    private int size;

    @Override
    public void clear() {
        root = null;
        size = 0;
    }
    @Override
    public boolean containsKey(K key) {
        BSTNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return true;
            }
        }
        return false;
    }
    @Override
    public V get(K key) {
        BSTNode node = root;
        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp > 0) {
                node = node.right;
            } else if (cmp < 0) {
                node = node.left;
            } else {
                return node.value;
            }
        }
        return null;
    }
    @Override
    public int size() {
        return size;
    }
    private BSTNode put(BSTNode node, K key, V value){
        if (node == null) {
            return new BSTNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp > 0) {
            node.right = put(node.right, key, value);
        } else if (cmp < 0) {
            node.left = put(node.left, key, value);
        } else {
            node.value = value;
        }
        return node;
    }
    @Override
    public void put(K key, V value) {
       root = put(root, key, value);
       size++;
    }
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
    private void printInOrder(BSTNode root) {
        if (root == null) {
            return;
        }
        printInOrder(root.left);
        System.out.println(root.key);
        printInOrder(root.right);
    }
    public void printInOrder() {
        printInOrder(root);
    }
}
