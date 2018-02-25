package dr.evomodel.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import dr.inference.model.Statistic;
import java.util.ArrayList;
import java.util.List;
public class ExternalLengthStatistic extends Statistic.Abstract implements TreeStatistic {
public ExternalLengthStatistic(String name, Tree tree, TaxonList taxa) throws Tree.MissingTaxonException {
super(name);
this.tree = tree;
int m = taxa.getTaxonCount();
int n = tree.getExternalNodeCount();
for (int i = 0; i < m; i++) {
Taxon taxon = taxa.getTaxon(i);
NodeRef node = null;
boolean found = false;
for (int j = 0; j < n; j++) {
node = tree.getExternalNode(j);
if (tree.getNodeTaxon(node).getId().equals(taxon.getId())) {
found = true;
break;
}
}
if (!found) {
throw new Tree.MissingTaxonException(taxon);
}
leafSet.add(node);
}
}
public void setTree(Tree tree) {
this.tree = tree;
}
public Tree getTree() {
return tree;
}
public int getDimension() {
return leafSet.size();
}
public double getStatisticValue(int dim) {
NodeRef node = leafSet.get(dim);
return tree.getBranchLength(node);
}
private Tree tree = null;
private List<NodeRef> leafSet = new ArrayList<NodeRef>();
}