package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.inference.model.Statistic;
import java.util.Set;
public class TMRCAStatistic extends Statistic.Abstract implements TreeStatistic {
    public TMRCAStatistic(String name, Tree tree, TaxonList taxa, boolean isRate, boolean forParent)
            throws Tree.MissingTaxonException {
        super(name);
        this.tree = tree;
        this.leafSet = Tree.Utils.getLeavesForTaxa(tree, taxa);
        this.isRate = isRate;
        this.forParent = forParent;
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
        NodeRef node = Tree.Utils.getCommonAncestorNode(tree, leafSet);
        if (forParent && !tree.isRoot(node))
            node = tree.getParent(node);       
        if (node == null) throw new RuntimeException("No node found that is MRCA of " + leafSet);
        if (isRate) {
            return tree.getNodeRate(node);
        }
        return tree.getNodeHeight(node);
    }
    private Tree tree = null;
    private Set<String> leafSet = null;
    private final boolean isRate;
    private final boolean forParent;
}
