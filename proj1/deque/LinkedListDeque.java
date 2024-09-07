package deque;


import java.util.Stack;

public class LinkedListDeque<T> {
    private class StuffNode {
        public T item;
        public StuffNode next;
        public StuffNode prev;
        public StuffNode(T i){
            item = i;
        }
    }

    private final StuffNode sentinel;
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
        StuffNode first = getFirst();
        sentinel.next = new StuffNode(item);
        first.prev = sentinel.next;
        sentinel.next.next = first;
        sentinel.next.prev = sentinel;
        size = size + 1;
    }
    public void addLast(T item){
        StuffNode last = getLast();
        sentinel.prev = new StuffNode(item);
        last.next = sentinel.prev;
        sentinel.prev.prev = last;
        sentinel.prev.next = sentinel;
        size = size + 1;
    }
    public boolean isEmpty(){
        return size == 0;
    }
    public int size(){
        return size;
    }
    public void printDeque(){
        StuffNode start = sentinel.next;
        while (start != sentinel){
            System.out.print(start.item+" ");
            start = start.next;
        }
    }
    private StuffNode getFirst(){
        return sentinel.next;
    }
    private StuffNode getLast(){
        return sentinel.prev;
    }
    public T removeFirst(){
        if(size == 0) return null;
        StuffNode first = getFirst();
        sentinel.next = first.next;
        first.next.prev = sentinel;
        first.prev = null;
        first.next = null;
        size = size - 1;
        return first.item;
    }
    public T removeLast(){
        if(size == 0) return null;
        StuffNode last = getLast();
        sentinel.prev = last.prev;
        last.prev.next = sentinel;
        last.prev = null;
        last.next = null;
        size = size - 1;
        return last.item;
    }
    public T get(int i){
        StuffNode start = getFirst();
        for(int k = 0; k < i; k++){
            if(start.next == sentinel) return null;
            start = start.next;
        }
        return start.item;
    }
    private T _getRecursive(StuffNode node, int index){
        if(index == 0){
            return node.item;
        }
        return _getRecursive(node.next,index - 1);
    }
    public T getRecursive(int index){
        if(index < 0 || index >= size) return null;
        return _getRecursive(sentinel.next, index);
    }
    public static void main(String[] args) {
        LinkedListDeque<Integer> L = new LinkedListDeque<>();
        L.addLast(2);
        L.addFirst(3);
        L.addLast(4);
        L.addFirst(1);
        L.addFirst(9);
        L.addLast(5);
//        L.removeFirst();
//        L.removeLast();
        System.out.println(L.getRecursive(3));
        System.out.println(L.get(4));
        System.out.println(L.get(0));
        System.out.println(L.get(2));
        L.printDeque();
    }
}
