package dr.util;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public interface Transform {
double transform(double value);
double inverse(double value);
String getTransformName();
public double getLogJacobian(double value);
public static class LogTransform implements Transform, Citable {
public LogTransform() {
}
public double transform(double value) {
return Math.log(value);
}
public double inverse(double value) {
return Math.exp(value);
}
public String getTransformName() {
return "log";
}
public double getLogJacobian(double value) {
return -Math.log(value);
}
public List<Citation> getCitations() {
List<Citation> citations = new ArrayList<Citation>();
citations.add(new Citation(
new Author[]{
new Author("MA", "Suchard"),
new Author("G", "Baele"),
new Author("P", "Lemey"),
},
Citation.Status.IN_PREPARATION
));
return citations;
}
}
public static class LogitTransform implements Transform {
public LogitTransform() {
}
public double transform(double value) {
return Math.log(value / (1.0 - value));
}
public double inverse(double value) {
return 1.0 / (1.0 + Math.exp(-value));
}
public String getTransformName() {
return "logit";
}
public double getLogJacobian(double value) {
return -Math.log(1.0 - value) - Math.log(value);
}
}
public static class FisherZTransform implements Transform {
public FisherZTransform() {
}
public double transform(double value) {
return 0.5 * (Math.log(1.0 + value) - Math.log(1.0 - value));
}
public double inverse(double value) {
return (Math.exp(2 * value) - 1) / (Math.exp(2 * value) + 1);
}
public String getTransformName() {
return "fisherz";
}
public double getLogJacobian(double value) {
return -Math.log(1 - value) - Math.log(1 + value);
}
}
public static class NoTransform implements Transform {
public NoTransform() {
}
public double transform(double value) {
return value;
}
public double inverse(double value) {
return value;
}
public String getTransformName() {
return "none";
}
public double getLogJacobian(double value) {
return 0.0;
}
}
public class ParsedTransform {
public Transform transform;
public int start; // zero-indexed
public int end; // zero-indexed, i.e, i = start; i < end; ++i
public int every;
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Transform thisTransform = Transform.NONE;
String name = (String) xo.getAttribute(TYPE);
for (Transform type : Transform.transformList) {
if (name.equals(type.getTransformName())) {
thisTransform = type;
break;
}
}
ParsedTransform transform = new ParsedTransform();
transform.transform = thisTransform;
transform.start = xo.getAttribute(START, 1);
transform.end = xo.getAttribute(END, Integer.MAX_VALUE);
transform.every = xo.getAttribute(EVERY, 1);
transform.start--; // zero-indexed
return transform;
}
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[]{
AttributeRule.newStringRule(TYPE),
AttributeRule.newIntegerRule(START, true),
AttributeRule.newIntegerRule(END, true),
AttributeRule.newIntegerRule(EVERY, true),
};
}
public String getParserDescription() {
return null;
}
public Class getReturnType() {
return ParsedTransform.class;
}
public String getParserName() {
return TRANSFORM;
}
};
public class Util {
public static Transform[] getListOfNoTransforms(int size) {
Transform[] transforms = new Transform[size];
for (int i = 0; i < size; ++i) {
transforms[i] = NONE;
}
return transforms;
}
}
public static final LogTransform LOG = new LogTransform();
public static final LogitTransform LOGIT = new LogitTransform();
public static final NoTransform NONE = new NoTransform();
public static final FisherZTransform FISHER_Z = new FisherZTransform();
public static final Transform[] transformList = {LOG, LOGIT, NONE, FISHER_Z};
public static final String TRANSFORM = "transform";
public static final String TYPE = "type";
public static final String START = "start";
public static final String END = "end";
public static final String EVERY = "every";
}
