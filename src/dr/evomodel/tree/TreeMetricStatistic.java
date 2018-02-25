package dr.evomodel.tree;
import dr.evolution.tree.BranchScoreMetric;
import dr.evolution.tree.CladeMetric;
import dr.evolution.tree.Tree;
import dr.inference.model.Statistic;
import jebl.evolution.treemetrics.BilleraMetric;
import jebl.evolution.treemetrics.CladeHeightMetric;
import jebl.evolution.treemetrics.RobinsonsFouldMetric;
import jebl.evolution.treemetrics.RootedTreeMetric;
import jebl.evolution.trees.SimpleRootedTree;
public class TreeMetricStatistic extends Statistic.Abstract implements TreeStatistic {
public enum Method {
TOPOLOGY, BILLERA, ROBINSONSFOULD, CLADEHEIGHTM, BRANCHSCORE, CLADEMETRIC
}
public TreeMetricStatistic(String name, Tree target, Tree reference, Method method) {
super(name);
this.target = target;
this.method = method;
switch (method) {
case TOPOLOGY: {
this.referenceNewick = Tree.Utils.uniqueNewick(reference, reference.getRoot());
break;
}
default: {
jreference = Tree.Utils.asJeblTree(reference);
break;
}
}
switch (method) {
case BILLERA:
metric = new BilleraMetric();
break;
case ROBINSONSFOULD:
metric = new RobinsonsFouldMetric();
break;
case CLADEHEIGHTM:
metric = new CladeHeightMetric();
break;
case BRANCHSCORE:
metric = new BranchScoreMetric();
break;
case CLADEMETRIC:
metric = new CladeMetric();
break;
}
}
public void setTree(Tree tree) {
this.target = tree;
}
public Tree getTree() {
return target;
}
public int getDimension() {
return 1;
}
public double getStatisticValue(int dim) {
if (method == Method.TOPOLOGY) {
return compareTreesByTopology();
}
return metric.getMetric(jreference, Tree.Utils.asJeblTree(target));
}
private double compareTreesByTopology() {
final String tar = Tree.Utils.uniqueNewick(target, target.getRoot());
return tar.equals(referenceNewick) ? 0.0 : 1.0;
}
public static String methodNames(String s) {
String r = "";
for (Method m : Method.values()) {
if (r.length() > 0)
r = r + s;
r = r + m.name();
}
return r;
}
private final Method method;
private Tree target = null;
private String referenceNewick = null;
private SimpleRootedTree jreference = null;
RootedTreeMetric metric = null;
}
