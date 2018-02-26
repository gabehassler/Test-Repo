
package dr.evolution.coalescent;

public class ExponentialExponential extends ExponentialGrowth {

	public ExponentialExponential(Type units) {
	
		super(units);
	}
	
	public double getTransitionTime() { return transitionTime; }
	public void setTransitionTime(double transitionTime) { this.transitionTime = transitionTime; }

    public final double getAncestralGrowthRate() { return r1; }

    public void setAncestralGrowthRate(double r1) { this.r1 = r1; }


    // Implementation of abstract methods

	public double getDemographic(double t) {
		
		double N0 = getN0();
		double r = getGrowthRate();
        double r1 = getAncestralGrowthRate();
        double changeTime = getTransitionTime();
		
		//return nOne + ((nZero - nOne) * Math.exp(-r*t));
	
        if(t < changeTime) {
            return N0*Math.exp(-r * t);
        }

        double N1 = N0 * Math.exp(-r * changeTime);

		return N1 * Math.exp(-r1* (t - changeTime));
	}

    public double getIntegral(double start, double finish)
    {
        return getNumericalIntegral(start, finish);
    }

	public double getIntensity(double t) {

        throw new RuntimeException("Not implemented!");
  	}

	public double getInverseIntensity(double x) {
        throw new RuntimeException("Not implemented!");
	}
	
	public int getNumArguments() {
		return 4;
	}
	
	public String getArgumentName(int n) {
		switch (n) {
			case 0: return "N0";
			case 1: return "r0";
            case 2: return "r1";
			case 3: return "transitionTime";
		}
		throw new IllegalArgumentException("Argument " + n + " does not exist");
	}
	
	public double getArgument(int n) {
		switch (n) {
			case 0: return getN0();
			case 1: return getGrowthRate();
            case 2: return getAncestralGrowthRate();
			case 3: return getTransitionTime();
		}
		throw new IllegalArgumentException("Argument " + n + " does not exist");
	}
	
	public void setArgument(int n, double value) {
		switch (n) {
			case 0: setN0(value); break;
			case 1: setGrowthRate(value); break;
            case 2: setAncestralGrowthRate(value); break;
			case 3: setTransitionTime(value); break;
			default: throw new IllegalArgumentException("Argument " + n + " does not exist");

		}
	}

	public double getLowerBound(int n) {
        switch (n) {
            case 0: return 0;
            case 1: return Double.NEGATIVE_INFINITY;
            case 2: return Double.NEGATIVE_INFINITY;
            case 3: return Double.NEGATIVE_INFINITY;
            default: throw new IllegalArgumentException("Argument " + n + " does not exist");
        }
	}
	
	public double getUpperBound(int n) {
		return Double.POSITIVE_INFINITY;
	}

	//
	// private stuff
	//

    private double transitionTime;
    private double r1;
}
