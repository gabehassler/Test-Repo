
package dr.inference.distribution;

import dr.inference.model.SplineBasis;

public class SplineInterpolatedLikelihood extends EmpiricalDistributionLikelihood {

    protected static double outsideLogDensity = Double.NEGATIVE_INFINITY; // Use for a proper posterior

    public SplineInterpolatedLikelihood(String fileName, int degree, boolean inverse, boolean byColumn) {
        super(fileName, inverse, byColumn);

         // Set-up spline basis, could be degree = 1 for linear interpolation
//        splineBasis = new SplineBasis(getId(),new Variable.D(values), new Variable.D(density), degree);

        // Something is wrong with the spline basis routines...  just do simple linear interpolation

    }

    @Override
    protected double logPDF(double x) {
//        return splineBasis.evaluate(x);

        final int len = values.length;

        if (x < values[0] || x > values[len - 1])
            return outsideLogDensity;

        double rtnValue = 0;

        for(int i=1; i<len; i++) {
            if (values[i] > x) { // first largest point
                final double diffValue = values[i] - values[i-1];
                final double diffDensity = density[i] - density[i-1];
                rtnValue = density[i] - (values[i]-x) / diffValue * diffDensity;
                break;
            }
        }

        rtnValue = Math.log(rtnValue);
        if (inverse)
            rtnValue *= -1;
  
        return rtnValue;
    }

    private SplineBasis splineBasis = null;
}
