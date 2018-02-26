
package dr.evolution.coalescent;

public class ExponentialGrowth extends ConstantPopulation {

    public ExponentialGrowth(Type units) {

        super(units);
    }

    public final double getGrowthRate() { return r; }

    public void setGrowthRate(double r) { this.r = r; }

    public void setDoublingTime(double doublingTime) {
        setGrowthRate( Math.log(2) / doublingTime );
    }

    // Implementation of abstract methods

    public double getDemographic(double t) {

        double r = getGrowthRate();
        if (r == 0) {
            return getN0();
        } else {
            return getN0() * Math.exp(-t * r);
        }
    }

    @Override
    public double getIntegral(double start, double finish) {
        double r = getGrowthRate();
        if (r == 0.0) {
            return (finish - start)/getN0();
        } else {
            return (Math.exp(finish*r) - Math.exp(start*r))/getN0()/r;
        }
    }

    public double getIntensity(double t)
    {
        double r = getGrowthRate();
        if (r == 0.0) {
            return t/getN0();
        } else {
            return (Math.exp(t*r)-1.0)/getN0()/r;
        }
    }

    public double getInverseIntensity(double x) {

        double r = getGrowthRate();
        if (r == 0.0) {
            return getN0()*x;
        } else {
            return Math.log(1.0+getN0()*x*r)/r;
        }
    }

    public int getNumArguments() {
        return 2;
    }

    public String getArgumentName(int n) {
        if (n == 0) {
            return "N0";
        } else {
            return "r";
        }
    }

    public double getArgument(int n) {
        if (n == 0) {
            return getN0();
        } else {
            return getGrowthRate();
        }
    }

    public void setArgument(int n, double value) {
        if (n == 0) {
            setN0(value);
        } else {
            setGrowthRate(value);
        }
    }

    public double getLowerBound(int n) {
        return 0.0;
    }

    public double getUpperBound(int n) {
        return Double.POSITIVE_INFINITY;
    }

    public DemographicFunction getCopy() {
        ExponentialGrowth df = new ExponentialGrowth(getUnits());
        df.setN0(getN0());
        df.r = r;

        return df;
    }

    //
    // private stuff
    //

    private double r;
}
