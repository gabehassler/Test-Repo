package dr.app.treestat.statistics;
import dr.evolution.tree.Tree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class GammaStatistic extends AbstractTreeSummaryStatistic {
    private GammaStatistic() { }
    public double[] getSummaryStatistic(Tree tree) {
        int n = tree.getExternalNodeCount();
        double[] g = getIntervals(tree);
        double T = 0; // total branch length
        for (int j = 2; j <= n; j++) {
            T += j * g[j-2];
        }
        double sum = 0.0;
        for (int i = 2; i < n; i++) {
            for (int k = 2; k <= i; k++) {
                sum += k * g[k-2];
            }
        }
        double gamma = ((sum / (n-2.0)) - (T / 2.0)) / (T * Math.sqrt(1.0 / (12.0 * (n - 2.0))));
        return new double[] { gamma };
    }
    private static double[] getIntervals(Tree tree) {
        List<Double> heights = new ArrayList<Double>();
        for (int i = 0; i < tree.getInternalNodeCount(); i++) {
            heights.add(tree.getNodeHeight(tree.getInternalNode(i)));
        }
        Collections.sort(heights, Collections.reverseOrder());
        double[] intervals = new double[heights.size()];
        for (int i = 0; i < intervals.length - 1; i++) {
            double height1 = heights.get(i);
            double height2 = heights.get(i + 1);
            intervals[i] = height1 - height2;
        }
        intervals[intervals.length - 1] = heights.get(intervals.length - 1);
        return intervals;
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
            return new GammaStatistic();
        }
        public String getSummaryStatisticName() {
            return "Gamma";
        }
        public String getSummaryStatisticDescription() {
            return "The gamma-statistic is a summary of the information contained in the inter-node " +
                "intervals of a phylogeny; under the assumption that the clade diversified " +
                "with constant rates, it follows a normal distribution with mean of zero and a standard-deviation " +
                "of one " +
                "(Pybus and Harvey 2000). Thus, the null hypothesis that the clade diversified with constant " +
                "rates may be tested with 1 -  2*pnorm(abs(gamma.stat(phy))) for a two-tailed test, " +
                "or 1 - pnorm(abs(gamma.stat(phy))) for a one-tailed test, both returning the corresponding P-value.";
        }
        public String getSummaryStatisticReference() {
            return "Pybus & Harvey (2000)";
        }
        public boolean allowsPolytomies() { return true; }
        public boolean allowsNonultrametricTrees() { return false; }
        public boolean allowsUnrootedTrees() { return false; }
        public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.SPECIATION; }
    };
}
