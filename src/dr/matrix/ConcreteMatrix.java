package dr.matrix;
class ConcreteMatrix extends MutableMatrix.AbstractMutableMatrix {
public ConcreteMatrix(double[][] v) {
this.values = new double[v.length][v[0].length];
for (int i = 0; i < values.length; i++) {
System.arraycopy(v[i], 0, values[i], 0, values[0].length);
}
}
public final void setDimension(int rows, int columns) {
if (values.length != rows || values[0].length != columns) {
values = new double[rows][columns];
}
}
public final void setElement(int rows, int column, double value) {
values[rows][column] = value;
}
public final int getRowCount() {
return values.length;
}
public final int getColumnCount() {
return values[0].length;
}
public final double getElement(int i, int j) {
return values[i][j];
}
public String toString() {
StringBuffer buffer = new StringBuffer();
for (int i = 0; i < values.length; i++) {
for (int j = 0; j < values[0].length; j++) {
buffer.append(values[i][j]).append("\t");
}
buffer.append("\n");
}
return buffer.toString();
}
double[][] values = null;
}
