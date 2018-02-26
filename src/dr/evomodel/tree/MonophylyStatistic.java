
package dr.evomodel.tree;

import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.inference.model.BooleanStatistic;

import java.util.Collections;
import java.util.Set;

public class MonophylyStatistic extends BooleanStatistic implements TreeStatistic {

    public MonophylyStatistic(String name, Tree tree, TaxonList taxa, TaxonList ignore) throws Tree.MissingTaxonException {

        this(name, tree, taxa, ignore, false);

    }

    public MonophylyStatistic(String name, Tree tree, TaxonList taxa, TaxonList ignore, boolean inverse) throws Tree.MissingTaxonException {

        super(name);
        this.tree = tree;
        this.leafSet = Tree.Utils.getLeavesForTaxa(tree, taxa);
        if (ignore != null) {
            this.ignoreLeafSet = Tree.Utils.getLeavesForTaxa(tree, ignore);
        } else {
            this.ignoreLeafSet = Collections.emptySet();
        }
        this.inverse = inverse;

    }

    public void setTree(Tree tree) {
        this.tree = tree;
    }

    public Tree getTree() {
        return tree;
    }

    public int getDimension() {
        return 1;
    }

    public boolean getBoolean(int dim) {
        boolean monophyletic = Tree.Utils.isMonophyletic(this.tree, this.leafSet, this.ignoreLeafSet);
        if (inverse){
            return !monophyletic;
        } else {
            return monophyletic;
        }
    }

    private Tree tree = null;
    private Set<String> leafSet = null;
    private Set<String> ignoreLeafSet = null;
    private boolean inverse = false;

}
