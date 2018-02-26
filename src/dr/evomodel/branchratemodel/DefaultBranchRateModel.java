package dr.evomodel.branchratemodel;
import dr.evolution.tree.*;
import dr.inference.model.Model;
import dr.inference.model.ModelListener;
import dr.inference.model.Variable;
public final class DefaultBranchRateModel implements BranchRateModel {
    public double getBranchRate(Tree tree, NodeRef node) {
        return 1.0;
    }
    public void addModelListener(ModelListener listener) {
        // nothing to do
    }
    public void removeModelListener(ModelListener listener) {
        // nothing to do
    }
    public void storeModelState() {
        // nothing to do
    }
    public void restoreModelState() {
        // nothing to do
    }
    public void acceptModelState() {
        // nothing to do
    }
    public boolean isValidState() {
        return true;
    }
    public int getModelCount() {
        return 0;
    }
    public Model getModel(int i) {
        return null;
    }
    public int getVariableCount() {
        return 0;
    }
    public Variable getVariable(int i) {
        return null;
    }
    public String getModelName() {
        return null;
    }
    public String getId() {
        return null;
    }
    public void setId(String id) {
        // nothing to do
    }
    public boolean isUsed() {
        return false;
    }
    public String getTraitName() {
        return RATE;
    }
    public Intent getIntent() {
        return Intent.BRANCH;
    }
    public boolean getLoggable() {
        return true;
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
    public int getDimension() {
        return 1;
    }
    public Double getTrait(final Tree tree, final NodeRef node) {
        return getBranchRate(tree, node);
    }
    public String getTraitString(final Tree tree, final NodeRef node) {
        return Double.toString(getBranchRate(tree, node));
    }
}
