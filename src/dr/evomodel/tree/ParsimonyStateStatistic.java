package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.inference.model.Statistic;
import java.util.Set;
public class ParsimonyStateStatistic extends Statistic.Abstract implements TreeStatistic {
public ParsimonyStateStatistic(String name, Tree tree, TaxonList stateTaxa, TaxonList mrcaTaxa) throws Tree.MissingTaxonException {
super(name);
this.tree = tree;
this.stateLeafSet = Tree.Utils.getLeavesForTaxa(tree, stateTaxa);
if (mrcaTaxa != null) {
this.mrcaLeafSet = Tree.Utils.getLeavesForTaxa(tree, mrcaTaxa);
}
}
public void setTree(Tree tree) {
this.tree = tree;
}
public Tree getTree() {
return tree;
}
public int getDimension() {
return 1;
}
public double getStatisticValue(int dim) {
NodeRef node;
if (mrcaLeafSet != null) {
node = Tree.Utils.getCommonAncestorNode(tree, mrcaLeafSet);
} else {
node = tree.getRoot();
}
return Tree.Utils.getParsimonyState(tree, node, stateLeafSet);
}
private Tree tree = null;
private Set stateLeafSet = null;
private Set<String> mrcaLeafSet = null;
}
