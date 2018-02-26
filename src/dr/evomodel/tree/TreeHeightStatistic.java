package dr.evomodel.tree;

import dr.evolution.tree.Tree;
import dr.inference.model.Statistic;

public class TreeHeightStatistic extends Statistic.Abstract implements TreeStatistic {

    public TreeHeightStatistic(String name, Tree tree) {
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

        return tree.getNodeHeight(tree.getRoot());
    }

    private Tree tree = null;
}
