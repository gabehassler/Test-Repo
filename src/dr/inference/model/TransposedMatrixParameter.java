package dr.inference.model;
import java.util.ArrayList;
import java.util.List;
public class TransposedMatrixParameter extends MatrixParameter {
public TransposedMatrixParameter(String name) {
super(name + ".transpose");
}
public TransposedMatrixParameter(String name, Parameter[] parameters) {
super(name + ".transpose", parameters);
}
public static TransposedMatrixParameter recast(String name, CompoundParameter compoundParameter) {
final int count = compoundParameter.getParameterCount();
Parameter[] parameters = new Parameter[count];
for (int i = 0; i < count; ++i) {
parameters[i] = compoundParameter.getParameter(i);
}
return new TransposedMatrixParameter(name, parameters);
}
//    public double getParameterValue(int row, int col) {
//        // transposed
//        return super.getParameterValue(col, row);
//    }
public int getColumnDimension() {
// transposed
return super.getParameter(0).getDimension();
}
public int getRowDimension() {
// transposed
return super.getParameterCount();
}
public int getParameterCount() {
// MatrixParameter.getParamaterCount examines unique parameters
// and not column dimension, as it probably should
return getColumnDimension();
}
public double[][] getParameterAsMatrix() {
final int I = getColumnDimension();
final int J = getRowDimension();
double[][] parameterAsMatrix = new double[J][I];
for (int i = 0; i < I; i++) {
for (int j = 0; j < J; j++)
parameterAsMatrix[j][i] = getParameterValue(i, j);
}
return parameterAsMatrix;
}
public double getParameterValue(int dim) {
// TODO Map to transposed dimension
int transposedDim = dim;
return super.getParameterValue(transposedDim);
}
public Parameter getParameter(int index) {
if (slices == null) {
// construct vector_slices
slices = new ArrayList<Parameter>();
for (int i = 0; i < getColumnDimension(); ++i) {
VectorSliceParameter thisSlice = new VectorSliceParameter(getParameterName() + "." + i, i);
for (int j = 0; j < getRowDimension(); ++j) {
thisSlice.addParameter(super.getParameter(j));
}
slices.add(thisSlice);
}
}
return slices.get(index);
}
MatrixParameter transposeBack(){
return MatrixParameter.recast(null, this);
}
private List<Parameter> slices = null;
}
