package dr.inference.distribution;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inferencexml.distribution.UniformDistributionModelParser;
import dr.math.UnivariateFunction;
import dr.math.distributions.UniformDistribution;
public class UniformDistributionModel extends AbstractModel implements ParametricDistributionModel {
public UniformDistributionModel(Parameter lowerParameter, Parameter upperParameter) {
super(UniformDistributionModelParser.UNIFORM_DISTRIBUTION_MODEL);
this.lowerParameter = lowerParameter;
addVariable(lowerParameter);
lowerParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
this.upperParameter = upperParameter;
addVariable(upperParameter);
upperParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
}
public double getLower() {
return lowerParameter.getParameterValue(0);
}
public double getUpper() {
return upperParameter.getParameterValue(0);
}
// *****************************************************************
// Interface Distribution
// *****************************************************************
public double pdf(double x) {
return UniformDistribution.pdf(x, getLower(), getUpper());
}
public double logPdf(double x) {
return UniformDistribution.logPdf(x, getLower(), getUpper());
}
public double cdf(double x) {
return UniformDistribution.cdf(x, getLower(), getUpper());
}
public double quantile(double y) {
return UniformDistribution.quantile(y, getLower(), getUpper());
}
public double mean() {
return UniformDistribution.mean(getLower(), getUpper());
}
public double variance() {
return UniformDistribution.variance(getLower(), getUpper());
}
public final UnivariateFunction getProbabilityDensityFunction() {
return pdfFunction;
}
private final UnivariateFunction pdfFunction = new UnivariateFunction() {
public final double evaluate(double x) {
return 1.0;
}
public final double getLowerBound() {
return getLower();
}
public final double getUpperBound() {
return getUpper();
}
};
// *****************************************************************
// Interface Model
// *****************************************************************
public void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
// no intermediates need to be recalculated...
}
protected void storeState() {
} // no additional state needs storing
protected void restoreState() {
} // no additional state needs restoring
protected void acceptState() {
} // no additional state needs accepting
// **************************************************************
// Private instance variables
// **************************************************************
private final Parameter lowerParameter;
private final Parameter upperParameter;
}
