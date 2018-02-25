package dr.math.iterations;
import dr.math.functionEval.DrMath;
import dr.math.functionEval.FunctionDerivative;
import dr.math.interfaces.OneVariableFunction;
public class NewtonZeroFinder extends FunctionalIterator
{
private OneVariableFunction df;
public NewtonZeroFinder( OneVariableFunction func, double start)
{
super( func);
setStartingValue( start);
}
public NewtonZeroFinder( OneVariableFunction func,
OneVariableFunction dFunc, double start)
throws IllegalArgumentException
{
this( func, start);
setDerivative( dFunc);
}
public double evaluateIteration()
{
double delta = f.value( result) / df.value( result);
result -= delta;
return relativePrecision( Math.abs( delta));
}
public void initializeIterations()
{
if ( df == null)
df = new FunctionDerivative( f);
if ( Double.isNaN( result) )
result = 0;
int n = 0;
while ( DrMath.equal( df.value( result), 0) )
{
if ( ++n > getMaximumIterations() )
break;
result += Math.random();
}
}
public void setDerivative( OneVariableFunction dFunc)
throws IllegalArgumentException
{
df = new FunctionDerivative( f);
if ( !DrMath.equal( df.value( result), dFunc.value( result), 0.001) )
throw new IllegalArgumentException
( "Supplied derative function is inaccurate");
df = dFunc;
}
public void setFunction( OneVariableFunction func)
{
super.setFunction( func);
df = null;
}
public void setStartingValue( double start)
{
result = start;
}
}