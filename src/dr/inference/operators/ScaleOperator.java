package dr.inference.operators;
import dr.inference.model.Bounds;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.inferencexml.operators.ScaleOperatorParser;
import dr.math.MathUtils;
import java.util.logging.Logger;
public class ScaleOperator extends AbstractCoercableOperator {
private Parameter indicator;
private double indicatorOnProb;
public ScaleOperator(Variable variable, double scale) {
this(variable, scale, CoercionMode.COERCION_ON, 1.0);
}
public ScaleOperator(Variable<Double> variable, double scale, CoercionMode mode, double weight) {
this(variable, false, 0, scale, mode, null, 1.0, false);
setWeight(weight);
}
public ScaleOperator(Variable<Double> variable, boolean scaleAll, int degreesOfFreedom, double scale,
CoercionMode mode, Parameter indicator, double indicatorOnProb, boolean scaleAllInd) {
super(mode);
this.variable = variable;
this.indicator = indicator;
this.indicatorOnProb = indicatorOnProb;
this.scaleAll = scaleAll;
this.scaleAllIndependently = scaleAllInd;
this.scaleFactor = scale;
this.degreesOfFreedom = degreesOfFreedom;
}
public Variable getVariable() {
return variable;
}
public final double doOperation() throws OperatorFailedException {
final double scale = (scaleFactor + (MathUtils.nextDouble() * ((1.0 / scaleFactor) - scaleFactor)));
double logq;
final Bounds<Double> bounds = variable.getBounds();
final int dim = variable.getSize();
if (scaleAllIndependently) {
// update all dimensions independently.
logq = 0;
for (int i = 0; i < dim; i++) {
final double scaleOne = (scaleFactor + (MathUtils.nextDouble() * ((1.0 / scaleFactor) - scaleFactor)));
final double value = scaleOne * variable.getValue(i);
logq -= Math.log(scaleOne);
if (value < bounds.getLowerLimit(i) || value > bounds.getUpperLimit(i)) {
throw new OperatorFailedException("proposed value outside boundaries");
}
variable.setValue(i, value);
}
} else if (scaleAll) {
// update all dimensions
// hasting ratio is dim-2 times of 1dim case. would be nice to have a reference here
// for the proof. It is supposed to be somewhere in an Alexei/Nicholes article.
if (degreesOfFreedom > 0)
// For parameters with non-uniform prior on only one dimension
logq = -degreesOfFreedom * Math.log(scale);
else
logq = (dim - 2) * Math.log(scale);
// Must first set all parameters first and check for boundaries later for the operator to work
// correctly with dependent parameters such as tree node heights.
for (int i = 0; i < dim; i++) {
variable.setValue(i, variable.getValue(i) * scale);
}
for (int i = 0; i < dim; i++) {
if (variable.getValue(i) < variable.getBounds().getLowerLimit(i) ||
variable.getValue(i) > variable.getBounds().getUpperLimit(i)) {
throw new OperatorFailedException("proposed value outside boundaries");
}
}
} else {
logq = -Math.log(scale);
// which bit to scale
int index;
if (indicator != null) {
final int idim = indicator.getDimension();
final boolean impliedOne = idim == (dim - 1);
// available bit locations
int[] loc = new int[idim + 1];
int nLoc = 0;
// choose active or non active ones?
final boolean takeOne = indicatorOnProb >= 1.0 || MathUtils.nextDouble() < indicatorOnProb;
if (impliedOne && takeOne) {
loc[nLoc] = 0;
++nLoc;
}
for (int i = 0; i < idim; i++) {
final double value = indicator.getStatisticValue(i);
if (takeOne == (value > 0)) {
loc[nLoc] = i + (impliedOne ? 1 : 0);
++nLoc;
}
}
if (nLoc > 0) {
final int rand = MathUtils.nextInt(nLoc);
index = loc[rand];
} else {
throw new OperatorFailedException("no active indicators");
}
} else {
// any is good
index = MathUtils.nextInt(dim);
}
final double oldValue = variable.getValue(index);
if (oldValue == 0) {
Logger.getLogger("dr.inference").warning("The " + ScaleOperatorParser.SCALE_OPERATOR +
" for " +
variable.getVariableName()
+ " has failed since the parameter has a value of 0.0." +
"\nTo fix this problem, initalize the value of " +
variable.getVariableName() + " to be a positive real number"
);
throw new OperatorFailedException("");
}
final double newValue = scale * oldValue;
if (newValue < bounds.getLowerLimit(index) || newValue > bounds.getUpperLimit(index)) {
throw new OperatorFailedException("proposed value outside boundaries");
}
variable.setValue(index, newValue);
// provides a hook for subclasses
cleanupOperation(newValue, oldValue);
}
return logq;
}
void cleanupOperation(double newValue, double oldValue) {
// DO NOTHING
}
//MCMCOperator INTERFACE
public final String getOperatorName() {
return "scale(" + variable.getVariableName() + ")";
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
public double getScaleFactor() {
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
return ScaleOperatorParser.SCALE_OPERATOR + "(" + variable.getVariableName() + " [" + scaleFactor + ", " + (1.0 / scaleFactor) + "]";
}
//PRIVATE STUFF
private Variable<Double> variable = null;
private boolean scaleAll = false;
private boolean scaleAllIndependently = false;
private int degreesOfFreedom = 0;
private double scaleFactor = 0.5;
}
