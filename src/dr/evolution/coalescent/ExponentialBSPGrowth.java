package dr.evolution.coalescent;
public class ExponentialBSPGrowth extends DemographicFunction.Abstract {
    public ExponentialBSPGrowth(Type units) {
        super(units);
    }
    public void setup(double N0, double N1, double time) {
        this.N0 = N0;
        this.r = (Math.log(N0) - Math.log(N1)) / time;
    }
    public void setup(double N0, double r){
        this.N0 = N0;
        this.r = r;
    }
    public void setupN1(double N1, double r, double time) {
        this.r = r;
        this.N0 = N1*Math.exp(r*time);
    }
    // Implementation of abstract methods
    public double getDemographic(double t) {
        if (r == 0) {
            return N0;
        } else {
            return N0 * Math.exp(-t * r);
        }
    }
    public double getLogDemographic(double t) {
        if (r == 0) {
            return Math.log(N0);
        } else {
            return Math.log(N0) - (t * r);
        }
    }
    @Override
    public double getIntegral(double start, double finish) {
//        double integral1 = getNumericalIntegral(start, finish);
        double integral;
        if (r == 0.0) {
            integral = (finish - start) / N0;
        } else {
            integral = (Math.exp(finish*r) - Math.exp(start*r))/N0/r;
        }
        return integral;
    }
    public int getNumArguments() {
        return 0;
    }
    public String getArgumentName(int n) {
        return null;
    }
    public double getArgument(int n) {
        return 0;
    }
    public void setArgument(int n, double value) {
    }
    public double getLowerBound(int n) {
        return 0;
    }
    public double getUpperBound(int n) {
        return 0;
    }
    public DemographicFunction getCopy() {
        return null;
    }
    public double getIntensity(double t) {
        throw new RuntimeException("not implemented");
    }
    public double getInverseIntensity(double x) {
        throw new RuntimeException("not implemented");
    }
    private double r, N0;
}