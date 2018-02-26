package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class FuLiD extends AbstractTreeSummaryStatistic {
	private FuLiD() { }
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
		int n = externalNodeCount;
		double total = externalLength + internalLength;
		// difference in expectations
		double D = total - a(n)*externalLength;
		// normalized
		D /= Math.sqrt(u(n)*total + (v(n)*(total*total)));
		return new double[] { D };
	}
	private double a(int n) {
		double a = 0.0;
		for (int k = 1; k < n; k++) {
			a += 1/(double)k;
		}
		return a;
	}
	private double b(int n) {
		double b = 0.0;
		for (int k = 1; k < n; k++) {
			b += 1/(double)(k*k);
		}
		return b;
	}
	private double c(int n) {
		if (n==2) return 1.0;
		double an = a(n);
		double c = 2.0 * (n * an - 2.0 * (n - 1.0));
		c /= (n-1)*(n-2);
		return c;
	}
	private double v(int n) {
		double an2 = a(n);
		an2 *= an2;
		double v = 1 + (an2/(b(n)+an2)) * (c(n)-((n+1)/(n-1)));
		return v;
	}
	private double u(int n) {
		return a(n) - 1 - v(n);
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
			return new FuLiD();
		}
		public String getSummaryStatisticName() {
			return "Fu & Li's D";
		}
		public String getSummaryStatisticDescription() {
			return "The normalized difference between total tree length and external branch tree length. " +
				"Note that this implementation uses branch lengths rather than the counts of mutations (this " +
				"has a nice side-effect of taking into account complex mutation models.)";
		}
		public String getSummaryStatisticReference() {
			return "Fu & Li (1993)";
		}
		public boolean allowsPolytomies() { return false; }
		public boolean allowsNonultrametricTrees() { return false; }
		public boolean allowsUnrootedTrees() { return false; }
		public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.POPULATION_GENETIC; }
	};
}
