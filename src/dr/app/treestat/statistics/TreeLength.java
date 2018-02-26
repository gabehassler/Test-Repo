package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class TreeLength extends AbstractTreeSummaryStatistic {
	private TreeLength() { }
	public double[] getSummaryStatistic(Tree tree) {
		double externalLength = 0.0;
		double internalLength = 0.0;
		int externalNodeCount = tree.getExternalNodeCount();
		for (int i = 0; i < externalNodeCount; i++) {
			NodeRef node = tree.getExternalNode(i);
			externalLength += tree.getBranchLength(node);
		}
		int internalNodeCount = tree.getInternalNodeCount();
		for (int i = 0; i < internalNodeCount; i++) {
			NodeRef node = tree.getInternalNode(i);
			if (!tree.isRoot(node)) {
				internalLength += tree.getBranchLength(node);
			}
		}
		return new double[] { internalLength + externalLength };
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
			return new TreeLength();
		}
		public String getSummaryStatisticName() {
			return "Tree Length";
		}
		public String getSummaryStatisticDescription() {
			return "The sum of the branch lengths.";
		}
		public String getSummaryStatisticReference() {
			return "-";
		}
		public boolean allowsPolytomies() { return true; }
		public boolean allowsNonultrametricTrees() { return true; }
		public boolean allowsUnrootedTrees() { return true; }
		public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.GENERAL; }
	};
}
