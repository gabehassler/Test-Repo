package dr.evomodel.substmodel;
import dr.evomodelxml.substmodel.GTRParser;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class GTR extends AbstractNucleotideModel {
private Variable<Double> rateACValue = null;
private Variable<Double> rateAGValue = null;
private Variable<Double> rateATValue = null;
private Variable<Double> rateCGValue = null;
private Variable<Double> rateCTValue = null;
private Variable<Double> rateGTValue = null;
public GTR(
Variable rateACValue,
Variable rateAGValue,
Variable rateATValue,
Variable rateCGValue,
Variable rateCTValue,
Variable rateGTValue,
FrequencyModel freqModel) {
super(GTRParser.GTR_MODEL, freqModel);
if (rateACValue != null) {
addVariable(rateACValue);
rateACValue.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateACValue = rateACValue;
}
if (rateAGValue != null) {
addVariable(rateAGValue);
rateAGValue.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateAGValue = rateAGValue;
}
if (rateATValue != null) {
addVariable(rateATValue);
rateATValue.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateATValue = rateATValue;
}
if (rateCGValue != null) {
addVariable(rateCGValue);
rateCGValue.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateCGValue = rateCGValue;
}
if (rateCTValue != null) {
addVariable(rateCTValue);
rateCTValue.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateCTValue = rateCTValue;
}
if (rateGTValue != null) {
addVariable(rateGTValue);
rateGTValue.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateGTValue = rateGTValue;
}
}
public void setAbsoluteRates(double[] rates, int relativeTo) {
for (int i = 0; i < relativeRates.length; i++) {
relativeRates[i] = rates[i] / rates[relativeTo];
}
updateMatrix = true;
fireModelChanged();
}
public void setRelativeRates(double[] rates) {
System.arraycopy(rates, 0, relativeRates, 0, relativeRates.length);
updateMatrix = true;
fireModelChanged();
}
protected void setupRelativeRates() {
if (rateACValue != null) {
relativeRates[0] = rateACValue.getValue(0);
}
if (rateAGValue != null) {
relativeRates[1] = rateAGValue.getValue(0);
}
if (rateATValue != null) {
relativeRates[2] = rateATValue.getValue(0);
}
if (rateCGValue != null) {
relativeRates[3] = rateCGValue.getValue(0);
}
if (rateCTValue != null) {
relativeRates[4] = rateCTValue.getValue(0);
}
if (rateGTValue != null) {
relativeRates[5] = rateGTValue.getValue(0);
}
}
// **************************************************************
// XHTMLable IMPLEMENTATION
// **************************************************************
public String toXHTML() {
StringBuffer buffer = new StringBuffer();
buffer.append("<em>GTR Model</em> Instantaneous Rate Matrix = <table><tr><td></td><td>A</td><td>C</td><td>G</td><td>T</td></tr>");
buffer.append("<tr><td>A</td><td></td><td>");
buffer.append(relativeRates[0]);
buffer.append("</td><td>");
buffer.append(relativeRates[1]);
buffer.append("</td><td>");
buffer.append(relativeRates[2]);
buffer.append("</td></tr>");
buffer.append("<tr><td>C</td><td></td><td></td><td>");
buffer.append(relativeRates[3]);
buffer.append("</td><td>");
buffer.append(relativeRates[4]);
buffer.append("</td></tr>");
buffer.append("<tr><td>G</td><td></td><td></td><td></td><td>");
buffer.append(relativeRates[5]);
buffer.append("</td></tr>");
buffer.append("<tr><td>G</td><td></td><td></td><td></td><td></td></tr></table>");
return buffer.toString();
}
}
