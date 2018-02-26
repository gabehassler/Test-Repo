
package dr.evomodel.tree;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.inference.model.Parameter;

public class MultivariateTreeParameterModel extends AbstractTreeParameterModel<double[]> {

    public MultivariateTreeParameterModel(TreeModel tree, Parameter parameter,
                                          boolean includeRoot, boolean includeTips, int dim) {
        super(tree, parameter, includeRoot, includeTips);
        this.dim = dim;
    }

    @Override
    public int getParameterSize() {
        int treeSize = tree.getNodeCount();
        if (!doesIncludeRoot()) {
            treeSize -= 1;
        }
        if (!doesIncludeTips()) {
            treeSize -= tree.getExternalNodeCount();
        }
        return treeSize * dim;
    }

    @Override
    public double[] getNodeValue(Tree tree, NodeRef node) {

        assert checkNode(tree, node);

        int nodeNumber = node.getNumber();
        int index = getParameterIndexFromNodeNumber(nodeNumber);

        double[] result = new double[dim];
        for (int i = 0; i < dim; ++i) {
            result[i] = parameter.getParameterValue(index * dim + i);
        }
        return result;
    }

    @Override
    public void setNodeValue(Tree tree, NodeRef node, double[] value) {

        assert checkNode(tree, node);

        int nodeNumber = node.getNumber();
        int index = getParameterIndexFromNodeNumber(nodeNumber);

        for (int i = 0; i < dim; ++i) {
            parameter.setParameterValue(index * dim + i, value[i]); // TODO Fire change event once
        }
    }

    @Override
    public double[] getTrait(Tree tree, NodeRef node) {
        return getNodeValue(tree, node);
    }

    @Override
    public String getTraitString(Tree tree, NodeRef node) {
        return TreeTrait.DA.formatTrait(getNodeValue(tree, node));
    }

    private final int dim;
}
