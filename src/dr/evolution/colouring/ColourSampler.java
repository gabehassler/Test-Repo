
package dr.evolution.colouring;

import dr.evolution.coalescent.structure.MetaPopulation;
import dr.evolution.tree.Tree;

public interface ColourSampler {

    DefaultTreeColouring sampleTreeColouring(Tree tree, ColourChangeMatrix colourChangeMatrix, MetaPopulation mp);

    double getProposalProbability(TreeColouring treeColouring, Tree tree, ColourChangeMatrix colourChangeMatrix, MetaPopulation mp);

    int[] getLeafColourCounts();
}
