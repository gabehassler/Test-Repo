package dr.math.matrixAlgebra;
public class CholeskyDecomposition {
private int n;
public boolean isSPD() {
return isspd;
}
private boolean isspd;
public double[][] getL() {
return L;
}
private double[][] L;
public CholeskyDecomposition(double[][] A) throws IllegalDimension {
n = A.length;
L = new double[n][n];
isspd = (A[0].length == n);
if (!isspd)
throw new IllegalDimension("Cholesky decomposition is only defined for square matrices");
// Main loop.
for (int j = 0; j < n; j++) {
double[] Lrowj = L[j];
double d = 0.0;
for (int k = 0; k < j; k++) {
double[] Lrowk = L[k];
double s = 0.0;
for (int i = 0; i < k; i++) {
s += Lrowk[i] * Lrowj[i];
}
Lrowj[k] = s = (A[j][k] - s) / L[k][k];
d = d + s * s;
isspd = isspd & (A[k][j] == A[j][k]);
}
d = A[j][j] - d;
isspd = isspd & (d > 0.0);
L[j][j] = Math.sqrt(Math.max(d, 0.0));
L[j][k] = 0.0;
}*/
}
}
}
