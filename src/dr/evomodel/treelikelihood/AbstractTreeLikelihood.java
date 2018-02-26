
package dr.evomodel.treelikelihood;

import dr.evolution.alignment.PatternList;
import dr.evolution.datatype.DataType;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.inference.model.AbstractModelLikelihood;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.xml.Reportable;


public abstract class AbstractTreeLikelihood extends AbstractModelLikelihood implements Reportable {

    protected static final boolean COUNT_TOTAL_OPERATIONS = true;

    public AbstractTreeLikelihood(String name, PatternList patternList,
                                  TreeModel treeModel) {

        super(name);

        this.patternList = patternList;
        this.dataType = patternList.getDataType();
        patternCount = patternList.getPatternCount();
        stateCount = dataType.getStateCount();

        patternWeights = patternList.getPatternWeights();

        this.treeModel = treeModel;
        addModel(treeModel);

        nodeCount = treeModel.getNodeCount();

        updateNode = new boolean[nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            updateNode[i] = true;
        }

        likelihoodKnown = false;

    }

    protected final void setStates(LikelihoodCore likelihoodCore, PatternList patternList,
                                   int sequenceIndex, int nodeIndex) {
        int i;

        int[] states = new int[patternCount];

        for (i = 0; i < patternCount; i++) {

            states[i] = patternList.getPatternState(sequenceIndex, i);
        }

        likelihoodCore.setNodeStates(nodeIndex, states);
    }

    public TreeModel getTreeModel() {
        return treeModel;
    }

    protected final void setPartials(LikelihoodCore likelihoodCore, PatternList patternList,
                                     int categoryCount,
                                     int sequenceIndex, int nodeIndex) {
        double[] partials = new double[patternCount * stateCount];

        boolean[] stateSet;

        int v = 0;
        for (int i = 0; i < patternCount; i++) {

            int state = patternList.getPatternState(sequenceIndex, i);
            stateSet = dataType.getStateSet(state);

            for (int j = 0; j < stateCount; j++) {
                if (stateSet[j]) {
                    partials[v] = 1.0;
                } else {
                    partials[v] = 0.0;
                }
                v++;
            }
        }

        likelihoodCore.setNodePartials(nodeIndex, partials);
    }

    protected final void setMissingStates(LikelihoodCore likelihoodCore, int nodeIndex) {
        int[] states = new int[patternCount];

        for (int i = 0; i < patternCount; i++) {
            states[i] = dataType.getGapState();
        }

        likelihoodCore.setNodeStates(nodeIndex, states);
    }

    protected final void setMissingPartials(LikelihoodCore likelihoodCore, int nodeIndex) {
        double[] partials = new double[patternCount * stateCount];

        int v = 0;
        for (int i = 0; i < patternCount; i++) {
            for (int j = 0; j < stateCount; j++) {
                partials[v] = 1.0;
                v++;
            }
        }

        likelihoodCore.setNodePartials(nodeIndex, partials);
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

    protected void updatePattern(int i) {
        if (updatePattern != null) {
            updatePattern[i] = true;
        }
        likelihoodKnown = false;
    }

    protected void updateAllPatterns() {
        if (updatePattern != null) {
            for (int i = 0; i < patternCount; i++) {
                updatePattern[i] = true;
            }
        }
        likelihoodKnown = false;
    }

    public final double[] getPatternWeights() {
        return patternWeights;
    }

    public final int getPatternCount() {
        return patternCount;
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

    public final PatternList getPatternList() {
        return patternList;
    }

    public final double getLogLikelihood() {
        if (!likelihoodKnown) {
            logLikelihood = calculateLogLikelihood();
            likelihoodKnown = true;
        }
        return logLikelihood;
    }

    public void makeDirty() {
        likelihoodKnown = false;
        updateAllNodes();
        updateAllPatterns();
    }

    protected abstract double calculateLogLikelihood();

    public String getReport() {
        getLogLikelihood();
        return getClass().getName() + "(" + logLikelihood + ") total operations = " + totalOperationCount;

    }

    // **************************************************************
    // INSTANCE VARIABLES
    // **************************************************************

    protected TreeModel treeModel = null;

    protected PatternList patternList = null;

    protected DataType dataType = null;

    protected double[] patternWeights;

    protected int patternCount;

    protected int stateCount;

    protected int nodeCount;

    protected boolean[] updatePattern = null;

    protected boolean[] updateNode;

    private double logLikelihood;
    private double storedLogLikelihood;
    protected boolean likelihoodKnown = false;
    private boolean storedLikelihoodKnown = false;

    protected int totalOperationCount = 0;
}