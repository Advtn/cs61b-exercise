package deque;

import java.util.Comparator;
import java.util.ArrayDeque;


public class MaxArrayDeque<T> extends ArrayDeque<T>{

    Comparator<T> comparator;
    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }


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
    public static void main(String[] args) {
        // 创建一个 MaxArrayDeque，并使用一个自定义 Comparator
//        MaxArrayDeque<Integer> maxDeque = new MaxArrayDeque<>(Comparator.naturalOrder());

        // 添加元素
//        maxDeque.add(5);
//        maxDeque.add(3);
//        maxDeque.add(9);
//        maxDeque.add(1);

        // 使用构造时提供的 Comparator 来获取最大值
//        System.out.println("Max using constructor's comparator: " + maxDeque.max());

        // 使用一个新的 Comparator 来获取最大值
//        System.out.println("Max using new comparator (reverse order): " + maxDeque.max(Comparator.reverseOrder()));
    }
}

