package dr.math;
public class CompoundFunction implements UnivariateFunction {
public CompoundFunction(UnivariateFunction[] functions, double Z) {
this.functions = functions;
this.Z = Z;
}
public double evaluate(double x) {
double value = Z;
for (int i =0; i < functions.length; i++) {
value *= functions[i].evaluate(x);
}
return value;
}
public double getLowerBound() { return functions[0].getLowerBound(); }
public double getUpperBound() { return functions[0].getUpperBound(); }
UnivariateFunction[] functions = null;	
double Z = 1.0;
}
