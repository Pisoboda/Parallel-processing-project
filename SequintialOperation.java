import java.util.concurrent.ThreadLocalRandom;

public class SequintialOperation implements PiEstimator {

    @Override
    public double estimatePi(SimulationConfiguration config) {
        long inside = 0;
        long total = config.getTotalPoints();

        for(long i = 0; i < total; i++) {
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            if(x*x + y*y <= 1.0) inside++;
        }

        return 4.0 * inside / total;
    }
}
