package dr.evoxml;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.GeneralDataType;
import dr.util.Identifiable;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public class CompositeDataTypeParser extends AbstractXMLObjectParser {
public static final String COMPOSITE_DATA_TYPE = "compositeDataType";
public static final String COMPOSITE_STATE_SEPARATOR = "-";
public String getParserName() { return COMPOSITE_DATA_TYPE; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
List<String> states = new ArrayList<String>();
List<DataType> dataTypes = new ArrayList<DataType>();
for (int i =0; i < xo.getChildCount(); i++) {
if (xo.getChild(i) instanceof DataType)  {
dataTypes.add((DataType)xo.getChild(i));
} else {
throw new XMLParseException("illegal element in " + getParserName() + " element");
}
}
if (dataTypes.size() != 2) {
throw new XMLParseException("CompositeDataType requires exactly 2 component DataTypes");
}
DataType dt1 = dataTypes.get(0);
DataType dt2 = dataTypes.get(1);
List<String> jointStates = new ArrayList<String>();
for (int state1 = 0; state1 < dt1.getStateCount(); state1++) {
String code1  = dt1.getCode(state1);
for (int state2 = 0; state2 < dt2.getStateCount(); state2++) {
String code2  = dt2.getCode(state2);
jointStates.add(code1 + COMPOSITE_STATE_SEPARATOR + code2);
}
}
if (states.size() == 0) {
throw new XMLParseException("No state elements defined in " + getParserName() + " element");
} else if (states.size() < 2 ) {
throw new XMLParseException("Fewer than two state elements defined in " + getParserName() + " element");
}
GeneralDataType dataType = new GeneralDataType(states);
return dataType;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "Defines a composite DataType consisting of multiple data types";
}
public Class getReturnType() { return DataType.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new ElementRule(DataType.class, 2, 2),
};
}
