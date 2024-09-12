package deque;


import java.util.Iterator;
import java.util.Objects;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class StuffNode {
        private T item;
        private StuffNode next;
        private StuffNode prev;
        StuffNode(T i) {
            item = i;
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }
    private class LinkedListDequeIterator implements Iterator<T> {
        private StuffNode wizPos;
        LinkedListDequeIterator() {
            wizPos = sentinel.next;
        }
        @Override
        public boolean hasNext() {
            return wizPos != sentinel;
        }
        @Override
        public T next() {
            T returnItem = wizPos.item;
            wizPos = wizPos.next;
            return returnItem;
        }
    }
    private final StuffNode sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new StuffNode(null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }
    @Override
    public void addFirst(T item) {
        StuffNode first = getFirstNode();
        sentinel.next = new StuffNode(item);
        first.prev = sentinel.next;
        sentinel.next.next = first;
        sentinel.next.prev = sentinel;
        size = size + 1;
    }
    @Override
    public void addLast(T item) {
        StuffNode last = getLastNode();
        sentinel.prev = new StuffNode(item);
        last.next = sentinel.prev;
        sentinel.prev.prev = last;
        sentinel.prev.next = sentinel;
        size = size + 1;
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public void printDeque() {
        StuffNode start = sentinel.next;
        while (start != sentinel) {
            System.out.print(start.item + " ");
            start = start.next;
        }
        System.out.println();
    }

    private StuffNode getFirstNode() {
        return sentinel.next;
    }
    private StuffNode getLastNode() {
        return sentinel.prev;
    }
    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        StuffNode first = getFirstNode();
        sentinel.next = first.next;
        first.next.prev = sentinel;
        first.prev = null;
        first.next = null;
        size = size - 1;
        return first.item;
    }
    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        StuffNode last = getLastNode();
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        last.prev = null;
        last.next = null;
        size = size - 1;
        return last.item;
    }
    @Override
    public T get(int i) {
        StuffNode start = getFirstNode();
        for (int k = 0; k < i; k++) {
            if (start.next == sentinel) {
                return null;
            }
            start = start.next;
        }
        return start.item;
    }
    private T getFirst() {
        return sentinel.next.item;
    }
    private T getLast() {
        return sentinel.prev.item;
    }
    private T getRecursive(StuffNode node, int index) {
        if (index == 0) {
            return node.item;
        }
        return getRecursive(node.next, index - 1);
    }
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursive(sentinel.next, index);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (this.size != other.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            T thisItem = this.get(i);
            T otherItem = other.get(i);
            if (!Objects.equals(thisItem, otherItem)) {
                return false;
            }
        }
        return true;
    }
}
