
package dr.evomodel.branchratemodel;

import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.tree.TreeModel;
import dr.evomodel.tree.TreeParameterModel;
import dr.evomodel.tree.randomlocalmodel.RandomLocalTreeVariable;
import dr.evomodelxml.branchratemodel.RandomLocalClockModelParser;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;

import java.util.logging.Logger;

public class RandomLocalClockModel extends AbstractBranchRateModel
        implements RandomLocalTreeVariable {

    public RandomLocalClockModel(TreeModel treeModel,
                                 Parameter meanRateParameter,
                                 Parameter rateIndicatorParameter,
                                 Parameter ratesParameter,
                                 boolean ratesAreMultipliers) {

        super(RandomLocalClockModelParser.LOCAL_BRANCH_RATES);

        this.ratesAreMultipliers = ratesAreMultipliers;

        indicators = new TreeParameterModel(treeModel, rateIndicatorParameter, false);
        rates = new TreeParameterModel(treeModel, ratesParameter, false);

        rateIndicatorParameter.addBounds(new Parameter.DefaultBounds(1, 0, rateIndicatorParameter.getDimension()));
        ratesParameter.addBounds(new Parameter.DefaultBounds(Double.MAX_VALUE, 0, ratesParameter.getDimension()));

        for (int i = 0; i < rateIndicatorParameter.getDimension(); i++) {
            rateIndicatorParameter.setParameterValue(i, 0.0);
            ratesParameter.setParameterValue(i, 1.0);
        }

        this.meanRateParameter = meanRateParameter;

        addModel(treeModel);
        this.treeModel = treeModel;

        addModel(indicators);
        addModel(rates);
        if (meanRateParameter != null) addVariable(meanRateParameter);

        unscaledBranchRates = new double[treeModel.getNodeCount()];

        Logger.getLogger("dr.evomodel").info("  indicator parameter name is '" + rateIndicatorParameter.getId() + "'");

        recalculateScaleFactor();
    }

    public final double getVariable(Tree tree, NodeRef node) {
        return rates.getNodeValue(tree, node);
    }

    public final boolean isVariableSelected(Tree tree, NodeRef node) {
        return indicators.getNodeValue(tree, node) > 0.5;
    }

    public void handleModelChangedEvent(Model model, Object object, int index) {
        recalculationNeeded = true;
        fireModelChanged();
    }

    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        recalculationNeeded = true;
        fireModelChanged();
    }

    protected void storeState() {
    }

    protected void restoreState() {
        recalculateScaleFactor();
    }

    protected void acceptState() {
    }

    public double getBranchRate(final Tree tree, final NodeRef node) {
        if (recalculationNeeded) {
            recalculateScaleFactor();
            recalculationNeeded = false;
        }
        return unscaledBranchRates[node.getNumber()] * scaleFactor;
    }

    private void calculateUnscaledBranchRates(TreeModel tree) {
        cubr(tree, tree.getRoot(), 1.0);
    }

    private void cubr(TreeModel tree, NodeRef node, double rate) {

        int nodeNumber = node.getNumber();

        if (!tree.isRoot(node)) {
            if (isVariableSelected(tree, node)) {
                if (ratesAreMultipliers) {
                    rate *= getVariable(tree, node);
                } else {
                    rate = getVariable(tree, node);
                }
            }
        }
        unscaledBranchRates[nodeNumber] = rate;

        int childCount = tree.getChildCount(node);
        for (int i = 0; i < childCount; i++) {
            cubr(tree, tree.getChild(node, i), rate);
        }
    }

    private void recalculateScaleFactor() {

        calculateUnscaledBranchRates(treeModel);

        double timeTotal = 0.0;
        double branchTotal = 0.0;

        for (int i = 0; i < treeModel.getNodeCount(); i++) {
            NodeRef node = treeModel.getNode(i);
            if (!treeModel.isRoot(node)) {

                double branchInTime =
                        treeModel.getNodeHeight(treeModel.getParent(node)) -
                                treeModel.getNodeHeight(node);

                double branchLength = branchInTime * unscaledBranchRates[node.getNumber()];

                timeTotal += branchInTime;
                branchTotal += branchLength;
            }
        }

        scaleFactor = timeTotal / branchTotal;

        if (meanRateParameter != null)
            scaleFactor *= meanRateParameter.getParameterValue(0);
    }

    // AR - as TreeParameterModels are now loggable, the indicator parameter should be logged
    // directly.
//    private static String[] attributeLabel = {"changed"};
//
//    public String[] getNodeAttributeLabel() {
//        return attributeLabel;
//    }
//
//    public String[] getAttributeForNode(Tree tree, NodeRef node) {
//
//        if (tree.isRoot(node)) {
//            return new String[]{"false"};
//        }
//
//        return new String[]{(isVariableSelected((TreeModel) tree, node) ? "true" : "false")};
//    }

    // the scale factor necessary to maintain the mean rate
    private double scaleFactor;

    // the tree model
    private TreeModel treeModel;

    // true if the rate variables are treated as relative
    // to the parent rate rather than absolute rates
    private boolean ratesAreMultipliers = false;

    // the unscaled rates of each branch, taking into account the indicators
    private double[] unscaledBranchRates;

    // the mean rate across all the tree, if null then mean rate is scaled to 1.0
    private Parameter meanRateParameter;

    private TreeParameterModel indicators;
    private TreeParameterModel rates;

    boolean recalculationNeeded = true;
}