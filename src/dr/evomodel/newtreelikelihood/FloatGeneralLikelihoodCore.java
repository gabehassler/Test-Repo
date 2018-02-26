
package dr.evomodel.newtreelikelihood;

import java.util.logging.Logger;

import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.sitemodel.SiteModel;

public class FloatGeneralLikelihoodCore implements LikelihoodCore {

    protected int stateCount;
    protected int nodeCount;
    protected int patternCount;
    protected int partialsSize;
    protected int matrixSize;
    protected int matrixCount;

    protected float[] cMatrix;
    protected float[] storedCMatrix;
    protected float[] eigenValues;
    protected float[] storedEigenValues;

    protected float[] frequencies;
    protected float[] storedFrequencies;
    protected float[] categoryProportions;
    protected float[] storedCategoryProportions;
    protected float[] categoryRates;
    protected float[] storedCategoryRates;

    protected float[][][] partials;

    protected int[][] states;

    protected float[][][] matrices;

    protected int[] currentMatricesIndices;
    protected int[] storedMatricesIndices;
    protected int[] currentPartialsIndices;
    protected int[] storedPartialsIndices;


    public FloatGeneralLikelihoodCore(int stateCount) {
        this.stateCount = stateCount;
        Logger.getLogger("dr.evomodel.treelikelihood").info("Constructing float-precision java likelihood core.");
    }

    public boolean canHandleTipPartials() {
        return true;
    }

    public boolean canHandleTipStates() {
        return false;
    }
    
    public boolean canHandleDynamicRescaling() {
    	return true;
    }

    public void initialize(int nodeCount, int stateTipCount, int patternCount, int matrixCount) {
        this.nodeCount = nodeCount;
        this.patternCount = patternCount;
        this.matrixCount = matrixCount;

        cMatrix = new float[stateCount * stateCount * stateCount];
        storedCMatrix = new float[stateCount * stateCount * stateCount];

        eigenValues = new float[stateCount];
        storedEigenValues = new float[stateCount];

        frequencies = new float[stateCount];
        storedFrequencies = new float[stateCount];

        categoryRates = new float[matrixCount];
        storedCategoryRates = new float[matrixCount];

        categoryProportions = new float[matrixCount];
        storedCategoryProportions = new float[matrixCount];

        partialsSize = patternCount * stateCount * matrixCount;

        partials = new float[2][nodeCount][];

        currentMatricesIndices = new int[nodeCount];
        storedMatricesIndices = new int[nodeCount];

        currentPartialsIndices = new int[nodeCount];
        storedPartialsIndices = new int[nodeCount];

        states = new int[nodeCount][];

        for (int i = 0; i < nodeCount; i++) {
            partials[0][i] = new float[partialsSize];
            partials[1][i] = new float[partialsSize];
        }

        matrixSize = stateCount * stateCount;

        matrices = new float[2][nodeCount][matrixCount * matrixSize];
    }

    public void finalize() throws Throwable  {
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
    }


    public void setTipPartials(int tipIndex, double[] partials) {

        int k = 0;
        for (int i = 0; i < matrixCount; i++) {
            for (int j = 0; j < partials.length; j++) {
                this.partials[0][tipIndex][k] = (float)partials[j];
                k++;
            }
        }
    }

    public void setTipStates(int tipIndex, int[] states) {
        throw new UnsupportedOperationException("setTipStates not implemented in FloatGeneralLikelihoodCore");
    }

    public void updateSubstitutionModel(SubstitutionModel substitutionModel) {
//        System.arraycopy(substitutionModel.getFrequencyModel().getFrequencies(), 0, frequencies, 0, frequencies.length);
    	// Must down-cast from double to float
    	double[] doubleFreqs = substitutionModel.getFrequencyModel().getFrequencies();
    	for(int i=0; i<frequencies.length; i++)
    		frequencies[i] = (float) doubleFreqs[i];

        double[][] Evec = substitutionModel.getEigenVectors();
        double[][] Ievc = substitutionModel.getInverseEigenVectors();
        int l =0;
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < stateCount; j++) {
                for (int k = 0; k < stateCount; k++) {
                    cMatrix[l] = (float)(Evec[i][k] * Ievc[k][j]);
                    l++;
                }
            }
        }

        double[] Eval = substitutionModel.getEigenValues();
        for (int i = 0; i < eigenValues.length; i++) {
            eigenValues[i] = (float)Eval[i];
        }

    }

    public void updateSiteModel(SiteModel siteModel) {
        for (int i = 0; i < categoryRates.length; i++) {
            categoryRates[i] = (float)siteModel.getRateForCategory(i);
            categoryProportions[i] = (float)siteModel.getCategoryProportions()[i];
        }
    }

    public void updateMatrices(int[] branchUpdateIndices, double[] branchLengths, int branchUpdateCount) {
        for (int i = 0; i < branchUpdateCount; i++) {
            currentMatricesIndices[branchUpdateIndices[i]] = 1 - currentMatricesIndices[branchUpdateIndices[i]];
            calculateTransitionProbabilityMatrices(branchUpdateIndices[i], (float)branchLengths[i]);
        }
    }

    private void calculateTransitionProbabilityMatrices(int nodeIndex, float branchLength) {

        float[] tmp = new float[stateCount];

        int n = 0;
        for (int l = 0; l < matrixCount; l++) {
            for (int i = 0; i < stateCount; i++) {
                tmp[i] = (float)Math.exp(eigenValues[i] * branchLength * categoryRates[l]);
            }

            int m = 0;
            for (int i = 0; i < stateCount; i++) {
                for (int j = 0; j < stateCount; j++) {
                    float sum = 0.0f;
                    for (int k = 0; k < stateCount; k++) {
                        sum += cMatrix[m] * tmp[k];
                        m++;
                    }
                    matrices[currentMatricesIndices[nodeIndex]][nodeIndex][n] = sum;
                    n++;
                }
            }
        }
    }
    
    public void updatePartials(int[] operations, int[] dependencies, int operationCount, boolean rescale) {
    	updatePartials(operations, dependencies, operationCount);
    }
 
    public void updatePartials(int[] operations, int[] dependencies, int operationCount) {

        int x = 0;
        for (int op = 0; op < operationCount; op++) {
            int nodeIndex1 = operations[x];
            x++;
            int nodeIndex2 = operations[x];
            x++;
            int nodeIndex3 = operations[x];
            x++;


            float[] matrices1 = matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1];
            float[] matrices2 = matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2];

            float[] partials1 = partials[currentPartialsIndices[nodeIndex1]][nodeIndex1];
            float[] partials2 = partials[currentPartialsIndices[nodeIndex2]][nodeIndex2];

            currentPartialsIndices[nodeIndex3] = 1 - currentPartialsIndices[nodeIndex3];
            float[] partials3 = partials[currentPartialsIndices[nodeIndex3]][nodeIndex3];

            float sum1, sum2;

            int u = 0;
            int v = 0;

            for (int l = 0; l < matrixCount; l++) {

                for (int k = 0; k < patternCount; k++) {

                    int w = l * matrixSize;

                    for (int i = 0; i < stateCount; i++) {

                        sum1 = sum2 = 0.0f;

                        for (int j = 0; j < stateCount; j++) {
                            sum1 += matrices1[w] * partials1[v + j];
                            sum2 += matrices2[w] * partials2[v + j];

                            w++;
                        }

                        partials3[u] = sum1 * sum2;
                        u++;
                    }
                    v += stateCount;
                }
            }
        }
    }

    public void calculateLogLikelihoods(int rootNodeIndex, double[] outLogLikelihoods) {

        // @todo I have a feeling this could be done in a single set of nested loops.

        float[] rootPartials = partials[currentPartialsIndices[rootNodeIndex]][rootNodeIndex];

        float[] tmp = new float[patternCount * stateCount];

        int u = 0;
        int v = 0;
        for (int k = 0; k < patternCount; k++) {

            for (int i = 0; i < stateCount; i++) {

                tmp[u] = rootPartials[v] * categoryProportions[0];
                u++;
                v++;
            }
        }


        for (int l = 1; l < matrixCount; l++) {
            u = 0;

            for (int k = 0; k < patternCount; k++) {

                for (int i = 0; i < stateCount; i++) {

                    tmp[u] += rootPartials[v] * categoryProportions[l];
                    u++;
                    v++;
                }
            }
        }

        u = 0;
        for (int k = 0; k < patternCount; k++) {

            float sum = 0.0f;
            for (int i = 0; i < stateCount; i++) {

                sum += frequencies[i] * tmp[u];
                u++;
            }
            outLogLikelihoods[k] = Math.log(sum);
        }
    }

    public void storeState() {

        System.arraycopy(cMatrix, 0, storedCMatrix, 0, cMatrix.length);
        System.arraycopy(eigenValues, 0, storedEigenValues, 0, eigenValues.length);

        System.arraycopy(frequencies, 0, storedFrequencies, 0, frequencies.length);
        System.arraycopy(categoryRates, 0, storedCategoryRates, 0, categoryRates.length);
        System.arraycopy(categoryProportions, 0, storedCategoryProportions, 0, categoryProportions.length);

        System.arraycopy(currentMatricesIndices, 0, storedMatricesIndices, 0, nodeCount);
        System.arraycopy(currentPartialsIndices, 0, storedPartialsIndices, 0, nodeCount);
    }

    public void restoreState() {
        // Rather than copying the stored stuff back, just swap the pointers...
        float[] tmp = cMatrix;
        cMatrix = storedCMatrix;
        storedCMatrix = tmp;

        tmp = eigenValues;
        eigenValues = storedEigenValues;
        storedEigenValues = tmp;

        tmp = frequencies;
        frequencies = storedFrequencies;
        storedFrequencies = tmp;

        tmp = categoryRates;
        categoryRates = storedCategoryRates;
        storedCategoryRates = tmp;

        tmp = categoryProportions;
        categoryProportions = storedCategoryProportions;
        storedCategoryProportions = tmp;

        int[] tmp3 = currentMatricesIndices;
        currentMatricesIndices = storedMatricesIndices;
        storedMatricesIndices = tmp3;

        int[] tmp4 = currentPartialsIndices;
        currentPartialsIndices = storedPartialsIndices;
        storedPartialsIndices = tmp4;
    }
}