package deque;



public class LinkedListDeque<T> {
    private class StuffNode {
        public T item;
        public StuffNode next;
        public StuffNode prev;
        public StuffNode(T i){
            item = i;
        }
    }

    private StuffNode sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new StuffNode(null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public LinkedListDeque(T item){
        sentinel = new StuffNode(null);
        sentinel.next = new StuffNode(item);
        sentinel.prev = sentinel.next;
        sentinel.next.next = sentinel;
        sentinel.next.prev = sentinel;
        size = 1;
    }

    public void addFirst(T item) {
        StuffNode first = sentinel.next;
        sentinel.next = new StuffNode(item);
        first.prev = sentinel.next;
        sentinel.next.prev = sentinel;
        sentinel.next.next = first;
        size = size + 1;
    }
    public void addLast(T item){
        StuffNode last = sentinel.prev;
        sentinel.prev = new StuffNode(item);
        last.next = sentinel.prev;
        sentinel.prev.prev = last;
        sentinel.prev.next = sentinel;
        size = size + 1;
    }
    public boolean isEmpty(){
        return false;
    }
    public int size(){
        return size;
    }
    public void printDeque(){

    }
    public T removeFirst(){
        return null;
    }
    public T removeLast(){
        return null;
    }
    public T get(int i){
        return null;
    }

    public static void main(String[] args) {
        LinkedListDeque<Integer> L = new LinkedListDeque<>(1);
        L.addLast(2);
        L.addFirst(3);
        L.addLast(4);
        L.addFirst(1);
        L.addFirst(9);
        L.addLast(5);
    }
}
