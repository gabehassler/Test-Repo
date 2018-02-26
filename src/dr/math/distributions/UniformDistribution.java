
package dr.math.distributions;

import dr.math.UnivariateFunction;
import dr.util.DataTable;

public class UniformDistribution implements Distribution {
    //
    // Public stuff
    //


    public UniformDistribution(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public double pdf(double x) {
        return pdf(x, lower, upper);
    }

    public double logPdf(double x) {
        return logPdf(x, lower, upper);
    }

    public double cdf(double x) {
        return cdf(x, lower, upper);
    }

    public double quantile(double y) {
        return quantile(y, lower, upper);
    }

    public double mean() {
        return mean(lower, upper);
    }

    public double variance() {
        return variance(lower, upper);
    }

    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }

    private final UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }

        public final double getLowerBound() {
            return lower;
        }

        public final double getUpperBound() {
            return upper;
        }
    };


    public static double pdf(double x, double lower, double upper) {
        return (x >= lower && x <= upper ? 1.0 / (upper - lower) : 0.0);
    }

    public static double logPdf(double x, double lower, double upper) {
        if (x < lower || x > upper) return Double.NEGATIVE_INFINITY;

        // improve numerical stability:
        return - Math.log(upper - lower);
//        return Math.log(pdf(x, lower, upper));
    }

    public static double cdf(double x, double lower, double upper) {
        if (x < lower) return 0.0;
        if (x > upper) return 1.0;
        return (x - lower) / (upper - lower);
    }


    public static double quantile(double y, double lower, double upper) {
        if (!(y >= 0.0 && y <= 1.0)) throw new IllegalArgumentException("y must in range [0,1]");
        return (y * (upper - lower)) + lower;
    }

    public static double mean(double lower, double upper) {
        return (upper + lower) / 2;
    }

    public static double variance(double lower, double upper) {
        return (upper - lower) * (upper - lower) / 12;
    }

    // Private

    private final double upper;
    private final double lower;
}
