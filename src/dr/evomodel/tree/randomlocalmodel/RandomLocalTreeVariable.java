package dr.evomodel.tree.randomlocalmodel;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;

public interface RandomLocalTreeVariable {

    double getVariable(Tree tree, NodeRef node);

    boolean isVariableSelected(Tree tree, NodeRef node);
}
