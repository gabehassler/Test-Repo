package dr.app.treestat.statistics;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import java.util.Map;
public interface TreeSummaryStatistic extends SummaryStatisticDescription {
	int getStatisticDimensions(Tree tree);
	String getStatisticLabel(Tree tree, int i);
    void setTaxonList(TaxonList taxonList);
    void setInteger(int value);
    void setDouble(double value);
    void setString(String value);
	double[] getSummaryStatistic(Tree tree);
	public abstract class Factory implements SummaryStatisticDescription {
		public TreeSummaryStatistic createStatistic() {
			throw new RuntimeException("This factory method is not implemented");
		}
		public boolean allowsWholeTree() { return true; }
		public boolean allowsTaxonList() { return false; }
		public boolean allowsInteger() { return false; }
		public boolean allowsDouble() { return false; }
        public boolean allowsString() { return false; }
		public String getValueName() { return ""; }
    }
}
