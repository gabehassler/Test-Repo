
package dr.math;


public interface MultivariateFunction
{
	double evaluate(double[] argument);


	 int getNumArguments();

	double getLowerBound(int n);

	double getUpperBound(int n);

}
