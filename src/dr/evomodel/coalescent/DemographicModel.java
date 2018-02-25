package dr.evomodel.coalescent;
import dr.evolution.coalescent.DemographicFunction;
import dr.evolution.util.Units;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public abstract class DemographicModel extends AbstractModel implements Units {
public DemographicModel(String name) {
super(name);
}
// general functions
public abstract DemographicFunction getDemographicFunction();
// **************************************************************
// Model IMPLEMENTATION
// **************************************************************
protected void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
// no intermediates need to be recalculated...
}
protected void storeState() {
} // no additional state needs storing
protected void restoreState() {
} // no additional state needs restoring
protected void acceptState() {
} // no additional state needs accepting
// **************************************************************
// Units IMPLEMENTATION
// **************************************************************
private Type units;
public void setUnits(Type u) {
units = u;
}
public Type getUnits() {
return units;
}
}