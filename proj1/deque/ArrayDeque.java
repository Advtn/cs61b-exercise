package deque;

public class ArrayDeque <T>{
    private T[] items;
    private int first;
    private int last;
    private int size;
    private int capacity = 8;
    public ArrayDeque() {
        items = (T []) new Object[capacity];
        size = 0;
        first = 0;
        last = 0;
    }
    /** I learned from AI, this is a perfect method.It puts the original queue into the new queue from 0. */
    private void resize(int newCapacity){
        T[] newArray = (T []) new Object[newCapacity];
        for(int i = 0; i < size; i++){
            newArray[i] = items[(first + i) % capacity];
        }
        items = newArray;
        first = 0;
        last = size % newCapacity;
        capacity = newCapacity;
    }
    public void addFirst(T x){
        if(size == capacity){
            resize(capacity * 2);
        }
        first = (first - 1 + capacity) % capacity;
        items[first] = x;
        size++;
    }
    public void addLast(T x) {
        if(size == capacity){
            resize(capacity * 2);
        }
        items[last] = x;
        last = (last + 1) % capacity;
        size++;
    }
    public boolean isEmpty(){
        return size == 0;
    }
    public T get(int i) {
        return items[(first + i) % capacity];
    }
    public int size() {
        return size;
    }
    public T removeLast() {
        if(isEmpty()) return null;
        last = (last - 1 + capacity) % capacity;
        T value = items[last];
        items[last] = null;
        size--;
        if(size > 0 && size == capacity / 4){
            resize(capacity / 2);
        }
        return value;
    }
    public T removeFirst(){
        if(isEmpty()) return null;
        T value = items[first];
        items[first] = null;
        first = (first + 1) % capacity;
        size--;
        if(size > 0 && size == capacity / 4){
            resize(capacity / 2);
        }
        return value;
    }
    public void printDeque(){
        int index = first;
        for(int i = 0; i < items.length; i++){
            if(items[index] != null){
                System.out.print(items[index]+" ");
                index = (index + 1) % capacity;
            }else{
                break;
            }
        }
        System.out.println();
    }

}
