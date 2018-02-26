
package dr.evomodel.continuous;

public class StochasticDifferentialEquationModel extends MultivariateDiffusionModel {

    private static double maxTimeIncrement = 1E-3;
    private static int defaultNumberSteps = 100;

    protected double calculateLogDensity(double[] start, double[] stop, double time) {

        int numSteps = defaultNumberSteps;
        if (time / numSteps > maxTimeIncrement) {
            numSteps = (int) (time / maxTimeIncrement);
        }

        return 0;
    }

    private double[] getDriftVector(double[] X) {
        return null;
    }

    private double[] getVarianceMatrix(double[] X) {
        return null;
    }

}
