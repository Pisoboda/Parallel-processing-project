import java.util.*;

public class PiExpremintaRunner {


    public void runSequentialOnly(long TotalPoints ) {
        List<Double> piList = new ArrayList<>();
        List<Long> timeList = new ArrayList<>();
        long N=TotalPoints;

            SimulationConfiguration config = new SimulationConfiguration(N, 1, 1);
            PiEstimator seq = new SequintialOperation(N);

            long start = System.nanoTime();
            double pi = seq.estimatePi(config);
            long end = System.nanoTime();
            long elapsed = end - start;

            double absError = Math.abs(Math.PI - pi);
            System.out.println("Sequential π = " + pi + " | time = " + elapsed / 1e6 + " ms | Absolute Error = " + absError);

            piList.add(pi);
            timeList.add(elapsed);


        double avgPi = piList.stream().mapToDouble(d -> d).average().orElse(0.0);
        double avgTime = timeList.stream().mapToLong(l -> l).average().orElse(0.0)/1e6;
        System.out.println("\nSequential Average π = " + avgPi + " | Average Time = " + avgTime + " ms");
    }




    public void runParallelOnly(long TotalPoints ,int Numtasks) {
        List<Double> piList = new ArrayList<>();
        List<Long> timeList = new ArrayList<>();



            long N = TotalPoints;
            int numTasks = Numtasks;


            PiEstimator par = new ParallelOperation(N,numTasks);
            SimulationConfiguration config = new SimulationConfiguration(N, numTasks, Numtasks);


            long start = System.nanoTime();
            double pi = par.estimatePi(config);
            long end = System.nanoTime();
            long elapsed = end - start;

            double absError = Math.abs(Math.PI - pi);
            System.out.println("Parallel π = " + pi + " | time = " + elapsed / 1e6 + " ms | Absolute Error = " + absError);

            piList.add(pi);
            timeList.add(elapsed);


        double avgPi = piList.stream().mapToDouble(d -> d).average().orElse(0.0);
        double avgTime = timeList.stream().mapToLong(l -> l).average().orElse(0.0)/1e6;
        System.out.println("\nParallel Average π = " + avgPi + " | Average Time = " + avgTime + " ms");
    }
}
