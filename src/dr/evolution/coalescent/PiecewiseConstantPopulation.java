package dr.evolution.coalescent;
import java.util.ArrayList;
import java.util.Collections;
public class PiecewiseConstantPopulation extends DemographicFunction.Abstract {
    ArrayList<Double> cif = null;
    ArrayList<Double> endTime = null;
    final boolean cacheCumulativeIntensities = true;
    public PiecewiseConstantPopulation(Type units) {
        super(units);
    }
    public PiecewiseConstantPopulation(double[] intervals, double[] thetas, Type units) {
        super(units);
        setIntervals(intervals, thetas);
    }
    public void setIntervals(final double[] intervals, final double[] thetas) {
        if (thetas == null || intervals == null) {
            throw new IllegalArgumentException();
        }
        if (thetas.length != intervals.length + 1) {
            throw new IllegalArgumentException();
        }
        this.intervals = intervals;
        this.thetas = thetas;
        if (cacheCumulativeIntensities) {
            cif = new ArrayList<Double>(intervals.length);
            endTime = new ArrayList<Double>(intervals.length);
            cif.add(getIntensity(0));
            endTime.add(intervals[0]);
            for (int i = 1; i < intervals.length; i++) {
                cif.add(getIntensity(i) + cif.get(i - 1));
                endTime.add(intervals[i] + endTime.get(i - 1));
            }
        }
    }
    // **************************************************************
    // Implementation of abstract methods
    // **************************************************************
    public double getDemographic(double t) {
        int epoch = 0;
        double t1 = t;
        while (t1 > getEpochDuration(epoch)) {
            t1 -= getEpochDuration(epoch);
            epoch += 1;
        }
        return getDemographic(epoch, t1);
    }
    public double getIntensity(double t) {
        if (cif != null) {
            int epoch = Collections.binarySearch(endTime, t);
            if (epoch < 0) {
                epoch = -epoch - 1;
                if (epoch > 0) {
                    return cif.get(epoch - 1) + getIntensity(epoch, t - endTime.get(epoch - 1));
                } else {
                    assert epoch == 0;
                    return getIntensity(0, t);
                }
            } else {
                return cif.get(epoch);
            }
        } else {
            double intensity = 0.0;
            int epoch = 0;
            double t1 = t;
            while (t1 > getEpochDuration(epoch)) {
                t1 -= getEpochDuration(epoch);
                intensity += getIntensity(epoch);
                epoch += 1;
            }
            // probably same bug as in EmpiricalPiecewiseConstant when t1 goes negative
            // add last fraction of intensity
            intensity += getIntensity(epoch, t1);
            return intensity;
        }
    }
    public double getInverseIntensity(double x) {
        throw new RuntimeException("Not implemented!");
    }
    public double getUpperBound(int i) {
        return 1e9;
    }
    public double getLowerBound(int i) {
        return Double.MIN_VALUE;
    }
    public int getNumArguments() {
        return thetas.length;
    }
    public String getArgumentName(int i) {
        return "theta" + i;
    }
    public double getArgument(int i) {
        return thetas[i];
    }
    public void setArgument(int i, double value) {
        thetas[i] = value;
    }
    public DemographicFunction getCopy() {
        PiecewiseConstantPopulation df = new PiecewiseConstantPopulation(new double[intervals.length], new double[thetas.length], getUnits());
        System.arraycopy(intervals, 0, df.intervals, 0, intervals.length);
        System.arraycopy(thetas, 0, df.thetas, 0, thetas.length);
        return df;
    }
    protected double getDemographic(int epoch, double t) {
        return getEpochDemographic(epoch);
    }
    protected double getIntensity(int epoch) {
        return getEpochDuration(epoch) / getEpochDemographic(epoch);
    }
    protected double getIntensity(final int epoch, final double relativeTime) {
        assert 0 <= relativeTime && relativeTime <= getEpochDuration(epoch);
        return relativeTime / getEpochDemographic(epoch);
    }
    public double getEpochDuration(int epoch) {
        if (epoch == intervals.length) {
            return Double.POSITIVE_INFINITY;
        } else return intervals[epoch];
    }
    public void setEpochDuration(int epoch, double duration) {
        if (epoch < 0 || epoch >= intervals.length) {
            throw new IllegalArgumentException("epoch must be between 0 and " + (intervals.length - 1));
        }
        if (duration < 0.0) {
            throw new IllegalArgumentException("duration must be positive.");
        }
        intervals[epoch] = duration;
    }
    public final double getEpochDemographic(int epoch) {
        if (epoch < 0 || epoch >= thetas.length) {
            throw new IllegalArgumentException();
        }
        return thetas[epoch];
    }
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(thetas[0]);
        for (int i = 1; i < thetas.length; i++) {
            buffer.append("\t").append(thetas[i]);
        }
        return buffer.toString();
    }
    double[] intervals;
    double[] thetas;
}
