package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.evolution.alignment.PatternList;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.HiddenDataType;
import dr.evoxml.util.DataTypeUtils;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.text.NumberFormat;
import java.util.logging.Logger;
public class FrequencyModelParser extends AbstractXMLObjectParser {
public static final String FREQUENCIES = "frequencies";
public static final String FREQUENCY_MODEL = "frequencyModel";
public static final String NORMALIZE = "normalize";
public static final String COMPRESS = "compress";
public String getParserName() {
return FREQUENCY_MODEL;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
DataType dataType = DataTypeUtils.getDataType(xo);
Parameter freqsParam = (Parameter) xo.getElementFirstChild(FREQUENCIES);
double[] frequencies = null;
for (int i = 0; i < xo.getChildCount(); i++) {
Object obj = xo.getChild(i);
if (obj instanceof PatternList) {
PatternList patternList = (PatternList) obj;
if (xo.getAttribute(COMPRESS, false) && (patternList.getDataType() instanceof HiddenDataType)) {
double[] hiddenFrequencies = patternList.getStateFrequencies();
int hiddenCount = ((HiddenDataType) patternList.getDataType()).getHiddenClassCount();
int baseStateCount = hiddenFrequencies.length / hiddenCount;
frequencies = new double[baseStateCount];
for (int j = 0; j < baseStateCount; ++j) {
for (int k = 0; k < hiddenCount; ++k) {
frequencies[j] += hiddenFrequencies[j + k * baseStateCount];
}
}
} else {
frequencies = patternList.getStateFrequencies();
}
break;
}
}
StringBuilder sb = new StringBuilder("Creating state frequencies model '" + freqsParam.getParameterName() + "': ");
if (frequencies != null) {
if (freqsParam.getDimension() != frequencies.length) {
throw new XMLParseException("dimension of frequency parameter and number of sequence states don't match.");
}
for (int j = 0; j < frequencies.length; j++) {
freqsParam.setParameterValue(j, frequencies[j]);
}
sb.append("Using empirical frequencies from data ");
} else {
sb.append("Initial frequencies ");
}
sb.append("= {");
if (xo.getAttribute(NORMALIZE, false)) {
double sum = 0;
for (int j = 0; j < freqsParam.getDimension(); j++)
sum += freqsParam.getParameterValue(j);
for (int j = 0; j < freqsParam.getDimension(); j++) {
if (sum != 0)
freqsParam.setParameterValue(j, freqsParam.getParameterValue(j) / sum);
else
freqsParam.setParameterValue(j, 1.0 / freqsParam.getDimension());
}
}
NumberFormat format = NumberFormat.getNumberInstance();
format.setMaximumFractionDigits(5);
sb.append(format.format(freqsParam.getParameterValue(0)));
for (int j = 1; j < freqsParam.getDimension(); j++) {
sb.append(", ");
sb.append(format.format(freqsParam.getParameterValue(j)));
}
sb.append("}");
Logger.getLogger("dr.evomodel").info(sb.toString());
return new FrequencyModel(dataType, freqsParam);
}
public String getParserDescription() {
return "A model of equilibrium base frequencies.";
}
public Class getReturnType() {
return FrequencyModel.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newBooleanRule(NORMALIZE, true),
AttributeRule.newBooleanRule(COMPRESS, true),
new ElementRule(PatternList.class, "Initial value", 0, 1),
new XORRule(
new StringAttributeRule(DataType.DATA_TYPE, "The type of sequence data",
DataType.getRegisteredDataTypeNames(), false),
new ElementRule(DataType.class)
),
new ElementRule(FREQUENCIES,
new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
};
}