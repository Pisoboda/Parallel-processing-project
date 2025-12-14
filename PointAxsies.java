import java.util.ArrayList;
import java.util.List;

public class PointAxsies {
    private List<Double> PointOf_x;
    private List<Double>PointOf_y;
    public PointAxsies() {
        PointOf_x = new ArrayList<>();
        PointOf_y = new ArrayList<>();
    }
    public void setPointOf_x(double x, double y) {
       this.PointOf_x.add(x);
       this.PointOf_y.add(y);
    }


    public List<Double> getPointOf_x() {
        return this.PointOf_x;
    }
    public List<Double> getPointOf_y() {
        return this.PointOf_y;
    }

}
