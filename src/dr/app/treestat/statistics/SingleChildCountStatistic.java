
package dr.app.treestat.statistics;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;

public class SingleChildCountStatistic extends AbstractTreeSummaryStatistic {

	private SingleChildCountStatistic() { }

	public double[] getSummaryStatistic(Tree tree) {

		int singleChildCount = 0;

		int internalNodeCount = tree.getInternalNodeCount();
		for (int i = 0; i < internalNodeCount; i++) {
			NodeRef node = tree.getInternalNode(i);
			if (tree.getChildCount(node) == 1) {
                singleChildCount += 1;
            }
		}
		return new double[] { (double)singleChildCount };
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
			return new SingleChildCountStatistic();
		}

		public String getSummaryStatisticName() {
			return "Single child count";
		}

		public String getSummaryStatisticDescription() {

			return "The number of internal nodes that have only a single child node.";
		}

		public String getSummaryStatisticReference() {
			return "Drummond unpublished";
		}

		public boolean allowsPolytomies() { return true; }

		public boolean allowsNonultrametricTrees() { return true; }

		public boolean allowsUnrootedTrees() { return false; }

		public Category getCategory() { return Category.TREE_SHAPE; }
	};
}
