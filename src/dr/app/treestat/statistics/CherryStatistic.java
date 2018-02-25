package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class CherryStatistic extends AbstractTreeSummaryStatistic {
private CherryStatistic() { }
public double[] getSummaryStatistic(Tree tree) {
int cherryCount = 0;
int internalNodeCount = tree.getInternalNodeCount();
for (int i = 0; i < internalNodeCount; i++) {
NodeRef node = tree.getInternalNode(i);
boolean allChildrenExternal = true;
for (int j = 0; j < tree.getChildCount(node); j++) {
if (!tree.isExternal(tree.getChild(node, j))) {
allChildrenExternal = false;
}
}
if (allChildrenExternal) cherryCount += 1;
}
return new double[] { (double)cherryCount };
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
return new CherryStatistic();
}
public String getSummaryStatisticName() {
return "Cherry count";
}
public String getSummaryStatisticDescription() {
return "The number of internal nodes that have only tips as children.";
}
public String getSummaryStatisticReference() {
return "Steel and McKenzie (2001)";
}
public boolean allowsPolytomies() { return true; }
public boolean allowsNonultrametricTrees() { return true; }
public boolean allowsUnrootedTrees() { return false; }
public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.TREE_SHAPE; }
};
}
