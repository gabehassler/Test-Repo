package dr.evolution.tree;
import dr.math.ConjugateDirectionSearch;
import dr.math.MultivariateFunction;
import dr.math.MultivariateMinimum;
public class LeastSquaresClockTree extends SimpleTree {
public LeastSquaresClockTree(Tree sourceTree) {
super(sourceTree);
this.sourceTree = sourceTree;
this.mu = 1.0;
this.optimizeMu = true;
}
public LeastSquaresClockTree(Tree sourceTree, double mu) {
this.sourceTree = sourceTree;
this.mu = mu;
this.optimizeMu = false;
}
public double getMu() { return mu; }
public void optimize() {
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
optimizer.optimize(leastSquaresClock, xvec, 1E-8, 1E-8);
}
public double getSumOfSquares() {
double[] score = new double[] { 0.0 };
NodeRef root = getRoot();
if (getChildCount(root) != 2) {
throw new IllegalArgumentException("The tree must have a bifurcating root node");
}
NodeRef node1 = getChild(root, 0);
NodeRef node2 = getChild(root, 1);
if (!isExternal(node1)) {
getSumOfSquaresAtNode(node1, score);
}
if (!isExternal(node2)) {
getSumOfSquaresAtNode(node2, score);
}
double dist1 = sourceTree.getBranchLength(sourceTree.getNode(node1.getNumber())) +
sourceTree.getBranchLength(sourceTree.getNode(node2.getNumber()));
double time = getNodeHeight(root) - getNodeHeight(node1) +
getNodeHeight(root) - getNodeHeight(node2);
double dist2 = time * mu;
double diff = dist1 - dist2;
score[0] += diff * diff;
return score[0];
}
//
// Private stuff
//	
private void getSumOfSquaresAtNode(NodeRef node, double[] score) {
if (!isExternal(node)) {
for (int i = 0; i < getChildCount(node); i++) {
NodeRef child = getChild(node, i);
if (!isExternal(child)) {
getSumOfSquaresAtNode(child, score);
score[0] += getScoreAtNode(child);
}
}
}
}
private double getScoreAtNode(NodeRef node) {
double dist1 = sourceTree.getBranchLength(sourceTree.getNode(node.getNumber()));
double time = getNodeHeight(getParent(node)) - getNodeHeight(node);
double dist2 = time * mu;
double diff = dist1 - dist2;
return diff * diff;
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
private MultivariateFunction leastSquaresClock = new MultivariateFunction() {
public double evaluate(double[] argument) {
for (int i = 0; i < getInternalNodeCount(); i++) {
nodeValues[i] = argument[i];
}
setNodeHeightsFromValues(getRoot());
if (optimizeMu) {
mu = argument[muIndex];
}
double score = getSumOfSquares();
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
private double mu;
private boolean optimizeMu;
private int muIndex;
}
