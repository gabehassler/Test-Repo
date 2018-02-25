package dr.evomodel.branchratemodel;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodelxml.branchratemodel.RateEpochBranchRateModelParser;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class RateEpochBranchRateModel extends AbstractBranchRateModel {
protected final Parameter[] timeParameters;
protected final Parameter[] rateParameters;
public RateEpochBranchRateModel(Parameter[] timeParameters,
Parameter[] rateParameters) {
super(RateEpochBranchRateModelParser.RATE_EPOCH_BRANCH_RATES);
this.timeParameters = timeParameters;
for (Parameter parameter : timeParameters) {
addVariable(parameter);
}
this.rateParameters = rateParameters;
for (Parameter parameter : rateParameters) {
addVariable(parameter);
}
}
public void handleModelChangedEvent(Model model, Object object, int index) {
// nothing to do
}
protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
fireModelChanged();
}
protected void storeState() {
// nothing to do
}
protected void restoreState() {
// nothing to do
}
protected void acceptState() {
// nothing to do
}
public double getBranchRate(final Tree tree, final NodeRef node) {
NodeRef parent = tree.getParent(node);
if (parent != null) {
double height0 = tree.getNodeHeight(node);
double height1 = tree.getNodeHeight(parent);
int i = 0;
double rate = 0.0;
double lastHeight = height0;
// First find the epoch which contains the node height
while (i < timeParameters.length && height0 > timeParameters[i].getParameterValue(0)) {
i++;
}
// Now walk up the branch until we reach the last epoch or the height of the parent
while (i < timeParameters.length && height1 > timeParameters[i].getParameterValue(0)) {
// add the rate for that epoch multiplied by the time spent at that rate
rate += rateParameters[i].getParameterValue(0) * (timeParameters[i].getParameterValue(0) - lastHeight);
lastHeight = timeParameters[i].getParameterValue(0);
i++;
}
// Add that last rate segment
rate += rateParameters[i].getParameterValue(0) * (height1 - lastHeight);
// normalize the rate for the branch length
return normalizeRate(rate / (height1 - height0));
}
throw new IllegalArgumentException("root node doesn't have a rate!");
}
protected double normalizeRate(double rate) {
return rate;
}
}
