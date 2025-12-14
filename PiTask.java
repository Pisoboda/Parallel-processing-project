//import java.util.concurrent.*;
//
//public class PiTask implements Callable {
//
//    private long numpoints;
//    private Solution_algorithms solution;
//
//    public PiTask(long numpoints, Solution_algorithms solution) {
//        this.numpoints = numpoints;
//        this.solution = solution;
//    }
//
//    public Long call()
//    {
//        return solution.solved_solution(numpoints);
//    }
//
//}

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class PiTask implements Callable<Long> {
    private long points;

    public PiTask(long points) {
        this.points = points;
    }


    @Override
    public Long call() {

        long inside = 0;
        for (long i = 0; i < points; i++) {
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            if ((x * x + y * y <= 1.0) && (x * x + y * y >=0.0)) inside++;
        }
        return inside;
    }
}
