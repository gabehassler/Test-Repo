
package dr.math.iterations;

import dr.math.interfaces.OneVariableFunction;

public abstract class FunctionalIterator extends IterativeProcess
{
	protected double result = Double.NaN;

	protected OneVariableFunction f;

    public FunctionalIterator(OneVariableFunction func)
    {
        setFunction( func);
    }

    public double getResult( )
    {
        return result;
    }

    public double relativePrecision( double epsilon)
    {
        return relativePrecision( epsilon, Math.abs( result));
    }

    public void setFunction( OneVariableFunction func)
    {
        f = func;
    }

    public void setInitialValue( double x)
    {
        result = x;
    }
}