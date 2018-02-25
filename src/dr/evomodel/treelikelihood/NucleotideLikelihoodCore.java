package dr.evomodel.treelikelihood;
public class NucleotideLikelihoodCore extends AbstractLikelihoodCore {
public NucleotideLikelihoodCore() {
super(4);
}
protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
int[] states2, double[] matrices2,
double[] partials3)
{
int v = 0;
int u = 0;
for (int j = 0; j < matrixCount; j++) {
for (int k = 0; k < patternCount; k++) {
int w = u;
int state1 = states1[k];
int state2 = states2[k];
if (state1 < 4 && state2 < 4) {
partials3[v] = matrices1[w + state1] * matrices2[w + state2];
v++;	w += 4;
partials3[v] = matrices1[w + state1] * matrices2[w + state2];
v++;	w += 4;
partials3[v] = matrices1[w + state1] * matrices2[w + state2];
v++;	w += 4;
partials3[v] = matrices1[w + state1] * matrices2[w + state2];
v++;	w += 4;
} else if (state1 < 4) {
// child 2 has a gap or unknown state so don't use it
partials3[v] = matrices1[w + state1];
v++;	w += 4;
partials3[v] = matrices1[w + state1];
v++;	w += 4;
partials3[v] = matrices1[w + state1];
v++;	w += 4;
partials3[v] = matrices1[w + state1];
v++;	w += 4;
} else if (state2 < 4) {
// child 2 has a gap or unknown state so don't use it
partials3[v] = matrices2[w + state2];
v++;	w += 4;
partials3[v] = matrices2[w + state2];
v++;	w += 4;
partials3[v] = matrices2[w + state2];
v++;	w += 4;
partials3[v] = matrices2[w + state2];
v++;	w += 4;
} else {
// both children have a gap or unknown state so set partials to 1
partials3[v] = 1.0;
v++;
partials3[v] = 1.0;
v++;
partials3[v] = 1.0;
v++;
partials3[v] = 1.0;
v++;
}
}
u += matrixSize;
}
}
protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3)
{
int u = 0;
int v = 0;
int w = 0;
for (int l = 0; l < matrixCount; l++) {
for (int k = 0; k < patternCount; k++) {
int state1 = states1[k];
if (state1 < 4) {
double sum;
sum =	matrices2[w] * partials2[v];
sum +=	matrices2[w + 1] * partials2[v + 1];
sum +=	matrices2[w + 2] * partials2[v + 2];
sum +=	matrices2[w + 3] * partials2[v + 3];
partials3[u] = matrices1[w + state1] * sum;	u++;
sum =	matrices2[w + 4] * partials2[v];
sum +=	matrices2[w + 5] * partials2[v + 1];
sum +=	matrices2[w + 6] * partials2[v + 2];
sum +=	matrices2[w + 7] * partials2[v + 3];
partials3[u] = matrices1[w + 4 + state1] * sum;	u++;
sum =	matrices2[w + 8] * partials2[v];
sum +=	matrices2[w + 9] * partials2[v + 1];
sum +=	matrices2[w + 10] * partials2[v + 2];
sum +=	matrices2[w + 11] * partials2[v + 3];
partials3[u] = matrices1[w + 8 + state1] * sum;	u++;
sum =	matrices2[w + 12] * partials2[v];
sum +=	matrices2[w + 13] * partials2[v + 1];
sum +=	matrices2[w + 14] * partials2[v + 2];
sum +=	matrices2[w + 15] * partials2[v + 3];
partials3[u] = matrices1[w + 12 + state1] * sum;	u++;
v += 4;
} else {
// Child 1 has a gap or unknown state so don't use it
double sum;
sum =	matrices2[w] * partials2[v];
sum +=	matrices2[w + 1] * partials2[v + 1];
sum +=	matrices2[w + 2] * partials2[v + 2];
sum +=	matrices2[w + 3] * partials2[v + 3];
partials3[u] = sum;	u++;
sum =	matrices2[w + 4] * partials2[v];
sum +=	matrices2[w + 5] * partials2[v + 1];
sum +=	matrices2[w + 6] * partials2[v + 2];
sum +=	matrices2[w + 7] * partials2[v + 3];
partials3[u] = sum;	u++;
sum =	matrices2[w + 8] * partials2[v];
sum +=	matrices2[w + 9] * partials2[v + 1];
sum +=	matrices2[w + 10] * partials2[v + 2];
sum +=	matrices2[w + 11] * partials2[v + 3];
partials3[u] = sum;	u++;
sum =	matrices2[w + 12] * partials2[v];
sum +=	matrices2[w + 13] * partials2[v + 1];
sum +=	matrices2[w + 14] * partials2[v + 2];
sum +=	matrices2[w + 15] * partials2[v + 3];
partials3[u] = sum;	u++;
v += 4;
}
}
w += matrixSize;
}
}
protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3)
{
double sum1, sum2;
int u = 0;
int v = 0;
int w = 0;
for (int l = 0; l < matrixCount; l++) {
for (int k = 0; k < patternCount; k++) {
sum1 = matrices1[w] * partials1[v];
sum2 = matrices2[w] * partials2[v];
sum1 += matrices1[w + 1] * partials1[v + 1];
sum2 += matrices2[w + 1] * partials2[v + 1];
sum1 += matrices1[w + 2] * partials1[v + 2];
sum2 += matrices2[w + 2] * partials2[v + 2];
sum1 += matrices1[w + 3] * partials1[v + 3];
sum2 += matrices2[w + 3] * partials2[v + 3];
partials3[u] = sum1 * sum2; u++;
sum1 = matrices1[w + 4] * partials1[v];
sum2 = matrices2[w + 4] * partials2[v];
sum1 += matrices1[w + 5] * partials1[v + 1];
sum2 += matrices2[w + 5] * partials2[v + 1];
sum1 += matrices1[w + 6] * partials1[v + 2];
sum2 += matrices2[w + 6] * partials2[v + 2];
sum1 += matrices1[w + 7] * partials1[v + 3];
sum2 += matrices2[w + 7] * partials2[v + 3];
partials3[u] = sum1 * sum2; u++;
sum1 = matrices1[w + 8] * partials1[v];
sum2 = matrices2[w + 8] * partials2[v];
sum1 += matrices1[w + 9] * partials1[v + 1];
sum2 += matrices2[w + 9] * partials2[v + 1];
sum1 += matrices1[w + 10] * partials1[v + 2];
sum2 += matrices2[w + 10] * partials2[v + 2];
sum1 += matrices1[w + 11] * partials1[v + 3];
sum2 += matrices2[w + 11] * partials2[v + 3];
partials3[u] = sum1 * sum2; u++;
sum1 = matrices1[w + 12] * partials1[v];
sum2 = matrices2[w + 12] * partials2[v];
sum1 += matrices1[w + 13] * partials1[v + 1];
sum2 += matrices2[w + 13] * partials2[v + 1];
sum1 += matrices1[w + 14] * partials1[v + 2];
sum2 += matrices2[w + 14] * partials2[v + 2];
sum1 += matrices1[w + 15] * partials1[v + 3];
sum2 += matrices2[w + 15] * partials2[v + 3];
partials3[u] = sum1 * sum2; u++;
v += 4;
}
w += matrixSize;
}
}
protected void calculateStatesStatesPruning(int[] states1, double[] matrices1,
int[] states2, double[] matrices2,
double[] partials3, int[] matrixMap)
{
throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
}
protected void calculateStatesPartialsPruning(	int[] states1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3, int[] matrixMap)
{
throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
}
protected void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3, int[] matrixMap)
{
throw new RuntimeException("calculateStatesStatesPruning not implemented using matrixMap");
}
public void calculateIntegratePartials(double[] inPartials, double[] proportions, double[] outPartials) {
int u = 0;
int v = 0;
for (int k = 0; k < patternCount; k++) {
outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
outPartials[u] = inPartials[v] * proportions[0]; u++; v++;
}
for (int j = 1; j < matrixCount; j++) {
u = 0;
for (int k = 0; k < patternCount; k++) {
outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
outPartials[u] += inPartials[v] * proportions[j]; u++; v++;
}
}
}
public void calculateLogLikelihoods(double[] partials, double[] frequencies, double[] outLogLikelihoods)
{
int v = 0;
for (int k = 0; k < patternCount; k++) {
double sum = frequencies[0] * partials[v];	v++;
sum += frequencies[1] * partials[v];	v++;
sum += frequencies[2] * partials[v];	v++;
sum += frequencies[3] * partials[v];	v++;
outLogLikelihoods[k] = Math.log(sum) + getLogScalingFactor(k);
}
}
}
