import java.util.concurrent.ThreadLocalRandom;
public class SequentialPiestimator implements PiEstimator  {

    @Override
    public double estimatePi(simulationconfig cfg) {
        long inside=0;
        long total=cfg.getTotalPoints();
        for (long i=1;i< cfg.getTotalPoints(); i++ )
        {
            double x=ThreadLocalRandom.current().nextDouble();
            double y=ThreadLocalRandom.current().nextDouble();
            if (x*x+y*y<=1.0)
            {
                inside++;
            }
        }
        return (4 * inside) / total;
    }
}
