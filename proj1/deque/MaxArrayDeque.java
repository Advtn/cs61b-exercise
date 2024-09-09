package deque;

import java.util.Comparator;
import java.util.Iterator;

public class MaxArrayDeque<T> extends ArrayDeque<T>{
    private ArrayDeque<T> deque;
    private Comparator<T> comp;
    public MaxArrayDeque(Comparator<T> c){
        deque = new ArrayDeque<>();
        comp = c;
    }
    public T max(){
        if(deque.isEmpty()) {
            return null;
        }
        return null;
    }
    public T max(Comparator<T> c){
        return null;
    }
}
