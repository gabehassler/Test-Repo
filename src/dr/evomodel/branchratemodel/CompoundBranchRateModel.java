package dr.evomodel.branchratemodel;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodelxml.branchratemodel.CompoundBranchRateModelParser;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class CompoundBranchRateModel extends AbstractBranchRateModel {
private final List<BranchRateModel> branchRateModels = new ArrayList<BranchRateModel>();
public CompoundBranchRateModel(Collection<BranchRateModel> branchRateModels) {
super(CompoundBranchRateModelParser.COMPOUND_BRANCH_RATE_MODEL);
for (BranchRateModel branchRateModel : branchRateModels) {
addModel(branchRateModel);
this.branchRateModels.add(branchRateModel);
}
}
public void handleModelChangedEvent(Model model, Object object, int index) {
fireModelChanged();
}
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
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
double rate = 1.0;
for (BranchRateModel branchRateModel : branchRateModels) {
rate *= branchRateModel.getBranchRate(tree, node);
}
return rate;
}
}