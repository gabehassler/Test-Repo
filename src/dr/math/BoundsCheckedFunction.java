package dr.math;
public class BoundsCheckedFunction implements MultivariateFunction {
public BoundsCheckedFunction(MultivariateFunction func) {
this(func, 1000000);
}
public BoundsCheckedFunction(MultivariateFunction func, double largeNumber) {
f = func;
veryLarge = largeNumber;
}
public double evaluate(double[] x) {
int len = f.getNumArguments();
for (int i = 0; i < len; i++) {
if (x[i] < f.getLowerBound(i) ||
x[i] > f.getUpperBound(i)) {
return veryLarge;
}
}
return f.evaluate(x);
}
public int getNumArguments() {
return f.getNumArguments();
}
public double getLowerBound(int n) {
return f.getLowerBound(n);
}
public double getUpperBound(int n) {
return f.getUpperBound(n);
}
//
// Private stuff
//
private MultivariateFunction f;
private double veryLarge;
}
