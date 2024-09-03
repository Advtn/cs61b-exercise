package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        System.out.println("Timing table for addLast");
        AList<Integer> Ns=new AList<>();
        AList<Double> times=new AList<>();
        AList<Integer> opCounts=new AList<>();
        int startNum=1000;
        for(int j = 0 ; j < 15 ; j++){
            AList<Integer> El=new AList<>();
            Stopwatch sw = new Stopwatch();
            for(int i = 0; i < startNum; i++){
                El.addLast(1);
            }
            double timeInSeconds = sw.elapsedTime();
            times.addLast(timeInSeconds);
            Ns.addLast(El.size());
            opCounts.addLast(El.size());
            startNum*=2;
        }
        printTimingTable(Ns,times,opCounts);
    }
}
