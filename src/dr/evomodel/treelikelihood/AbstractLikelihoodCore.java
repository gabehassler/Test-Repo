package dr.evomodel.treelikelihood;
import java.util.Arrays;
public abstract class AbstractLikelihoodCore implements LikelihoodCore {
protected int stateCount;
protected int nodeCount;
protected int patternCount;
protected int partialsSize;
protected int matrixSize;
protected int matrixCount;
protected boolean integrateCategories;
protected double[][][] partials;
protected int[][] states;
protected double[][][] matrices;
protected int[] currentMatricesIndices;
protected int[] storedMatricesIndices;
protected int[] currentPartialsIndices;
protected int[] storedPartialsIndices;
protected boolean useScaling = false;
protected double[][][] scalingFactors;
private double scalingThreshold = 1.0E-100;
public AbstractLikelihoodCore(int stateCount) {
this.stateCount = stateCount;
}
public void initialize(int nodeCount, int patternCount, int matrixCount, boolean integrateCategories) {
this.nodeCount = nodeCount;
this.patternCount = patternCount;
this.matrixCount = matrixCount;
this.integrateCategories = integrateCategories;
if (integrateCategories) {
partialsSize = patternCount * stateCount * matrixCount;
} else {
partialsSize = patternCount * stateCount;
}
partials = new double[2][nodeCount][];
currentMatricesIndices = new int[nodeCount];
storedMatricesIndices = new int[nodeCount];
currentPartialsIndices = new int[nodeCount];
storedPartialsIndices = new int[nodeCount];
states = new int[nodeCount][];
for (int i = 0; i < nodeCount; i++) {
partials[0][i] = null;
partials[1][i] = null;
states[i] = null;
}
matrixSize = stateCount * stateCount;
matrices = new double[2][nodeCount][matrixCount * matrixSize];
}
public void finalize() throws java.lang.Throwable {
super.finalize();
nodeCount = 0;
patternCount = 0;
matrixCount = 0;
partials = null;
currentPartialsIndices = null;
storedPartialsIndices = null;
states = null;
matrices = null;
currentMatricesIndices = null;
storedMatricesIndices = null;
scalingFactors = null;
}
public void setUseScaling(boolean useScaling) {
this.useScaling = useScaling;
if (useScaling) {
scalingFactors = new double[2][nodeCount][patternCount];
}
}
public void createNodePartials(int nodeIndex) {
this.partials[0][nodeIndex] = new double[partialsSize];
this.partials[1][nodeIndex] = new double[partialsSize];
}
public void setNodePartials(int nodeIndex, double[] partials) {
if (this.partials[0][nodeIndex] == null) {
createNodePartials(nodeIndex);
}
if (partials.length < partialsSize) {
int k = 0;
for (int i = 0; i < matrixCount; i++) {
System.arraycopy(partials, 0, this.partials[0][nodeIndex], k, partials.length);
k += partials.length;
}
} else {
System.arraycopy(partials, 0, this.partials[0][nodeIndex], 0, partials.length);
}
}
public void createNodeStates(int nodeIndex) {
this.states[nodeIndex] = new int[patternCount];
}
public void setNodeStates(int nodeIndex, int[] states) {
if (this.states[nodeIndex] == null) {
createNodeStates(nodeIndex);
}
System.arraycopy(states, 0, this.states[nodeIndex], 0, patternCount);
}
public void getNodeStates(int nodeIndex, int[] states) {
System.arraycopy(this.states[nodeIndex], 0, states, 0, patternCount);
}
public void setNodeMatrixForUpdate(int nodeIndex) {
currentMatricesIndices[nodeIndex] = 1 - currentMatricesIndices[nodeIndex];
}
public void setNodeMatrix(int nodeIndex, int matrixIndex, double[] matrix) {
System.arraycopy(matrix, 0, matrices[currentMatricesIndices[nodeIndex]][nodeIndex],
matrixIndex * matrixSize, matrixSize);
}
public void getNodeMatrix(int nodeIndex, int matrixIndex, double[] matrix) {
System.arraycopy(matrices[currentMatricesIndices[nodeIndex]][nodeIndex],
matrixIndex * matrixSize, matrix, 0, matrixSize);
}
public void setNodePartialsForUpdate(int nodeIndex) {
currentPartialsIndices[nodeIndex] = 1 - currentPartialsIndices[nodeIndex];
}
public void setCurrentNodePartials(int nodeIndex, double[] partials) {
if (partials.length < partialsSize) {
int k = 0;
for (int i = 0; i < matrixCount; i++) {
System.arraycopy(partials, 0, this.partials[currentPartialsIndices[nodeIndex]][nodeIndex], k, partials.length);
k += partials.length;
}
} else {
System.arraycopy(partials, 0, this.partials[currentPartialsIndices[nodeIndex]][nodeIndex], 0, partials.length);
}
}
public void calculatePartials(int nodeIndex1, int nodeIndex2, int nodeIndex3) {
if (states[nodeIndex1] != null) {
if (states[nodeIndex2] != null) {
calculateStatesStatesPruning(
states[nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
states[nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3]);
} else {
calculateStatesPartialsPruning(states[nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
partials[currentPartialsIndices[nodeIndex2]][nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3]);
}
} else {
if (states[nodeIndex2] != null) {
calculateStatesPartialsPruning(states[nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex1]][nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3]);
} else {
calculatePartialsPartialsPruning(partials[currentPartialsIndices[nodeIndex1]][nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
partials[currentPartialsIndices[nodeIndex2]][nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3]);
}
}
if (useScaling) {
scalePartials(nodeIndex3);
}
//
//        int k =0;
//        for (int i = 0; i < patternCount; i++) {
//            double f = 0.0;
//
//            for (int j = 0; j < stateCount; j++) {
//                f += partials[currentPartialsIndices[nodeIndex3]][nodeIndex3][k];
//                k++;
//            }
//            if (f == 0.0) {
//                Logger.getLogger("error").severe("A partial likelihood (node index = " + nodeIndex3 + ", pattern = "+ i +") is zero for all states.");
//            }
//        }
}
protected abstract void calculateStatesStatesPruning(int[] states1, double[] matrices1,
int[] states2, double[] matrices2,
double[] partials3);
protected abstract void calculateStatesPartialsPruning(int[] states1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3);
protected abstract void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3);
public void calculatePartials(int nodeIndex1, int nodeIndex2, int nodeIndex3, int[] matrixMap) {
if (states[nodeIndex1] != null) {
if (states[nodeIndex2] != null) {
calculateStatesStatesPruning(
states[nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
states[nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3], matrixMap);
} else {
calculateStatesPartialsPruning(
states[nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
partials[currentPartialsIndices[nodeIndex2]][nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3], matrixMap);
}
} else {
if (states[nodeIndex2] != null) {
calculateStatesPartialsPruning(
states[nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex1]][nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3], matrixMap);
} else {
calculatePartialsPartialsPruning(
partials[currentPartialsIndices[nodeIndex1]][nodeIndex1], matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1],
partials[currentPartialsIndices[nodeIndex2]][nodeIndex2], matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2],
partials[currentPartialsIndices[nodeIndex3]][nodeIndex3], matrixMap);
}
}
if (useScaling) {
scalePartials(nodeIndex3);
}
}
protected abstract void calculateStatesStatesPruning(int[] states1, double[] matrices1,
int[] states2, double[] matrices2,
double[] partials3, int[] matrixMap);
protected abstract void calculateStatesPartialsPruning(int[] states1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3, int[] matrixMap);
protected abstract void calculatePartialsPartialsPruning(double[] partials1, double[] matrices1,
double[] partials2, double[] matrices2,
double[] partials3, int[] matrixMap);
public void integratePartials(int nodeIndex, double[] proportions, double[] outPartials) {
calculateIntegratePartials(partials[currentPartialsIndices[nodeIndex]][nodeIndex], proportions, outPartials);
}
protected abstract void calculateIntegratePartials(double[] inPartials, double[] proportions, double[] outPartials);
protected void scalePartials(int nodeIndex) {
int u = 0;
for (int i = 0; i < patternCount; i++) {
double scaleFactor = 0.0;
int v = u;
for (int k = 0; k < matrixCount; k++) {
for (int j = 0; j < stateCount; j++) {
if (partials[currentPartialsIndices[nodeIndex]][nodeIndex][v] > scaleFactor) {
scaleFactor = partials[currentPartialsIndices[nodeIndex]][nodeIndex][v];
}
v++;
}
v += (patternCount - 1) * stateCount;
}
if (scaleFactor < scalingThreshold) {
v = u;
for (int k = 0; k < matrixCount; k++) {
for (int j = 0; j < stateCount; j++) {
partials[currentPartialsIndices[nodeIndex]][nodeIndex][v] /= scaleFactor;
v++;
}
v += (patternCount - 1) * stateCount;
}
scalingFactors[currentPartialsIndices[nodeIndex]][nodeIndex][i] = Math.log(scaleFactor);
} else {
scalingFactors[currentPartialsIndices[nodeIndex]][nodeIndex][i] = 0.0;
}
u += stateCount;
}
}
public double getLogScalingFactor(int pattern) {
double logScalingFactor = 0.0;
if (useScaling) {
for (int i = 0; i < nodeCount; i++) {
logScalingFactor += scalingFactors[currentPartialsIndices[i]][i][pattern];
}
}
return logScalingFactor;
}
public void getLogScalingFactors(int nodeIndex, double[] buffer) {
if (useScaling) {
System.arraycopy(scalingFactors[currentPartialsIndices[nodeIndex]][nodeIndex], 0,
buffer, 0, patternCount);
} else {
Arrays.fill(buffer, 0.0);
}
}
public boolean arePartialsRescaled() {
return useScaling;
}
public void getPartials(int nodeIndex, double[] outPartials) {
double[] partials1 = partials[currentPartialsIndices[nodeIndex]][nodeIndex];
System.arraycopy(partials1, 0, outPartials, 0, partialsSize);
}
public void storeState() {
System.arraycopy(currentMatricesIndices, 0, storedMatricesIndices, 0, nodeCount);
System.arraycopy(currentPartialsIndices, 0, storedPartialsIndices, 0, nodeCount);
}
public void restoreState() {
// Rather than copying the stored stuff back, just swap the pointers...
int[] tmp1 = currentMatricesIndices;
currentMatricesIndices = storedMatricesIndices;
storedMatricesIndices = tmp1;
int[] tmp2 = currentPartialsIndices;
currentPartialsIndices = storedPartialsIndices;
storedPartialsIndices = tmp2;
}
}
