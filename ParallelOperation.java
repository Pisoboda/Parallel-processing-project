import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;


public class ParallelOperation implements PiEstimator{
public SimulationConfiguration simConfig;

public ParallelOperation(SimulationConfiguration simConfig){
    this.simConfig = simConfig;
}

    @Override
    public double estimatePi(SimulationConfiguration cfg) {

        long Numpoints = simConfig.getTotalPoints();
        int NumpointTask = simConfig.getNumberTasks();
        int NumThread=simConfig.getNumThreads();


        ExecutorService executor = Executors.newFixedThreadPool(NumThread);
        List<Future<Long>> futures = new ArrayList<>();

        long PointPerTask = Numpoints/NumpointTask;

        for(int i=0; i<NumpointTask; i++)
        {
            long points=PointPerTask;
            if (i==NumpointTask-1){
                points+=Numpoints%NumpointTask;
            }
            PiTask task= new PiTask(points);
            futures.add(executor.submit(task));

        }
        int Totalhits=0;
        for(Future<Long> fut: futures){
            try {
                Totalhits+=fut.get();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        }

        executor.shutdown();

        return (double) (Totalhits*4)/Numpoints;
    }
}
