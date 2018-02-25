package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class SingleScalarTreeTransform extends TreeTransform {
public SingleScalarTreeTransform(Parameter scale) {
super("singleScalarTreeTransform");
scale.addBounds(new Parameter.DefaultBounds(1.0, 0.0, 1));
this.scale = scale;
addVariable(scale);
}
public double transform(Tree tree, NodeRef node, double originalHeight) {
if (tree.isExternal(node)) {
return originalHeight;
}
final double rootHeight = tree.getNodeHeight(tree.getRoot());
return rootHeight - getScaleForNode(tree, node) * (rootHeight - originalHeight);
}
protected double getScaleForNode(Tree tree, NodeRef node) {
return scale.getParameterValue(0);
}
public String getInfo() {
return "Linear transform by " + scale.getId();
}
protected void handleVariableChangedEvent(Variable variable, int index, Variable.ChangeType type) {
fireModelChanged(scale);
}
private final Parameter scale;
}
