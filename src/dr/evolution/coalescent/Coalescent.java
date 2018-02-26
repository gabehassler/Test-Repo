package dr.evolution.coalescent;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.math.Binomial;
import dr.math.MultivariateFunction;
public class Coalescent implements MultivariateFunction, Units {
    // PUBLIC STUFF
    public Coalescent(Tree tree, DemographicFunction demographicFunction) {
        this(new TreeIntervals(tree), demographicFunction);
    }
    public Coalescent(IntervalList intervals, DemographicFunction demographicFunction) {
        this.intervals = intervals;
        this.demographicFunction = demographicFunction;
    }
    public double calculateLogLikelihood() {
        return calculateLogLikelihood(intervals, demographicFunction);
    }
    public static double calculateLogLikelihood(IntervalList intervals, DemographicFunction demographicFunction) {
        return calculateLogLikelihood(intervals, demographicFunction, 0.0);
    }
    public static double calculateLogLikelihood(IntervalList intervals, DemographicFunction demographicFunction, double threshold) {
        double logL = 0.0;
        double startTime = 0.0;
        final int n = intervals.getIntervalCount();
        for (int i = 0; i < n; i++) {
            final double duration = intervals.getInterval(i);
            final double finishTime = startTime + duration;
            final double intervalArea = demographicFunction.getIntegral(startTime, finishTime);
            if( intervalArea == 0 && duration != 0 ) {
                return Double.NEGATIVE_INFINITY;
            }
            final int lineageCount = intervals.getLineageCount(i);
            final double kChoose2 = Binomial.choose2(lineageCount);
            // common part
            logL += -kChoose2 * intervalArea;
            if (intervals.getIntervalType(i) == IntervalType.COALESCENT) {
                final double demographicAtCoalPoint = demographicFunction.getDemographic(finishTime);
                // if value at end is many orders of magnitude different than mean over interval reject the interval
                // This is protection against cases where ridiculous infinitesimal population size at the end of a
                // linear interval drive coalescent values to infinity.
                if( duration == 0.0 || demographicAtCoalPoint * (intervalArea/duration) >= threshold ) {
                    //                if( duration == 0.0 || demographicAtCoalPoint >= threshold * (duration/intervalArea) ) {
                    logL -= Math.log(demographicAtCoalPoint);
                } else {
                    // remove this at some stage
                    //  System.err.println("Warning: " + i + " " + demographicAtCoalPoint + " " + (intervalArea/duration) );
                    return Double.NEGATIVE_INFINITY;
                }
            }
            startTime = finishTime;
        }
        return logL;
    }
    public static double calculateAnalyticalLogLikelihood(IntervalList intervals) {
        if (!intervals.isCoalescentOnly()) {
            throw new IllegalArgumentException("Can only calculate analytical likelihood for pure coalescent intervals");
        }
        final double lambda = getLambda(intervals);
        final int n = intervals.getSampleCount();
        // assumes a 1/theta prior
        //logLikelihood = Math.log(1.0/Math.pow(lambda,n));
        // assumes a flat prior
        return (1-n) * Math.log(lambda); // Math.log(1.0/Math.pow(lambda,n-1));
    }
    private static double getLambda(IntervalList intervals) {
        double lambda = 0.0;
        for (int i= 0; i < intervals.getIntervalCount(); i++) {
            lambda += (intervals.getInterval(i) * intervals.getLineageCount(i));
        }
        lambda /= 2;
        return lambda;
    }
    // **************************************************************
    // MultivariateFunction IMPLEMENTATION
    // **************************************************************
    public double evaluate(double[] argument) {
        for (int i = 0; i < argument.length; i++) {
            demographicFunction.setArgument(i, argument[i]);
        }
        return calculateLogLikelihood();
    }
    public int getNumArguments() {
        return demographicFunction.getNumArguments();
    }
    public double getLowerBound(int n) {
        return demographicFunction.getLowerBound(n);
    }
    public double getUpperBound(int n) {
        return demographicFunction.getUpperBound(n);
    }
    // **************************************************************
    // Units IMPLEMENTATION
    // **************************************************************
    public final void setUnits(Type u)
    {
        demographicFunction.setUnits(u);
    }
    public final Type getUnits()
    {
        return demographicFunction.getUnits();
    }
    public DemographicFunction getDemographicFunction(){
        return demographicFunction;
    }
    public IntervalList getIntervals(){
        return intervals;
    }
    DemographicFunction demographicFunction = null;
    IntervalList intervals = null;
}