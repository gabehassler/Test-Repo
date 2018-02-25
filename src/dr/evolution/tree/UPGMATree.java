package dr.evolution.tree;
import dr.evolution.distance.DistanceMatrix;
public class UPGMATree extends ClusteringTree {
public UPGMATree(DistanceMatrix distanceMatrix) {
super(distanceMatrix, 2);
}
//
// Protected and Private stuff
//
protected void findNextPair() {
besti = 0;
bestj = 1;
double dmin = getDist(0, 1);
for (int i = 0; i < numClusters-1; i++) {
for (int j = i+1; j < numClusters; j++) {
if (getDist(i, j) < dmin) {
dmin = getDist(i, j);
besti = i;
bestj = j;
}
}
}
abi = alias[besti];
abj = alias[bestj];
}
protected double newNodeHeight() {
return getDist(besti, bestj) / 2.0;
}
protected double updatedDistance(int i, int j, int k)
{
int ai = alias[i];
int aj = alias[j];
double tipSum = (double) (tipCount[ai] + tipCount[aj]);
return 	(((double)tipCount[ai]) / tipSum) * getDist(k, i) +
(((double)tipCount[aj]) / tipSum) * getDist(k, j);
}
}
