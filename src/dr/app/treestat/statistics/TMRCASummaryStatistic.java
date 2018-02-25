package dr.app.treestat.statistics;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.*;
import java.util.*;
public class TMRCASummaryStatistic extends AbstractTreeSummaryStatistic {
private TMRCASummaryStatistic() {
this.taxonList = null;
}
public void setTaxonList(TaxonList taxonList) {
this.taxonList = taxonList;
}
public double[] getSummaryStatistic(Tree tree) {
if (taxonList == null) {
return new double[] { tree.getNodeHeight(tree.getRoot()) };
}
try {
Set<String> leafSet = Tree.Utils.getLeavesForTaxa(tree, taxonList);
NodeRef node = Tree.Utils.getCommonAncestorNode(tree, leafSet);
if (node == null) throw new RuntimeException("No node found that is MRCA of " + leafSet);
return new double[] { tree.getNodeHeight(node) };
} catch (Tree.MissingTaxonException e) {
throw new RuntimeException("Missing taxon!");
}
}
public String getSummaryStatisticName() {
if (characterState != null) {
return "tMRCA(" + characterState + ")";
} else if (taxonList != null) {
return "tMRCA(" + taxonList.getId() + ")";
} else {
return "tMRCA";
}
}
public String getSummaryStatisticDescription() {
if (characterState != null) {
return "The time of the most recent common ancestor of the character state " + characterState;
} else if (taxonList != null) {
return "The time of the most recent common ancestor of the given taxon list";
}
return "The time of the most recent common ancestor of a set of taxa. In order to use this statistic, a taxon set must be defined (see the Taxon Set tab).";
}
public String getSummaryStatisticReference() { return FACTORY.getSummaryStatisticReference(); }
public boolean allowsPolytomies() { return FACTORY.allowsPolytomies(); }
public boolean allowsNonultrametricTrees() { return FACTORY.allowsNonultrametricTrees(); }
public boolean allowsUnrootedTrees() { return FACTORY.allowsUnrootedTrees(); }
public SummaryStatisticDescription.Category getCategory() { return FACTORY.getCategory(); }
public static final TreeSummaryStatistic.Factory FACTORY = new TreeSummaryStatistic.Factory() {
public TreeSummaryStatistic createStatistic() {
return new TMRCASummaryStatistic();
}
public String getSummaryStatisticName() {
return "tMRCA";
}
public String getSummaryStatisticDescription() {
return "The time of the most recent common ancestor";
}
public String getSummaryStatisticReference() {
return "-";
}
public boolean allowsPolytomies() { return true; }
public boolean allowsNonultrametricTrees() { return true; }
public boolean allowsUnrootedTrees() { return false; }
public SummaryStatisticDescription.Category getCategory() { return SummaryStatisticDescription.Category.GENERAL; }
public boolean allowsWholeTree() { return true; }
public boolean allowsCharacter() { return false; }
public boolean allowsCharacterState() { return false; }
public boolean allowsTaxonList() { return true; }
};
private String characterState = null;
private TaxonList taxonList = null;
}
