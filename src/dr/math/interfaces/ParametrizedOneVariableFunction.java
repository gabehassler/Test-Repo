package dr.math.interfaces;
public interface ParametrizedOneVariableFunction
										extends OneVariableFunction
{
double[] parameters();
void setParameters( double[] p);
double[] valueAndGradient( double x);
}