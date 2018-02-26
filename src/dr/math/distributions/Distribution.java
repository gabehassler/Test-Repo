
package dr.math.distributions;

import dr.math.UnivariateFunction;

public interface Distribution {
    public double pdf(double x);

    public double logPdf(double x);

    public double cdf(double x);

    public double quantile(double y);

    public double mean();

    public double variance();

    public UnivariateFunction getProbabilityDensityFunction();

}
