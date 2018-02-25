package dr.math.matrixAlgebra;
public class LUPDecomposition {
private double[][] rows;
private int[] permutation = null;
private int parity = 1;
public LUPDecomposition(double[][] components)
throws IllegalDimension {
int n = components.length;
if (components[0].length != n)
throw new IllegalDimension("Illegal system: a" + n + " by "
+ components[0].length + " matrix is not a square matrix");
rows = components;
initialize();
}
public LUPDecomposition(Matrix m) throws IllegalDimension {
if (!m.isSquare())
throw new IllegalDimension(
"Supplied matrix is not a square matrix");
initialize(m.components);
}
public LUPDecomposition(SymmetricMatrix m) {
initialize(m.components);
}
private double[] backwardSubstitution(double[] xTilde) {
int n = rows.length;
double[] answer = new double[n];
for (int i = n - 1; i >= 0; i--) {
answer[i] = xTilde[i];
for (int j = i + 1; j < n; j++)
answer[i] -= rows[i][j] * answer[j];
answer[i] /= rows[i][i];
}
return answer;
}
private void decompose() {
int n = rows.length;
permutation = new int[n];
for (int i = 0; i < n; i++)
permutation[i] = i;
parity = 1;
try {
for (int i = 0; i < n; i++) {
swapRows(i, largestPivot(i));
pivot(i);
}
} catch (ArithmeticException e) {
parity = 0;
}
}
private boolean decomposed() {
if (parity == 1 && permutation == null)
decompose();
return parity != 0;
}
public double determinant() {
if (!decomposed())
return Double.NaN;
double determinant = parity;
for (int i = 0; i < rows.length; i++)
determinant *= rows[i][i];
return determinant;
}
public double logDeterminant() {
if (!decomposed()) {
return Double.NaN;
}
int sign = parity;
double logDeterminant = 0.0;
for (int i = 0; i < rows.length; i++) {
if (rows[i][i] == 0.0) {
return Double.NaN;
}
logDeterminant += Math.log(Math.abs(rows[i][i]));
sign *= (rows[i][i] > 0 ? 1 : -1);
}
if (sign < 0) {
return Double.NaN;
} else {
return logDeterminant;
}
}
public boolean isPD() { // TODO Fix; check sign and for 0 in rows[i][i]
for (int i = 0; i < rows.length; i++) {
if (rows[i][i] <= 0)
return false;
}
return true;
}
private double[] forwardSubstitution(double[] c) {
int n = rows.length;
double[] answer = new double[n];
for (int i = 0; i < n; i++) {
answer[i] = c[permutation[i]];
for (int j = 0; j <= i - 1; j++)
answer[i] -= rows[i][j] * answer[j];
}
return answer;
}
private void initialize() {
permutation = null;
parity = 1;
}
private void initialize(double[][] components) {
int n = components.length;
rows = new double[n][n];
for (int i = 0; i < n; i++) {
for (int j = 0; j < n; j++)
rows[i][j] = components[i][j];
}
initialize();
}
public double[][] inverseMatrixComponents() {
if (!decomposed())
return null;
int n = rows.length;
double[][] inverseRows = new double[n][n];
double[] column = new double[n];
for (int i = 0; i < n; i++) {
for (int j = 0; j < n; j++)
column[j] = 0;
column[i] = 1;
column = solve(column);
for (int j = 0; j < n; j++)
inverseRows[i][j] = column[j];
}
return inverseRows;
}
private int largestPivot(int k) {
double maximum = Math.abs(rows[k][k]);
double abs;
int index = k;
for (int i = k + 1; i < rows.length; i++) {
abs = Math.abs(rows[i][k]);
if (abs > maximum) {
maximum = abs;
index = i;
}
}
return index;
}
private void pivot(int k) {
double inversePivot = 1 / rows[k][k];
int k1 = k + 1;
int n = rows.length;
for (int i = k1; i < n; i++) {
rows[i][k] *= inversePivot;
for (int j = k1; j < n; j++)
rows[i][j] -= rows[i][k] * rows[k][j];
}
}
public double[] solve(double[] c) {
return decomposed()
? backwardSubstitution(forwardSubstitution(c))
: null;
}
public Vector solve(Vector c) {
double[] components = solve(c.components);
if (components == null)
return null;
return components == null ? null : new Vector(components);
}
private void swapRows(int i, int k) {
if (i != k) {
double temp;
for (int j = 0; j < rows.length; j++) {
temp = rows[i][j];
rows[i][j] = rows[k][j];
rows[k][j] = temp;
}
int nTemp;
nTemp = permutation[i];
permutation[i] = permutation[k];
permutation[k] = nTemp;
parity = -parity;
}
}
public static void symmetrizeComponents(double[][] components) {
for (int i = 0; i < components.length; i++) {
for (int j = i + 1; j < components.length; j++) {
components[i][j] += components[j][i];
components[i][j] *= 0.5;
components[j][i] = components[i][j];
}
}
}
public String toString() {
StringBuffer sb = new StringBuffer();
char[] separator = {'[', ' '};
int n = rows.length;
for (int i = 0; i < n; i++) {
separator[0] = '{';
for (int j = 0; j < n; j++) {
sb.append(separator);
sb.append(rows[i][j]);
separator[0] = ' ';
}
sb.append('}');
sb.append('\n');
}
if (permutation != null) {
sb.append(parity == 1 ? '+' : '-');
sb.append("( " + permutation[0]);
for (int i = 1; i < n; i++)
sb.append(", " + permutation[i]);
sb.append(')');
sb.append('\n');
}
return sb.toString();
}
}