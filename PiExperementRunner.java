import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

class piExperementRunner  {

   ;
    private final long minPoints, maxPoints;
    private final int minThreads, maxThreads;
    private final int numTasks, numRuns;


    public piExperementRunner(long minPoints, long maxPoints, int minThreads, int maxThreads, int numTasks, int numRuns) {
        this.minPoints = minPoints;
        this.maxPoints = maxPoints;
        this.minThreads = minThreads;
        this.maxThreads = maxThreads;
        this.numTasks = numTasks;
        this.numRuns = numRuns;
    }

    public void runSequentialOnly() {
        List<Double> piList = new ArrayList<>();
        List<Long> timeList = new ArrayList<>();

        for(int run = 1; run <= numRuns; run++) {
            long N = ThreadLocalRandom.current().nextLong(minPoints, maxPoints + 1);
            System.out.println("\n=== Sequential Run " + run + " ===");
            System.out.println("N = " + N);

            simulationconfig config = new simulationconfig(N,1,1);
            PiEstimator seq = new SequentialPiestimator ();

            long start = System.nanoTime();
            double pi = seq.estimatePi(config);
            long end = System.nanoTime();
            long elapsed = end - start;

            double absError = Math.abs(Math.PI - pi);
            System.out.println("Sequential π = " + pi + " | time = " + elapsed / 1e6 + " ms | Absolute Error = " + absError);

            piList.add(pi);
            timeList.add(elapsed);
        }

        double avgPi = piList.stream().mapToDouble(d -> d).average().orElse(0.0);
        double avgTime = timeList.stream().mapToLong(l -> l).average().orElse(0.0)/1e6;
        System.out.println("\nSequential Average π = " + avgPi + " | Average Time = " + avgTime + " ms");
    }

    public void runParallelOnly() {
        List<Double> piList = new ArrayList<>();
        List<Long> timeList = new ArrayList<>();

        for(int run = 1; run <= numRuns; run++) {
            long N = ThreadLocalRandom.current().nextLong(minPoints, maxPoints + 1);
            int randomThreads = ThreadLocalRandom.current().nextInt(minThreads, maxThreads + 1);

            System.out.println("\n=== Parallel Run " + run + " ===");
            System.out.println("N = " + N + " | Threads = " + randomThreads);

            simulationconfig config = new   simulationconfig(N, numTasks, randomThreads);
            PiEstimator seq = new SequentialPiestimator ();
            long start = System.nanoTime();
            double pi = seq.estimatePi(config);
            long end = System.nanoTime();
            long elapsed = end - start;

            double absError = Math.abs(Math.PI - pi);
            System.out.println("Parallel π = " + pi + " | time = " + elapsed / 1e6 + " ms | Absolute Error = " + absError);

            piList.add(pi);
            timeList.add(elapsed);
        }

        double avgPi = piList.stream().mapToDouble(d -> d).average().orElse(0.0);
        double avgTime = timeList.stream().mapToLong(l -> l).average().orElse(0.0)/1e6;
        System.out.println("\nParallel Average π = " + avgPi + " | Average Time = " + avgTime + " ms");
    }
}