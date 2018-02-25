package dr.inference.distribution;
import dr.inference.model.*;
import dr.inferencexml.distribution.MultivariateNormalDistributionModelParser;
import dr.math.distributions.GaussianProcessRandomGenerator;
import dr.math.distributions.MultivariateNormalDistribution;
public class MultivariateNormalDistributionModel extends AbstractModel implements ParametricMultivariateDistributionModel, GaussianProcessRandomGenerator {
public MultivariateNormalDistributionModel(Parameter meanParameter, MatrixParameter precParameter) {
super(MultivariateNormalDistributionModelParser.NORMAL_DISTRIBUTION_MODEL);
this.mean = meanParameter;
addVariable(meanParameter);
meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
meanParameter.getDimension()));
this.precision = precParameter;
addVariable(precParameter);
distribution = createNewDistribution();
distributionKnown = true;
}
public MatrixParameter getPrecisionMatrixParameter() {
return precision;
}
public Parameter getMeanParameter() {
return mean;
}
// *****************************************************************
// Interface MultivariateDistribution
// *****************************************************************
private void checkDistribution() {
if (!distributionKnown) {
distribution = createNewDistribution();
distributionKnown = true;
}
}
public double logPdf(double[] x) {
checkDistribution();
return distribution.logPdf(x);
}
public double[][] getScaleMatrix() {
return precision.getParameterAsMatrix();
}
public double[] getMean() {
return mean.getParameterValues();
}
public String getType() {
return distribution.getType();
}
// *****************************************************************
// Interface Model
// *****************************************************************
public void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
public Likelihood getLikelihood() {
return null;
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
distributionKnown = false;
}
protected void storeState() {
storedDistribution = distribution;
storedDistributionKnown = distributionKnown;
}
protected void restoreState() {
distributionKnown = storedDistributionKnown;
distribution = storedDistribution;
}
protected void acceptState() {
} // no additional state needs accepting
// **************************************************************
// Private instance variables and functions
// **************************************************************
private MultivariateNormalDistribution createNewDistribution() {
return new MultivariateNormalDistribution(getMean(), getScaleMatrix());
}
private final Parameter mean;
private final MatrixParameter precision;
private MultivariateNormalDistribution distribution;
private MultivariateNormalDistribution storedDistribution;
private boolean distributionKnown;
private boolean storedDistributionKnown;
// RandomGenerator interface
public double[] nextRandom() {
checkDistribution();
return distribution.nextMultivariateNormal();
}
public double logPdf(Object x) {
checkDistribution();
return distribution.logPdf(x);
}
}
