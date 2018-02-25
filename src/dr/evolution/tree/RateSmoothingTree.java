package dr.evolution.tree;
import dr.math.ConjugateDirectionSearch;
import dr.math.MultivariateFunction;
import dr.math.MultivariateMinimum;
public class RateSmoothingTree extends SimpleTree {
public RateSmoothingTree(Tree sourceTree) {
super(sourceTree);
this.sourceTree = sourceTree;
this.mu = 1.0;
this.optimizeMu = false;
}
public RateSmoothingTree(Tree sourceTree, double mu) {
this.sourceTree = sourceTree;
this.mu = mu;
this.optimizeMu = false;
}
public double getMu() { return mu; }
public void smoothRates() {
nodeCount = getInternalNodeCount();
int argumentCount = nodeCount;
if (optimizeMu) {
argumentCount++;
muIndex = nodeCount;
}
MultivariateMinimum optimizer = new ConjugateDirectionSearch();
nodeValues = new double[nodeCount];
double[] xvec = new double[argumentCount];
for (int i = 0; i < nodeCount; i++) { 
xvec[i] = 1.0;
}
if (optimizeMu) {
xvec[muIndex] = mu;
}
optimizer.optimize(nonParametricRateSmoothing, xvec, 1E-8, 1E-8);
}
public double getSumOfRates() {
double[] score = new double[] { 0.0 };
NodeRef root = getRoot();
if (getChildCount(root) != 2) {
throw new IllegalArgumentException("The tree must have a bifurcating root node");
}
sumDist = 0;
sumTime = 0;
double rate1 = sumScoreAtNode(getChild(root, 0), score);
double rate2 = sumScoreAtNode(getChild(root, 1), score);
mu = sumDist / sumTime;
double diff = rate2 - rate1;
score[0] += diff * diff;
return score[0];
}
//
// Private stuff
//	
private double sumScoreAtNode(NodeRef node, double[] score) {
double rate0 = getRateAtNode(node);
if (!isExternal(node)) {
for (int i = 0; i < getChildCount(node); i++) {
double rate1 = sumScoreAtNode(getChild(node, i), score);
double diff = rate0 - rate1;
score[0] += diff * diff;
}
}
return rate0;
}
private double getRateAtNode(NodeRef node) {
double time = getNodeHeight(getParent(node)) - getNodeHeight(node);
double dist = sourceTree.getBranchLength(sourceTree.getNode(node.getNumber()));
double rate;
if (time == 0.0) {
if (dist == 0.0) {
rate = 1.0;
} else {
rate = Double.MIN_VALUE;
}
} else {
rate = (dist / time);
}
sumDist += dist;
sumTime += time;
setNodeRate(node, rate);
return rate;
}
private double setNodeHeightsFromValues(NodeRef node) {
if (!isExternal(node)) {
double maxHeight = setNodeHeightsFromValues(getChild(node, 0));
for (int i = 1; i < getChildCount(node); i++) {
double height = setNodeHeightsFromValues(getChild(node, i));
if (height > maxHeight) maxHeight = height;
}
setNodeHeight(node, maxHeight + nodeValues[node.getNumber() - getExternalNodeCount()]);
}
return getNodeHeight(node);
}
private MultivariateFunction nonParametricRateSmoothing = new MultivariateFunction() {
public double evaluate(double[] argument) {
for (int i = 0; i < getInternalNodeCount(); i++) {
nodeValues[i] = argument[i];
}
setNodeHeightsFromValues(getRoot());
if (optimizeMu) {
mu = argument[muIndex];
}
double score = getSumOfRates();
return score;
}
public int getNumArguments() { 
if (optimizeMu) {
return getInternalNodeCount() + 1; 
} else {
return getInternalNodeCount(); 
}
}
public double getLowerBound(int n) {
if (optimizeMu && n == muIndex) {
return Double.MIN_VALUE;
} else {
return 0.0;
}
}
public double getUpperBound(int n) {
if (optimizeMu && n == muIndex) {
return Double.MAX_VALUE;
} else {
return Double.MAX_VALUE;
}
}
};
private int nodeCount;
private double[] nodeValues;
private Tree sourceTree;
private double mu, sumDist, sumTime;
private boolean optimizeMu;
private int muIndex;
}
