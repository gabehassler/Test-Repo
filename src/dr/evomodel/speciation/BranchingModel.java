package dr.evomodel.speciation;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public abstract class BranchingModel extends AbstractModel {
public BranchingModel(String name) {
super(name); 
}
//
// functions that define a speciation model
//
public abstract double logNodeProbability(Tree tree, NodeRef node);
// **************************************************************
// Model IMPLEMENTATION
// **************************************************************
protected void handleModelChangedEvent(Model model, Object object, int index) {
// no intermediates need to be recalculated...
}
protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
// no intermediates need to be recalculated...
}
protected void storeState() {} // no additional state needs storing
protected void restoreState() {} // no additional state needs restoring	
protected void acceptState() {} // no additional state needs accepting	
// **************************************************************
// Units IMPLEMENTATION
// **************************************************************
public org.w3c.dom.Element createElement(org.w3c.dom.Document document) {
throw new RuntimeException("Not implemented!");
}
}