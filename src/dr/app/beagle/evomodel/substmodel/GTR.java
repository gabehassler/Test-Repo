package dr.app.beagle.evomodel.substmodel;
import dr.evolution.datatype.Nucleotides;
import dr.inference.model.Parameter;
import dr.inference.model.Variable;
public class GTR extends BaseSubstitutionModel {
private Variable<Double> rateACVariable = null;
private Variable<Double> rateAGVariable = null;
private Variable<Double> rateATVariable = null;
private Variable<Double> rateCGVariable = null;
private Variable<Double> rateCTVariable = null;
private Variable<Double> rateGTVariable = null;
public GTR(
Variable<Double> rateACVariable,
Variable<Double> rateAGVariable,
Variable<Double> rateATVariable,
Variable<Double> rateCGVariable,
Variable<Double> rateCTVariable,
Variable<Double> rateGTVariable,
FrequencyModel freqModel) {
super("GTR", Nucleotides.INSTANCE, freqModel);
if (rateACVariable != null) {
addVariable(rateACVariable);
rateACVariable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateACVariable = rateACVariable;
}
if (rateAGVariable != null) {
addVariable(rateAGVariable);
rateAGVariable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateAGVariable = rateAGVariable;
}
if (rateATVariable != null) {
addVariable(rateATVariable);
rateATVariable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateATVariable = rateATVariable;
}
if (rateCGVariable != null) {
addVariable(rateCGVariable);
rateCGVariable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateCGVariable = rateCGVariable;
}
if (rateCTVariable != null) {
addVariable(rateCTVariable);
rateCTVariable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateCTVariable = rateCTVariable;
}
if (rateGTVariable != null) {
addVariable(rateGTVariable);
rateGTVariable.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
this.rateGTVariable = rateGTVariable;
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
protected void frequenciesChanged() {
// nothing to do...
}
protected void ratesChanged() {
// nothing to do...
}
protected void setupRelativeRates(double[] rates) {
if (rateACVariable != null) {
rates[0] = rateACVariable.getValue(0);
}
if (rateAGVariable != null) {
rates[1] = rateAGVariable.getValue(0);
}
if (rateATVariable != null) {
rates[2] = rateATVariable.getValue(0);
}
if (rateCGVariable != null) {
rates[3] = rateCGVariable.getValue(0);
}
if (rateCTVariable != null) {
rates[4] = rateCTVariable.getValue(0);
}
if (rateGTVariable != null) {
rates[5] = rateGTVariable.getValue(0);
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