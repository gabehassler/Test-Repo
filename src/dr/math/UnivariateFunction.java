package dr.math;
public interface UnivariateFunction
{
	double evaluate(double argument);
	double getLowerBound();
	double getUpperBound();
    public abstract class AbstractLogEvaluatableUnivariateFunction implements UnivariateFunction{
        public double logEvaluate(double argument){
            return Math.log(evaluate(argument));
        }
    }
}
