import java.util.Scanner;
import java.util.concurrent.*;
import java.util.concurrent.ExecutionException;

public class InteractiveConsole {
    public static void run() {
        // move previous console logic here
        long totalPointsDefault = 1_000_000L;
        int numTasksDefault = 4;

        Scanner sc = new Scanner(System.in);
        String choice = null;

        while (true) {
            System.out.println("Choose mode: (1) Sequential  (2) Parallel");
            if (!sc.hasNextLine()) break;
            String line = sc.nextLine().trim();
            if (line.equals("1") || line.equalsIgnoreCase("seq") || line.equalsIgnoreCase("sequential")) {
                choice = "seq";
                break;
            } else if (line.equals("2") || line.equalsIgnoreCase("par") || line.equalsIgnoreCase("parallel")) {
                choice = "par";
                break;
            } else {
                System.out.println("Invalid choice, please enter '1' or '2'.");
            }
        }

        long totalPoints = totalPointsDefault;
        int numTasks = numTasksDefault;

        System.out.println("Enter total points (press Enter for default " + totalPointsDefault + "): ");
        if (sc.hasNextLine()) {
            String s = sc.nextLine().trim();
            if (!s.isEmpty()) {
                try { totalPoints = Long.parseLong(s); } catch (NumberFormatException ignored) {}
            }
        }

        if ("par".equals(choice)) {
            System.out.println("Enter number of tasks (press Enter for default " + numTasksDefault + "): ");
            if (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                if (!s.isEmpty()) {
                    try { numTasks = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
                }
            }
        }

        if ("seq".equals(choice)) {
            System.out.println("Enter number of runs (default 1): ");
            int runs = 1;
            if (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                if (!s.isEmpty()) {
                    try { runs = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
                }
            }

            double sumPi = 0.0;
            long sumTimeNs = 0L;
            for (int i = 1; i <= runs; i++) {
                SequintialOperation seq = new SequintialOperation();
                SimulationConfiguration cfg = new SimulationConfiguration(totalPoints, 1, 1);
                long start = System.nanoTime();
                double pi = seq.estimatePi(cfg);
                long end = System.nanoTime();
                long elapsed = end - start;
                sumPi += pi;
                sumTimeNs += elapsed;
                System.out.println("Run " + i + ": π = " + pi + " | time = " + elapsed / 1e6 + " ms");
            }
            double avgPi = sumPi / runs;
            double avgTimeMs = (double) sumTimeNs / runs / 1e6;
            double absError = Math.abs(Math.PI - avgPi);
            double absErrorPct = absError / Math.PI * 100.0;
            System.out.println("\nSequential Average π = " + avgPi + " | Average Time = " + avgTimeMs + " ms | Absolute Error (%) = " + String.format("%.6f%%", absErrorPct));

        } else if ("par".equals(choice)) {
            int defaultThreads = Runtime.getRuntime().availableProcessors();
            System.out.println("Enter number of threads (press Enter for default " + defaultThreads + "): ");
            int numThreadsUser = defaultThreads;
            if (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                if (!s.isEmpty()) {
                    try { numThreadsUser = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
                }
            }
            if (numThreadsUser <= 0) numThreadsUser = 1;
            if (numThreadsUser > defaultThreads) {
                System.out.println("Requested threads too high, using max available: " + defaultThreads);
                numThreadsUser = defaultThreads;
            }

            System.out.println("Enter number of runs (default 1): ");
            int runs = 1;
            if (sc.hasNextLine()) {
                String s = sc.nextLine().trim();
                if (!s.isEmpty()) {
                    try { runs = Integer.parseInt(s); } catch (NumberFormatException ignored) {}
                }
            }

            double sumPi = 0.0;
            long sumTimeNs = 0L;
            for (int i = 1; i <= runs; i++) {
                // Prepare executor and monitoring structures
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreadsUser);
                java.util.Set<String> runningThreadNames = ConcurrentHashMap.newKeySet();

                // split points into tasks similar to ParallelOperation
                long Numpoints = totalPoints;
                int NumpointTask = numTasks;
                long PointPerTask = Numpoints / NumpointTask;

                java.util.List<Future<Long>> futures = new java.util.ArrayList<>();

                long start = System.nanoTime();
                for (int t = 0; t < NumpointTask; t++) {
                    long points = PointPerTask;
                    if (t == NumpointTask - 1) points += Numpoints % NumpointTask;
                    PiTask baseTask = new PiTask(points);
                    java.util.concurrent.Callable<Long> wrapper = () -> {
                        String name = Thread.currentThread().getName();
                        runningThreadNames.add(name);
                        try {
                            return baseTask.call();
                        } finally {
                            runningThreadNames.remove(name);
                        }
                    };
                    futures.add(executor.submit(wrapper));
                }

                // monitor progress
                int totalTasks = futures.size();
                int completed = 0;
                while (true) {
                    completed = 0;
                    for (Future<Long> f : futures) if (f.isDone()) completed++;
                    System.out.println("Progress: completed " + completed + "/" + totalTasks + " | Active threads = " + executor.getActiveCount() + " | Running threads = " + runningThreadNames);
                    if (completed >= totalTasks) break;
                    try { Thread.sleep(250); } catch (InterruptedException ignored) {}
                }

                long totalInside = 0L;
                for (Future<Long> f : futures) {
                    try { totalInside += f.get(); } catch (InterruptedException | ExecutionException ex) { ex.printStackTrace(); }
                }

                executor.shutdown();
                try { executor.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException ignored) {}

                long end = System.nanoTime();
                long elapsed = end - start;
                double pi = (double) (totalInside * 4) / Numpoints;
                sumPi += pi;
                sumTimeNs += elapsed;
                System.out.println("Run " + i + ": π = " + pi + " | time = " + elapsed / 1e6 + " ms");
            }
            double avgPi = sumPi / runs;
            double avgTimeMs = (double) sumTimeNs / runs / 1e6;
            double absError = Math.abs(Math.PI - avgPi);
            double absErrorPct = absError / Math.PI * 100.0;
            System.out.println("\nParallel Average π = " + avgPi + " | Average Time = " + avgTimeMs + " ms | Absolute Error (%) = " + String.format("%.6f%%", absErrorPct));

        } else {
            System.out.println("No valid choice provided. Exiting.");
        }
    }
}
