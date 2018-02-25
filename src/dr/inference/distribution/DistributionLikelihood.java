package dr.inference.distribution;
import dr.math.distributions.Distribution;
import dr.util.Attribute;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
public class DistributionLikelihood extends AbstractDistributionLikelihood {
public final static boolean DEBUG = false;
public static final String DISTRIBUTION_LIKELIHOOD = "distributionLikelihood";
private int from = -1;
private int to = Integer.MAX_VALUE;
private final boolean evaluateEarly;
public DistributionLikelihood(Distribution distribution) {
this(distribution, 0.0, false, 1.0);
}
public DistributionLikelihood(Distribution distribution, double offset) {
this(distribution, offset, offset > 0.0, 1.0);
}
public DistributionLikelihood(Distribution distribution, double offset, double scale){
this(distribution, offset, offset>0.0, scale);
}
public DistributionLikelihood(Distribution distribution, boolean evaluateEarly) {
this(distribution, 0.0, evaluateEarly, 1.0);
}
public DistributionLikelihood(Distribution distribution, double offset, boolean evaluateEarly, double scale) {
super(null);
this.distribution = distribution;
this.offset = offset;
this.evaluateEarly = evaluateEarly;
this.scale=scale;
}
public DistributionLikelihood(ParametricDistributionModel distributionModel) {
super(distributionModel);
this.distribution = distributionModel;
this.offset = 0.0;
this.evaluateEarly = false;
this.scale=1.0;
}
public Distribution getDistribution() {
return distribution;
}
public void setRange(int from, int to) {
this.from = from;
this.to = to;
}
// **************************************************************
// Likelihood IMPLEMENTATION
// **************************************************************
public double calculateLogLikelihood() {
if (DEBUG) {
System.out.println("Calling DistributionLikelihood.calculateLogLikelihood()");
System.out.println(distribution.toString());
System.out.println(dataList.toString() + "\n");
}
double logL = 0.0;
for( Attribute<double[]> data : dataList ) {
// Using this in the loop is incredibly wasteful, especially in the loop condition to get the length
final double[] attributeValue = data.getAttributeValue();
for (int j = Math.max(0, from); j < Math.min(attributeValue.length, to); j++) {
final double value = attributeValue[j] - offset;
if (offset > 0.0 && value < 0.0) {
// fixes a problem with the offset on exponential distributions not
// actually bounding the distribution. This only performs this check
// if a non-zero offset is actually given otherwise it assumes the
// parameter is either legitimately allowed to go negative or is bounded
// at zero anyway.
return Double.NEGATIVE_INFINITY;
}
logL += distribution.logPdf(value/scale)/scale;
}
}
return logL;
}
@Override
public boolean evaluateEarly() {
return evaluateEarly;
}
// **************************************************************
// XMLElement IMPLEMENTATION
// **************************************************************
public Element createElement(Document d) {
throw new RuntimeException("Not implemented yet!");
}
@Override
public String prettyName() {
String s = distribution.getClass().getName();
String[] parts = s.split("\\.");
s = parts[parts.length - 1];
if( s.endsWith("Distribution") ) {
s = s.substring(0, s.length() - "Distribution".length());
}
s = s + '(';
for( Attribute<double[]> data : dataList ) {
String name = data.getAttributeName();
if( name == null ) {
name = "?";
}
s = s + name + ',';
}
s = s.substring(0,s.length()-1) + ')';
return s;
}
protected Distribution distribution;
private final double offset;
private final double scale;
}
