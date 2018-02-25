package dr.math;
public class FastFourierTransform {
public static void fft(double[] data, int nn, boolean inverse) {
int n, mmax, m, j, istep, i;
double wtemp, wr, wpr, wpi, wi, theta;
double tempr, tempi;
final double radians;
if (inverse) {
radians = 2.0 * Math.PI;
} else {
radians = -2.0 * Math.PI;
}
// reverse-binary reindexing
n = nn << 1;
j = 1;
for (i = 1; i < n; i += 2) {
if (j > i) {
swap(data, j - 1, i - 1);
swap(data, j, i);
}
m = nn;
while (m >= 2 && j > m) {
j -= m;
m >>= 1;
}
j += m;
}
// here begins the Danielson-Lanczos section
mmax = 2;
while (n > mmax) {
istep = mmax << 1;
theta = radians / mmax;
wtemp = Math.sin(0.5 * theta);
wpr = -2.0 * wtemp * wtemp;
wpi = Math.sin(theta);
wr = 1.0;
wi = 0.0;
for (m = 1; m < mmax; m += 2) {
for (i = m; i <= n; i += istep) {
j = i + mmax;
tempr = wr * data[j - 1] - wi * data[j];
tempi = wr * data[j] + wi * data[j - 1];
data[j - 1] = data[i - 1] - tempr;
data[j] = data[i] - tempi;
data[i - 1] += tempr;
data[i] += tempi;
}
wtemp = wr;
wr += wr * wpr - wi * wpi;
wi += wi * wpr + wtemp * wpi;
}
mmax = istep;
}
}
public static void fft(ComplexArray ca, boolean inverse) {
final double[] real = ca.real;
final double[] complex = ca.complex;
int n, mmax, m, j, istep, i;
double wtemp, wr, wpr, wpi, wi, theta;
double tempr, tempi;
final double radians;
if (inverse) {
radians = 2.0 * Math.PI;
} else {
radians = -2.0 * Math.PI;
}
// reverse-binary reindexing
n = ca.length << 1;
j = 1;
for (i = 1; i < n; i += 2) {
if (j > i) {
final int halfI = i >> 1;
final int halfJ = j >> 1;
swap(real, halfJ, halfI);
swap(complex, halfJ, halfI);
}
m = ca.length;
while (m >= 2 && j > m) {
j -= m;
m >>= 1;
}
j += m;
}
// here begins the Danielson-Lanczos section
mmax = 2;
while (n > mmax) {
istep = mmax << 1;
theta = (radians / mmax);
wtemp = Math.sin(0.5 * theta);
wpr = -2.0 * wtemp * wtemp;
wpi = Math.sin(theta);
wr = 1.0;
wi = 0.0;
for (m = 1; m < mmax; m += 2) {
for (i = m; i <= n; i += istep) {
j = i + mmax;
final int halfI = i >> 1;
final int halfJ = j >> 1;
tempr = wr * real[halfJ] - wi * complex[halfJ];
tempi = wr * complex[halfJ] + wi * real[halfJ];
real[halfJ] = real[halfI] - tempr;
complex[halfJ] = complex[halfI] - tempi;
real[halfI] += tempr;
complex[halfI] += tempi;
}
wtemp = wr;
wr += wr * wpr - wi * wpi;
wi += wi * wpr + wtemp * wpi;
}
mmax = istep;
}
}
private static void swap(double[] x, int i, int j) {
double tmp = x[i];
x[i] = x[j];
x[j] = tmp;
}
}
