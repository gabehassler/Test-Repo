package dr.evomodel.continuous;

import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.distributions.BifractionalDiffusionDensity;

public class BifractionalDiffusionModel extends MultivariateDiffusionModel {

    public BifractionalDiffusionModel(Parameter alpha, Parameter beta) {
        super();
        this.alpha = alpha;
        this.beta = beta;
        addVariable(alpha);
        addVariable(beta);
        densityKnown = false;
    }

    protected double calculateLogDensity(double[] start, double[] stop, double time) {
        // Compute finite-time transition probability

        // Equation (2) from Brockman, Hufnagel and Geisel (2006)
//        final double ratio = alpha.getParameterValue(0) / beta.getParameterValue(0);
//        final double r = distanceEuclidean(start, stop);
//        final double scaledTime = Math.pow(time,ratio);
//        return -ratio * Math.log(time) + logUniversalScalingFunction(r / scaledTime);
         double a = alpha.getParameterValue(0);
         double b = beta.getParameterValue(0);
         double r = distanceEuclidean(start, stop) * scalarDistance;


//        a = 2.0
//b = 1.0
//r = 0.3597864505508788
//t = 3.290985204423155E-5
//        a = 2.0;
//b = 1.0;
//r = 0.3597864505508788;
//time = 3.290985204423155E-5;
//        time = 1;
//        System.err.println("a = " + a);
//        System.err.println("b = " + b);
//        System.err.println("r = " + r);
//        System.err.println("t = " + time);

        if (!densityKnown) {
            setupDensity();
            densityKnown = true;
        }

//        return BifractionalDiffusionDensity.logPdf(r, time, a, b);

                double pdf = bifractionalDensity.pdf(r, time * scalarTime);
         double logPdf = Math.log(pdf);
        if (Double.isNaN(logPdf)) {
            System.err.println("a = " + a);
        System.err.println("b = " + b);
        System.err.println("r = " + r);
        System.err.println("t = " + time * scalarTime);
           System.err.println("pdf    = " + pdf);
            System.err.println("logPdf = " + logPdf);
        }
        return logPdf;
    }

    private void setupDensity() {
        final double a = alpha.getParameterValue(0);
        final double b = beta.getParameterValue(0);
        bifractionalDensity = new BifractionalDiffusionDensity(a, b);
    }

     protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
         super.handleVariableChangedEvent(variable, index, type);
         if (variable == alpha || variable == beta) {
             densityKnown = false;
         }
     }

    private double distanceEuclidean(double[] start, double[] stop) {
        final int dim = start.length;
        double total = 0;
        for(int i=0; i<dim; i++) {
            final double dX = stop[i] - start[i];
            total += dX*dX;
        }
        return Math.sqrt(total);
    }

//    private double logUniversalScalingFunction(double x) {
//        return x - x;
//    }

    protected void calculatePrecisionInfo() {
        // Precompute normalizing constants if necessary
    }

    protected void restoreState() {
        super.restoreState();
        densityKnown = false;
    }

    private Parameter alpha;
    private Parameter beta;
    private BifractionalDiffusionDensity bifractionalDensity;
    private boolean densityKnown;
    private double scalarDistance = 10E-3;
    private double scalarTime = 10E+3;
}
