package dr.math.distributions;
import dr.math.UnivariateFunction;
public class LogNormalDistribution implements Distribution {
//
// Public stuff
//
public LogNormalDistribution(double M, double S) {
this.M = M;
this.S = S;
}
public final double getM() {
return M;
}
public final void setM(double M) {
this.M = M;
}
public final double getS() {
return S;
}
public final void setS(double S) {
this.S = S;
}
public double pdf(double x) {
return pdf(x, M, S);
}
public double logPdf(double x) {
return logPdf(x, M, S);
}
public double cdf(double x) {
return cdf(x, M, S);
}
public double quantile(double y) {
return quantile(y, M, S);
}
public double mean() {
return mean(M, S);
}
public double variance() {
return variance(M, S);
}
public double mode() {
return mode(M, S);
}
public final UnivariateFunction getProbabilityDensityFunction() {
return pdfFunction;
}
private final UnivariateFunction pdfFunction = new UnivariateFunction() {
public final double evaluate(double x) {
return pdf(x);
}
public final double getLowerBound() {
return 0.0;
}
public final double getUpperBound() {
return Double.POSITIVE_INFINITY;
}
};
public static double pdf(double x, double M, double S) {
if (x <= 0) return 0; // no density for x<=0
return NormalDistribution.pdf(Math.log(x), M, S) / x;
}
public static double logPdf(double x, double M, double S) {
if (x < 0) return Double.NEGATIVE_INFINITY; // no density for x<0
return NormalDistribution.logPdf(Math.log(x), M, S) - Math.log(x);
}
public static double cdf(double x, double M, double S) {
if (x < 0) return 0; // no density for x<0
return NormalDistribution.cdf(Math.log(x), M, S, false);
//return NormalDistribution.cdf(Math.log(x), M, S);
}
public static double quantile(double z, double M, double S) {
return Math.exp(NormalDistribution.quantile(z, M, S));
}
public static double mean(double M, double S) {
return Math.exp(M + (S * S / 2));
}
public static double mode(double M, double S) {
return Math.exp(M - S * S);
}
public static double variance(double M, double S) {
final double S2 = S * S;
return Math.exp(S2 + 2 * M) * (Math.exp(S2) - 1);
}
// Private
protected double M, S;
}
