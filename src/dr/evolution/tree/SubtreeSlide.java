package dr.evolution.tree;
import dr.math.MathUtils;
import java.util.ArrayList;
public class SubtreeSlide {
public SubtreeSlide(double size, boolean gaussian) {
this.size = size;
this.gaussian = gaussian;
}
public double getSize() { return size; }
public void setSize(double size) { this.size = size; }
public double slideSubtree(MutableTree tree) {
double logHastingsRatio;
NodeRef i, newParent, newChild;
// 1. choose a random node avoiding root
do {
i = tree.getNode(MathUtils.nextInt(tree.getNodeCount()));
} while (tree.getRoot() == i);
NodeRef iP = tree.getParent(i);
NodeRef CiP = getOtherChild(tree,iP, i);
NodeRef PiP = tree.getParent(iP);
// 2. choose a delta to move
double delta = getDelta();
double oldHeight = tree.getNodeHeight(iP);
double newHeight = oldHeight + delta;
// 3. if the move is up
if (delta > 0) {
// 3.1 if the topology will change
if (PiP != null && tree.getNodeHeight(PiP) < newHeight) {
// find new parent
newParent = PiP; newChild = iP;
while (tree.getNodeHeight(newParent) < newHeight) {
newChild = newParent;
newParent = tree.getParent(newParent);
if (newParent == null) break;
}
tree.beginTreeEdit();
// 3.1.1 if creating a new root
if (tree.isRoot(newChild)) {
tree.removeChild(iP, CiP); tree.removeChild(PiP, iP);
tree.addChild(iP, newChild); tree.addChild(PiP, CiP);
tree.setRoot(iP);
//System.err.println("Creating new root!");
}
// 3.1.2 no new root
else {
tree.removeChild(iP, CiP); tree.removeChild(PiP, iP);
tree.removeChild(newParent, newChild);
tree.addChild(iP, newChild); tree.addChild(PiP, CiP);
tree.addChild(newParent, iP);
//System.err.println("No new root!");
}
tree.setNodeHeight(iP, newHeight);
tree.endTreeEdit();
// 3.1.3 count the hypothetical sources of this destination.
int possibleSources = intersectingEdges(tree, newChild, oldHeight, null);
//System.out.println("possible sources = " + possibleSources);
logHastingsRatio = Math.log(1.0/(double)possibleSources);
} else {
// just change the node height
tree.setNodeHeight(iP, newHeight);
logHastingsRatio = 0.0;
}
}
// 4 if we are sliding the subtree down.
else {
// 4.0 is it a valid move?
if (tree.getNodeHeight(i) > newHeight) {
return Double.NEGATIVE_INFINITY;
}
// 4.1 will the move change the topology
if (tree.getNodeHeight(CiP) > newHeight) {
ArrayList newChildren = new ArrayList();
int possibleDestinations = intersectingEdges(tree, CiP, newHeight, newChildren);
// if no valid destinations then return a failure
if (newChildren.size() == 0) { return Double.NEGATIVE_INFINITY; }
// pick a random parent/child destination edge uniformly from options
int childIndex = MathUtils.nextInt(newChildren.size());
newChild = (NodeRef)newChildren.get(childIndex);
newParent = tree.getParent(newChild);
tree.beginTreeEdit();
// 4.1.1 if iP was root
if (tree.isRoot(iP)) {
// new root is CiP
tree.removeChild(iP, CiP); tree.removeChild(newParent, newChild);
tree.addChild(iP, newChild); tree.addChild(newParent, iP);
tree.setRoot(CiP);
//System.err.println("DOWN: Creating new root!");
} else {
tree.removeChild(iP, CiP); tree.removeChild(PiP, iP);
tree.removeChild(newParent, newChild);
tree.addChild(iP, newChild); tree.addChild(PiP, CiP);
tree.addChild(newParent, iP);
//System.err.println("DOWN: no new root!");
}
tree.setNodeHeight(iP, newHeight);
tree.endTreeEdit();
logHastingsRatio = Math.log((double)possibleDestinations);
} else {
tree.setNodeHeight(iP, newHeight);
logHastingsRatio = 0.0;
}
}
return logHastingsRatio;
}
private double getDelta() {
if (!gaussian) {
return (MathUtils.nextDouble() * size) - (size/2.0);
} else {
return MathUtils.nextGaussian() * size;
}
}
private int intersectingEdges(Tree tree, NodeRef node, double height, ArrayList directChildren) {
NodeRef parent = tree.getParent(node);
if (tree.getNodeHeight(parent) < height) return 0;
if (tree.getNodeHeight(node) < height) {
if (directChildren != null) directChildren.add(node);
return 1;
}
int count = 0;
for (int i = 0; i < tree.getChildCount(node); i++) {
count += intersectingEdges(tree, tree.getChild(node, i), height, directChildren);
}
return count;
}
private NodeRef getOtherChild(Tree tree, NodeRef parent, NodeRef child) {
if (tree.getChild(parent, 0) == child) {
return tree.getChild(parent, 1);
} else {
return tree.getChild(parent, 0);
}
}
private double size = 1.0;
private boolean gaussian = false;
}