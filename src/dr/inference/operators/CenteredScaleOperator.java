package dr.inference.operators;
import dr.inference.model.Parameter;
import dr.math.MathUtils;
public class CenteredScaleOperator extends AbstractCoercableOperator {
public CenteredScaleOperator(Parameter parameter) {
super(CoercionMode.DEFAULT);
this.parameter = parameter;
}
public CenteredScaleOperator(Parameter parameter, double scale, int weight, CoercionMode mode) {
super(mode);
this.parameter = parameter;
this.scaleFactor = scale;
setWeight(weight);
}
public Parameter getParameter() {
return parameter;
}
public final double doOperation() throws OperatorFailedException {
double total = 0.0;
for (int i = 0; i < parameter.getDimension(); i++) {
total += parameter.getParameterValue(i);
}
double mean = total / parameter.getDimension();
double scaleFactor = getRandomScaleFactor();
double logq = parameter.getDimension() * Math.log(1.0 / scaleFactor);
for (int i = 0; i < parameter.getDimension(); i++) {
double newScalar = (parameter.getParameterValue(i) - mean) * scaleFactor + mean;
if (newScalar < parameter.getBounds().getLowerLimit(i) || newScalar > parameter.getBounds().getUpperLimit(i)) {
throw new OperatorFailedException("Proposed value out of bounds");
}
parameter.setParameterValue(i, newScalar);
}
// non-symmetrical move
return logq;
}
public final double getRandomScaleFactor() {
return scaleFactor + (MathUtils.nextDouble() * ((1 / scaleFactor) - scaleFactor));
}
// Interface MCMCOperator
public final String getOperatorName() {
return parameter.getParameterName();
}
public double getCoercableParameter() {
return Math.log(1.0 / scaleFactor - 1.0);
}
public void setCoercableParameter(double value) {
scaleFactor = 1.0 / (Math.exp(value) + 1.0);
}
public double getRawParameter() {
return scaleFactor;
}
public double getTargetAcceptanceProbability() {
return 0.234;
}
public final String getPerformanceSuggestion() {
double prob = MCMCOperator.Utils.getAcceptanceProbability(this);
double targetProb = getTargetAcceptanceProbability();
dr.util.NumberFormatter formatter = new dr.util.NumberFormatter(5);
double sf = OperatorUtils.optimizeScaleFactor(scaleFactor, prob, targetProb);
if (prob < getMinimumGoodAcceptanceLevel()) {
return "Try setting scaleFactor to about " + formatter.format(sf);
} else if (prob > getMaximumGoodAcceptanceLevel()) {
return "Try setting scaleFactor to about " + formatter.format(sf);
} else return "";
}
public String toString() {
return getOperatorName() + "(scaleFactor=" + scaleFactor + ")";
}
// Private instance variables
private Parameter parameter = null;
public double scaleFactor = 0.5;
}
