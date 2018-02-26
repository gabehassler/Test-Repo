
package dr.evomodel.newtreelikelihood;

import dr.evomodel.substmodel.SubstitutionModel;
import dr.evomodel.sitemodel.SiteModel;

public class GeneralLikelihoodCore implements LikelihoodCore {

    public static final boolean DEBUG = false;

    protected final int stateCount;
    protected int nodeCount;
    protected int stateTipCount;
    protected int patternCount;
    protected int partialsSize;
    protected int matrixSize;
    protected int matrixCount;

    protected double[] cMatrix;
    protected double[] storedCMatrix;
    protected double[] eigenValues;
    protected double[] storedEigenValues;

    protected double[] frequencies;
    protected double[] storedFrequencies;
    protected double[] categoryProportions;
    protected double[] storedCategoryProportions;
    protected double[] categoryRates;
    protected double[] storedCategoryRates;

    protected double[][][] partials;

    protected int[][] states;

    protected double[][][] matrices;

    protected int[] currentMatricesIndices;
    protected int[] storedMatricesIndices;
    protected int[] currentPartialsIndices;
    protected int[] storedPartialsIndices;

    protected boolean useScaling = false;

    protected double[][][] scalingFactors;


    public GeneralLikelihoodCore(int stateCount) {
        this.stateCount = stateCount;
    }

    public boolean canHandleTipPartials() {
        return true;
    }

    public boolean canHandleTipStates() {
        return true;
    }
    
    public boolean canHandleDynamicRescaling() {
    	return true;
    }
   

    public void initialize(int nodeCount, int stateTipCount, int patternCount, int matrixCount) {

        this.nodeCount = nodeCount;
        this.stateTipCount = stateTipCount;
        this.patternCount = patternCount;
        this.matrixCount = matrixCount;

        cMatrix = new double[stateCount * stateCount * stateCount];
        storedCMatrix = new double[stateCount * stateCount * stateCount];

        eigenValues = new double[stateCount];
        storedEigenValues = new double[stateCount];

        frequencies = new double[stateCount];
        storedFrequencies = new double[stateCount];

        categoryRates = new double[matrixCount];
        storedCategoryRates = new double[matrixCount];

        categoryProportions = new double[matrixCount];
        storedCategoryProportions = new double[matrixCount];

        partialsSize = patternCount * stateCount * matrixCount;

        partials = new double[2][nodeCount][partialsSize];

        scalingFactors = new double[2][nodeCount][patternCount];

        states = new int[nodeCount][patternCount * matrixCount];

        matrixSize = (stateCount + 1) * stateCount;

        matrices = new double[2][nodeCount][matrixCount * matrixSize];

        currentMatricesIndices = new int[nodeCount];
        storedMatricesIndices = new int[nodeCount];

        currentPartialsIndices = new int[nodeCount];
        storedPartialsIndices = new int[nodeCount];
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
            System.arraycopy(partials, 0, this.partials[0][tipIndex], k, partials.length);
            k += partials.length;
        }
    }

    public void setTipStates(int tipIndex, int[] states) {
        int k = 0;
        for (int i = 0; i < matrixCount; i++) {
            for (int j = 0; j < states.length; j++) {
                this.states[tipIndex][k] = (states[j] < stateCount ? states[j] : stateCount);
                k++;
            }
        }
    }

    public void updateSubstitutionModel(SubstitutionModel substitutionModel) {
        System.arraycopy(substitutionModel.getFrequencyModel().getFrequencies(), 0, frequencies, 0, frequencies.length);

        double[][] Evec = substitutionModel.getEigenVectors();
//        if (DEBUG) System.err.println(new dr.math.matrixAlgebra.Vector(Evec[0]));
        double[][] Ievc = substitutionModel.getInverseEigenVectors();
//        if (DEBUG) System.err.println(new dr.math.matrixAlgebra.Vector(Ievc[0]));
        int l =0;
        for (int i = 0; i < stateCount; i++) {
            for (int j = 0; j < stateCount; j++) {
                for (int k = 0; k < stateCount; k++) {
                    cMatrix[l] = Evec[i][k] * Ievc[k][j];
                    l++;
                }
            }
        }

        System.arraycopy(substitutionModel.getEigenValues(), 0, eigenValues, 0, eigenValues.length);
        
  //      if (DEBUG) System.err.println(new dr.math.matrixAlgebra.Vector(cMatrix));
//        if (DEBUG) System.err.println(cMatrix[stateCount*stateCount*stateCount-1]);
 //       if (DEBUG) System.exit(-1);

    }

    public void updateSiteModel(SiteModel siteModel) {
        for (int i = 0; i < categoryRates.length; i++) {
            categoryRates[i] = siteModel.getRateForCategory(i);
        }
        System.arraycopy(siteModel.getCategoryProportions(), 0, categoryProportions, 0, categoryProportions.length);
    }

    public void updateMatrices(int[] branchUpdateIndices, double[] branchLengths, int branchUpdateCount) {
        for (int i = 0; i < branchUpdateCount; i++) {
        	if (DEBUG) System.err.println("Updating matrix for node "+branchUpdateIndices[i]);
            currentMatricesIndices[branchUpdateIndices[i]] = 1 - currentMatricesIndices[branchUpdateIndices[i]];
            calculateTransitionProbabilityMatrices(branchUpdateIndices[i], branchLengths[i]);
            if (DEBUG && branchUpdateIndices[i] == 0) {
            	System.err.println(matrices[currentMatricesIndices[0]][0][0]);
            	System.err.println(matrices[currentMatricesIndices[0]][0][184]);
            }
        }
    }

    int debugCount = 0;

    private void calculateTransitionProbabilityMatrices(int nodeIndex, double branchLength) {

        double[] tmp = new double[stateCount];

        int n = 0;
        for (int l = 0; l < matrixCount; l++) {
//	    if (DEBUG) System.err.println("1: Rate "+l+" = "+categoryRates[l]);
            for (int i = 0; i < stateCount; i++) {
                tmp[i] =  Math.exp(eigenValues[i] * branchLength * categoryRates[l]);
            }
//            if (DEBUG) System.err.println(new dr.math.matrixAlgebra.Vector(tmp));
    //        if (DEBUG) System.exit(-1);

            int m = 0;
            for (int i = 0; i < stateCount; i++) {
                for (int j = 0; j < stateCount; j++) {
                    double sum = 0.0;
                    for (int k = 0; k < stateCount; k++) {
                        sum += cMatrix[m] * tmp[k];
                        m++;
                    }
	//	    if (DEBUG) System.err.println("1: matrices[][]["+n+"] = "+sum);
                    matrices[currentMatricesIndices[nodeIndex]][nodeIndex][n] = sum;
                    n++;
                }
                matrices[currentMatricesIndices[nodeIndex]][nodeIndex][n] = 1.0;
                n++;
            }
//            if (DEBUG) System.err.println(new dr.math.matrixAlgebra.Vector(matrices[currentMatricesIndices[nodeIndex]][nodeIndex]));
//            if (DEBUG) System.exit(0);
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

            currentPartialsIndices[nodeIndex3] = 1 - currentPartialsIndices[nodeIndex3];

            if (nodeIndex1 < stateTipCount) {
                if (nodeIndex2 < stateTipCount) {
                    updateStatesStates(nodeIndex1, nodeIndex2, nodeIndex3);
                } else {
                    updateStatesPartials(nodeIndex1, nodeIndex2, nodeIndex3);
                }
            } else {
                if (nodeIndex2 < stateTipCount) {
                    updateStatesPartials(nodeIndex2, nodeIndex1, nodeIndex3);
                } else {
                    updatePartialsPartials(nodeIndex1, nodeIndex2, nodeIndex3);
                }
            }

            if (useScaling) {
                scalePartials(nodeIndex3);
            }
        }
    }

    private void updateStatesStates(int nodeIndex1, int nodeIndex2, int nodeIndex3)
    {
        double[] matrices1 = matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1];
        double[] matrices2 = matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2];

        int[] states1 = states[nodeIndex1];
        int[] states2 = states[nodeIndex2];

        double[] partials3 = partials[currentPartialsIndices[nodeIndex3]][nodeIndex3];

        int v = 0;

        for (int l = 0; l < matrixCount; l++) {

            for (int k = 0; k < patternCount; k++) {

                int state1 = states1[k];
                int state2 = states2[k];

                int w = l * matrixSize;

                for (int i = 0; i < stateCount; i++) {

                    partials3[v] = matrices1[w + state1] * matrices2[w + state2];

                    v++;
                    w += (stateCount + 1);
                }

            }
        }
    }

    private void updateStatesPartials(int nodeIndex1, int nodeIndex2, int nodeIndex3)
    {
        double[] matrices1 = matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1];
        double[] matrices2 = matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2];

        int[] states1 = states[nodeIndex1];
        double[] partials2 = partials[currentPartialsIndices[nodeIndex2]][nodeIndex2];

        double[] partials3 = partials[currentPartialsIndices[nodeIndex3]][nodeIndex3];

        double sum, tmp;

        int u = 0;
        int v = 0;

        for (int l = 0; l < matrixCount; l++) {

            for (int k = 0; k < patternCount; k++) {

                int state1 = states1[k];

                int w = l * matrixSize;

                for (int i = 0; i < stateCount; i++) {

                    tmp = matrices1[w + state1];

                    sum = 0.0;
                    for (int j = 0; j < stateCount; j++) {
                        sum += matrices2[w] * partials2[v + j];
                        w++;
                    }

                    // increment for the extra column at the end
                    w++;

                    partials3[u] = tmp * sum;
                    u++;
                }

                v += stateCount;
            }
        }
    }

    private void updatePartialsPartials(int nodeIndex1, int nodeIndex2, int nodeIndex3)
    {
        double[] matrices1 = matrices[currentMatricesIndices[nodeIndex1]][nodeIndex1];
        double[] matrices2 = matrices[currentMatricesIndices[nodeIndex2]][nodeIndex2];

        double[] partials1 = partials[currentPartialsIndices[nodeIndex1]][nodeIndex1];
        double[] partials2 = partials[currentPartialsIndices[nodeIndex2]][nodeIndex2];

        double[] partials3 = partials[currentPartialsIndices[nodeIndex3]][nodeIndex3];

        double sum1, sum2;

        int u = 0;
        int v = 0;

        for (int l = 0; l < matrixCount; l++) {

            for (int k = 0; k < patternCount; k++) {

                int w = l * matrixSize;

                for (int i = 0; i < stateCount; i++) {

                    sum1 = sum2 = 0.0;

                    for (int j = 0; j < stateCount; j++) {
                        sum1 += matrices1[w] * partials1[v + j];
                        sum2 += matrices2[w] * partials2[v + j];

                        w++;
                    }

                    // increment for the extra column at the end
                    w++;

                    partials3[u] = sum1 * sum2;
                    u++;
                }
                v += stateCount;

            }
            
            if (DEBUG) {
//    	    	System.err.println("1:PP node = "+nodeIndex3);
//    	    	for(int p=0; p<partials3.length; p++) {
//    	    		System.err.println("1:PP\t"+partials3[p]);
//    	    	}
            	System.err.println("node = "+nodeIndex3);
            	System.err.println(new dr.math.matrixAlgebra.Vector(partials3));
    	    	System.err.println(new dr.math.matrixAlgebra.Vector(scalingFactors[currentPartialsIndices[nodeIndex3]][nodeIndex3]));
    	    	//System.exit(-1);
    	    }
        }
    }


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

            if (scaleFactor < 1E+40) {

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
                if (DEBUG && pattern == 1) System.err.println("Adding "+scalingFactors[currentPartialsIndices[i]][i][pattern]);
            }
        }
        
        if (DEBUG) System.err.println("1:SF "+logScalingFactor+" for "+pattern);
        return logScalingFactor;
    }


    public void calculateLogLikelihoods(int rootNodeIndex, double[] outLogLikelihoods) {

        // @todo I have a feeling this could be done in a single set of nested loops.

        double[] rootPartials = partials[currentPartialsIndices[rootNodeIndex]][rootNodeIndex];

        double[] tmp = new double[patternCount * stateCount];

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

            double sum = 0.0;
            for (int i = 0; i < stateCount; i++) {

                sum += frequencies[i] * tmp[u];
                u++;
            }
            outLogLikelihoods[k] = Math.log(sum) + getLogScalingFactor(k);
            if (DEBUG) {
            	System.err.println("log lik "+k+" = "+outLogLikelihoods[k]);
            }
        }
        if (DEBUG) System.exit(-1);
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
        double[] tmp = cMatrix;
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