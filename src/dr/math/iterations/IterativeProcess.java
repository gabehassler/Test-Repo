package dr.math.iterations;
import dr.math.functionEval.DrMath;
public abstract class IterativeProcess
{
private int iterations;
private int maximumIterations = 50;
private double desiredPrecision = DrMath.defaultNumericalPrecision();
private double precision;
public IterativeProcess() {
}
public void evaluate()
{
iterations = 0;
initializeIterations();
while ( iterations++ < maximumIterations )
{
precision = evaluateIteration();
if ( hasConverged() )
break;
}
finalizeIterations();
}
abstract public double evaluateIteration();
public void finalizeIterations ( )
{
}
public double getDesiredPrecision( )
{
return desiredPrecision;
}
public int getIterations()
{
return iterations;
}
public int getMaximumIterations( )
{
return maximumIterations;
}
public double getPrecision()
{
return precision;
}
public boolean hasConverged()
{
return precision < desiredPrecision;
}
public void initializeIterations()
{
}
public double relativePrecision( double epsilon, double x)
{
return x > DrMath.defaultNumericalPrecision()
? epsilon / x: epsilon;
}
public void setDesiredPrecision( double prec )
throws IllegalArgumentException
{
if ( prec <= 0 )
throw new IllegalArgumentException
( "Non-positive precision: "+prec);
desiredPrecision = prec;
}
public void setMaximumIterations( int maxIter)
throws IllegalArgumentException
{
if ( maxIter < 1 )
throw new IllegalArgumentException
( "Non-positive maximum iteration: "+maxIter);
maximumIterations = maxIter;
}
}