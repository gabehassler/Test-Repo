package dr.math.functionEval;
import dr.math.interfaces.OneVariableFunction;
public final class FunctionDerivative implements OneVariableFunction
{
private OneVariableFunction f;
private double relativePrecision = 0.0001;
public FunctionDerivative( OneVariableFunction func)
{
this( func, 0.000001);
}
public FunctionDerivative( OneVariableFunction func, double precision)
{
f = func;
relativePrecision = precision;
}
public double value( double x)
{
double x1 = x == 0 ? relativePrecision
: x * ( 1 + relativePrecision);
double x2 = 2 * x - x1;
return (f.value(x1) - f.value(x2)) / (x1 - x2);
}
}