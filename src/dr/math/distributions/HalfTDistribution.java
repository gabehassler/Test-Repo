package dr.math.distributions;
public class HalfTDistribution extends TDistribution {
public HalfTDistribution(double scale, double df) {
super(0.0, scale, df);
}
public double pdf(double x) {
return x < 0.0 ? 0.0 : super.pdf(x) * 2.0;
}
public double logPdf(double x) {
return x < 0.0 ? Double.NEGATIVE_INFINITY : super.logPdf(x) + Math.log(2.0);
}
public double mean() {
throw new RuntimeException("Not yet implemented");
}
public double variance() {
throw new RuntimeException("Not yet implemented");
}
}
