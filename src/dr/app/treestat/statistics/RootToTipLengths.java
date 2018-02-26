
package dr.app.treestat.statistics;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;

public class RootToTipLengths implements TreeSummaryStatistic {

    private RootToTipLengths() { }

    public int getStatisticDimensions(Tree tree) {
        return tree.getExternalNodeCount();
    }

    public String getStatisticLabel(Tree tree, int i) {
        return tree.getTaxonId(i);
    }

    public double[] getSummaryStatistic(Tree tree) {

        int externalNodeCount = tree.getExternalNodeCount();
        double[] stats = new double[externalNodeCount];
        for (int i = 0; i < externalNodeCount; i++) {
            NodeRef node = tree.getExternalNode(i);
            stats[i] = 0.0;
            while (node != tree.getRoot()) {
                stats[i] += tree.getBranchLength(node);
                node = tree.getParent(node);
            }
        }

        return stats;
    }

    public void setTaxonList(TaxonList taxonList) {
        throw new UnsupportedOperationException("not implemented in this statistic");
    }

    public void setInteger(int value) {
        throw new UnsupportedOperationException("not implemented in this statistic");
    }

    public void setDouble(double value) {
        throw new UnsupportedOperationException("not implemented in this statistic");
    }

    public void setString(String value) {
        throw new UnsupportedOperationException("not implemented in this statistic");
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
            return new RootToTipLengths();
        }

        public String getSummaryStatisticName() {
            return "Root-Tip Lengths";
        }

        public String getSummaryStatisticDescription() {

            return "The length from the root to each tip in the tree.";
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
