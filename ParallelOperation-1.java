import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;


public class ParallelOperation implements PiEstimator{
private Solution_algorithms solution;
private SimulationConfiguration simConfig;
private long TotalPoint;
private int Numoftask;
public ParallelOperation(long point,int numoftask ) {
    this.TotalPoint = point;
    this.Numoftask = numoftask;
}

    public double estimate() throws ExecutionException, InterruptedException
    {
    long Numpoints = TotalPoint;
    int NumpointTask = Numoftask;
    int maxThread=Runtime.getRuntime().availableProcessors();
    int NumThread=1+(int)(Math.random()*maxThread);
    SimulationConfiguration simConfig=new SimulationConfiguration(Numpoints,NumThread,NumpointTask);


    ExecutorService executor = Executors.newFixedThreadPool(NumThread);
    List<Future<Integer>> futures = new ArrayList<>();

    long PointPerTask = Numpoints/NumpointTask;

    for(int i=0; i<NumpointTask; i++)
    {
        long points=PointPerTask;
        if (i==NumpointTask-1){
            points+=Numpoints%NumpointTask;
        }
            PiTask task= new PiTask(points,solution);
            futures.add(executor.submit(task));

    }
    int Totalhits=0;
    for(Future<Integer> fut: futures){
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


    @Override
    public double estimatePi(SimulationConfiguration cfg) {
        return 0;
    }
}
