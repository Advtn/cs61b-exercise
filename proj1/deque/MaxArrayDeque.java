package deque;

import java.util.ArrayDeque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;
    MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }
    public T max() {
        return max(comparator);
    }
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxElement = null;
        for (T element : this) {
            if (maxElement == null || c.compare(element, maxElement) > 0) {
                maxElement = element;
            }
        }
        return maxElement;
    }
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!(o instanceof MaxArrayDeque)) {
            return false;
        }
        if (((MaxArrayDeque<?>) o).max() != max()) {
            return false;
        }
        return super.equals(o);
    }
}



