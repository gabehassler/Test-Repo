package dr.app.treestat.statistics;
import dr.evolution.coalescent.TreeIntervals;
import dr.evolution.tree.Tree;
public class LineageProportionStatistic extends AbstractTreeSummaryStatistic {
public LineageProportionStatistic() {
this.t = 1.0;
}
public void setDouble(double value) {
this.t = value;
}
public double[] getSummaryStatistic(Tree tree) {
TreeIntervals intervals = new TreeIntervals(tree);
int tipCount = tree.getExternalNodeCount();
double totalTime = 0.0;
for (int i = 0; i < intervals.getIntervalCount(); i++) {
totalTime += intervals.getInterval(i);
if (totalTime > t) {
return new double[] { ((double)intervals.getLineageCount(i)) / tipCount };
}
}
return new double[] { 1.0 / tipCount };
}
public String getSummaryStatisticName() {
return "LineageProportion(" + t + ")";
}
public String getSummaryStatisticDescription() {
return getSummaryStatisticName() + " is the proportion of lineages that exists in the genealogy at " +
"time " + t + ".";
}
public String getSummaryStatisticReference() { return FACTORY.getSummaryStatisticReference(); }
public boolean allowsPolytomies() { return FACTORY.allowsPolytomies(); }
public boolean allowsNonultrametricTrees() { return FACTORY.allowsNonultrametricTrees(); }
public boolean allowsUnrootedTrees() { return FACTORY.allowsUnrootedTrees(); }
public Category getCategory() { return FACTORY.getCategory(); }
public static final Factory FACTORY = new Factory() {
public TreeSummaryStatistic createStatistic() {
return new LineageProportionStatistic();
}
public String getSummaryStatisticName() {
return "LineageProportion(t)";
}
public String getSummaryStatisticDescription() {
return getSummaryStatisticName() + " is the proportion of lineages that exists in the genealogy at " +
"time t.";
}
public String getSummaryStatisticReference() {
return "-";
}
public String getValueName() { return "The time (t):"; }
public boolean allowsPolytomies() { return true; }
public boolean allowsNonultrametricTrees() { return true; }
public boolean allowsUnrootedTrees() { return false; }
public Category getCategory() { return Category.POPULATION_GENETIC; }
public boolean allowsWholeTree() { return true; }
public boolean allowsCharacter() { return false; }
public boolean allowsCharacterState() { return false; }
public boolean allowsTaxonList() { return false; }
public boolean allowsInteger() { return false; }
public boolean allowsDouble() { return true; }
};
double t = 1.0;
}