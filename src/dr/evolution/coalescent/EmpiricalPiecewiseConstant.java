package dr.evolution.coalescent;
public class EmpiricalPiecewiseConstant extends DemographicFunction.Abstract {
public EmpiricalPiecewiseConstant(double[] intervals, double[] popSizes, double lag, Type units) {
super(units);
if (popSizes == null || intervals == null) { throw new IllegalArgumentException(); }
if (popSizes.length != intervals.length + 1) { throw new IllegalArgumentException(); }
if (lag < 0.0) throw new IllegalArgumentException("Lag must be greater than 1.");
this.intervals = intervals;
this.popSizes = popSizes;
this.lag = lag;
}
public void setLag(double lag) {
this.lag = lag;
}
public void setPopulationSizes(double[] popSizes) {
this.popSizes = popSizes;
}
// **************************************************************
// Implementation of abstract methods
// **************************************************************
public double getDemographic(double t) {
int epoch = 0;
double t1 = t+lag;
while (t1 > getEpochDuration(epoch)) {
t1 -= getEpochDuration(epoch);
epoch += 1;
}
return getDemographic(epoch, t1);
}
public double getIntensity(double t) { 
// find the first epoch that is involved
double t2 = lag;
int epoch = 0;
while (t2 > getEpochDuration(epoch)) {
t2 -= getEpochDuration(epoch);		
epoch += 1;
}
// add last fraction of first epoch
double intensity = getIntensity(epoch)-getIntensity(epoch, t2);
double t1 = t-(getEpochDuration(epoch)-t2);
epoch += 1;
while (t1 > getEpochDuration(epoch)) {
t1 -= getEpochDuration(epoch);		
intensity += getIntensity(epoch);
epoch += 1;
}
// add last fraction of intensity
// when t1 may be negative (for example when t is in the first epoch) the intensity need
// to be substracted
intensity += t1 >= 0 ? getIntensity(epoch, t1) : getIntensity(epoch-1, t1);
return intensity; 
}
public double getInverseIntensity(double x) { 
throw new RuntimeException("Not implemented!");	
}
public double getUpperBound(int i) { return 1e9;}
public double getLowerBound(int i) { return Double.MIN_VALUE;}
public int getNumArguments() { return 1; }
public String getArgumentName(int i) { 
return "lag";
}
public double getArgument(int i) { 
return lag;
}
public void setArgument(int i, double value) { 
lag = value; 
}
public DemographicFunction getCopy() {
EmpiricalPiecewiseConstant df = new EmpiricalPiecewiseConstant(new double[intervals.length], new double[popSizes.length], lag, getUnits());
System.arraycopy(intervals, 0, df.intervals, 0, intervals.length);
System.arraycopy(popSizes, 0, df.popSizes, 0, popSizes.length);
return df;
}
protected double getDemographic(int epoch, double t) {
return getEpochDemographic(epoch);
}
protected double getIntensity(int epoch) {
return getEpochDuration(epoch) / getEpochDemographic(epoch);
}
protected double getIntensity(int epoch, double relativeTime) {
return relativeTime / getEpochDemographic(epoch);
}
public double getEpochDuration(int epoch) {
if (epoch < intervals.length) {
return intervals[epoch];
}
return Double.POSITIVE_INFINITY;
}
public double getEpochDemographic(int epoch) {
if (epoch >= popSizes.length) { throw new IllegalArgumentException(); }
return popSizes[epoch];
}
public String toString() {
StringBuffer buffer = new StringBuffer();
buffer.append(popSizes[0]);
for (int i =1; i < popSizes.length; i++) {
buffer.append("\t").append(popSizes[i]);
}
return buffer.toString();
}
double[] intervals;
double[] popSizes;
double lag;
}
