package dr.math.distributions;
public class ChiSquareDistribution extends GammaDistribution {
//
// Public stuff
//
public ChiSquareDistribution(double n) {
super(n / 2.0, 2.0);
this.n = n;
}
public double pdf(double x) {
return pdf(x, n);
}
public double cdf(double x) {
return cdf(x, n);
}
public double quantile(double y) {
return quantile(y, n);
}
public double mean() {
return mean(n);
}
public double variance() {
return variance(n);
}
public static double pdf(double x, double n) {
return pdf(x, n / 2.0, 2.0);
}
public static double cdf(double x, double n) {
return cdf(x, n / 2.0, 2.0);
}
public static double quantile(double y, double n) {
return quantile(y, n / 2.0, 2.0);
}
public static double mean(double n) {
return n;
}
public static double variance(double n) {
return 2.0 * n;
}
// Private
protected double n;
}
