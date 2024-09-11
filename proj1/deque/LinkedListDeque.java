package deque;


import java.util.Iterator;

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
        public LinkedListDequeIterator() {
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

    public LinkedListDeque(T item) {
        sentinel = new StuffNode(null);
        sentinel.next = new StuffNode(item);
        sentinel.prev = sentinel.next;
        sentinel.next.next = sentinel;
        sentinel.next.prev = sentinel;
        size = 1;
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
    public T getFirst() {
        return sentinel.next.item;
    }
    public T getLast() {
        return sentinel.prev.item;
    }
    private T _getRecursive(StuffNode node, int index) {
        if (index == 0) {
            return node.item;
        }
        return _getRecursive(node.next,index - 1);
    }
    public T getRecursive(int index) {
        if(index < 0 || index >= size) {
            return null;
        }
        return _getRecursive(sentinel.next, index);
    }
}
