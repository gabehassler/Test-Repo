
package dr.app.beagle.evomodel.treelikelihood;

import dr.evolution.tree.TreeTrait;
import dr.evomodel.tree.TreeModel;

public interface AncestralStateTraitProvider {

    public String getId();

    public TreeModel getTreeModel();

    public TreeTrait getTreeTrait(String key);

    public String formattedState(int[] state);

}
