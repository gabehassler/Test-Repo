package dr.math.distributions;
import dr.math.UnivariateFunction;
public class OffsetPositiveDistribution implements Distribution {
    public OffsetPositiveDistribution(Distribution distribution, double offset) {
        if (offset < 0.0) throw new IllegalArgumentException();
        this.offset = offset;
        this.distribution = distribution;
    }
    public final double pdf(double x) {
        if (offset < 0) return 0.0;
        return distribution.pdf(x - offset);
    }
    public final double logPdf(double x) {
        if (offset < 0) return Math.log(0.0);
        return distribution.logPdf(x - offset);
    }
    public final double cdf(double x) {
        if (offset < 0) return 0.0;
        return distribution.cdf(x - offset);
    }
    public final double quantile(double y) {
        return distribution.quantile(y) + offset;
    }
    public final double mean() {
        return distribution.mean() + offset;
    }
    public final double variance() {
        throw new UnsupportedOperationException();
    }
    public final UnivariateFunction getProbabilityDensityFunction() {
        return pdfFunction;
    }
    private UnivariateFunction pdfFunction = new UnivariateFunction() {
        public final double evaluate(double x) {
            return pdf(x);
        }
        public final double getLowerBound() {
            return offset + distribution.getProbabilityDensityFunction().getLowerBound();
        }
        public final double getUpperBound() {
            return offset + distribution.getProbabilityDensityFunction().getUpperBound();
        }
    };
    // the location parameter of the start of the positive distribution
    private double offset = 0.0;
    // the distribution to offset
    private Distribution distribution;
}
