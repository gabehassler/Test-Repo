
package dr.math.distributions;

import dr.math.UnivariateFunction;

public abstract class KernelDensityEstimatorDistribution implements Distribution {

    public KernelDensityEstimatorDistribution(Double[] sample, Double lowerBound, Double upperBound, Double bandWidth) {

        this.sample = new double[sample.length];
        for (int i = 0; i < sample.length; i++) {
            this.sample[i] = sample[i];
        }
        this.N = sample.length;
        processBounds(lowerBound, upperBound);
        setBandWidth(bandWidth);
    }

    abstract protected double evaluateKernel(double x);

    abstract protected void processBounds(Double lowerBound, Double upperBound);

    abstract protected void setBandWidth(Double bandWidth);

    public double pdf(double x) {
        return evaluateKernel(x);
    }

    public double logPdf(double x) {
        return Math.log(pdf(x));
    }

    public double cdf(double x) {
        throw new RuntimeException("Not Implemented.");
    }

    public double quantile(double y) {
        throw new RuntimeException("Not Implemented.");
    }

    public double mean() {
        throw new RuntimeException("Not Implemented.");
    }

    public double variance() {
        throw new RuntimeException("Not Implemented.");
    }

    public UnivariateFunction getProbabilityDensityFunction() {
        throw new RuntimeException("Not Implemented.");
    }

    public double getBandWidth() {
        return bandWidth;
    }

    public enum Type {
        GAUSSIAN("Gaussian"),
        GAMMA("Gamma"),
        LOGTRANSFORMEDGAUSSIAN("LogTransformedGaussian"),
        BETA("Beta");

        private Type(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }

        public static Type parseFromString(String text) {
            for (Type format : Type.values()) {
                if (format.getText().compareToIgnoreCase(text) == 0)
                    return format;
            }
            return null;
        }

        private final String text;
    }

    protected int N;
    protected double lowerBound;
    protected double upperBound;
    protected double bandWidth;
    protected double[] sample;
}
