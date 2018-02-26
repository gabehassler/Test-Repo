package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.inference.model.Statistic;
import dr.stats.DiscreteStatistics;
public class RateCovarianceStatistic extends Statistic.Abstract implements TreeStatistic {
    public RateCovarianceStatistic(String name, Tree tree, BranchRateModel branchRateModel) {
        super(name);
        this.tree = tree;
        this.branchRateModel = branchRateModel;
        int n = tree.getExternalNodeCount();
        childRate = new double[2 * n - 4];
        parentRate = new double[childRate.length];
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
        int n = tree.getNodeCount();
        int index = 0;
        for (int i = 0; i < n; i++) {
            NodeRef child = tree.getNode(i);
            NodeRef parent = tree.getParent(child);
            if (parent != null & !tree.isRoot(parent)) {
                childRate[index] = branchRateModel.getBranchRate(tree, child);
                parentRate[index] = branchRateModel.getBranchRate(tree, parent);
                index += 1;
            }
        }
        return DiscreteStatistics.covariance(childRate, parentRate);
    }
    private Tree tree = null;
    private BranchRateModel branchRateModel = null;
    private double[] childRate = null;
    private double[] parentRate = null;
}
