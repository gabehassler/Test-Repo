
package dr.evomodel.tree;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.evolution.util.TaxonList;
import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;

import java.util.Set;

public class AncestralTrait implements Loggable {

    public AncestralTrait(String name, TreeTrait ancestralTrait, Tree tree, TaxonList taxa) throws Tree.MissingTaxonException {
        this.name = name;
        this.tree = tree;
        this.ancestralTrait = ancestralTrait;
        if (taxa != null) {
            this.leafSet = Tree.Utils.getLeavesForTaxa(tree, taxa);
        }
    }

    public Tree getTree() {
        return tree;
    }

    public String getAncestralState() {

        NodeRef node;
        if (leafSet != null) {
            node = Tree.Utils.getCommonAncestorNode(tree, leafSet);
            if (node == null) throw new RuntimeException("No node found that is MRCA of " + leafSet);
        } else {
            node = tree.getRoot();
        }

        return ancestralTrait.getTraitString(tree, node);
    }

    // **************************************************************
    // Loggable IMPLEMENTATION
    // **************************************************************

    public LogColumn[] getColumns() {
        LogColumn[] columns = new LogColumn[1];
        columns[0] = new LogColumn.Abstract(name) {

            protected String getFormattedValue() {
                return getAncestralState();
            }
        };
        return columns;
    }

    private final Tree tree;
    private final TreeTrait ancestralTrait;
    private final String name;
    private Set<String> leafSet = null;

}