package dr.math.matrixAlgebra;
public class Matrix {
protected double[][] components;
protected LUPDecomposition lupDecomposition = null;
public Matrix(double[][] a) {
components = a;
}
public Matrix(double[] a, int n, int m) {
if (n <= 0 || m <= 0)
throw new NegativeArraySizeException(
"Requested matrix size: " + n + " by " + m);
if (n * m != a.length) {
throw new IllegalArgumentException(
"Requested matrix size: " + n + " by " + m + " doesn't match array size: " + a.length);
}
components = new double[n][m];
int k = 0;
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++) {
components[i][j] = a[k];
k++;
}
}
}
public Matrix(int n, int m) throws NegativeArraySizeException {
if (n <= 0 || m <= 0)
throw new NegativeArraySizeException(
"Requested matrix size: " + n + " by " + m);
components = new double[n][m];
clear();
}
public void accumulate(Matrix a) throws IllegalDimension {
if (a.rows() != rows() || a.columns() != columns())
throw new IllegalDimension("Operation error: cannot add a"
+ a.rows() + " by " + a.columns()
+ " matrix to a " + rows() + " by "
+ columns() + " matrix");
int m = components[0].length;
for (int i = 0; i < components.length; i++) {
for (int j = 0; j < m; j++)
components[i][j] += a.component(i, j);
}
}
public Matrix add(Matrix a) throws IllegalDimension {
if (a.rows() != rows() || a.columns() != columns())
throw new IllegalDimension("Operation error: cannot add a "
+ a.rows() + " by " + a.columns()
+ " matrix to a " + rows() + " by "
+ columns() + " matrix");
return new Matrix(addComponents(a));
}
protected double[][] addComponents(Matrix a) {
int n = this.rows();
int m = this.columns();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < n; j++)
newComponents[i][j] = components[i][j] + a.components[i][j];
}
return newComponents;
}
public void clear() {
int m = components[0].length;
for (int i = 0; i < components.length; i++) {
for (int j = 0; j < m; j++) components[i][j] = 0;
}
}
public int columns() {
return components[0].length;
}
public double component(int n, int m) {
return components[n][m];
}
public double determinant() throws IllegalDimension {
return lupDecomposition().determinant();
}
public double logDeterminant() throws IllegalDimension {
return lupDecomposition().logDeterminant();
}
public boolean isPD() throws IllegalDimension {
return lupDecomposition().isPD();
}
public boolean equals(Matrix a) {
int n = this.rows();
if (a.rows() != n)
return false;
int m = this.columns();
if (a.columns() != m)
return false;
for (int i = 0; i < n; i++) {
for (int j = 0; j < n; j++) {
if (a.components[i][j] != components[i][j])
return false;
}
}
return true;
}
public Matrix inverse() throws ArithmeticException {
try {
return new Matrix(
lupDecomposition().inverseMatrixComponents());
} catch (IllegalDimension e) {
return new Matrix(
transposedProduct().inverse()
.productWithTransposedComponents(this));
}
}
public boolean isSquare() {
return rows() == columns();
}
protected LUPDecomposition lupDecomposition()
throws IllegalDimension {
if (lupDecomposition == null)
lupDecomposition = new LUPDecomposition(this);
return lupDecomposition;
}
public Matrix product(double a) {
return new Matrix(productComponents(a));
}
public Vector product(Vector v) throws IllegalDimension {
int n = this.rows();
int m = this.columns();
if (v.dimension() != m)
throw new IllegalDimension("Product error: " + n + " by " + m
+ " matrix cannot by multiplied with vector of dimension "
+ v.dimension());
return secureProduct(v);
}
public Matrix product(Matrix a) throws IllegalDimension {
if (a.rows() != columns())
throw new IllegalDimension(
"Operation error: cannot multiply a "
+ rows() + " by " + columns()
+ " matrix with a " + a.rows()
+ " by " + a.columns()
+ " matrix");
return new Matrix(productComponents(a));
}
protected double[][] productComponents(double a) {
int n = this.rows();
int m = this.columns();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++)
newComponents[i][j] = a * components[i][j];
}
return newComponents;
}
protected double[][] productComponents(Matrix a) {
int p = this.columns();
int n = this.rows();
int m = a.columns();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++) {
double sum = 0;
for (int k = 0; k < p; k++)
sum += components[i][k] * a.components[k][j];
newComponents[i][j] = sum;
}
}
return newComponents;
}
public Matrix productWithTransposed(Matrix a)
throws IllegalDimension {
if (a.columns() != columns())
throw new IllegalDimension(
"Operation error: cannot multiply a " + rows()
+ " by " + columns()
+ " matrix with the transpose of a "
+ a.rows() + " by " + a.columns()
+ " matrix");
return new Matrix(productWithTransposedComponents(a));
}
public static Matrix buildIdentityTimesElementMatrix(int dim, double element){
double[][] idTemp=new double[dim][dim];
for (int i = 0; i < dim; i++) {
idTemp[i][i]=element;
}
return new Matrix(idTemp);
}
protected double[][] productWithTransposedComponents(Matrix a) {
int p = this.columns();
int n = this.rows();
int m = a.rows();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++) {
double sum = 0;
for (int k = 0; k < p; k++)
sum += components[i][k] * a.components[j][k];
newComponents[i][j] = sum;
}
}
return newComponents;
}
public int rows() {
return components.length;
}
protected Vector secureProduct(Vector v) {
int n = this.rows();
int m = this.columns();
double[] vectorComponents = new double[n];
for (int i = 0; i < n; i++) {
vectorComponents[i] = 0;
for (int j = 0; j < m; j++)
vectorComponents[i] += components[i][j] * v.components[j];
}
return new Vector(vectorComponents);
}
protected Matrix secureProduct(Matrix a) {
return new Matrix(productComponents(a));
}
protected Matrix secureSubtract(Matrix a) {
return new Matrix(subtractComponents(a));
}
public Matrix subtract(Matrix a) throws IllegalDimension {
if (a.rows() != rows() || a.columns() != columns())
throw new IllegalDimension(
"Product error: cannot subtract a" + a.rows()
+ " by " + a.columns() + " matrix to a "
+ rows() + " by " + columns() + " matrix");
return new Matrix(subtractComponents(a));
}
protected double[][] subtractComponents(Matrix a) {
int n = this.rows();
int m = this.columns();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++)
newComponents[i][j] = components[i][j] - a.components[i][j];
}
return newComponents;
}
public double[][] toComponents() {
int n = rows();
int m = columns();
double[][] answer = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++)
answer[i][j] = components[i][j];
}
return answer;
}
public String toString() {
StringBuffer sb = new StringBuffer();
char[] separator = {'[', ' '};
int n = rows();
int m = columns();
for (int i = 0; i < n; i++) {
separator[0] = '{';
for (int j = 0; j < m; j++) {
sb.append(separator);
sb.append(components[i][j]);
separator[0] = ' ';
}
sb.append('}');
sb.append('\n');
}
return sb.toString();
}
public String toStringOctave() {
StringBuffer sb = new StringBuffer();
int n = rows();
int m = columns();
sb.append("[ ");
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++) {
sb.append(components[i][j]);
if (j == m - 1) {
if (i == n - 1)
sb.append(" ");
else
sb.append("; ");
} else
sb.append(", ");
}
}
sb.append("]");
return sb.toString();
}
public Matrix transpose() {
int n = rows();
int m = columns();
double[][] newComponents = new double[m][n];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++)
newComponents[j][i] = components[i][j];
}
return new Matrix(newComponents);
}
public SymmetricMatrix transposedProduct() {
return new SymmetricMatrix(transposedProductComponents(this));
}
public Matrix transposedProduct(Matrix a) throws IllegalDimension {
if (a.rows() != rows())
throw new IllegalDimension(
"Operation error: cannot multiply a tranposed "
+ rows() + " by " + columns()
+ " matrix with a " + a.rows() + " by "
+ a.columns() + " matrix");
return new Matrix(transposedProductComponents(a));
}
protected double[][] transposedProductComponents(Matrix a) {
int p = this.rows();
int n = this.columns();
int m = a.columns();
double[][] newComponents = new double[n][m];
for (int i = 0; i < n; i++) {
for (int j = 0; j < m; j++) {
double sum = 0;
for (int k = 0; k < p; k++)
sum += components[k][i] * a.components[k][j];
newComponents[i][j] = sum;
}
}
return newComponents;
}
}