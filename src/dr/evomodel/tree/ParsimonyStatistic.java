
package dr.evomodel.tree;

import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.inference.model.Statistic;

import java.util.Set;


public class ParsimonyStatistic extends Statistic.Abstract implements TreeStatistic {

    public ParsimonyStatistic(String name, Tree tree, TaxonList taxa) throws Tree.MissingTaxonException {

        super(name);
        this.tree = tree;
        this.leafSet = Tree.Utils.getLeavesForTaxa(tree, taxa);
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

    public double getStatisticValue(int dim) {

        return Tree.Utils.getParsimonySteps(tree, leafSet);
    }

    private Tree tree = null;
    private Set leafSet = null;

}
