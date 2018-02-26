
package dr.evomodel.speciation;

import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.speciation.BranchingLikelihoodParser;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;


public class BranchingLikelihood extends AbstractModelLikelihood {

    // PUBLIC STUFF

    public BranchingLikelihood(Tree tree, BranchingModel branchingModel) {
        this(BranchingLikelihoodParser.BRANCHING_LIKELIHOOD, tree, branchingModel);
    }

    public BranchingLikelihood(String name, Tree tree, BranchingModel branchingModel) {

        super(name);

        this.tree = tree;
        this.branchingModel = branchingModel;
        if (tree instanceof TreeModel) {
            addModel((TreeModel) tree);
        }
        if (branchingModel != null) {
            addModel(branchingModel);
        }
    }

    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************


    protected void handleModelChangedEvent(Model model, Object object, int index) {
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
    }

    protected void storeState() {
    }

    protected void restoreState() {
    }

    protected final void acceptState() {
    } // nothing to do

    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

    public final Model getModel() {
        return this;
    }

    public final double getLogLikelihood() {
        return calculateLogLikelihood();

    }

    public final void makeDirty() {
    }

    public double calculateLogLikelihood() {

        double logL = 0.0;
        for (int j = 0; j < tree.getInternalNodeCount(); j++) {
            logL += branchingModel.logNodeProbability(tree, tree.getInternalNode(j));
        }
        //System.err.println("logL=" + logL);
        return logL;
    }

    // **************************************************************
    // XMLElement IMPLEMENTATION
    // **************************************************************

    public org.w3c.dom.Element createElement(org.w3c.dom.Document d) {
        throw new RuntimeException("createElement not implemented");
    }

    // ****************************************************************
    // Private and protected stuff
    // ****************************************************************

    BranchingModel branchingModel = null;

    Tree tree = null;
}