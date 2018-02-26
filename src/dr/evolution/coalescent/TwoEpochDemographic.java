package dr.evolution.coalescent;
public class TwoEpochDemographic extends DemographicFunction.Abstract {
    private DemographicFunction epoch1;
    private DemographicFunction epoch2;
    private double transitionTime;
	public TwoEpochDemographic(DemographicFunction epoch1, DemographicFunction epoch2, Type units) {
		super(units);
        this.epoch1 = epoch1;
        this.epoch2 = epoch2;
	}
	public final double getTransitionTime() { return transitionTime; }
    public final void setTransitionTime(double t) {
        if (t < 0.0 || t > Double.MAX_VALUE) {
            throw new IllegalArgumentException("transition time out of bounds.");
        }
        transitionTime = t;
    }
    public final DemographicFunction getFirstEpochDemography() {
        return epoch1;
    }
    public final DemographicFunction getSecondEpochDemography() {
        return epoch2;
    }
	// Implementation of abstract methods
	public final double getDemographic(double t) {
		if (t < transitionTime) {
            return epoch1.getDemographic(t);
        } else {
            return epoch2.getDemographic(t-transitionTime);
        }
	}
	public final double getIntensity(double t) {
		if (t < transitionTime) {
            return epoch1.getIntensity(t);
        } else {
            return epoch1.getIntensity(transitionTime) + epoch2.getIntensity(t-transitionTime);
        }
	}
    public final double getIntegral(double start, double finish) {
		if (start < transitionTime) {
            if (finish < transitionTime) {
                return epoch1.getIntegral(start, finish);
            } else {
                return epoch1.getIntegral(start, transitionTime) + epoch2.getIntegral(0, finish-transitionTime);
            }
        } else {
            return epoch2.getIntegral(start-transitionTime, finish-transitionTime);
        }
	}
	public final double getInverseIntensity(double x) {
        double time = epoch1.getInverseIntensity(x);
        if (time < transitionTime) {
            return time;
        }
        x -= epoch1.getIntensity(transitionTime);
        return transitionTime + epoch2.getInverseIntensity(x);
  	}
	public int getNumArguments() {
		return epoch1.getNumArguments() + epoch2.getNumArguments() + 1;
	}
	public final String getArgumentName(int n) {
		if (n < epoch1.getNumArguments()) {
            return epoch1.getArgumentName(n);
        }
        n -= epoch1.getNumArguments();
        if (n < epoch2.getNumArguments()) {
            return epoch2.getArgumentName(n);
        }
        n -= epoch2.getNumArguments();
        if (n == 0) return "transitionTime";
        throw new IllegalArgumentException();
	}
	public final double getArgument(int n) {
        if (n < epoch1.getNumArguments()) {
            return epoch1.getArgument(n);
        }
        n -= epoch1.getNumArguments();
        if (n < epoch2.getNumArguments()) {
            return epoch2.getArgument(n);
        }
        n -= epoch2.getNumArguments();
        if (n == 0) return transitionTime;
        throw new IllegalArgumentException();
	}
	public final void setArgument(int n, double value) {
        if (n < epoch1.getNumArguments()) {
            epoch1.setArgument(n, value);
        }
        n -= epoch1.getNumArguments();
        if (n < epoch2.getNumArguments()) {
            epoch2.setArgument(n, value);
        }
        n -= epoch2.getNumArguments();
        if (n == 0) transitionTime = value;
        throw new IllegalArgumentException();
	}
    public final double getLowerBound(int n) {
        if (n < epoch1.getNumArguments()) {
            return epoch1.getLowerBound(n);
        }
        n -= epoch1.getNumArguments();
        if (n < epoch2.getNumArguments()) {
            return epoch2.getLowerBound(n);
        }
        n -= epoch2.getNumArguments();
        if (n == 0) return 0.0;
        throw new IllegalArgumentException();
    }
    public final double getUpperBound(int n) {
        if (n < epoch1.getNumArguments()) {
            return epoch1.getUpperBound(n);
        }
        n -= epoch1.getNumArguments();
        if (n < epoch2.getNumArguments()) {
            return epoch2.getUpperBound(n);
        }
        n -= epoch2.getNumArguments();
        if (n == 0) return Double.MAX_VALUE;
        throw new IllegalArgumentException();
    }
    public final DemographicFunction getCopy() {
		TwoEpochDemographic df = new TwoEpochDemographic(epoch1, epoch2, getUnits());
		df.setTransitionTime(transitionTime);
		return df;
	}
}
