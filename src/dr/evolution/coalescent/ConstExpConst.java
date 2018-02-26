
package dr.evolution.coalescent;

public class ConstExpConst extends DemographicFunction.Abstract {
    public enum Parameterization {
        GROWTH_RATE,
        ANCESTRAL_POPULATION_SIZE
    }

    public ConstExpConst(Type units) {
        this(Parameterization.ANCESTRAL_POPULATION_SIZE, false, units);
    }

    public ConstExpConst(Parameterization parameterization, boolean useNumericalIntegrator, Type units) {
        super(units);
        this.parameterization = parameterization;
        this.useNumericalIntegrator = useNumericalIntegrator;
    }

    public double getN0() { return N0; }

    public void setN0(double N0) { this.N0 = N0; }

    public double getN1() {
        if (parameterization == Parameterization.ANCESTRAL_POPULATION_SIZE) {
            return N1;
        }

        return N0 * Math.exp(-epochTime * growthRate);
    }

    public void setN1(double N1) {
        assert(parameterization == Parameterization.ANCESTRAL_POPULATION_SIZE);
        this.N1 = N1;
    }

    public double getTime1() {
        return time1;
    }

    public void setTime1(double time1) {
        this.time1 = time1;
    }

    public double getEpochTime() {
        return epochTime;
    }

    public void setEpochTime(double epochTime) {
        this.epochTime = epochTime;
    }

    public double getTime2() {
        return time1 + epochTime;
    }

    public final void setGrowthRate(double growthRate) {
        assert(parameterization == Parameterization.GROWTH_RATE);
        this.growthRate = growthRate;
    }

    public final double getGrowthRate() {
        if (parameterization == Parameterization.GROWTH_RATE) {
            return growthRate;
        }

        return (Math.log(N0) - Math.log(N1)) / epochTime;
    }


    // Implementation of abstract methods

    public double getDemographic(double t) {

        double r = getGrowthRate();

        if (t < getTime1()) {
            return getN0();
        }

        if (t >= getTime2()) {
            return getN1();
        }

        return getN0() * Math.exp(-r*(t - getTime1()));
    }

    public double getIntegral(double start, double finish)
    {
        if (useNumericalIntegrator) {
            return getNumericalIntegral(start, finish);
        } else {
            return getIntensity(finish) - getIntensity(start);
        }
    }

    public double getIntensity(double t) {
        double time2 = getTime2();
        double oneOverN0 = 1.0 / getN0();
        double r = getGrowthRate();

        if (t < time1) {
            return (t * oneOverN0);
        }
        if (t > time1 && t < time2) {
            return (time1 * oneOverN0) + (( (Math.exp(t*r) - Math.exp(time1*r)) * oneOverN0) / r);
        }

        double oneOverN1 = 1.0 / getN1();
        // if (t >= time2) {
        return (time1 * oneOverN0) + (( (Math.exp(time2*r) - Math.exp(time1*r)) * oneOverN0) / r) + (oneOverN1 * (t-time2));
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
            case 1: return (parameterization == Parameterization.ANCESTRAL_POPULATION_SIZE ? "N1": "r");
            case 2: return "epochTime";
            case 3: return "time1";
        }
        throw new IllegalArgumentException("Argument " + n + " does not exist");
    }

    public double getArgument(int n) {
        switch (n) {
            case 0: return getN0();
            case 1: return (parameterization == Parameterization.ANCESTRAL_POPULATION_SIZE ? getN1(): getGrowthRate());
            case 2: return getEpochTime();
            case 3: return getTime1();
        }
        throw new IllegalArgumentException("Argument " + n + " does not exist");
    }

    public void setArgument(int n, double value) {
        switch (n) {
            case 0: setN0(value); break;
            case 1:
                if (parameterization == Parameterization.ANCESTRAL_POPULATION_SIZE) {
                    setN1(value);
                } else {
                    setGrowthRate(value);
                }
                break;
            case 2: setEpochTime(value); break;
            case 3: setTime1(value); break;
            default: throw new IllegalArgumentException("Argument " + n + " does not exist");

        }
    }

    public double getLowerBound(int n) {
        if (n == 1 && parameterization == Parameterization.GROWTH_RATE) {
            return Double.NEGATIVE_INFINITY;
        }
        return 0.0;
    }

    public double getUpperBound(int n) {
        return Double.POSITIVE_INFINITY;
    }

    //
    // private stuff
    //

    private double N0;
    private double N1;
    private double time1;
    private double epochTime;
    private double growthRate;
    private final boolean useNumericalIntegrator;
    private final Parameterization parameterization;
}