package dr.evomodel.treelikelihood;
public interface LikelihoodPartialsProvider extends LikelihoodScalingProvider {
void getPartials(int nodeNumber, double[] partialsVector);
}
