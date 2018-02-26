
package dr.evomodel.epidemiology;

import dr.evolution.coalescent.*;

public class LogisticGrowthN0 extends ExponentialGrowth {

    public LogisticGrowthN0(Type units) {
        super(units);
    }

	public void setT50(double value) {
		t50 = value;
	}
	
	public double getT50() {
		return t50;
	}

    // Implementation of abstract methods

    public double getDemographic(double t) {
        double N0 = getN0();
        double r = getGrowthRate();
        double T50 = getT50();
		
		return N0 * (1 + Math.exp(-r * T50)) / (1 + Math.exp(-r * (T50-t)));
    }

    public double getLogDemographic(double t) {
		return Math.log(getDemographic(t));
	}

    public double getIntensity(double t) {
        double N0 = getN0();
        double r = getGrowthRate();
        double T50 = getT50();
		double exp_rT50 = Math.exp(-r*T50);
		
		return (t + exp_rT50 * (Math.exp(r * t) - 1)/r) / (N0 * (1 + exp_rT50));
    }

    public double getInverseIntensity(double x) {

        double q = -getGrowthRate();
        double T50 = getT50();
        double N0 = getN0();
        double a = (q/Math.exp(q*T50));
        double k = N0*(1+Math.exp(q*T50))*x - (1/q)*Math.exp(q*T50);

        double lambertInput = q*Math.exp(-q*k)/a;

        double lambertResult;

        if(lambertInput==Double.POSITIVE_INFINITY){

            //use the asymptote; note q/a = exp(q*T50)

            double logInput = q*T50-q*k;
            lambertResult = logInput - Math.log(logInput);

        } else {
            lambertResult = LambertW.branch0(lambertInput);
        }

        return k + (1/q)*lambertResult;

    }

    public double getIntegral(double start, double finish) {
		return getIntensity(finish) - getIntensity(start);
    }

    public int getNumArguments() {
        return 3;
    }

    public String getArgumentName(int n) {
        switch (n) {
            case 0:
                return "N0";
            case 1:
                return "r";
            case 2:
                return "t50";
        }
        throw new IllegalArgumentException("Argument " + n + " does not exist");
    }

    public double getArgument(int n) {
        switch (n) {
            case 0:
                return getN0();
            case 1:
                return getGrowthRate();
            case 2:
                return getT50();
        }
        throw new IllegalArgumentException("Argument " + n + " does not exist");
    }

    public void setArgument(int n, double value) {
        switch (n) {
            case 0:
                setN0(value);
                break;
            case 1:
                setGrowthRate(value);
                break;
            case 2:
                setT50(value);
                break;
            default:
                throw new IllegalArgumentException("Argument " + n + " does not exist");

        }
    }

    public double getLowerBound(int n) {
        return 0.0;
    }

    public double getUpperBound(int n) {
        return Double.POSITIVE_INFINITY;
    }

    public DemographicFunction getCopy() {
        LogisticGrowthN0 df = new LogisticGrowthN0(getUnits());
        df.setN0(getN0());
        df.setGrowthRate(getGrowthRate());
        df.setT50(getT50());
        return df;
    }

    //
    // private stuff
    //

    private double t50;
}
