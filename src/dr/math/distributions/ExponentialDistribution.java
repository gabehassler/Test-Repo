
package dr.math.distributions;

import dr.math.UnivariateFunction;

public class ExponentialDistribution implements Distribution {
    //
    // Public stuff
    //

    public ExponentialDistribution(double lambda) {
        this.lambda = lambda;
    }

    public double pdf(double x) {
        return pdf(x, lambda);
    }

    public double logPdf(double x) {
        return logPdf(x, lambda);
    }

    public double cdf(double x) {
        return cdf(x, lambda);
    }

    public double quantile(double y) {
        return quantile(y, lambda);
    }

    public double mean() {
        return mean(lambda);
    }

    public double variance() {
        return variance(lambda);
    }

    public UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }

    private UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }

        public final double getLowerBound() {
            return 0.0;
        }

        public final double getUpperBound() {
            return Double.POSITIVE_INFINITY;
        }
    };

    public static double pdf(double x, double lambda) {
    	if (x < 0) return 0;
    	
        return lambda * Math.exp(-lambda * x);
    }

    public static double logPdf(double x, double lambda) {
    	if (x < 0) return Double.NEGATIVE_INFINITY;
    	
        return Math.log(lambda) - (lambda * x);
    }

    public static double cdf(double x, double lambda) {
        return 1.0 - Math.exp(-lambda * x);
    }


    public static double quantile(double y, double lambda) {
        return -(1.0 / lambda) * Math.log(1.0 - y);
    }

    public static double mean(double lambda) {
        return 1.0 / (lambda);
    }

    public static double variance(double lambda) {
        return 1.0 / (lambda * lambda);
    }

    // the rate parameter of this exponential distribution (1/mean)
    double lambda;
}
