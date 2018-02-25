package dr.evolution.tree;
public interface ImportanceDistribution {
public void addTree(Tree tree);	
public double splitClade(Clade parent, Clade[] children);
public double getTreeProbability(Tree tree);
}
