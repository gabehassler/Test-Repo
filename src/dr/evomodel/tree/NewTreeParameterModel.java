
package dr.evomodel.tree;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeDoubleTraitProvider;
import dr.inference.model.Parameter;

public class NewTreeParameterModel extends AbstractTreeParameterModel<Double> implements TreeDoubleTraitProvider {

    public NewTreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot, boolean includeTips) {
        super(tree, parameter, includeRoot, includeTips);
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
        return treeSize;
    }

    public double getNodeDoubleValue(Tree tree, NodeRef node) {
        return getNodeValue(tree, node);
    }

    @Override
    public Double getNodeValue(Tree tree, NodeRef node) {

        assert checkNode(tree, node);

        int nodeNumber = node.getNumber();
        int index = getParameterIndexFromNodeNumber(nodeNumber);
        return parameter.getParameterValue(index);
    }

    @Override
    public void setNodeValue(Tree tree, NodeRef node, Double value) {

        assert checkNode(tree, node);

        int nodeNumber = node.getNumber();
        int index = getParameterIndexFromNodeNumber(nodeNumber);
        parameter.setParameterValue(index, value);
    }

    @Override
    public Double getTrait(Tree tree, NodeRef node) {
        return getNodeValue(tree, node);
    }

    @Override
    public String getTraitString(Tree tree, NodeRef node) {
        return Double.toString(getNodeValue(tree, node));
    }
}
