
package dr.geo.operators;

import dr.geo.Polygon2D;
import dr.inference.model.Parameter;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.MathUtils;

import java.awt.geom.Point2D;
import java.util.List;

public class UniformGeoSpatialOperator extends SimpleMCMCOperator {

    public UniformGeoSpatialOperator(Parameter parameter, double weight, List<Polygon2D> polygonList) {
        this.parameter = parameter;
        this.polygonList = polygonList;
        setWeight(weight);
    }

    public final double doOperation() {

        double[] mass = null;
        int whichRegion = 0;
        if (polygonList.size() > 1) {
            mass = new double[polygonList.size()];
            for (int i = 0; i < polygonList.size(); ++i) {
                mass[i] = polygonList.get(i).calculateArea();
            }
            whichRegion = MathUtils.randomChoicePDF(mass);
        }

        totalOps++;
        if (whichRegion == 0) {
            currentSum++; // For debugging prior probability of first region
        }

        Polygon2D polygon = polygonList.get(whichRegion);

        double[][] minMax = polygon.getXYMinMax();

        int attempts = 0;
        Point2D pt;
        do {
            pt = new Point2D.Double(
                    (MathUtils.nextDouble() * (minMax[0][1] - minMax[0][0])) + minMax[0][0],
                    (MathUtils.nextDouble() * (minMax[1][1] - minMax[1][0])) + minMax[1][0]);
            attempts++;

        } while (!polygon.containsPoint2D(pt));

        if (DEBUG) {
            System.err.println("region: " + whichRegion + " attempts: " + attempts + " " + mass[0] + " " + mass[1] + "     " + (
                    (double) currentSum / (double) totalOps
            ));
        }

        parameter.setParameterValue(0, pt.getX());
        parameter.setParameterValue(1, pt.getY());

        return 0.0;
    }

    //MCMCOperator INTERFACE
    public final String getOperatorName() {
        return "uniformGeoSpatial(" + parameter.getParameterName() + ")";
    }

    public String getPerformanceSuggestion() {
        return "";
    }

    public String toString() {
        return UniformGeoSpatialOperatorParser.UNIFORM_OPERATOR + "(" + parameter.getParameterName() + ")";
    }

    //PRIVATE STUFF
    private final Parameter parameter;
    private final List<Polygon2D> polygonList;

    private long totalOps = 0;
    private long currentSum = 0;

    private static final boolean DEBUG = false;
}
