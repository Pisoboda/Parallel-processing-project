import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Solution_algorithms {

    private SimulationConfiguration CFG;
    public long Number_Of_Hits;
    public List<Double>PointOf_x;
    public List<Double>PointOf_y;


    public List<Double>getPointOf_x(){

    for (int i = 0; i < PointOf_x.size(); i++) {
      double x_axis = ThreadLocalRandom.current().nextDouble();
        PointOf_x.set(i, x_axis);
    }
     return PointOf_x;

}
public List<Double>getPointOf_y(){

    for (int i = 0; i < PointOf_y.size(); i++) {
     double y_axis =ThreadLocalRandom.current().nextDouble() ;
        PointOf_y.set(i, y_axis);
    }
    return PointOf_y;
}

    public long solved_solution(long TotalPoints){


        for(int i = 0; i <=TotalPoints; i++){
            double x_axis= PointOf_x.get(i);
            double y_axis= PointOf_y.get(i);
            if(((x_axis*x_axis)+(y_axis*y_axis))<=1.0){
                Number_Of_Hits++;
            }

        }
        return Number_Of_Hits;
    }
}
