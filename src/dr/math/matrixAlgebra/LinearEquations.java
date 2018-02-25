package dr.math.matrixAlgebra;
public class LinearEquations {
private double[][] rows;
private Vector[] solutions;
public LinearEquations(double[][] m, double[][] c)
throws IllegalDimension {
int n = m.length;
if (m[0].length != n)
throw new IllegalDimension("Illegal system: a" + n + " by "
+ m[0].length + " matrix is not a square matrix");
if (c[0].length != n)
throw new IllegalDimension("Illegal system: a " + n + " by " + n
+ " matrix cannot build a system with a "
+ c[0].length + "-dimensional vector");
rows = new double[n][n + c.length];
for (int i = 0; i < n; i++) {
for (int j = 0; j < n; j++)
rows[i][j] = m[i][j];
for (int j = 0; j < c.length; j++)
rows[i][n + j] = c[j][i];
}
}
public LinearEquations(double[][] m, double[] c)
throws IllegalDimension {
int n = m.length;
if (m[0].length != n)
throw new IllegalDimension("Illegal system: a" + n + " by "
+ m[0].length + " matrix is not a square matrix");
if (c.length != n)
throw new IllegalDimension("Illegal system: a " + n + " by " + n
+ " matrix cannot build a system with a "
+ c.length + "-dimensional vector");
rows = new double[n][n + 1];
for (int i = 0; i < n; i++) {
for (int j = 0; j < n; j++)
rows[i][j] = m[i][j];
rows[i][n] = c[i];
}
}
public LinearEquations(Matrix a, Vector y)
throws IllegalDimension {
this(a.components, y.components);
}
private void backSubstitution(int p) throws ArithmeticException {
int n = rows.length;
double[] answer = new double[n];
double x;
for (int i = n - 1; i >= 0; i--) {
x = rows[i][n + p];
for (int j = i + 1; j < n; j++)
x -= answer[j] * rows[i][j];
answer[i] = x / rows[i][i];
}
solutions[p] = new Vector(answer);
return;
}
private int largestPivot(int p) {
double pivot = Math.abs(rows[p][p]);
int answer = p;
double x;
for (int i = p + 1; i < rows.length; i++) {
x = Math.abs(rows[i][p]);
if (x > pivot) {
answer = i;
pivot = x;
}
}
return answer;
}
private void pivot(int p) throws ArithmeticException {
double inversePivot = 1 / rows[p][p];
double r;
int n = rows.length;
int m = rows[0].length;
for (int i = p + 1; i < n; i++) {
r = inversePivot * rows[i][p];
for (int j = p; j < m; j++)
rows[i][j] -= rows[p][j] * r;
}
return;
}
private void pivotingStep(int p) {
swapRows(p, largestPivot(p));
pivot(p);
return;
}
public Vector solution() throws ArithmeticException {
return solution(0);
}
public Vector solution(int p) throws ArithmeticException {
if (solutions == null)
solve();
if (solutions[p] == null)
backSubstitution(p);
return solutions[p];
}
private void solve() throws ArithmeticException {
int n = rows.length;
for (int i = 0; i < n; i++)
pivotingStep(i);
solutions = new Vector[rows[0].length - n];
}
private void swapRows(int p, int q) {
if (p != q) {
double temp;
int m = rows[p].length;
for (int j = 0; j < m; j++) {
temp = rows[p][j];
rows[p][j] = rows[q][j];
rows[q][j] = temp;
}
}
return;
}
public String toString() {
StringBuffer sb = new StringBuffer();
char[] separator = {'[', ' '};
int n = rows.length;
int m = rows[0].length;
for (int i = 0; i < n; i++) {
separator[0] = '(';
for (int j = 0; j < n; j++) {
sb.append(separator);
sb.append(rows[i][j]);
separator[0] = ',';
}
separator[0] = ':';
for (int j = n; j < m; j++) {
sb.append(separator);
sb.append(rows[i][j]);
separator[0] = ',';
}
sb.append(')');
sb.append('\n');
}
return sb.toString();
}
}