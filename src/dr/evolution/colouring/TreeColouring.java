package dr.evolution.colouring;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public interface TreeColouring {
int getColourCount();
Tree getTree();
int getNodeColour(NodeRef node);
BranchColouring getBranchColouring(NodeRef node);
int getColourChangeCount();
boolean hasProbability();
void setLogProbabilityDensity(double p);
}
