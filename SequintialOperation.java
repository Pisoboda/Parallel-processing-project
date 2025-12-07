import java.util.Random;

public class SequintialOperation implements PiEstimator {
    long numperofPoint ;


    public SequintialOperation(long numperofPoint)
    {
        this.numperofPoint = numperofPoint;
    }

    public double estimatesolution() {
        Solution_algorithms sol=new Solution_algorithms();
        long hits=  sol.solved_solution(numperofPoint);
        return (double) hits*4/numperofPoint;

    }

    @Override
    public double estimatePi(SimulationConfiguration cfg) {
        return 0;
    }
}
