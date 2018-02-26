package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.inference.model.Statistic;
public class TreeLengthStatistic extends Statistic.Abstract implements TreeStatistic {
    public TreeLengthStatistic(String name, Tree tree) {
        super(name);
        this.tree = tree;
    }
    public void setTree(Tree tree) {
        this.tree = tree;
    }
    public Tree getTree() {
        return tree;
    }
    public int getDimension() {
        return 1;
    }
    public double getStatisticValue(int dim) {
        double treeLength = 0.0;
        for (int i = 0; i < tree.getNodeCount(); i++) {
            NodeRef node = tree.getNode(i);
            if (node != tree.getRoot()) {
                NodeRef parent = tree.getParent(node);
                treeLength += tree.getNodeHeight(parent) - tree.getNodeHeight(node);
            }
        }
        return treeLength;
    }
    private Tree tree = null;
}
