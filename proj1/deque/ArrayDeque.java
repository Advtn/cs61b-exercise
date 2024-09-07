package deque;

public class ArrayDeque <T>{
    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;
    public ArrayDeque() {
        items = (T []) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }
    private void resize(int capacity){
        T[] a = (T []) new Object[capacity];
        System.arraycopy(items,0,a,0,size);
        items = a;
    }
    public void addFirst(T x){
        if(size == items.length){
            resize(size + 1);
        }
        items[nextFirst] = x;
        if(nextFirst == 0){
            nextFirst = items.length;
        }
        nextFirst = nextFirst-1;
        size = size + 1;
    }
    public void addLast(T x) {
        if(size == items.length){
            resize(size * 2);
        }
        items[nextLast] = x;
        if(nextLast == items.length - 1){
            nextLast = -1;
        }
        nextLast = nextLast + 1;
        size = size + 1;
    }
    public boolean isEmpty(){
        return size == 0;
    }
    public T getLast() {
        return items[nextLast - 1];
    }
    public T getFirst(){
        return items[nextFirst + 1];
    }
    public T get(int i) {
        int index;
        if(nextFirst + 1 + i > items.length - 1){
            index = nextFirst + 1 + i - items.length;
        }else{
            index = nextFirst + 1 + i;
        }
        return items[index];
    }
    public int size() {
        return size;
    }
    public T removeLast() {
        T last = getLast();
        items[nextLast - 1] = null;
        nextLast = nextLast - 1;
        size = size - 1;
        return last;
    }
    public T removeFirst(){
        T first = getFirst();
        items[nextFirst + 1] = null;
        nextFirst = nextFirst + 1;
        size = size - 1;
        return first;
    }
    public void printDeque(){
        int index = nextFirst + 1;
        for(int i = 0; i < items.length; i++){
            if(items[index] != null){
                System.out.print(items[index]+" ");
                index++;
            }else{
                break;
            }
            if(index == items.length) {
                index = 0;
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ArrayDeque<Character> L = new ArrayDeque<>();
        L.addFirst('a');
        L.addLast('b');
        L.addFirst('c');
        L.addLast('d');
        L.addLast('e');
        L.addFirst('f');
        L.addFirst('g');
        L.addLast('h');
        L.printDeque();
        L.addLast('i');
        L.printDeque();
    }
}
