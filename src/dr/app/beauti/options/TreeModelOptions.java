
package dr.app.beauti.options;


import dr.app.beauti.types.PriorType;
import dr.evolution.util.Taxa;

import java.util.List;


public class TreeModelOptions extends ModelOptions {
    private static final long serialVersionUID = 5328826852511460749L;

    // Instance variables
    private final BeautiOptions options;


    public TreeModelOptions(BeautiOptions options) {
        this.options = options;

        initGlobalTreeModelParaAndOpers();
    }

    private void initGlobalTreeModelParaAndOpers() {

    }

    public void selectParameters(List<Parameter> params) {

    }

    public void selectOperators(List<Operator> ops) {

    }

    /////////////////////////////////////////////////////////////
//    public double getRandomStartingTreeInitialRootHeight(PartitionTreeModel model) {
//    	Parameter rootHeight = model.getParameter("treeModel.rootHeight");
//
//    	if (rootHeight.priorType != PriorType.NONE_TREE_PRIOR) {
//    		return rootHeight.initial;
//    	} else {
//    		return calculateMeanDistance(model.getDataPartitions());
//    	}
//
//    }

    public double getExpectedAvgBranchLength(double rootHeight) {
        double sum = 0;
        int taxonCount = options.taxonList.getTaxonCount();

        for (int i = 2; i <= taxonCount; i++) {
            sum += (double) 1 / i;
        }

        return rootHeight * sum / (double) (2 * taxonCount - 2);
    }

    public int isNodeCalibrated(PartitionTreeModel treeModel) {
        if (isNodeCalibrated(treeModel.getParameter("treeModel.rootHeight"))) {
            return 0; // root node
        } else if (options.getKeysFromValue(options.taxonSetsTreeModel, treeModel).size() > 0) {
            Taxa taxonSet = (Taxa) options.getKeysFromValue(options.taxonSetsTreeModel, treeModel).get(0);
            Parameter tmrca = options.statistics.get(taxonSet);
            if (tmrca != null && isNodeCalibrated(tmrca)) {
                return 1; // internal node (tmrca) with a proper prior
            }
            return -1;
        } else {
            return -1;
        }
    }

    public boolean isNodeCalibrated(Parameter para) {
        return (para.taxaId != null && hasProperPriorOn(para)) // param.taxa != null is TMRCA
                || (para.getBaseName().endsWith("treeModel.rootHeight") && hasProperPriorOn(para));
    }

    private boolean hasProperPriorOn(Parameter para) {
        return para.priorType == PriorType.EXPONENTIAL_PRIOR
//                || para.priorType == PriorType.TRUNC_NORMAL_PRIOR
                || (para.priorType == PriorType.UNIFORM_PRIOR && para.uniformLower > 0 && para.uniformUpper < Double.POSITIVE_INFINITY)
                || para.priorType == PriorType.LAPLACE_PRIOR
                || para.priorType == PriorType.NORMAL_PRIOR
                || para.priorType == PriorType.LOGNORMAL_PRIOR
                || para.priorType == PriorType.GAMMA_PRIOR
                || para.priorType == PriorType.INVERSE_GAMMA_PRIOR
                || para.priorType == PriorType.BETA_PRIOR
                || para.priorType == PriorType.CTMC_RATE_REFERENCE_PRIOR
                || para.priorType == PriorType.LOGNORMAL_HPM_PRIOR
                || para.priorType == PriorType.POISSON_PRIOR;
    }

}
