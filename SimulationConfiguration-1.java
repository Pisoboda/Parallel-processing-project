public class SimulationConfiguration {
    public long TotalPoints;
    public int NumberTasks;
    public int numThreads;

    public SimulationConfiguration(long totalPoints, int numberTasks, int numThreads) {


        if(totalPoints <= 0) throw new IllegalArgumentException("totalPoints must be > 0");
        if(numberTasks <= 0) throw new IllegalArgumentException("numTasks must be > 0");
        if(numThreads <= 0) throw new IllegalArgumentException("numThreads must be > 0");


        this.TotalPoints = totalPoints;
        this.NumberTasks = numberTasks;
        this.numThreads = numThreads;
    }

    public long getTotalPoints() {
        return TotalPoints;
    }

    public int getNumberTasks() {
        return NumberTasks;
    }

    public int getNumThreads() {
        return numThreads;
    }


}
