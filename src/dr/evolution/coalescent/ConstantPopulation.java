
package dr.evolution.coalescent;

public class ConstantPopulation extends DemographicFunction.Abstract
{
	//
	// Public stuff
	//
	
	public ConstantPopulation(Type units) {

		super(units);
	}

	public double getN0() { return N0; }

	public void setN0(double N0) { this.N0 = N0; }

		
	// Implementation of abstract methods
	
	public double getDemographic(double t) { return getN0(); }
	public double getIntensity(double t) { return t/getN0(); }
	public double getInverseIntensity(double x) { return getN0()*x; }

    // same as abstract
//	/**
//	 * Calculates the integral 1/N(x) dx between start and finish. The
//	 * inherited function in DemographicFunction.Abstract calls a
//	 * numerical integrater which is unecessary.
//	 */
//	public double getIntegral(double start, double finish) {
//		return getIntensity(finish) - getIntensity(start);
//	}
//
	public int getNumArguments() {
		return 1;
	}
	
	public String getArgumentName(int n) {
		return "N0";
	}
	
	public double getArgument(int n) {
		return getN0();
	}
	
	public void setArgument(int n, double value) {
		setN0(value);
	}

	public double getLowerBound(int n) {
		return 0.0;
	}
	
	public double getUpperBound(int n) {
		return Double.POSITIVE_INFINITY;
	}

	public DemographicFunction getCopy() {
		ConstantPopulation df = new ConstantPopulation(getUnits());
		df.N0 = N0;
		
		return df;
	}

	//
	// private stuff
	//
	
	private double N0;
}
