
package dr.app.treestat.statistics;

import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;

public abstract  class AbstractTreeSummaryStatistic implements TreeSummaryStatistic {
    public int getStatisticDimensions(Tree tree) {
        return 1;
    }

    public String getStatisticLabel(Tree tree, int i) {
        return getSummaryStatisticName();
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
}
