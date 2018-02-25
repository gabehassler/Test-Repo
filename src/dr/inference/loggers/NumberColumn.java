package dr.inference.loggers;
import java.text.DecimalFormat;
public abstract class NumberColumn extends LogColumn.Abstract {
private int sf = -1;
private int dp = -1;
private double upperCutoff;
private double[] cutoffTable;
private final DecimalFormat decimalFormat = new DecimalFormat();
private DecimalFormat scientificFormat = null;
public NumberColumn(String label) {
super(label);
decimalFormat.setGroupingUsed(false); // not use comma
}
public NumberColumn(String label, int sf) {
this(label);
setSignificantFigures(sf);
}
public void setSignificantFigures(int sf) {
this.sf = sf;
this.dp = -1;
upperCutoff = Math.pow(10,sf-1);
cutoffTable = new double[sf];
long num = 10;
for (int i =0; i < cutoffTable.length; i++) {
cutoffTable[i] = (double)num;
num *= 10;
}
decimalFormat.setGroupingUsed(false);
decimalFormat.setMinimumIntegerDigits(1);
decimalFormat.setMaximumFractionDigits(sf-1);
decimalFormat.setMinimumFractionDigits(sf-1);
scientificFormat = new DecimalFormat(getPattern(sf));
}
public int getSignificantFigures() { return sf; }
public void setDecimalPlaces(int dp) {
this.dp = dp;
this.sf = -1;
}
public int getDecimalPlaces() { return dp; }
public String formatValue(double value) {
if (dp < 0 && sf < 0) {
// return it at full precision
return Double.toString(value);
}
int numFractionDigits = 0;
if (dp < 0) {
double absValue = Math.abs(value);
if ((absValue > upperCutoff) || (absValue < 0.1)) {
return scientificFormat.format(value);
} else {
numFractionDigits = getNumFractionDigits(value);
}
} else {
numFractionDigits = dp;
}
decimalFormat.setMaximumFractionDigits(numFractionDigits);
decimalFormat.setMinimumFractionDigits(numFractionDigits);
return decimalFormat.format(value);
}
protected String getFormattedValue() {
return formatValue(getDoubleValue());
}
private int getNumFractionDigits(double value) {
value = Math.abs(value);
for (int i = 0; i < cutoffTable.length; i++) {
if (value < cutoffTable[i]) return sf-i-1;
}
return sf - 1;
}
private String getPattern(int sf) {
String pattern = "0.";
for (int i =0; i < sf-1; i++) {
pattern += "#";
}
pattern += "E0";
return pattern;
}
public abstract double getDoubleValue();
}
