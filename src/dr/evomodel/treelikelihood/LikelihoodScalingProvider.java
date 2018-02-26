
package dr.evomodel.treelikelihood;


public interface LikelihoodScalingProvider {

    void getLogScalingFactors(int nodeNumber, double[] buffer);

    boolean arePartialsRescaled();

}
