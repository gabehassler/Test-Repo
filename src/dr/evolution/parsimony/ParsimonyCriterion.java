package dr.evolution.parsimony;
import dr.evolution.tree.Tree;
import dr.evolution.tree.NodeRef;
public interface ParsimonyCriterion {
    double[] getSiteScores(Tree tree);
    double getScore(Tree tree);
    int[] getStates(Tree tree, NodeRef node);
}
