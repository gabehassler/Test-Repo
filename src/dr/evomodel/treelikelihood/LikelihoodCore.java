package dr.evomodel.treelikelihood;
public interface LikelihoodCore extends LikelihoodPartialsProvider {
    void initialize(int nodeCount, int patternCount, int matrixCount, boolean integrateCategories);
    void finalize() throws java.lang.Throwable;
    void createNodePartials(int nodeIndex);
    void setNodePartials(int nodeIndex, double[] partials);
    void setNodeStates(int nodeIndex, int[] states);
    void createNodeStates(int nodeIndex);
    void setNodeMatrixForUpdate(int nodeIndex);
    void setNodeMatrix(int nodeIndex, int matrixIndex, double[] matrix);
    void setNodePartialsForUpdate(int nodeIndex);
    void setCurrentNodePartials(int nodeIndex, double[] partials);
    void calculatePartials(int nodeIndex1, int nodeIndex2, int nodeIndex3);
    void calculatePartials(int nodeIndex1, int nodeIndex2, int nodeIndex3, int[] matrixMap);
    public void getPartials(int nodeIndex, double[] outPartials);
    void integratePartials(int nodeIndex, double[] proportions, double[] outPartials);
    void calculateLogLikelihoods(double[] partials, double[] frequencies, double[] outLogLikelihoods);
    void setUseScaling(boolean useScaling);
    double getLogScalingFactor(int pattern);
    boolean arePartialsRescaled();
    void getLogScalingFactors(int nodeIndex, double[] buffer);
    void storeState();
    void restoreState();
}
