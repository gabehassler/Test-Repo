package dr.math.iterations;
import dr.math.interfaces.OneVariableFunction;
public class BisectionZeroFinder extends FunctionalIterator
{
private double xNeg;
private double xPos;
public BisectionZeroFinder(OneVariableFunction func) {
super(func);
}
public BisectionZeroFinder( OneVariableFunction func, double x1, double x2)
throws IllegalArgumentException
{
this(func);
setNegativeX( x1);
setPositiveX( x2);
}
public double evaluateIteration()
{
result = ( xPos + xNeg) * 0.5;
if ( f.value(result) > 0 )
xPos = result;
else
xNeg = result;
return relativePrecision( Math.abs( xPos - xNeg));
}
public void setNegativeX( double x) throws IllegalArgumentException
{
if ( f.value( x) > 0 )
throw new IllegalArgumentException( "f("+x+
") is positive instead of negative");
xNeg = x;
}
public void setPositiveX( double x) throws IllegalArgumentException
{
if ( f.value( x) < 0 )
throw new IllegalArgumentException( "f("+x+
") is negative instead of positive");
xPos = x;
}
}