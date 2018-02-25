package dr.math.distributions;
import dr.math.GammaFunction;
import dr.math.UnivariateFunction;
public class InverseGammaDistribution implements Distribution {
private double shape, scale;
private final double factor;
private final double logFacor;
public InverseGammaDistribution(double shape, double scale) {
this.shape = shape;
this.scale = scale;
this.factor = Math.pow(scale, shape) / Math.exp(GammaFunction.lnGamma(shape));
this.logFacor = shape * Math.log(scale) - GammaFunction.lnGamma(shape);
}
public double getShape() {
return shape;
}
public void setShape(double value) {
shape = value;
}
public double getScale() {
return scale;
}
public void setScale(double value) {
scale = value;
}
public double pdf(double x) {
return pdf(x, shape, scale, factor);
}
public double logPdf(double x) {
return logPdf(x, shape, scale, logFacor);
}
public double cdf(double x) {
return cdf(x, shape, scale);
}
public double quantile(double y) {
return quantile(y, shape, scale);
}
public double mean() {
return mean(shape, scale);
}
public double variance() {
return variance(shape, scale);
}
public double nextInverseGamma() {
return nextInverseGamma(shape, scale);
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
public static double pdf(double x, double shape, double scale, double factor) {
if (x <= 0)
return 0.0;
final double a = Math.exp(shape*Math.log(scale) - (shape + 1)*Math.log(x) - (scale/x)
- GammaFunction.lnGamma(shape));
return factor * a;
}
public static double logPdf(double x, double shape, double scale, double factor) {
if (x <= 0)
return Double.NEGATIVE_INFINITY;
return  factor + shape*Math.log(scale) - (shape + 1)*Math.log(x) - (scale/x) - GammaFunction.lnGamma(shape);
}
public static double cdf(double x, double shape, double scale) {        
if (x <= 0.0 || shape <= 0.0) {
return 0.0;
}
return GammaFunction.incompleteGammaQ(shape, scale/x);
}
public static double quantile(double y, double shape, double scale) {
// this is what R thinks
final GammaDistribution g = new GammaDistribution(shape, scale);
return 1/g.quantile(1-y);
}
public static double mean(double shape, double scale) {
if( shape > 1 ) {
return scale / (shape - 1);
}
return Double.POSITIVE_INFINITY;
}
public static double variance(double shape, double scale) {
if( shape > 2 ) {
return scale*scale / ((shape - 1)*(scale-1)*(scale-2));
}
return Double.POSITIVE_INFINITY;
}
public static double nextInverseGamma(double shape, double scale) {
return 1.0 / new GammaDistribution(shape, 1/scale).nextGamma();
}
}