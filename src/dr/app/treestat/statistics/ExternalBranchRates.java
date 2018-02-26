package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
public class ExternalBranchRates implements TreeSummaryStatistic {
    private ExternalBranchRates() {
    }
    public int getStatisticDimensions(Tree tree) {
        return tree.getExternalNodeCount();
    }
    public String getStatisticLabel(Tree tree, int i) {
        return "Branch Rate " + Integer.toString(i + 1);
    }
    public double[] getSummaryStatistic(Tree tree) {
        int externalNodeCount = tree.getExternalNodeCount();
        double[] stats = new double[externalNodeCount];
        int count = 0;
        for (int i = 0; i < externalNodeCount; i++) {
            NodeRef node = tree.getExternalNode(i);
            stats[count++] = (Double) tree.getNodeAttribute(node, "rate");
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
            return new ExternalBranchRates();
        }
        public String getSummaryStatisticName() {
            return "External Branch Rates";
        }
        public String getSummaryStatisticDescription() {
            return "The rates of the external edges of the tree.";
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