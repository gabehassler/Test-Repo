package dr.math;
import dr.math.distributions.GammaDistribution;
import dr.stats.DiscreteStatistics;
public class EmpiricalBayesPoissonSmoother {
public static double[] smooth(double[] in) {
final int length = in.length;
double[] out = new double[length];
double[] gammaStats = getNegBin(in);
double alpha = gammaStats[0];
double beta = gammaStats[1]; // As defined on wiki page (scale)
double mean = gammaStats[2];
if (beta == 0) {
for (int i = 0; i < length; i++) {
out[i] = mean;
}
} else {
for (int i = 0; i < length; i++) {
out[i] = (in[i] + alpha) / (1 + 1 / beta);
}
}
return out;
}
public static double[] smoothWithSample(double[] in) {
final int length = in.length;
double[] out = new double[length];
double[] gammaStats = getNegBin(in);
double alpha = gammaStats[0];
double beta = gammaStats[1]; // As defined on wiki page (scale)
double mean = gammaStats[2];
if (beta == 0) {
for (int i = 0; i < length; i++) {
out[i] = mean;
}
} else {
for (int i = 0; i < length; i++) {
double shape = in[i] + alpha;
double scale = 1 / (1 + 1 / beta);
out[i] = GammaDistribution.nextGamma(shape, scale);
}
}
return out;
}
public static double[] smoothOld(double[] in) {
final int length = in.length;
double[] out = new double[length];
double[] gammaStats = getNegBin(in);
for (int i = 0; i < length; i++) {
out[i] = (in[i] + gammaStats[0]) / (1 + 1 / gammaStats[1]);
}
return out;
}
// Method of moments estimators following Martiz 1969
private static double[] getNegBin(double[] array) {
double mean = DiscreteStatistics.mean(array);
double variance = DiscreteStatistics.variance(array, mean);
double returnArray0 = (1 - (mean / variance));
double returnArray1 = (mean * ((1 - returnArray0) / returnArray0));
double shape = returnArray1;
double scale = (returnArray0 / (1 - returnArray0));
if (variance <= mean) {
shape = 0.0;
scale = 0.0;
}
//        // Check against Martiz 1969 (beta = shape, alpha = rate in the 1969 paper)
//        double matrizBeta = mean * mean / (variance - mean);
//        double matrizAlphaInv = mean / matrizBeta; // scale
//        System.err.println("mb = " + matrizBeta + " shape = " + shape);
//        System.err.println("ma = " + matrizAlphaInv + " scale = " + scale);
return new double[]{shape, scale, mean};
}
}
