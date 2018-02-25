package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class TreenessStatistic extends AbstractTreeSummaryStatistic {
private TreenessStatistic() { }
public double[] getSummaryStatistic(Tree tree) {
double externalLength = 0.0;
double internalLength = 0.0;
int externalNodeCount = tree.getExternalNodeCount();
for (int i = 0; i < externalNodeCount; i++) {
NodeRef node = tree.getExternalNode(i);
NodeRef parent = tree.getParent(node);
externalLength += tree.getNodeHeight(parent) - tree.getNodeHeight(node);
}
int internalNodeCount = tree.getInternalNodeCount();
for (int i = 0; i < internalNodeCount; i++) {
NodeRef node = tree.getInternalNode(i);
if (!tree.isRoot(node)) {
NodeRef parent = tree.getParent(node);
internalLength += tree.getNodeHeight(parent) - tree.getNodeHeight(node);
}
}
return new double[] { internalLength/(internalLength+externalLength) };
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
return new TreenessStatistic();
}
public String getSummaryStatisticName() {
return "Treeness";
}
public String getSummaryStatisticDescription() {
return "The proportion of the total length of the tree that is taken up by internal branches. " +
"Interpreted as a signal/(signal+noise) measure for phylogenetic reconstruction.";
}
public String getSummaryStatisticReference() {
return "see Phillips & Penny (2001)";
}
public boolean allowsPolytomies() { return true; }
public boolean allowsNonultrametricTrees() { return true; }
public boolean allowsUnrootedTrees() { return true; }
public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.PHYLOGENETIC; }
};
}
