package dr.evolution.tree;
import dr.inference.model.Model;
public interface MultivariateTraitTree extends MutableTree, Model {
    public double[] getMultivariateNodeTrait(NodeRef node, String name);
    public void setMultivariateTrait(NodeRef n, String name, double[] value);
}
