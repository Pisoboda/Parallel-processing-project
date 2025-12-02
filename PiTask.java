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
        for (long i = 1; i < points; i++) {
            double x = ThreadLocalRandom.current().nextDouble();
            double y = ThreadLocalRandom.current().nextDouble();
            if (x * x + y * y <= 1.0) {
                inside++;
            }
        }
        return inside;
    }
}
