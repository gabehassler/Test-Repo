package dr.inference.model;
import dr.util.Transform;
public class MarkovRandomFieldMatrix extends MatrixParameter {
private Parameter diagonalParameter;
private Parameter offDiagonalParameter;
private final Transform diagonalTransform;
private final Transform offDiagonalTransform;
private boolean asCorrelation = false;
private int dim;
public MarkovRandomFieldMatrix(Parameter diagonals, Parameter offDiagonal, boolean asCorrelation) {
this(diagonals, offDiagonal, asCorrelation, null, null);
}
public MarkovRandomFieldMatrix(Parameter diagonals, Parameter offDiagonal, boolean asCorrelation,
Transform diagonalTransform, Transform offDiagonalTransform) {
super(MATRIX_PARAMETER);
diagonalParameter = diagonals;
offDiagonalParameter = offDiagonal;
addParameter(diagonalParameter);
addParameter(offDiagonalParameter);
dim = diagonalParameter.getDimension();
this.asCorrelation = asCorrelation;
this.diagonalTransform = (diagonalTransform != null) ? diagonalTransform : Transform.NONE;
this.offDiagonalTransform = (offDiagonalTransform != null) ? offDiagonalTransform : Transform.NONE;
}
//    public double[] getAttributeValue() {
//        double[] stats = new double[dim * dim];
//        int index = 0;
//        for (int i = 0; i < dim; i++) {
//            for (int j = 0; j < dim; j++) {
//                stats[index] = getParameterValue(i, j);
//                index++;
//            }
//        }
//        return stats;
//    }
//
//    public int getDimension() {
//        return dim * dim;
//    }
private double getDiagonalParameterValue(int i) {
return diagonalTransform.inverse(diagonalParameter.getParameterValue(i));
}
private double getOffDiagonalParameterValue(int i) {
return offDiagonalTransform.inverse(offDiagonalParameter.getParameterValue(i));
}
public double getParameterValue(int i) {
int row = i / dim;
int col = i % dim;
return getParameterValue(row, col);
}
public double getParameterValue(int row, int col) {
if (row == col) {
return getDiagonalParameterValue(row);
} else if (row == (col - 1) || row == (col + 1)) {
if (asCorrelation) {
return -getOffDiagonalParameterValue(0) *
Math.sqrt(getDiagonalParameterValue(row) * getDiagonalParameterValue(col));
}
return getOffDiagonalParameterValue(0);
}
return 0.0;
}
//    public double[][] getParameterAsMatrix() {
//        final int I = dim;
//        double[][] parameterAsMatrix = new double[I][I];
//        for (int i = 0; i < I; i++) {
//            parameterAsMatrix[i][i] = getParameterValue(i, i);
//            for (int j = i + 1; j < I; j++) {
//                parameterAsMatrix[j][i] = parameterAsMatrix[i][j] = getParameterValue(i, j);
//            }
//        }
//        return parameterAsMatrix;
//    }
public int getColumnDimension() {
return diagonalParameter.getDimension();
}
public int getRowDimension() {
return diagonalParameter.getDimension();
}
}
