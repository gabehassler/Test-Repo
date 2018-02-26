
package dr.evomodel.continuous;

import dr.evolution.tree.NodeRef;
import dr.math.distributions.GaussianProcessRandomGenerator;
import dr.math.distributions.MultivariateNormalDistribution;
import dr.math.matrixAlgebra.CholeskyDecomposition;
import dr.math.matrixAlgebra.IllegalDimension;
import dr.math.matrixAlgebra.SymmetricMatrix;

public class GaussianProcessFromTree implements GaussianProcessRandomGenerator {

    private final FullyConjugateMultivariateTraitLikelihood traitModel;

    public GaussianProcessFromTree(FullyConjugateMultivariateTraitLikelihood traitModel) {
        this.traitModel = traitModel;
    }

    //    boolean firstTime=true;
    public double[] nextRandomFast() {

        double[] random = new double[traitModel.getTreeModel().getExternalNodeCount()*traitModel.getDimTrait()];
        NodeRef root = traitModel.getTreeModel().getRoot();
        double[] traitStart=traitModel.getPriorMean();
        double[][] precisionCholesky=null;
        double[][] temp= new SymmetricMatrix(traitModel.getDiffusionModel().getPrecisionmatrix()).inverse().toComponents();
        try {
            precisionCholesky = (new CholeskyDecomposition(temp).getL());
        } catch (IllegalDimension illegalDimension) {
            illegalDimension.printStackTrace();
        }
//        if(traitModel.getTreeModel().isExternal(root)) {
//            random[0] = traitModel.getTreeModel().getMultivariateNodeTrait(root, traitModel.getTraitName())[i];
//        }
//        else{


//        double[][] var = MultivariateTraitUtils.computeTreeVariance(traitModel, true);
//        if(firstTime) {
//            for (int j = 0; j < var[0].length; j++) {
//                for (int k = 0; k < var[0].length; k++) {
//                    if(j!=k)
//                        var[j][k] = var[j][k] / Math.sqrt(var[k][k] * var[j][j]);
//                }
//            }
//
//
//
//            for (int j = 0; j < var[0].length; j++) {
//                String empty = "";
//                for (int k = 0; k < var[0].length; k++) {
//                    empty += Double.toString(var[j][k]) + "\t";
//                }
//                System.out.println(empty);
//            }
//            firstTime=false;
//        }
        nextRandomFast(traitStart, root, random, precisionCholesky);
//        }
        return random;
    }

    private void nextRandomFast(double[] currentValue, NodeRef currentNode, double[] random, double[][] precisionCholesky) {
        double rescaledLength;
        rescaledLength = traitModel.getRescaledBranchLengthForPrecision(currentNode);
        double[] draw= MultivariateNormalDistribution.nextMultivariateNormalCholesky(currentValue, precisionCholesky);
        if (traitModel.getTreeModel().isExternal(currentNode)) {
            //System.out.println(currentNode.toString());
            for (int i = 0; i <currentValue.length ; i++) {
                random[currentNode.getNumber()*currentValue.length+i] = currentValue[i] + draw[i] * Math.sqrt(rescaledLength);
            }
        } else {
            int childCount = traitModel.getTreeModel().getChildCount(currentNode);
            double[] newValue=new double[currentValue.length];
            for (int i = 0; i <currentValue.length ; i++) {
                newValue[i] = currentValue[i] +  draw[i] * Math.sqrt(rescaledLength);
            }
            for (int i = 0; i < childCount; i++) {
                nextRandomFast(newValue, traitModel.getTreeModel().getChild(currentNode, i), random, precisionCholesky);
            }
        }
    }

    @Override
    public Object nextRandom() {
        return nextRandomFast();
    }

    @Override
    public double logPdf(Object x) {
        return 0;
    }
}
