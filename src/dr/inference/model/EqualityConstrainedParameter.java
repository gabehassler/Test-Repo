package dr.inference.model;
import dr.util.Author;
import dr.util.Citable;
import dr.util.Citation;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public class EqualityConstrainedParameter extends Parameter.Abstract implements VariableListener, Citable {
public static final String EQUALITY_CONSTRAINED_PARAMETER = "constrainedEqualParameter";
public EqualityConstrainedParameter(String name, List<Parameter> params) {
super(name);
uniqueParameters = params;
dimension = uniqueParameters.get(0).getDimension();  // TODO Assumes that all parameters have the same dimension
bounds = makeIntersectionBounds();
for (Parameter parameter : params) {
parameter.addParameterListener(this);
// Convert all unique parameters' bounds into intersection bounds on all parameters
for (Parameter otherParameter : params) {
if (parameter != otherParameter) {
parameter.addBounds(otherParameter.getBounds());
}
}
}
StringBuilder sb = new StringBuilder("Constraining multiple parameters to be equal: ");
sb.append(getId()).append("\n");
sb.append("\tPlease cite:\n");
sb.append(Citable.Utils.getCitationString(this));
java.util.logging.Logger.getLogger("dr.inference.model").info(sb.toString());
}
public final String getParameterName() {
return getId();
}
public int getDimension() {
return dimension;
}
public void setDimension(int dim) {
throw new UnsupportedOperationException();
}
public void addBounds(Bounds<Double> boundary) {
throw new UnsupportedOperationException();
}
public Bounds<Double> getBounds() {
return bounds;
}
private Bounds<Double> makeIntersectionBounds() {
Bounds<Double> bounds = new IntersectionBounds(dimension);
for (Parameter p : uniqueParameters) {
((IntersectionBounds) bounds).addBounds(p.getBounds());
}
return bounds;
}
public void addDimension(int index, double value) {
throw new UnsupportedOperationException();
}
public double removeDimension(int index) {
throw new UnsupportedOperationException();
}
public double getParameterValue(int dim) {
return uniqueParameters.get(0).getParameterValue(dim);
}
public void setParameterValue(int dim, double value) {
isLocked = true;
for (Parameter p : uniqueParameters) {
p.setParameterValue(dim, value);
}
isLocked = false;
}
public void setParameterValueQuietly(int dim, double value) {
isLocked = true;
for (Parameter p : uniqueParameters) {
p.setParameterValueQuietly(dim, value);
}
isLocked = false;
}
public void setParameterValueNotifyChangedAll(int dim, double value) {
isLocked = true;
for (Parameter p : uniqueParameters) {
p.setParameterValueNotifyChangedAll(dim, value);
}
isLocked = false;
}
protected void storeValues() {
for (Parameter parameter : uniqueParameters) {
parameter.storeParameterValues();
}
}
protected void restoreValues() {
for (Parameter parameter : uniqueParameters) {
parameter.restoreParameterValues();
}
}
protected final void acceptValues() {
for (Parameter parameter : uniqueParameters) {
parameter.acceptParameterValues();
}
}
protected final void adoptValues(Parameter source) {
// Do nothing
}
public void variableChangedEvent(Variable variable, int index, ChangeType type) {
if (!isLocked) {
Parameter changedParameter = (Parameter) variable;
if (type == ChangeType.ALL_VALUES_CHANGED) {
double[] newValues = changedParameter.getParameterValues();
for (Parameter p : uniqueParameters) {
if (p != changedParameter) {
for (int i = 1; i < newValues.length; ++i) {
isLocked = true;
p.setParameterValueQuietly(i, newValues[i]);
isLocked = false;
}
isLocked = true;
p.setParameterValueNotifyChangedAll(0, newValues[0]);
isLocked = false;
}
}
} else if (type == ChangeType.VALUE_CHANGED) {
double newValue = changedParameter.getParameterValue(index);
for (Parameter p : uniqueParameters) {
if (p != changedParameter) {
isLocked = true;
p.setParameterValue(index, newValue);
isLocked = false;
}
}
} else {
throw new UnsupportedOperationException();
}
}
}
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
int dim = -1;
double[] firstValues = null;
List<Parameter> parameterList = new ArrayList<Parameter>();
for (int i = 0; i < xo.getChildCount(); ++i) {
Parameter param = (Parameter) xo.getChild(i);
if (i == 0) {
dim = param.getDimension();
firstValues = param.getParameterValues();
} else {
if (param.getDimension() != dim) {
throw new XMLParseException("All parameters must have the same dimension.");
}
for (int j = 0; j < dim; ++j) {
param.setParameterValue(j, firstValues[j]);
}
}
parameterList.add(param);
}
return new EqualityConstrainedParameter(xo.getId(), parameterList);
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new ElementRule(Parameter.class, 2, Integer.MAX_VALUE),
};
public String getParserDescription() {
return "Forces a set of parameters to have equal values";
}
public Class getReturnType() {
return EqualityConstrainedParameter.class;
}
public String getParserName() {
return EQUALITY_CONSTRAINED_PARAMETER;
}
};
public List<Citation> getCitations() {
List<Citation> list = new ArrayList<Citation>();
list.add(
new Citation(
new Author[]{
new Author("MA", "Suchard"),
},
Citation.Status.IN_PREPARATION
)
);
return list;
}
private final List<Parameter> uniqueParameters;
private final Bounds bounds;
private int dimension;
private boolean isLocked = false;
}
