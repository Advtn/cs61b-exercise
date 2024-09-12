package deque;

import java.util.ArrayDeque;
import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comparator;

    // 构造函数
    public MaxArrayDeque(Comparator<T> c) {
        this.comparator = c;
    }

    // 返回由先前给定的 Comparator 控制的双端队列中的最大元素
    public T max() {
        if (isEmpty()) {
            return null;
        }
        T maxElement = null;
        for (T element : this) {
            if (maxElement == null || comparator.compare(element, maxElement) > 0) {
                maxElement = element;
            }
        }
        return maxElement;
    }

    // 返回由参数 Comparator c 控制的双端队列中的最大元素
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
}



