package dr.evomodel.tree;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeShape;
import dr.inference.model.Statistic;
public class TreeShapeStatistic extends Statistic.Abstract implements TreeStatistic {
    public TreeShapeStatistic(String name, TreeModel target) {
        super(name);
        this.target = target;
        ultrametric = Tree.Utils.isUltrametric(target);
    }
    public void setTree(Tree tree) {
        this.target = tree;
    }
    public Tree getTree() {
        return target;
    }
    public int getDimension() {
        if (ultrametric) return 5;
        return 4;
    }
    public String getDimensionName(int dim) {
        switch (dim) {
            case 0:
                return "N-bar";
            case 1:
                return "N-bar-var";
            case 2:
                return "C";
            case 3:
                return "B1";
            case 4:
                return "gamma";
        }
        throw new IllegalArgumentException("Dimension doesn't exist!");
    }
    public double getStatisticValue(int dim) {
        switch (dim) {
            case 0:
                return TreeShape.getNBarStatistic(target);
            case 1:
                return TreeShape.getVarNBarStatistic(target);
            case 2:
                return TreeShape.getCStatistic(target);
            case 3:
                return TreeShape.getB1Statistic(target);
            case 4:
                return TreeShape.getGammaStatistic(target);
        }
        throw new IllegalArgumentException("Dimension doesn't exist!");
    }
    private Tree target = null;
    private boolean ultrametric = false;
}
