import java.util.concurrent.*;

public class PiTask implements Callable {

    private long numpoints;
    private Solution_algorithms solution;

    public PiTask(long numpoints, Solution_algorithms solution) {
        this.numpoints = numpoints;
        this.solution = solution;
    }

    public Long call()
    {
        return solution.solved_solution(numpoints);
    }

}
