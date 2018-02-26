
package dr.app.beagle.evomodel.treelikelihood;

import dr.evolution.alignment.PatternList;
import dr.evolution.datatype.DataType;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.*;
import dr.xml.Reportable;


public abstract class AbstractTreeLikelihood extends AbstractModelLikelihood implements Reportable {

    protected static final boolean COUNT_TOTAL_OPERATIONS = false;

    public AbstractTreeLikelihood(String name, TreeModel treeModel) {

        super(name);

        this.treeModel = treeModel;
        addModel(treeModel);

        nodeCount = treeModel.getNodeCount();

        updateNode = new boolean[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            updateNode[i] = true;
        }

        likelihoodKnown = false;

    }


    protected void updateNode(NodeRef node) {

        updateNode[node.getNumber()] = true;
        likelihoodKnown = false;
    }

    protected void updateNodeAndChildren(NodeRef node) {
        updateNode[node.getNumber()] = true;

        for (int i = 0; i < treeModel.getChildCount(node); i++) {
            NodeRef child = treeModel.getChild(node, i);
            updateNode[child.getNumber()] = true;
        }
        likelihoodKnown = false;
    }

    protected void updateNodeAndDescendents(NodeRef node) {
        updateNode[node.getNumber()] = true;

        for (int i = 0; i < treeModel.getChildCount(node); i++) {
            NodeRef child = treeModel.getChild(node, i);
            updateNodeAndDescendents(child);
        }

        likelihoodKnown = false;
    }

    protected void updateAllNodes() {
        for (int i = 0; i < nodeCount; i++) {
            updateNode[i] = true;
        }
        likelihoodKnown = false;
    }

    // **************************************************************
    // VariableListener IMPLEMENTATION
    // **************************************************************

    protected void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        // do nothing
    }

    // **************************************************************
    // Model IMPLEMENTATION
    // **************************************************************

    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (COUNT_TOTAL_OPERATIONS)
            totalModelChangedCount++;
        likelihoodKnown = false;
    }

    protected void storeState() {

        storedLikelihoodKnown = likelihoodKnown;
        storedLogLikelihood = logLikelihood;
    }

    protected void restoreState() {

        likelihoodKnown = storedLikelihoodKnown;
        logLikelihood = storedLogLikelihood;
    }

    protected void acceptState() {
    } // nothing to do

    // **************************************************************
    // Likelihood IMPLEMENTATION
    // **************************************************************

    public final Model getModel() {
        return this;
    }

    public final double getLogLikelihood() {
        if (COUNT_TOTAL_OPERATIONS)
            totalGetLogLikelihoodCount++;
        if (CompoundLikelihood.DEBUG_PARALLEL_EVALUATION) {
            System.err.println((likelihoodKnown ? "lazy" : "evaluate"));
        }
        if (!likelihoodKnown) {
            if (COUNT_TOTAL_OPERATIONS)
                totalCalculateLikelihoodCount++;
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood;
    }

    public void makeDirty() {
        if (COUNT_TOTAL_OPERATIONS)
            totalMakeDirtyCount++;
        likelihoodKnown = false;
        updateAllNodes();
    }
    
    public boolean isLikelihoodKnown() {
    	return likelihoodKnown;
    }

    protected abstract double calculateLogLikelihood();

    public String getReport() {
        if (hasInitialized) {
            String rtnValue =  getClass().getName() + "(" + getLogLikelihood() + ")";
            if (COUNT_TOTAL_OPERATIONS)
             rtnValue += " total operations = " + totalOperationCount +
                         " matrix updates = " + totalMatrixUpdateCount + " model changes = " + totalModelChangedCount +
                         " make dirties = " + totalMakeDirtyCount +
                         " calculate likelihoods = " + totalCalculateLikelihoodCount +
                         " get likelihoods = " + totalGetLogLikelihoodCount +
                         " all rate updates = " + totalRateUpdateAllCount +
                         " partial rate updates = " + totalRateUpdateSingleCount;
            return rtnValue;
        } else {
            return getClass().getName() + "(uninitialized)";
        }
    }

    // **************************************************************
    // INSTANCE VARIABLES
    // **************************************************************

    protected TreeModel treeModel = null;

    protected int nodeCount;

    protected boolean[] updateNode;

    private double logLikelihood;
    private double storedLogLikelihood;
    protected boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown = false;

    protected boolean hasInitialized = false;

    protected int totalOperationCount = 0;
    protected int totalMatrixUpdateCount = 0;
    protected int totalGetLogLikelihoodCount = 0;
    protected int totalModelChangedCount = 0;
    protected int totalMakeDirtyCount = 0;
    protected int totalCalculateLikelihoodCount = 0;
    protected int totalRateUpdateAllCount = 0;
    protected int totalRateUpdateSingleCount = 0;

}