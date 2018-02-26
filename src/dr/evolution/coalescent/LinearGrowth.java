package dr.evolution.coalescent;
public class LinearGrowth extends ConstantPopulation
{
	//
	// Public stuff
	//
	public LinearGrowth(Type units) {
		super(units);
	}
	public double getN0() { return N0; }
	public void setN0(double N0) { this.N0 = N0; }
	// Implementation of abstract methods
	public double getDemographic(double t) {
		if(t>0){
			throw new RuntimeException("Negative times only!");
		}
		return -getN0()*t;
	}
	public double getIntensity(double t) {
		throw new RuntimeException("getIntensity is not implemented (and not finite); use getIntegral instead");
	}
	public double getInverseIntensity(double x) {
		throw new RuntimeException("Not implemented");
	}
	public double getIntegral(double start, double finish) {
		return 1/getN0() * Math.log((-start)/(-finish));
	}
	public double getInverseIntegral(double x, double start){
		return start*Math.exp(-(x*getN0()));
	}
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
		LinearGrowth df = new LinearGrowth(getUnits());
		df.N0 = N0;
		return df;
	}
	//
	// private stuff
	//
	private double N0;
}
