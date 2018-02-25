package dr.evolution.tree;
import dr.evolution.distance.DistanceMatrix;
public class NeighborJoiningTree extends ClusteringTree {
public NeighborJoiningTree(DistanceMatrix distanceMatrix) {
super(distanceMatrix, 3);
}
//
// Private stuff
//
private double[] r;
private double scale, maxHeight;
protected void init(DistanceMatrix distanceMatrix) {
super.init(distanceMatrix);
r = new double[numClusters];
maxHeight = 0.0;
}
protected void findNextPair() {
for (int i = 0; i < numClusters; i++) {
r[i] = 0;
for (int j = 0; j < numClusters; j++) {
r[i] += getDist(i,j);
}
}
besti = 0;
bestj = 1;
double smax = -1.0;
scale = 1.0/(numClusters-2);
for (int i = 0; i < numClusters-1; i++) {
for (int j = i+1; j < numClusters; j++) {
double sij = (r[i] + r[j] ) * scale - getDist(i, j);
if (sij > smax) {
smax = sij;
besti = i;
bestj = j;
}
}
}
abi = alias[besti];
abj = alias[bestj];
}
protected void finish() {
// Connect up the final two clusters
abi = alias[0];
abj = alias[1];
newCluster = new SimpleNode();
double dij = getDist(0, 1);
double l = dij * 0.5;
if (l < 0.0)
l = 0.0;
appendHeight(clusters[abi], l);
appendHeight(clusters[abj], l);
newCluster.setHeight(0.0);
newCluster.addChild(clusters[abi]);
newCluster.addChild(clusters[abj]);
reverseHeights(newCluster, maxHeight);
super.finish();
}
private void reverseHeights(SimpleNode node, double totalHeight) {
double height = totalHeight - node.getHeight();
node.setHeight(height);
if (!node.isExternal()) {
int i, n = node.getChildCount();
for (i = 0; i < n; i++) {
reverseHeights(node.getChild(i), totalHeight);
}
}
}
protected double newNodeHeight() {
double dij = getDist(besti, bestj);
double li = (dij + (r[besti] - r[bestj]) * scale) * 0.5;
double lj = dij - li; // = (dij + (r[bestj]-r[besti])*scale)*0.5
if (li < 0.0)
li = 0.0;
if (lj < 0.0)
lj = 0.0;
appendHeight(clusters[abi], li);
appendHeight(clusters[abj], lj);
return 0.0;
}
private void appendHeight(SimpleNode node, double delta) {
double height = node.getHeight() + delta;
node.setHeight(height);
if (!node.isExternal()) {
int i, n = node.getChildCount();
for (i = 0; i < n; i++) {
appendHeight(node.getChild(i), delta);
}
} else {	
if (height > maxHeight)
maxHeight = height;
}
}
protected double updatedDistance(int i, int j, int k) {
return (getDist(k, i) + getDist(k, j) - getDist(i, j)) * 0.5;
}
}