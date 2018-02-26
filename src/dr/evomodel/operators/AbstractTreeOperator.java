
package dr.evomodel.operators;

import dr.evomodel.tree.TreeModel;
import dr.evolution.tree.*;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;

public abstract class AbstractTreeOperator extends SimpleMCMCOperator {

	private int transitions = 0;

    public int getTransitions() {
    	return transitions;
    }

    public void setTransitions(int transitions) {
    	this.transitions = transitions;
    }

    public double getTransistionProbability() {
        final int accepted = getAcceptCount();
        final int rejected = getRejectCount();
        final int transition = getTransitions();
        return (double) transition / (double) (accepted + rejected);
    }

	protected void exchangeNodes(TreeModel tree, NodeRef i, NodeRef j,
	                             NodeRef iP, NodeRef jP) throws OperatorFailedException {

	    tree.beginTreeEdit();
	    tree.removeChild(iP, i);
	    tree.removeChild(jP, j);
	    tree.addChild(jP, i);
	    tree.addChild(iP, j);

        tree.endTreeEdit();
	}

	public void reset() {
        super.reset();
        transitions = 0;
    }

    protected NodeRef getOtherChild(Tree tree, NodeRef parent, NodeRef child) {
        if( tree.getChild(parent, 0) == child ) {
            return tree.getChild(parent, 1);
        } else {
            return tree.getChild(parent, 0);
        }
    }
}
