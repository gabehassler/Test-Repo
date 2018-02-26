
package dr.evolution.coalescent;

public class ConstExponential extends ExponentialGrowth {

	public ConstExponential(Type units) {
	
		super(units);
	}
	
	public double getN1() { return N1; }
	public void setN1(double N1) { this.N1 = N1; }
			
	public void setProportion(double p) { this.N1 = getN0() * p; }
	
	// Implementation of abstract methods

	public double getDemographic(double t) {
		
		double N0 = getN0();
		double N1 = getN1();
		double r = getGrowthRate();
		
		//return nOne + ((nZero - nOne) * Math.exp(-r*t));
	
		double time = Math.log(N0/N1)/r;

        if (t < time) return N0 * Math.exp(-r*t);
		
		return N1;
	}

	public double getIntensity(double t) {

        double r = getGrowthRate();
        double time = Math.log(getN0()/getN1())/r;

        if (r == 0.0) return t/getN0();

        if (t < time) {
            return super.getIntensity(t);
        } else {
            return super.getIntensity(time) + (t-time)/getN1();
        }
  	}

	public double getInverseIntensity(double x) {

        double r = getGrowthRate();
        double time = Math.log(getN0()/getN1())/r;
        double N0 = getN0();
        double N1 = getN1();

        double integralToChangePoint = (Math.exp(time*r)-1)/(r*N0);

        if(x<integralToChangePoint){
            return Math.log(x*r*N0+1)/r;
        } else {
            return N1*(x-integralToChangePoint) + time;
        }

	}
	
	public int getNumArguments() {
		return 3;
	}
	
	public String getArgumentName(int n) {
		switch (n) {
			case 0: return "N0";
			case 1: return "r";
			case 2: return "N1";
		}
		throw new IllegalArgumentException("Argument " + n + " does not exist");
	}
	
	public double getArgument(int n) {
		switch (n) {
			case 0: return getN0();
			case 1: return getGrowthRate();
			case 2: return getN1();
		}
		throw new IllegalArgumentException("Argument " + n + " does not exist");
	}
	
	public void setArgument(int n, double value) {
		switch (n) {
			case 0: setN0(value); break;
			case 1: setGrowthRate(value); break;
			case 2: setN1(value); break;
			default: throw new IllegalArgumentException("Argument " + n + " does not exist");

		}
	}

	public double getLowerBound(int n) {
		return 0.0;
	}
	
	public double getUpperBound(int n) {
		return Double.POSITIVE_INFINITY;
	}

	//
	// private stuff
	//
	
	private double N1 = 0.0;
}
