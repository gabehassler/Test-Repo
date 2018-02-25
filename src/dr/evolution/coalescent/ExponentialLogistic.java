package dr.evolution.coalescent;
public class ExponentialLogistic extends LogisticGrowth {
public ExponentialLogistic(Type units) {
super(units);
}
public double getTime() {
return time;
}
public void setTime(double time) {
this.time = time;
}
public double getR1() {
return r1;
}
public void setR1(double r1) {
this.r1 = r1;
}
// Implementation of abstract methods
public double getDemographic(double t) {
double transition_time = getTime();
// size of the population under the logistic at transition_time
if (t < transition_time) {
return super.getDemographic(t);
} else {
double r1 = getR1();
double N1 = super.getDemographic(transition_time);
return N1 * Math.exp(-r1*(t - transition_time));
}
}
public double getIntensity(double t) {
throw new RuntimeException("Not implemented!");
}
public double getInverseIntensity(double x) {
throw new RuntimeException("Not implemented!");
}
public double getIntegral(double start, double finish) {
//final double v1 = getIntensity(finish) - getIntensity(start);
// Until the above getIntensity is implemented, numerically integrate
final double numerical = getNumericalIntegral(start, finish);
return numerical;
}
public int getNumArguments() {
return 5;
}
public String getArgumentName(int n) {
switch (n) {
case 0: return "N0";
case 1: return "r0";
case 2: return "c";
case 3: return "r1";
case 4: return "t1";
}
throw new IllegalArgumentException("Argument " + n + " does not exist");
}
public double getArgument(int n) {
switch (n) {
case 0: return getN0();
case 1: return getGrowthRate();
case 2: return getShape();
case 3: return getR1();
case 4: return getTime();
}
throw new IllegalArgumentException("Argument " + n + " does not exist");
}
public void setArgument(int n, double value) {
switch (n) {
case 0: setN0(value); break;
case 1: setGrowthRate(value); break;
case 2: setShape(value); break;
case 3: setR1(value); break;
case 4: setTime(value); break;
default: throw new IllegalArgumentException("Argument " + n + " does not exist");
}
}
public double getLowerBound(int n) {
return 0.0;
}
public double getUpperBound(int n) {
return Double.POSITIVE_INFINITY;
}
//
// private stuff
//
private double time = 0.0;
private double r1 = 0.0;
}