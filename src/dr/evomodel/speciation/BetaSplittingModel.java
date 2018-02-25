package dr.evomodel.speciation;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodelxml.speciation.BetaSplittingModelParser;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
import dr.math.GammaFunction;
public class BetaSplittingModel extends BranchingModel {
public BetaSplittingModel(Parameter phiParameter, Tree tree) {
super(BetaSplittingModelParser.BETA_SPLITTING_MODEL);
this.phiParameter = phiParameter;
addVariable(phiParameter);
phiParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, 1));
N = tree.getExternalNodeCount();
logProbs = new double[N + 1][N + 1];
storedLogProbs = new double[N + 1][N + 1];
makeSplitProbs(logProbs);
}
public double getPhi() {
return phiParameter.getParameterValue(0);
}
public void setPhi(double phi) {
phiParameter.setParameterValue(0, phi);
}
public double getBeta() {
return (Math.exp(getPhi()) - 1) * 2.0;
}
public void setBeta(double beta) {
if (beta < -2.0) throw new IllegalArgumentException();
setPhi(Math.log(beta / 2.0 + 1));
}
public double logNodeProbability(Tree tree, NodeRef node) {
if (tree.isExternal(node)) return 0.0;
int leftChildren = Tree.Utils.getLeafCount(tree, tree.getChild(node, 0));
int rightChildren = Tree.Utils.getLeafCount(tree, tree.getChild(node, 1));
// calculate the probability of this pair..
return logProbs[leftChildren + rightChildren][leftChildren] +
logProbs[leftChildren + rightChildren][rightChildren];
}
// **************************************************************
// XMLElement IMPLEMENTATION
// **************************************************************
protected final void handleVariableChangedEvent(Variable variable, int index, Parameter.ChangeType type) {
//System.out.println("parameter changed:" + parameter.getParameterName());
makeSplitProbs(logProbs);
}
protected void storeState() {
//copy the current logProbs into the storedLogProbs
for (int i = 0; i < logProbs.length; i++) {
System.arraycopy(logProbs[i], 0, storedLogProbs[i], 0, logProbs[i].length);
}
}
protected void restoreState() {
// swap the logProbs arrays
double[][] tmp = logProbs;
logProbs = storedLogProbs;
storedLogProbs = tmp;
}
private void makeSplitProbs(double[][] logProbs) {
// code modified from original code by Marc Suchard
double beta = getBeta();
//double[][] prob = new double[bigN+1][bigN+1];
logProbs[2][1] = 0;
logProbs[3][1] = logProbs[3][2] = Math.log(0.5);
//if( beta < 90 ) {
double[] logGammaBeta = new double[N];
double[] logGammaNone = new double[N];
for (int i = 1; i < N; i++) {
logGammaBeta[i] = GammaFunction.lnGamma(beta + i + 1.0); //Sfun.gamma(beta + i + 1.0);
logGammaNone[i] = GammaFunction.lnGamma(i + 1.0); //Sfun.gamma(i + 1.0);
}
for (int n = 4; n <= N; n++) {
double end = (n / 2.0) + 0.5;
for (int i = 1; i <= end; i++)
logProbs[n][i] = logProbs[n][n - i] =
logGammaBeta[i] + logGammaBeta[n - i] - logGammaNone[i] - logGammaNone[n - i];
// Normalize
double sum = 0;
for (int i = 1; i < n; i++) {
sum += Math.exp(logProbs[n][i]);
}
double logSum = Math.log(sum);
// divide the probabilities through by the sum
for (int i = 1; i < n; i++) {
logProbs[n][i] -= logSum;
//System.out.println("logProbs[" + n + "][" + i + "]=" + logProbs[n][i] );
}
}
//System.err.println("Log scale calculations");
double[] logGammaBeta = new double[bigN];
double[] logGammaNone = new double[bigN];
for(int i=1; i<bigN; i++) {
logGammaBeta[i] = Sfun.logGamma(beta + i + 1.0);
logGammaNone[i] = Sfun.logGamma(i + 1.0);
//System.err.print(beta+" "+logGammaBeta[i] + " "+logGammaNone[i]);
//System.err.println(" "+gammln(beta + i + 1.0));
}
double c = logGammaBeta[1];
logGammaBeta[1] = 0;
for(int i=2; i<bigN; i++)
logGammaBeta[i] -= c;
for(int n=4; n<=bigN; n++) {
double end = (n / 2.0) + 0.5;
for(int i=1; i<=end; i++) {
prob[n][i] = prob[n][n-i] = Math.exp(logGammaBeta[i] + logGammaBeta[n-i] - logGammaNone[i] - logGammaNone[n-i]);
//System.err.println(prob[n][i]);
}
double sum = 0;
for(int i=1; i<n; i++)
sum += prob[n][i];
for(int i=1; i<n; i++)
prob[n][i] /= sum;
}
}      */
//return prob;
}
//Protected stuff
final Parameter phiParameter;
double[][] logProbs;
double[][] storedLogProbs;
final int N;
}
