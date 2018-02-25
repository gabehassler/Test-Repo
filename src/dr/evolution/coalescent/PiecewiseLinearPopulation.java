package dr.evolution.coalescent;
import java.util.Collections;
public class PiecewiseLinearPopulation extends PiecewiseConstantPopulation {
public PiecewiseLinearPopulation(Type units) {
super(units);
}
public PiecewiseLinearPopulation(double[] intervals, double[] thetas, Type units) {
super(intervals, thetas, units);
}
// **************************************************************
// Implementation of abstract methods
// **************************************************************
protected final double getDemographic(int epoch, double t) {
// if in last epoch then the population is flat.
if (epoch == (thetas.length - 1)) {
return getEpochDemographic(epoch);
}
final double popSize1 = getEpochDemographic(epoch);
final double popSize2 = getEpochDemographic(epoch + 1);
final double width = getEpochDuration(epoch);
assert 0 <= t && t <= width;
return popSize1 + (t / width) * (popSize2 - popSize1);
}
public DemographicFunction getCopy() {
PiecewiseLinearPopulation df = new PiecewiseLinearPopulation(new double[intervals.length], new double[thetas.length], getUnits());
System.arraycopy(intervals, 0, df.intervals, 0, intervals.length);
System.arraycopy(thetas, 0, df.thetas, 0, thetas.length);
return df;
}
protected final double getIntensity(int epoch) {
final double N1 = getEpochDemographic(epoch);
final double N2 = getEpochDemographic(epoch + 1);
final double w = getEpochDuration(epoch);
if (N1 != N2) {
return w * Math.log(N2 / N1) / (N2 - N1);
} else {
return w / N1;
}
}
protected final double getIntensity(int epoch, double relativeTime) {
assert relativeTime <= getEpochDuration(epoch);
final double N1 = getEpochDemographic(epoch);
final double N2 = getEpochDemographic(epoch + 1);
final double w = getEpochDuration(epoch);
if (N1 != N2) {
return w * Math.log(N1 * w / (N2 * relativeTime + N1 * (w - relativeTime))) / (N1 - N2);
} else {
return relativeTime / N1;
}
}
public final double getInverseIntensity(double targetI) {
if (cif != null) {
int epoch = Collections.binarySearch(cif, targetI);
if (epoch < 0) {
epoch = -epoch - 1;
if (epoch > 0) {
return endTime.get(epoch - 1) +
getInverseIntensity(epoch, targetI - cif.get(epoch - 1));
} else {
assert epoch == 0;
return getInverseIntensity(0, targetI);
}
} else {
return endTime.get(epoch);
}
} else {
int epoch = 0;
double cI = 0;
double eI = getIntensity(epoch);
double time = 0.0;
while (cI + eI < targetI) {
cI += eI;
time += getEpochDuration(epoch);
epoch += 1;
eI = getIntensity(epoch);
}
time += getInverseIntensity(epoch, targetI - cI);
return time;
}
}
private double getInverseIntensity(int epoch, double I) {
final double N1 = getEpochDemographic(epoch);
final double N2 = getEpochDemographic(epoch + 1);
final double w = getEpochDuration(epoch);
final double dn = N2 - N1;
double time;
if (dn != 0.0) {
time = N1 * w * (Math.exp(I * dn / w) - 1.0) / dn;
} else {
time = I * N1;
}
return time;
}
}
