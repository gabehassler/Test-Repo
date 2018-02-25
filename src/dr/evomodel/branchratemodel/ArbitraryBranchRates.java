package dr.evomodel.branchratemodel;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.evomodel.tree.TreeParameterModel;
import dr.evomodelxml.branchratemodel.ArbitraryBranchRatesParser;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class ArbitraryBranchRates extends AbstractBranchRateModel {
// The rates of each branch
final TreeParameterModel rates;
final Parameter rateParameter;
final boolean reciprocal;
final boolean exp;
public ArbitraryBranchRates(TreeModel tree, Parameter rateParameter, boolean reciprocal, boolean exp, boolean setRates) {
super(ArbitraryBranchRatesParser.ARBITRARY_BRANCH_RATES);
if (setRates) {
double value = exp ? 0.0 : 1.0;
for (int i = 0; i < rateParameter.getDimension(); i++) {
rateParameter.setValue(i, value);
}
}
//Force the boundaries of rate
if (!exp) {
Parameter.DefaultBounds bound = new Parameter.DefaultBounds(Double.MAX_VALUE, 0, rateParameter.getDimension());
rateParameter.addBounds(bound);
}
this.rates = new TreeParameterModel(tree, rateParameter, false);
this.rateParameter = rateParameter;
addModel(rates);
this.reciprocal = reciprocal;
this.exp = exp;
}
public void setBranchRate(Tree tree, NodeRef node, double value) {
rates.setNodeValue(tree, node, value);
}
public double getBranchRate(final Tree tree, final NodeRef node) {
// Branch rates are proportional to time.
// In the traitLikelihoods, time is proportional to variance
// Fernandez and Steel (2000) shows the sampling density with the scalar proportional to precision 
double rate = rates.getNodeValue(tree, node);
if (reciprocal) {
rate = 1.0 / rate;
}
if (exp) {
rate = Math.exp(rate);
}
return rate;
}
public boolean usingReciprocal() {
return reciprocal;
}
public void handleModelChangedEvent(Model model, Object object, int index) {
// Should be called by TreeParameterModel
fireModelChanged(object, index);
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
// Changes to rateParameter are handled by model changed events
}
protected void storeState() {
}
protected void restoreState() {
}
protected void acceptState() {
}
}
