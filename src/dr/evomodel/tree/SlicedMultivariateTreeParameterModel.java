package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeDoubleTraitProvider;
import dr.inference.model.Parameter;
public class SlicedMultivariateTreeParameterModel extends MultivariateTreeParameterModel implements TreeDoubleTraitProvider {
public SlicedMultivariateTreeParameterModel(TreeModel tree, Parameter parameter,
boolean includeRoot, boolean includeTips, int dim, int slice) {
super(tree, parameter, includeRoot, includeTips, dim);
this.slice = slice;
}
@Override
public double[] getTrait(Tree tree, NodeRef node) {
return getNodeValue(tree, node);
}
@Override
public String getTraitString(Tree tree, NodeRef node) {
return DA.formatTrait(getNodeValue(tree, node));
}
public double getNodeDoubleValue(Tree tree, NodeRef node) {
return getNodeValue(tree, node)[slice];
}
private final int slice;
}
