package dr.evomodel.tree;
import dr.evolution.tree.Tree;
import dr.inference.model.Parameter;
import dr.inference.model.Statistic;
import java.util.Arrays;
public class NodeHeightsStatistic extends Statistic.Abstract implements TreeStatistic {
    public NodeHeightsStatistic(String name, Tree tree) {
        this(name, tree, null);
    }
    public NodeHeightsStatistic(String name, Tree tree, Parameter groupSizes) {
        super(name);
        this.tree = tree;
        this.groupSizes = groupSizes;
    }
    public void setTree(Tree tree) {
        this.tree = tree;
    }
    public Tree getTree() {
        return tree;
    }
    public int getDimension() {
        if (groupSizes != null) {
            return groupSizes.getDimension();
        }
        return tree.getInternalNodeCount();
    }
    public double getStatisticValue(int dim) {
        if (dim == 0) {
            // This assumes that each dimension will be called in turn, so
            // the call for dim 0 updates the array.
            calculateHeights();
        }
        return heights[dim];
    }
    private void calculateHeights() {
        heights = new double[tree.getInternalNodeCount()];
        for (int i = 0; i < heights.length; i++) {
            heights[i] = tree.getNodeHeight(tree.getInternalNode(i));
        }
        Arrays.sort(heights);
        if (groupSizes != null) {
            double[] allHeights = heights;
            heights = new double[groupSizes.getDimension()];
            int k = 0;
            for (int i = 0; i < groupSizes.getDimension(); i++) {
                k += groupSizes.getValue(i);
                heights[i] = allHeights[k - 1];
            }
        }
    }
    private Tree tree = null;
    private Parameter groupSizes = null;
    private double[] heights = null;
}