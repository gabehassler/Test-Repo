package dr.evomodel.newtreelikelihood;
import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.sitemodel.SiteModel;
public interface LikelihoodCore {
boolean canHandleTipPartials();
boolean canHandleTipStates();    
//    boolean canHandleDynamicRescaling();
void initialize(int nodeCount, int stateTipCount, int patternCount, int matrixCount);
void finalize() throws Throwable;
void setTipPartials(int tipIndex, double[] partials);
void setTipStates(int tipIndex, int[] states);
void updateSubstitutionModel(SubstitutionModel substitutionModel);
void updateSiteModel(SiteModel siteModel);
void updateMatrices(int[] branchUpdateIndices, double[] branchLengths, int branchUpdateCount);
void updatePartials(int[] operations, int[] dependencies, int operationCount, boolean rescale);
void calculateLogLikelihoods(int rootNodeIndex, double[] outLogLikelihoods);
void storeState();
void restoreState();
}