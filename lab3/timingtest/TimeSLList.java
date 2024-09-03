package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        System.out.println("Timing table for getLast");
        AList<Integer> Ns=new AList<>();
        AList<Double> times=new AList<>();
        AList<Integer> opCounts=new AList<>();
        int startNum=1000;
        for(int j = 0 ; j < 8 ; j++){
            SLList<Integer> El=new SLList<>();
            for(int i = 0; i < startNum; i++){
                El.addLast(1);
            }
            Stopwatch sw = new Stopwatch();
            for(int k = 0; k < 10000; k++){
                El.getLast();
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
            Ns.addLast(El.size());
            opCounts.addLast(10000);
            startNum*=2;
        }
        printTimingTable(Ns,times,opCounts);
    }

}
