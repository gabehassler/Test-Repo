package dr.math;
public class NumericalDerivative
{
//
// Public stuff
//
public static double firstDerivative(UnivariateFunction f, double x)
{	
double h = MachineAccuracy.SQRT_EPSILON*(Math.abs(x) + 1.0);
// Centered first derivative
return (f.evaluate(x + h) - f.evaluate(x - h))/(2.0*h);
}
public static double secondDerivative(UnivariateFunction f, double x)
{
double h = MachineAccuracy.SQRT_SQRT_EPSILON*(Math.abs(x) + 1.0);
// Centered second derivative
return (f.evaluate(x + h) - 2.0*f.evaluate(x) + f.evaluate(x - h))/(h*h);
}
public static double[] gradient(MultivariateFunction f, double[] x)
{	
double[] result = new double[x.length];
gradient(f, x, result);
return result;
}
public static void gradient(MultivariateFunction f, double[] x, double[] grad)
{	
for (int i = 0; i < f.getNumArguments(); i++)
{
double h = MachineAccuracy.SQRT_EPSILON*(Math.abs(x[i]) + 1.0);
double oldx = x[i];
x[i] = oldx + h;
double fxplus = f.evaluate(x);
x[i] = oldx - h;
double fxminus = f.evaluate(x);
x[i] = oldx;
// Centered first derivative
grad[i] = (fxplus-fxminus)/(2.0*h);
}
}
public static double[] diagonalHessian(MultivariateFunction f, double[] x)
{
int len = f.getNumArguments();
double[] result = new double[len];
for (int i = 0; i < len; i++)
{
double h = MachineAccuracy.SQRT_SQRT_EPSILON*(Math.abs(x[i]) + 1.0);
double oldx = x[i];
x[i] = oldx + h;
double fxplus = f.evaluate(x);
x[i] = oldx - h;
double fxminus = f.evaluate(x);
x[i] = oldx;
double fx = f.evaluate(x);
// Centered second derivative
result[i] = (fxplus - 2.0*fx + fxminus)/(h*h);
}
return result;
}
}
