package dr.evolution.coalescent;
import dr.evolution.util.Units;
import dr.math.Binomial;
import dr.math.MathUtils;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.integration.RombergIntegrator;
public interface DemographicFunction extends UnivariateRealFunction, Units {
double getDemographic(double t);
double getLogDemographic(double t);
double getIntensity(double t);
double getInverseIntensity(double x);
double getIntegral(double start, double finish);
int getNumArguments();
String getArgumentName(int n);
double getArgument(int n);
void setArgument(int n, double value);
double getLowerBound(int n);
double getUpperBound(int n);
//	DemographicFunction getCopy();
double getThreshold();
public abstract class Abstract implements DemographicFunction
{
// private static final double LARGE_POSITIVE_NUMBER = 1.0e50;
//        private static final double LARGE_NEGATIVE_NUMBER = -1.0e50;
//        private static final double INTEGRATION_PRECISION = 1.0e-5;
//        private static final double INTEGRATION_MAX_ITERATIONS = 50;
RombergIntegrator numericalIntegrator = null;
public Abstract(Type units) {
setUnits(units);
}
// general functions
public double getLogDemographic(double t) {
return Math.log(getDemographic(t));
}
public double getThreshold() {
return 0;
}
public double getIntegral(double start, double finish)
{
return getIntensity(finish) - getIntensity(start);
}
public double getNumericalIntegral(double start, double finish) {
// AER 19th March 2008: I switched this to use the RombergIntegrator from
// commons-math v1.2.
if (start > finish) {
throw new RuntimeException("NumericalIntegration start > finish");
}
if (start == finish) {
return 0.0;
}
if (numericalIntegrator == null) {
numericalIntegrator = new RombergIntegrator(this);
}
try {
return numericalIntegrator.integrate(start, finish);
} catch (MaxIterationsExceededException e) {
throw new RuntimeException(e);
} catch (FunctionEvaluationException e) {
throw new RuntimeException(e);
}
//            double lastST = LARGE_NEGATIVE_NUMBER;
//            double lastS = LARGE_NEGATIVE_NUMBER;
//
//            assert(finish > start);
//
//            for (int j = 1; j <= INTEGRATION_MAX_ITERATIONS; j++) {
//                // iterate doTrapezoid() until answer obtained
//
//                double st = doTrapezoid(j, start, finish, lastST);
//                double s = (4.0 * st - lastST) / 3.0;
//
//                // If answer is within desired accuracy then return
//                if (Math.abs(s - lastS) < INTEGRATION_PRECISION * Math.abs(lastS)) {
//                    return s;
//                }
//                lastS = s;
//                lastST = st;
//            }
//
//            throw new RuntimeException("Too many iterations in getNumericalIntegral");
}
//        private double doTrapezoid(int n, double low, double high, double lastS) {
//
//            double s;
//
//            if (n == 1) {
//                // On the first iteration s is reset
//                double demoLow = getDemographic(low); // Value of N(x) obtained here
//                assert(demoLow > 0.0);
//
//                double demoHigh = getDemographic(high);
//                assert(demoHigh > 0.0);
//
//                s = 0.5 * (high - low) * ( (1.0 / demoLow) + (1.0 / demoHigh) );
//            } else {
//                int it=1;
//                for (int j = 1; j < n - 1; j++) {
//                    it *= 2;
//                }
//
//                double tnm = it;	// number of points
//                double del = (high - low) / tnm;	// width of spacing between points
//
//                double x = low + 0.5 * del;
//
//                double sum = 0.0;
//                for (int j = 1; j <= it; j++) {
//                    double demoX = getDemographic(x); // Value of N(x) obtained here
//                    assert(demoX > 0.0);
//
//                    sum += (1.0 / demoX);
//                    x += del;
//                }
//                s =  0.5 * (lastS + (high - low) * sum / tnm);	// New s uses previous s value
//            }
//
//            return s;
//        }
// **************************************************************
// UnivariateRealFunction IMPLEMENTATION
// **************************************************************
public double value(double x) {
return 1.0 / getDemographic(x);
}
// **************************************************************
// Units IMPLEMENTATION
// **************************************************************
private Type units;
public void setUnits(Type u)
{
units = u;
}
public Type getUnits()
{
return units;
}
}
public static class Utils
{
private static double getInterval(double U, DemographicFunction demographicFunction,
int lineageCount, double timeOfLastCoalescent) {
final double intensity = demographicFunction.getIntensity(timeOfLastCoalescent);
final double tmp = -Math.log(U)/Binomial.choose2(lineageCount) + intensity;
return demographicFunction.getInverseIntensity(tmp) - timeOfLastCoalescent;
}
private static double getInterval(double U, DemographicFunction demographicFunction, int lineageCount,
double timeOfLastCoalescent, double earliestTimeOfFinalCoalescent){
if(timeOfLastCoalescent>earliestTimeOfFinalCoalescent){
throw new IllegalArgumentException("Given maximum height is smaller than given final coalescent time");
}
final double fullIntegral = demographicFunction.getIntegral(timeOfLastCoalescent,
earliestTimeOfFinalCoalescent);
final double normalisation = 1-Math.exp(-Binomial.choose2(lineageCount)*fullIntegral);
final double intensity = demographicFunction.getIntensity(timeOfLastCoalescent);
double tmp = -Math.log(1-U*normalisation)/Binomial.choose2(lineageCount) + intensity;
return demographicFunction.getInverseIntensity(tmp) - timeOfLastCoalescent;
}
public static double getSimulatedInterval(DemographicFunction demographicFunction,
int lineageCount, double timeOfLastCoalescent)
{
final double U = MathUtils.nextDouble(); // create unit uniform random variate
return getInterval(U, demographicFunction, lineageCount, timeOfLastCoalescent);
}
public static double getSimulatedInterval(DemographicFunction demographicFunction, int lineageCount,
double timeOfLastCoalescent, double earliestTimeOfFirstCoalescent){
final double U = MathUtils.nextDouble();
return getInterval(U, demographicFunction, lineageCount, timeOfLastCoalescent,
earliestTimeOfFirstCoalescent);
}
public static double getMedianInterval(DemographicFunction demographicFunction,
int lineageCount, double timeOfLastCoalescent)
{
return getInterval(0.5, demographicFunction, lineageCount, timeOfLastCoalescent);
}
public static void testConsistency(DemographicFunction demographicFunction, int steps, double maxTime) {
double delta = maxTime / (double)steps;
for (int i = 0; i <= steps; i++) {
double time = (double)i * delta;
double intensity = demographicFunction.getIntensity(time);
double newTime = demographicFunction.getInverseIntensity(intensity);
if (Math.abs(time - newTime) > 1e-12) {
throw new RuntimeException(
"Demographic model not consistent! error size = " +
Math.abs(time-newTime));
}
}
}
}
}