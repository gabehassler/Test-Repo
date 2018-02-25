package dr.evomodel.branchratemodel;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.branchratemodel.ScaledTreeLengthRateModelParser;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class ScaledTreeLengthRateModel extends AbstractBranchRateModel {
private Parameter totalLength;
protected Tree treeModel;
private double storedRateFactor;
private boolean currentFactorKnown;
private double rateFactor;
public ScaledTreeLengthRateModel(TreeModel treeModel, Parameter totalLength) {
super(ScaledTreeLengthRateModelParser.MODEL_NAME);
this.totalLength = totalLength;
this.treeModel = treeModel;
currentFactorKnown = false;
addModel(treeModel);
addVariable(totalLength);
}
public double getBranchRate(final Tree tree, final NodeRef node) {
assert(tree == treeModel);
if (!currentFactorKnown) {
updateCurrentLength();
}
return rateFactor;
}
public double getTotalLength() {
return totalLength.getParameterValue(0);
}
protected void updateCurrentLength() {
double currentLength = 0;
NodeRef root = treeModel.getRoot();
for (int i = 0; i < treeModel.getNodeCount(); ++i) {
NodeRef node = treeModel.getNode(i);
if (node != root) {
currentLength += treeModel.getBranchLength(node);
}
}
rateFactor = totalLength.getParameterValue(0) / currentLength;
currentFactorKnown = true;
}
protected void handleModelChangedEvent(Model model, Object object, int index) {
if (model == treeModel) {
currentFactorKnown = false;
}
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
if (variable == totalLength) {
currentFactorKnown = false;
}
}
protected void storeState() {
storedRateFactor = rateFactor;
}
protected void restoreState() {
rateFactor = storedRateFactor;
}
protected void acceptState() {
}
}
