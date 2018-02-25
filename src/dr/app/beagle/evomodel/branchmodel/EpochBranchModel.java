package dr.app.beagle.evomodel.branchmodel;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.util.Author;
import dr.util.Citable;
import dr.util.Citation;
import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("serial")
public class EpochBranchModel extends AbstractModel implements BranchModel, Citable {
public static final String EPOCH_BRANCH_MODEL = "EpochBranchModel";
public EpochBranchModel(TreeModel tree,
List<SubstitutionModel> substitutionModels,
Parameter epochTimes) {
super(EPOCH_BRANCH_MODEL);
this.substitutionModels = substitutionModels;
if (substitutionModels == null || substitutionModels.size() == 0) {
throw new IllegalArgumentException("EpochBranchModel must be provided with at least one substitution model");
}
this.epochTimes = epochTimes;
this.tree = tree;
for (SubstitutionModel model : substitutionModels) {
addModel(model);
}
addModel(tree);
addVariable(epochTimes);
}// END: Constructor
@Override
public Mapping getBranchModelMapping(NodeRef node) {
int nModels = substitutionModels.size();
int epochCount = nModels - 1;
double[] transitionTimes = epochTimes.getParameterValues();
double parentHeight = tree.getNodeHeight(tree.getParent(node));
double nodeHeight = tree.getNodeHeight(node);
List<Double> weightList = new ArrayList<Double>();
List<Integer> orderList = new ArrayList<Integer>();
// find the epoch that the node height is in...
int epoch = 0;
while (epoch < epochCount && nodeHeight >= transitionTimes[epoch]) {
epoch ++;
}
double currentHeight = nodeHeight;
// find the epoch that the parent height is in...
while (epoch < epochCount && parentHeight >= transitionTimes[epoch]) {
weightList.add( transitionTimes[epoch] - currentHeight );
orderList.add(epoch);
currentHeight = transitionTimes[epoch];
epoch ++;
}
weightList.add( parentHeight - currentHeight );
orderList.add(epoch);
if (orderList.size() == 0) {
throw new RuntimeException("EpochBranchModel failed to give a valid mapping");
}
final int[] order = new int[orderList.size()];
final double[] weights = new double[weightList.size()];
for (int i = 0; i < orderList.size(); i++) {
order[i] = orderList.get(i);
weights[i] = weightList.get(i);
}
return new Mapping() {
@Override
public int[] getOrder() {
return order;
}
@Override
public double[] getWeights() {
return weights;
}
};
}// END: getBranchModelMapping
@Override
public boolean requiresMatrixConvolution() {
return true;
}
@Override
public List<SubstitutionModel> getSubstitutionModels() {
return substitutionModels;
}
@Override
public SubstitutionModel getRootSubstitutionModel() {
return substitutionModels.get(substitutionModels.size() - 1);
}
public FrequencyModel getRootFrequencyModel() {
return getRootSubstitutionModel().getFrequencyModel();
}
protected void handleModelChangedEvent(Model model, Object object, int index) {
fireModelChanged();
}// END: handleModelChangedEvent
@SuppressWarnings("rawtypes")
protected void handleVariableChangedEvent(Variable variable, int index,
Parameter.ChangeType type) {
}// END: handleVariableChangedEvent
protected void storeState() {
}// END: storeState
protected void restoreState() {
}// END: restoreState
protected void acceptState() {
}// END: acceptState
public List<Citation> getCitations() {
List<Citation> citations = new ArrayList<Citation>();
citations.add(new Citation(new Author[]{new Author("F", "Bielejec"),
new Author("P", "Lemey"), new Author("G", "Baele"), new Author("A", "Rambaut"),
new Author("MA", "Suchard")}, Citation.Status.IN_PREPARATION));
return citations;
}// END: getCitations
private final TreeModel tree;
private final List<SubstitutionModel> substitutionModels;
private final Parameter epochTimes;
}// END: class
