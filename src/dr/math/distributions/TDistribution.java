package dr.math.distributions;
import dr.math.GammaFunction;
import dr.math.UnivariateFunction;
public class TDistribution implements Distribution {
public TDistribution(double center, double scale, double df) {
this.center = center;
this.scale = scale;
this.df = df;
}
public double pdf(double x) {
return Math.exp(logPDF(x, center, scale, df));
}
public double logPdf(double x) {
return logPDF(x, center, scale, df);
}
public double cdf(double x) {
throw new RuntimeException("Not yet implemented");
}
public double quantile(double y) {
throw new RuntimeException("Not yet implemented");
}
public double mean() {
if (df > 1)
return center;
return Double.NaN;
}
public double variance() {
if (df > 2)
return scale * df / (df - 2);
return Double.NaN;
}
public UnivariateFunction getProbabilityDensityFunction() {
throw new RuntimeException("Not yet implemented");
}
public static double logPDF(double x, double x0, double scale, double df) {
double loc = x - x0;
double logPDF = GammaFunction.lnGamma((df + 1) / 2)
- 0.5 * Math.log(df)
- logSqrtPi
- 0.25 * Math.log(scale)
- GammaFunction.lnGamma(df / 2)
- (df + 1) / 2 * Math.log(1 + loc * loc / df / scale);
return logPDF;
}
private double df;
private double scale;
private double center;
private static double logSqrtPi = Math.log(Math.sqrt(Math.PI));
}
