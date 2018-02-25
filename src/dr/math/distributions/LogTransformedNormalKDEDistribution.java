package dr.math.distributions;
import dr.math.ComplexArray;
import dr.math.FastFourierTransform;
import dr.stats.DiscreteStatistics;
import dr.util.HeapSort;
import java.util.Arrays;
public class LogTransformedNormalKDEDistribution extends KernelDensityEstimatorDistribution {
public static final int MINIMUM_GRID_SIZE = 2048;
public static final boolean DEBUG = false;
//the samples should not already be log transformed (the log transformation is done in this class)
public LogTransformedNormalKDEDistribution(Double[] sample) {
this(sample, null, null, null);
}
public LogTransformedNormalKDEDistribution(Double[] sample, int n) {
this(sample, null, null, null, 3.0, n);
}
public LogTransformedNormalKDEDistribution(Double[] sample, Double lowerBound, Double upperBound, Double bandWidth) {
this(sample, lowerBound, upperBound, bandWidth, 3.0, MINIMUM_GRID_SIZE);
}
public LogTransformedNormalKDEDistribution(Double[] sample, Double lowerBound, Double upperBound, Double bandWidth,
int n) {
this(sample, lowerBound, upperBound, bandWidth, 3.0, n);
}
public LogTransformedNormalKDEDistribution(Double[] sample, Double lowerBound, Double upperBound, Double bandWidth, double cut, int n) {
//first call the super constructor, but immediately overwrite the stored information
this.sample = new double[sample.length];
for (int i = 0; i < sample.length; i++) {
this.sample[i] = sample[i];
}
this.N = sample.length;
processBounds(lowerBound, upperBound);
setBandWidth(bandWidth); 
super(sample, lowerBound, upperBound, bandWidth);
//transform the data to the log scale and store in logSample
if (DEBUG) {
System.out.println("Creating the KDE in log space");
System.out.println("lowerBound = " + lowerBound);
System.out.println("upperBound = " + upperBound);
}
this.logSample = new double[sample.length];
for (int i = 0; i < logSample.length; i++) {
this.logSample[i] = Math.log(sample[i]);
}
//keep a backup copy of the samples in normal space
this.backupSample = new double[sample.length];
for (int i = 0; i < sample.length; i++) {
this.backupSample[i] = sample[i];
}
//overwrite the stored samples, sample.length stays the same
this.sample = logSample;
processBounds(lowerBound, upperBound);
setBandWidth(bandWidth);
this.gridSize = Math.max(n, MINIMUM_GRID_SIZE);
if (this.gridSize > MINIMUM_GRID_SIZE) {
this.gridSize = (int) Math.pow(2, Math.ceil(Math.log(this.gridSize) / Math.log(2.0)));
}
this.cut = cut;
from = DiscreteStatistics.min(this.sample) - this.cut * this.bandWidth;
to = DiscreteStatistics.max(this.sample) + this.cut * this.bandWidth;
if (DEBUG) {
System.out.println("bandWidth = " + this.bandWidth);
System.out.println("cut = " + this.cut);
System.out.println("from = " + from);
System.out.println("to = " + to);
}
lo = from - 4.0 * this.bandWidth;
up = to + 4.0 * this.bandWidth;
if (DEBUG) {
System.out.println("lo = " + lo);
System.out.println("up = " + up);
}
densityKnown = false;
//run computeDensity to estimate the KDE on the log scale
//and afterwards return to the normal scale
computeDensity();
}
public double getFromPoint() {
return from;
}
public double getToPoint() {
return to;
}
private double linearApproximate(double[] x, double[] y, double pt, double low, double high) {
int i = 0;
int j = x.length - 1;
if (pt < x[i]) {
return low;
}
if (pt > x[j]) {
return high;
}
// Bisection search
while (i < j - 1) {
int ij = (i + j) / 2;
if (pt < x[ij]) {
j = ij;
} else {
i = ij;
}
}
if (pt == x[j]) {
return y[j];
}
if (pt == x[i]) {
return y[i];
}
//System.out.println("return value: "+ (y[i] + (y[j] - y[i]) * ((pt - x[i]) / (x[j] - x[i]))));
return y[i] + (y[j] - y[i]) * ((pt - x[i]) / (x[j] - x[i]));
}
private double[] rescaleAndTrim(double[] x) {
final int length = x.length / 2;
final double scale = 1.0 / x.length;
double[] out = new double[length];
for (int i = 0; i < length; ++i) {
out[i] = x[i] * scale;
if (out[i] < 0) {
out[i] = 0;
}
}
return out;
}
private double[] massdist(double[] x, double xlow, double xhigh, int ny) {
int nx = x.length;
double[] y = new double[ny * 2];
final int ixmin = 0;
final int ixmax = ny - 2;
final double xdelta = (xhigh - xlow) / (ny - 1);
for (int i = 0; i < ny; ++i) {
y[i] = 0.0;
}
final double xmi = 1.0 / nx;
for (int i = 0; i < nx; ++i) {
final double xpos = (x[i] - xlow) /  xdelta;
final int ix = (int) Math.floor(xpos);
final double fx = xpos - ix;
//            final double xmi = xmass[i];
if (ixmin <= ix && ix <= ixmax) {
y[ix] += (1 - fx) * xmi;
y[ix + 1] += fx * xmi;
} else if (ix == -1) {
y[0] += fx * xmi;
} else if (ix == ixmax + 1) {
y[ix] += (1 - fx) * xmi;
}
}
return y;
}
protected void fillKernelOrdinates(ComplexArray ordinates, double bandWidth) {
final int length = ordinates.length;
final double a = 1.0 / (Math.sqrt(2.0 * Math.PI) * bandWidth);
final double precision = -0.5 / (bandWidth * bandWidth);
for (int i = 0; i < length; i++) {
final double x = ordinates.real[i];
ordinates.real[i] = a * Math.exp(x * x * precision);
}
}
protected void computeDensity() {
//transformData calls massdist and rescaleAndTrim
makeOrdinates();
//makeOrdinates calls fillKernelOrdinates
transformData();
//we're still in log space and need to return to normal space
//preferably before setting densityKnown to true
//stored values are in xPoints and densityPoints
transformEstimator();
densityKnown = true;
}
private void transformEstimator() {
if (DEBUG) {
System.out.println("\nCreating the KDE in normal space");
System.out.println("lowerBound = " + lowerBound);
System.out.println("upperBound = " + upperBound);
}
this.sample = backupSample;
//processBounds(lowerBound, upperBound);
setBandWidth(null);
from = DiscreteStatistics.min(this.sample) - this.cut * this.bandWidth;
to = DiscreteStatistics.max(this.sample) + this.cut * this.bandWidth;
if (DEBUG) {
System.out.println("min: " + DiscreteStatistics.min(this.sample));
System.out.println("max: " + DiscreteStatistics.max(this.sample));
System.out.println("bandWidth = " + this.bandWidth);
System.out.println("cut = " + this.cut);
System.out.println("from = " + from);
System.out.println("to = " + to);
}
lo = from - 4.0 * this.bandWidth;
up = to + 4.0 * this.bandWidth;
if (DEBUG) {
System.out.println("lo = " + lo);
System.out.println("up = " + up);
}
if (lo < 0.0) {
//small hack, but our asymmetric kernel estimators are terribly slow
lo = DiscreteStatistics.min(this.sample);
}
//make new ordinates for the transformation back to normal space
//need a backup of the xPoints for the log scale KDE
this.backupXPoints = new double[xPoints.length];
System.arraycopy(xPoints, 0, backupXPoints, 0, xPoints.length);
makeOrdinates();
//the KDE on log scale is contained in the xPoints and densityPoints arrays
//copy them to finalXPoints and finalDensityPoints
//this.finalXPoints = new double[xPoints.length - numberOfNegatives];
//this.finalXPoints = new double[xPoints.length];
//System.arraycopy(xPoints, numberOfNegatives, finalXPoints, 0, xPoints.length - numberOfNegatives);
//System.arraycopy(xPoints, numberOfNegatives, finalXPoints, 0, xPoints.length);
if (DEBUG) {
for (int i = 0; i < xPoints.length; i++) {
System.out.println(xPoints[i] + "   " + backupXPoints[i] + " : " + densityPoints[i]);
}
//System.out.println("\nfinalXPoints length = " + finalXPoints.length);
}
//this.finalDensityPoints = new double[densityPoints.length - numberOfNegatives];
this.finalDensityPoints = new double[densityPoints.length];
for (int i = 0; i < xPoints.length; i++) {
finalDensityPoints[i] = linearApproximate(backupXPoints, densityPoints, Math.log(xPoints[i]), 0.0, 0.0)*(1.0/xPoints[i]);
if (DEBUG) {
System.out.println(xPoints[i] + "\t" + finalDensityPoints[i]);
}
}
//System.exit(0);
}
private void transformData() {
ComplexArray Y  = new ComplexArray(massdist(this.logSample, lo, up, this.gridSize));
FastFourierTransform.fft(Y, false);
ComplexArray product = Y.product(kOrdinates);
FastFourierTransform.fft(product, true);
densityPoints = rescaleAndTrim(product.real); 
for (int i = 0; i < gridSize; i++) {
System.out.println(densityPoints[i]);
}*/
}
private void makeOrdinates() {
final int length = 2 * gridSize;
if (kOrdinates == null) {
kOrdinates = new ComplexArray(new double[length]);
}
// Fill with grid values
final double max = 2.0 * (up - lo);
double value = 0;
final double inc = max / (length - 1);
for (int i = 0; i <= gridSize; i++) {
kOrdinates.real[i] = value;
value += inc;
}
for (int i = gridSize + 1; i < length; i++) {
kOrdinates.real[i] = -kOrdinates.real[length - i];
}
fillKernelOrdinates(kOrdinates, bandWidth);
FastFourierTransform.fft(kOrdinates, false);
kOrdinates.conjugate();
// Make x grid
xPoints = new double[gridSize];
double x = lo;
double delta = (up - lo) / (gridSize - 1);
if (DEBUG) {
System.out.println("X");
}
for (int i = 0; i < gridSize; i++) {
xPoints[i] = x;
x += delta;
if (DEBUG) {
System.out.println(xPoints[i]);
}
}
}
@Override
protected double evaluateKernel(double x) {        
if (!densityKnown) {
//computeDensity() calls makeOrdinates and transformData
computeDensity();
}
//xPoints and densityPoints are now back in normal space
return linearApproximate(xPoints, finalDensityPoints, x, 0.0, 0.0);
}
@Override
protected void processBounds(Double lowerBound, Double upperBound) {
if ((lowerBound != null && lowerBound != Double.NEGATIVE_INFINITY) ||
(upperBound != null && upperBound != Double.POSITIVE_INFINITY)) {
throw new RuntimeException("LogTransformedNormalKDEDistribution must be unbounded");
}
}
@Override
protected void setBandWidth(Double bandWidth) {
if (bandWidth == null) {
// Default bandwidth
this.bandWidth = bandwidthNRD(sample);
} else
this.bandWidth = bandWidth;
densityKnown = false;
}
public double bandwidthNRD(double[] x) {
int[] indices = new int[x.length];
HeapSort.sort(x, indices);
final double h =
(DiscreteStatistics.quantile(0.75, x, indices) - DiscreteStatistics.quantile(0.25, x, indices)) / 1.34;
return 1.06 *
Math.min(Math.sqrt(DiscreteStatistics.variance(x)), h) *
Math.pow(x.length, -0.2);
}
private ComplexArray kOrdinates;
private double[] xPoints, backupXPoints;
private double[] densityPoints, finalDensityPoints;
private double[] backupSample, logSample;
private int gridSize;
private double cut;
private double from;
private double to;
private double lo;
private double up;
private boolean densityKnown = false;
public static void main(String[] args) {
LogTransformedNormalKDEDistribution five = new LogTransformedNormalKDEDistribution(testfivehundred);
for (int i = 0; i < 100; i++) {
System.out.println(((double) i / 1E7) + " : " + five.evaluateKernel((double)i/1E7));
}
System.exit(0);*/
LogTransformedNormalKDEDistribution five = new LogTransformedNormalKDEDistribution(testvpu);
for (int i = 0; i < 100; i++) {
System.out.println(((double) i / 1E12) + " : " + five.evaluateKernel((double)i/1E12));
}
System.out.println();
for (int i = 0; i < 100; i++) {
System.out.println(((double) i / 1E14) + " : " + five.evaluateKernel((double)i/1E14));
}
System.out.println();
for (int i = 0; i < 100; i++) {
System.out.println(((double) i / 1E16) + " : " + five.evaluateKernel((double)i/1E16));
}
System.out.println();
for (int i = 0; i < 100; i++) {
System.out.println(((double) i / 1E26) + " : " + five.evaluateKernel((double)i/1E26));
}
System.exit(0);*/
//Normal distribution
Double[] samples = new Double[10000];
NormalDistribution dist = new NormalDistribution(0.5, 0.10);
for (int i = 0; i < samples.length; i++) {
samples[i] = (Double)dist.nextRandom();
if (samples[i] < 0.0 && DEBUG) {
System.err.println("Negative value generated!");
}
}
Arrays.sort(samples);
if (DEBUG) {
System.out.println("min: " + samples[0]);
System.out.println("max: " + samples[samples.length - 1] + "\n");
}
LogTransformedNormalKDEDistribution ltn = new LogTransformedNormalKDEDistribution(samples);
NormalKDEDistribution nKDE = new NormalKDEDistribution(samples);
if (DEBUG) {
for (int i = 0; i < 50; i++) {
Double test = (Double) dist.nextRandom();
System.out.println("random draw: " + test);
System.out.println("normal KDE: " + nKDE.evaluateKernel(test));
System.out.println("log transformed normal KDE: " + ltn.evaluateKernel(test) + "\n");
}
}
//LogNormal distribution
samples = new Double[2000];
dist = new NormalDistribution(0.0, 1.0);
for (int i = 0; i < samples.length; i++) {
samples[i] = (Double)dist.nextRandom();
while (samples[i] < 0.0) {
samples[i] = (Double)dist.nextRandom();
}
samples[i] = Math.exp(samples[i]-Math.exp(1.0));
}
//Generate R code for visualisation
System.out.print("par(mfrow=c(2,2))\n\nsamples <- c(");
for (int i = 0; i < samples.length-1; i++) {
System.out.print(samples[i] + ",");
}
System.out.println(samples[samples.length-1] + ")\n");
System.out.println("hist(samples, 200)\nminimum=min(samples)\nabline(v=minimum,col=2,lty=2)\n");
System.out.println("plot(density(samples))\nabline(v=minimum,col=2,lty=2)\n");
Arrays.sort(samples);
if (DEBUG) {
System.out.println("min: " + samples[0]);
System.out.println("max: " + samples[samples.length - 1] + "\n");
}
ltn = new LogTransformedNormalKDEDistribution(samples);
nKDE = new NormalKDEDistribution(samples);
System.out.print("normalKDE <- c(");
for (int i = 0; i < 1999; i++) {
Double test = 0.0 + ((double)i)/((double)1000);
System.out.print(nKDE.evaluateKernel(test) + ",");
}
System.out.println(nKDE.evaluateKernel(((double)1999)/((double)1000)) + ")\n");
System.out.println("index <- seq(0.0,1.999,by=0.001)");
System.out.println("plot(index,normalKDE,type=\"l\")\nabline(v=minimum,col=2,lty=2)\n");
System.out.print("TransKDE <- c(");
for (int i = 0; i < 1999; i++) {
Double test = 0.0 + ((double)i)/((double)1000);
System.out.print(ltn.evaluateKernel(test) + ",");
}
System.out.println(ltn.evaluateKernel(((double)1999)/((double)1000)) + ")\n");
System.out.println("plot(index,TransKDE,type=\"l\")\nabline(v=minimum,col=2,lty=2)");
}
}