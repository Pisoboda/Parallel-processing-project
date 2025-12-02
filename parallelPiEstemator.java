import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;
public class parallelPiEstemator implements PiEstimator {

    @Override
    public double estimatePi(simulationconfig cfg) {

    ExecutorService exc=Executors.newFixedThreadPool(cfg.getNumThreads());
        long totalPoints =cfg.getTotalPoints();
        int numTasks = cfg.getNumTasks();


        long base = totalPoints / numTasks;
        long remainder = totalPoints % numTasks;

    List<Future<Long>> fut=new ArrayList<>();

    for (int i=0;i<numTasks;i++){
        long Tspo= base + (i < remainder ? 1 : 0);
        fut.add(exc.submit(new PiTask(Tspo)));
    }
       long hits=0;
        for (Future<Long> f:fut)
    {
        try
        {
            hits+=f.get();
        }
        catch (Exception e)
        {
e.printStackTrace();
        }
    }
        exc.shutdown();
      return   (hits * 4) /cfg.getTotalPoints();
    }
}
