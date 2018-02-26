package dr.app.tracer.traces;

import dr.math.distributions.KernelDensityEstimatorDistribution;

public class TraceOptions {

    public enum Type {
        REAL,
        INTEGER,
        CATEGORICAL
    }

    private int histogramBinCount = 50;
    private double lowerBound = Double.NEGATIVE_INFINITY;
    private double upperBound = Double.POSITIVE_INFINITY;

    private KernelDensityEstimatorDistribution.Type kdeType = KernelDensityEstimatorDistribution.Type.GAUSSIAN;
}
