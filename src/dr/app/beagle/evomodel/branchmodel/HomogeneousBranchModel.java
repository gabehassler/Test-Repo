package dr.app.beagle.evomodel.branchmodel;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.tree.NodeRef;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Variable;
import java.util.ArrayList;
import java.util.List;
public class HomogeneousBranchModel extends AbstractModel implements BranchModel {
private final SubstitutionModel substitutionModel;
private final FrequencyModel rootFrequencyModel;
public HomogeneousBranchModel(SubstitutionModel substitutionModel) {
this(substitutionModel, null);
}
public HomogeneousBranchModel(SubstitutionModel substitutionModel, FrequencyModel rootFrequencyModel) {
super("HomogeneousBranchModel");
this.substitutionModel = substitutionModel;
addModel(substitutionModel);
if (rootFrequencyModel != null) {
addModel(rootFrequencyModel);
this.rootFrequencyModel = rootFrequencyModel;
} else {
this.rootFrequencyModel = substitutionModel.getFrequencyModel();
}
}
public Mapping getBranchModelMapping(NodeRef node) {
return DEFAULT;
}
//    @Override // use java 1.5
public List<SubstitutionModel> getSubstitutionModels() {
List<SubstitutionModel> substitutionModels = new ArrayList<SubstitutionModel>();
substitutionModels.add(substitutionModel);
return substitutionModels;
}
//    @Override
public SubstitutionModel getRootSubstitutionModel() {
return substitutionModel;
}
public FrequencyModel getRootFrequencyModel() {
return rootFrequencyModel;
}
//    @Override
public boolean requiresMatrixConvolution() {
return false;
}
@Override
protected void handleModelChangedEvent(Model model, Object object, int index) {
fireModelChanged();
}
@Override
protected void handleVariableChangedEvent(Variable variable, int index, Variable.ChangeType type) {
}
@Override
protected void storeState() {
}
@Override
protected void restoreState() {
}
@Override
protected void acceptState() {
}
}
