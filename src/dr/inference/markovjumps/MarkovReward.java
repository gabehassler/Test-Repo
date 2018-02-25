package dr.inference.markovjumps;
public interface MarkovReward { // TODO Rename
double[] computePdf(double reward, double branchLength);
double computePdf(double reward, double branchLength, int i, int j);
double computeCdf(double x, double time, int i, int j);
double computeConditionalProbability(double branchLength, int i, int j);
}
