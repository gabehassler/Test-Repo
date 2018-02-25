package dr.inference.distribution;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inference.loggers.Logger;
import dr.inferencexml.distribution.LogNormalDistributionModelParser;
import dr.math.UnivariateFunction;
import dr.math.distributions.NormalDistribution;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class LogNormalDistributionModel extends AbstractModel implements ParametricDistributionModel {
//if mean is not in real space then exponentiate to get value in the lognormal space
boolean isMeanInRealSpace;
boolean isStdevInRealSpace;
boolean usesStDev = true;
public LogNormalDistributionModel(Parameter meanParameter, Parameter stdevParameter, double offset, boolean meanInRealSpace, boolean stdevInRealSpace) {
super(LogNormalDistributionModelParser.LOGNORMAL_DISTRIBUTION_MODEL);
isMeanInRealSpace = meanInRealSpace;
isStdevInRealSpace = stdevInRealSpace;
this.meanParameter = meanParameter;
this.scaleParameter = stdevParameter;
this.offset = offset;
addVariable(meanParameter);
if (isMeanInRealSpace) {
meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
} else {
meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
}
addVariable(stdevParameter);
stdevParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
}
public LogNormalDistributionModel(Parameter meanParameter, Parameter scaleParameter,
double offset, boolean meanInRealSpace, boolean stdevInRealSpace, boolean usesStDev) {
super(LogNormalDistributionModelParser.LOGNORMAL_DISTRIBUTION_MODEL);
isMeanInRealSpace = meanInRealSpace;
isStdevInRealSpace = stdevInRealSpace;
this.usesStDev = usesStDev;
this.meanParameter = meanParameter;
this.scaleParameter = scaleParameter;
this.offset = offset;
addVariable(meanParameter);
if (isMeanInRealSpace) {
meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
} else {
meanParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
}
addVariable(this.scaleParameter);
this.scaleParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
}
public final double getS() {
//System.out.println(isStdevInRealSpace+"\t" + isMeanInRealSpace + "\t" + Math.sqrt(Math.log(1 + scaleParameter.getParameterValue(0)/Math.pow(meanParameter.getParameterValue(0), 2))) + "\t" + scaleParameter.getParameterValue(0));
if(isStdevInRealSpace) {
if(isMeanInRealSpace) {
return Math.sqrt(Math.log(1 + scaleParameter.getParameterValue(0)/Math.pow(meanParameter.getParameterValue(0), 2)));
}
else {
throw new RuntimeException("S can not be computed with M and stdev");
}
}
return scaleParameter.getParameterValue(0);
}
public final void setS(double S) {
scaleParameter.setParameterValue(0, S);
}
public final Parameter getSParameter() {
return scaleParameter;
}
private double getStDev() {
return usesStDev ? getS() : Math.sqrt(1.0 / getS());
}
public final double getM() {
if (isMeanInRealSpace) {
double stDev = getStDev();
return Math.log(meanParameter.getParameterValue(0)) - (0.5 * stDev * stDev);
} else {
return meanParameter.getParameterValue(0);
}
}
public final void setM(double M) {
if (isMeanInRealSpace) {
double stDev = getStDev();
meanParameter.setParameterValue(0, Math.exp(M + (0.5 * stDev * stDev)));
} else {
meanParameter.setParameterValue(0, M);
}
}
public final Parameter getMeanParameter() {
return meanParameter;
}
public Parameter getPrecisionParameter() {
if (!usesStDev)
return scaleParameter;
return null;
}
// *****************************************************************
// Interface Distribution
// *****************************************************************
public double pdf(double x) {
if (x - offset <= 0.0) return 0.0;
return NormalDistribution.pdf(Math.log(x - offset), getM(), getStDev()) / (x - offset);
}
public double logPdf(double x) {
if (x - offset <= 0.0) return Double.NEGATIVE_INFINITY;
return NormalDistribution.logPdf(Math.log(x - offset), getM(), getStDev()) - Math.log(x - offset);
}
public double cdf(double x) {
if (x - offset <= 0.0) return 0.0;
return NormalDistribution.cdf(Math.log(x - offset), getM(), getStDev());
}
public double quantile(double y) {
return Math.exp(NormalDistribution.quantile(y, getM(), getStDev())) + offset;
}
public double mean() {
return Math.exp(getM() + (getStDev() * getStDev() / 2)) + offset;
}
public double variance() {
if (usesStDev) {
//double stdev = getStDev();//scaleParameter.getParameterValue(0);
return getStDev() * getStDev();
}
return 1.0 / scaleParameter.getParameterValue(0);
}
public final UnivariateFunction getProbabilityDensityFunction() {
return pdfFunction;
}
private final UnivariateFunction pdfFunction = new UnivariateFunction() {
public final double evaluate(double x) {
return pdf(Math.log(x));
}
public final double getLowerBound() {
return Double.NEGATIVE_INFINITY;
}
public final double getUpperBound() {
return Double.POSITIVE_INFINITY;
}
};
// *****************************************************************
// Interface Model
// *****************************************************************
public void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
public void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
// no intermediates need to be recalculated...
}
protected void storeState() {
} // no additional state needs storing
protected void restoreState() {
} // no additional state needs restoring
protected void acceptState() {
} // no additional state needs accepting
// **************************************************************
// XMLElement IMPLEMENTATION
// **************************************************************
public Element createElement(Document document) {
throw new RuntimeException("Not implemented!");
}
// **************************************************************
// Private instance variables
// **************************************************************
private final Parameter meanParameter;
private final Parameter scaleParameter;
private final double offset;
}