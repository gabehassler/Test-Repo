package dr.math;
public interface MFWithGradient extends MultivariateFunction
{
double evaluate(double[] argument, double[] gradient);
void computeGradient(double[] argument, double[] gradient);
}
