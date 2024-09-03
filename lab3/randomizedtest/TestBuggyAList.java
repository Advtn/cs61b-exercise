package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testAddThreeRemove(){
        AListNoResizing<Integer> listNoResizing = new AListNoResizing<>();
        BuggyAList<Integer> list = new BuggyAList<>();
        listNoResizing.addLast(4);
        listNoResizing.addLast(5);
        listNoResizing.addLast(6);
        list.addLast(4);
        list.addLast(5);
        list.addLast(6);
        assertEquals(listNoResizing.removeLast(),list.removeLast());
        assertEquals(listNoResizing.removeLast(),list.removeLast());
        assertEquals(listNoResizing.removeLast(),list.removeLast());
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> L1=new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                L1.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size1 = L1.size();
            }else if(L.size()>0 && operationNumber==2){
                // getLast
                int last=L.getLast();
                int last1=L1.getLast();
                assertEquals(last,last1);
                // removeLast
                int removeLast=L.removeLast();
                int removeLast1=L1.removeLast();
                assertEquals(removeLast,removeLast1);
            }
        }
    }
}
