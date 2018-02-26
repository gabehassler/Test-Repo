package dr.evomodel.branchratemodel;
import dr.evolution.tree.*;
import dr.inference.model.Model;
public interface BranchRateModel extends Model, BranchRates, TreeTraitProvider, TreeTrait<Double> {
    public static final String BRANCH_RATES = "branchRates";
    public static final String RATE = "rate";
    // This is inherited from BranchRates:
    // double getBranchRate(Tree tree, NodeRef node);
}
