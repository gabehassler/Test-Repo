package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.inference.model.Statistic;
import java.util.Set;
public class MRCATraitStatistic extends Statistic.Abstract implements TreeStatistic {
    public MRCATraitStatistic(String name, String trait, TreeModel tree, TaxonList taxa) throws Tree.MissingTaxonException {
        super(name);
        this.tree = tree;
        this.trait = trait;
        this.leafSet = Tree.Utils.getLeavesForTaxa(tree, taxa);
        this.isRate = trait.equals("rate");
    }
    public void setTree(Tree tree) {
        this.tree = (TreeModel) tree;
    }
    public Tree getTree() {
        return tree;
    }
    public int getDimension() {
        return 1;
    }
    public double getStatisticValue(int dim) {
        NodeRef node = Tree.Utils.getCommonAncestorNode(tree, leafSet);
        if (node == null) throw new RuntimeException("No node found that is MRCA of " + leafSet);
        if (isRate) {
            return tree.getNodeRate(node);
        }
        return tree.getNodeTrait(node, trait);
    }
    private TreeModel tree = null;
    private Set<String> leafSet = null;
    private String trait;
    private boolean isRate;
}