package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class CollessIndex extends AbstractTreeSummaryStatistic {
public CollessIndex() { }
public double[] getSummaryStatistic(Tree tree) {
double C = 0.0;
int n = tree.getInternalNodeCount();
for (int i =0; i < n; i++) {
NodeRef node = tree.getInternalNode(i);
int r = Tree.Utils.getLeafCount(tree, tree.getChild(node, 0));
int s = Tree.Utils.getLeafCount(tree, tree.getChild(node, 1));
C += Math.abs(r-s);
}
n = tree.getExternalNodeCount();
C *= 2.0 / (n * (n - 3) + 2);
return new double[] { C };
}
public String getSummaryStatisticName() { return FACTORY.getSummaryStatisticName(); }
public String getSummaryStatisticDescription() { return FACTORY.getSummaryStatisticDescription(); }
public String getSummaryStatisticReference() { return FACTORY.getSummaryStatisticReference(); }
public boolean allowsPolytomies() { return FACTORY.allowsPolytomies(); }
public boolean allowsNonultrametricTrees() { return FACTORY.allowsNonultrametricTrees(); }
public boolean allowsUnrootedTrees() { return FACTORY.allowsUnrootedTrees(); }
public SummaryStatisticDescription.Category getCategory() { return FACTORY.getCategory(); }
public static final TreeSummaryStatistic.Factory FACTORY = new TreeSummaryStatistic.Factory() {
public TreeSummaryStatistic createStatistic() {
return new CollessIndex();
}
public String getSummaryStatisticName() {
return "Colless tree-imbalance";
}
public String getSummaryStatisticDescription() {
return "The normalized sum of differences of number of children " +
"in left and right subtrees over all internal nodes";
}
public String getSummaryStatisticReference() {
return "Colless (1982)";
}
public boolean allowsPolytomies() { return false; }
public boolean allowsNonultrametricTrees() { return true; }
public boolean allowsUnrootedTrees() { return false; }
public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.TREE_SHAPE; }
};
}