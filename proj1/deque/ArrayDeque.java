package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T[] items;
    private int first;
    private int last;
    private int size;
    private int capacity = 8;
    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }
    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;
        private int count;
        ArrayDequeIterator() {
            wizPos = first;
            count = 0;
        }
        @Override
        public boolean hasNext() {
            return count < size;
        }
        @Override
        public T next() {
            T returnItem = items[wizPos];
            wizPos = (wizPos + 1) % capacity;
            count++;
            return returnItem;
        }
    }
    public ArrayDeque() {
        items = (T []) new Object[capacity];
        size = 0;
        first = 0;
        last = 0;
    }
    /** I learned from AI, this is a perfect method. */
    private void resize(int newCapacity) {
        T[] newArray = (T []) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = items[(first + i) % capacity];
        }
        items = newArray;
        first = 0;
        last = size % newCapacity;
        capacity = newCapacity;
    }
    @Override
    public void addFirst(T x) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        first = (first - 1 + capacity) % capacity;
        items[first] = x;
        size++;
    }
    @Override
    public void addLast(T x) {
        if (size == capacity) {
            resize(capacity * 2);
        }
        items[last] = x;
        last = (last + 1) % capacity;
        size++;
    }
    @Override
    public T get(int i) {
        return items[(first + i) % capacity];
    }
    private T getFirst() {
        return items[first];
    }
    private T getLast() {
        return items[(last - 1 + capacity) % capacity];
    }
    @Override
    public int size() {
        return size;
    }
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T value = getLast();
        last = (last - 1 + capacity) % capacity;
        items[last] = null;
        size--;
        if (size > 0 && size == capacity / 4) {
            resize(capacity / 2);
        }
        return value;
    }
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T value = getFirst();
        items[first] = null;
        first = (first + 1) % capacity;
        size--;
        if (size > 0 && size == capacity / 4) {
            resize(capacity / 2);
        }
        return value;
    }
    @Override
    public void printDeque() {
        int index = first;
        for (int i = 0; i < items.length; i++) {
            if (items[index] != null) {
                System.out.print(items[index] + " ");
                index = (index + 1) % capacity;
            } else {
                break;
            }
        }
        System.out.println();
    }
}
