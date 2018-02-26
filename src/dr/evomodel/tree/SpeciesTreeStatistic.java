
package dr.evomodel.tree;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.inference.model.BooleanStatistic;

import java.util.HashSet;
import java.util.Set;

public class SpeciesTreeStatistic extends BooleanStatistic implements TreeStatistic {

    public SpeciesTreeStatistic(String name, Tree speciesTree, Tree populationTree) {

        super(name);
        this.speciesTree = speciesTree;
        this.popTree = populationTree;
    }

    public void setTree(Tree tree) {
        this.popTree = tree;
    }

    public Tree getTree() {
        return popTree;
    }

    public int getDimension() {
        return 1;
    }

    public boolean getBoolean(int dim) {

        if (popTree.getNodeHeight(popTree.getRoot()) < speciesTree.getNodeHeight(speciesTree.getRoot())) {
            return false;
        }

        return isCompatible(popTree.getRoot(), null);
    }


    private boolean isCompatible(NodeRef popNode, Set<String> species) {

        //int n = popNode.getNumber() - popTree.getExternalNodeCount();

        if (popTree.isExternal(popNode)) {
            Taxon speciesTaxon = (Taxon) popTree.getTaxonAttribute(popNode.getNumber(), "species");
            species.add(speciesTaxon.getId());
        } else {

            Set<String> speciesTaxa = new HashSet<String>();

            int childCount = popTree.getChildCount(popNode);
            for (int i = 0; i < childCount; i++) {
                if (!isCompatible(popTree.getChild(popNode, i), speciesTaxa)) {
                    return false;
                }
            }

            if (species != null) {
                species.addAll(speciesTaxa);

                NodeRef speciesNode = Tree.Utils.getCommonAncestorNode(speciesTree, speciesTaxa);
                if (popTree.getNodeHeight(popNode) < speciesTree.getNodeHeight(speciesNode)) {
                    return false;
                }
            }
        }

        return true;
    }

    private Tree speciesTree;
    private Tree popTree;
}

