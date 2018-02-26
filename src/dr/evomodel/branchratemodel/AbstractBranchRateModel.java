package dr.evomodel.branchratemodel;
import dr.evolution.tree.*;
import dr.inference.model.*;
public abstract class AbstractBranchRateModel extends AbstractModelLikelihood implements BranchRateModel {
    public AbstractBranchRateModel(String name) {
        super(name);
    }
    public String getTraitName() {
        return BranchRateModel.RATE;
    }
    public Intent getIntent() {
        return Intent.BRANCH;
    }
    public TreeTrait getTreeTrait(final String key) {
        return this;
    }
    public TreeTrait[] getTreeTraits() {
        return new TreeTrait[] { this };
    }
    public Class getTraitClass() {
        return Double.class;
    }
    public boolean getLoggable() {
        return true;
    }
    public Double getTrait(final Tree tree, final NodeRef node) {
        return getBranchRate(tree, node);
    }
    public String getTraitString(final Tree tree, final NodeRef node) {
        return Double.toString(getBranchRate(tree, node));
    }
    public Model getModel() {
        return this;
    }
    public double getLogLikelihood() {
        return 0;
    }
    public void makeDirty() {
        // Do nothing
    }
}