package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.inference.model.Parameter;
public class TrialTreeParameterModel extends TreeParameterModel {
//    public TrialTreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot, boolean includeTips) {
//        this(tree, parameter, includeRoot, includeTips, Intent.NODE);
//    }
    public TrialTreeParameterModel(TreeModel tree, Parameter parameter, boolean includeRoot, boolean includeTips,
                                   Intent intent) {
        super(tree, parameter, includeRoot, intent);
        this.includeTips = includeTips;
    }
    public int getParameterSize() {
        int treeSize = super.getParameterSize();
        if (!includeTips) {
            treeSize -= tree.getExternalNodeCount();
        }
        return treeSize;
    }
    public int getNodeNumberFromParameterIndex(int parameterIndex) {
        int number = super.getNodeNumberFromParameterIndex(parameterIndex);
        if (!includeTips) {
            number += tree.getExternalNodeCount();
        }
        return number;
    }
    public int getParameterIndexFromNodeNumber(int nodeNumber) {
        int number = super.getParameterIndexFromNodeNumber(nodeNumber);
        if (!includeTips) {
            number -= tree.getExternalNodeCount();
        }
        return number;
    }
    public double getNodeValue(Tree tree, NodeRef node) {
        assert (!tree.isExternal(node) && !includeTips) : "tip nodes do not have parameter values!";
        return super.getNodeValue(tree, node);
    }
    public void setNodeValue(Tree tree, NodeRef node, double value) {
        assert (!tree.isExternal(node) && !includeTips) : "tip nodes do not have parameter values!";
        super.setNodeValue(tree, node, value);
    }
    private boolean includeTips = true;
//    protected final Tree tree;
}
