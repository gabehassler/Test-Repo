package dr.app.treestat.statistics;
import dr.evolution.tree.Tree;
public class TreeHeight extends AbstractTreeSummaryStatistic {
	private TreeHeight() { }
	public double[] getSummaryStatistic(Tree tree) {
		return new double[] { tree.getNodeHeight(tree.getRoot()) };
	}
	public String getSummaryStatisticName() { return FACTORY.getSummaryStatisticName(); }
	public String getSummaryStatisticDescription() { return FACTORY.getSummaryStatisticDescription(); }
	public String getSummaryStatisticReference() { return FACTORY.getSummaryStatisticReference(); }
	public boolean allowsPolytomies() { return FACTORY.allowsPolytomies(); }
	public boolean allowsNonultrametricTrees() { return FACTORY.allowsNonultrametricTrees(); }
	public boolean allowsUnrootedTrees() { return FACTORY.allowsUnrootedTrees(); }
	public Category getCategory() { return FACTORY.getCategory(); }
	public static final Factory FACTORY = new Factory() {
		public TreeSummaryStatistic createStatistic() {
			return new TreeHeight();
		}
		public String getSummaryStatisticName() {
			return "Tree Height";
		}
		public String getSummaryStatisticDescription() {
			return "The height of the root of the tree.";
		}
		public String getSummaryStatisticReference() {
			return "-";
		}
		public boolean allowsPolytomies() { return true; }
		public boolean allowsNonultrametricTrees() { return true; }
		public boolean allowsUnrootedTrees() { return false; }
		public Category getCategory() { return Category.GENERAL; }
	};
}
