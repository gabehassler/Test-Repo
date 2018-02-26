package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class Nbar extends AbstractTreeSummaryStatistic {
	private Nbar() { }
	public double[] getSummaryStatistic(Tree tree) {
		double NBar = 0.0;
		for (int i =0; i < tree.getExternalNodeCount(); i++) {
			NodeRef node = tree.getExternalNode(i);
			while (!tree.isRoot(node)) {
				node = tree.getParent(node);
				NBar += 1.0;
			}
		}
		return new double[] { NBar / tree.getExternalNodeCount() };
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
			return new Nbar();
		}
		public String getSummaryStatisticName() {
			return "N_bar";
		}
		public String getSummaryStatisticDescription() {
			return "The mean number of nodes above an external node.";
		}
		public String getSummaryStatisticReference() {
			return "Kirkpatrick & Slatkin (1992)";
		}
		public boolean allowsPolytomies() { return true; }
		public boolean allowsNonultrametricTrees() { return true; }
		public boolean allowsUnrootedTrees() { return false; }
		public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.TREE_SHAPE; }
	};
}
