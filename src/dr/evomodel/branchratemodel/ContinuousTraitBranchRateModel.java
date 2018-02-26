package dr.evomodel.branchratemodel;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.continuous.SampledMultivariateTraitLikelihood;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.branchratemodel.ContinuousTraitBranchRateModelParser;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class ContinuousTraitBranchRateModel extends AbstractBranchRateModel {
    private final String trait;
    private final int dimension;
    private final Parameter rateParameter;
    private final Parameter ratioParameter;
    private SampledMultivariateTraitLikelihood traitLikelihood;
    public ContinuousTraitBranchRateModel(SampledMultivariateTraitLikelihood traitLikelihood, int dimension) {
        super(ContinuousTraitBranchRateModelParser.TRAIT_BRANCH_RATES);
        this.traitLikelihood = traitLikelihood;
        this.trait = traitLikelihood.getTraitName();
        this.dimension = dimension;
        this.rateParameter = null;
        this.ratioParameter = null;
        addModel(traitLikelihood);
    }
    public ContinuousTraitBranchRateModel(String trait, Parameter rateParameter, Parameter ratioParameter) {
        super(ContinuousTraitBranchRateModelParser.TRAIT_BRANCH_RATES);
        this.trait = trait;
        dimension = 0;
        this.rateParameter = rateParameter;
        this.ratioParameter = ratioParameter;
        if (rateParameter != null) {
            addVariable(rateParameter);
        }
        if (ratioParameter != null) {
            addVariable(ratioParameter);
        }
    }
    public void handleModelChangedEvent(Model model, Object object, int index) {
        fireModelChanged();
    }
    protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
        fireModelChanged();
    }
    protected void storeState() {
    }
    protected void restoreState() {
    }
    protected void acceptState() {
    }
    public double getBranchRate(final Tree tree, final NodeRef node) {
        NodeRef parent = tree.getParent(node);
        if (parent == null) {
            throw new IllegalArgumentException("Root does not have a valid rate");
        }
        double rate = 1.0;
        TreeModel treeModel = (TreeModel) tree;
        if (rateParameter != null) {
            double scale = 1.0;
            double ratio = 1.0;
            if (rateParameter != null) {
                scale = rateParameter.getParameterValue(0);
            }
            if (ratioParameter != null) {
                ratio = ratioParameter.getParameterValue(0);
            }
            // get the log rate for the node and its parent
            double rate1 = ratio * treeModel.getMultivariateNodeTrait(node, trait)[0];
            double rate2 = ratio * treeModel.getMultivariateNodeTrait(parent, trait)[0];
            if (rate1 == rate2) {
                return scale * Math.exp(rate1);
            }
            rate = scale * (Math.exp(rate2) - Math.exp(rate1)) / (rate2 - rate1);
        } else {
            double rate1 =  treeModel.getMultivariateNodeTrait(node, trait)[dimension];
            double rate2 =  treeModel.getMultivariateNodeTrait(parent, trait)[dimension];
            if (rate1 == rate2) {
                return Math.exp(rate1);
            }
            rate = (Math.exp(rate2) - Math.exp(rate1)) / (rate2 - rate1); // TODO Should this not be averaged on the log-scale?
        }
        return rate;
    }
}