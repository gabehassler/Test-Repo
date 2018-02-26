
package dr.app.treestat.statistics;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;

public class InternalBranchRates implements TreeSummaryStatistic {

    private InternalBranchRates() {
    }

    public int getStatisticDimensions(Tree tree) {
        return tree.getInternalNodeCount() - 1;
    }

    public String getStatisticLabel(Tree tree, int i) {
        return "Branch Rate " + Integer.toString(i + 1);
    }

    public double[] getSummaryStatistic(Tree tree) {

        int internalNodeCount = tree.getInternalNodeCount();
        double[] stats = new double[internalNodeCount - 1];
        int count = 0;
        for (int i = 0; i < internalNodeCount; i++) {
            NodeRef node = tree.getInternalNode(i);
            if (!tree.isRoot(node)) {
                stats[count++] = (Double) tree.getNodeAttribute(node, "rate");
            }
        }
        //Arrays.sort(stats);

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

    public String getSummaryStatisticName() {
        return FACTORY.getSummaryStatisticName();
    }

    public String getSummaryStatisticDescription() {
        return FACTORY.getSummaryStatisticDescription();
    }

    public String getSummaryStatisticReference() {
        return FACTORY.getSummaryStatisticReference();
    }

    public boolean allowsPolytomies() {
        return FACTORY.allowsPolytomies();
    }

    public boolean allowsNonultrametricTrees() {
        return FACTORY.allowsNonultrametricTrees();
    }

    public boolean allowsUnrootedTrees() {
        return FACTORY.allowsUnrootedTrees();
    }

    public Category getCategory() {
        return FACTORY.getCategory();
    }

    public static final Factory FACTORY = new Factory() {

        public TreeSummaryStatistic createStatistic() {
            return new InternalBranchRates();
        }

        public String getSummaryStatisticName() {
            return "Internal Branch Rates";
        }

        public String getSummaryStatisticDescription() {

            return "The rates of the internal edges of the tree.";
        }

        public String getSummaryStatisticReference() {

            return "-";
        }

        public boolean allowsPolytomies() {
            return true;
        }

        public boolean allowsNonultrametricTrees() {
            return true;
        }

        public boolean allowsUnrootedTrees() {
            return false;
        }

        public Category getCategory() {
            return Category.GENERAL;
        }
    };
}