package dr.evoxml;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.GeneralDataType;
import dr.xml.*;
import dr.util.Attribute;
import dr.util.Identifiable;
import java.util.ArrayList;
import java.util.List;
public class GeneralDataTypeParser extends AbstractXMLObjectParser {
public static final String GENERAL_DATA_TYPE = "generalDataType";
public static final String STATE = "state";
public static final String STATES = "states";
public static final String ALIAS = "alias";
public static final String AMBIGUITY = "ambiguity";
public static final String CODE = "code";
public String getParserName() { return GENERAL_DATA_TYPE; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
List<String> states = new ArrayList<String>();
for (int i =0; i < xo.getChildCount(); i++) {
if (xo.getChild(i) instanceof XMLObject) {
XMLObject cxo = (XMLObject)xo.getChild(i);
if (cxo.getName().equals(STATE)) {
states.add(cxo.getStringAttribute(CODE));
} else if (cxo.getName().equals(ALIAS)) {
// Do nothing just now
} else if (cxo.getName().equals(AMBIGUITY)) {
// Do nothing just now
} else {
throw new XMLParseException("illegal element, " + cxo.getName() + ", in " + getParserName() + " element");
}
} else if (xo.getChild(i) instanceof Identifiable)  {
states.add(((Identifiable)xo.getChild(i)).getId());
} else {
throw new XMLParseException("illegal element in " + getParserName() + " element");
}
}
if (states.size() == 0) {
throw new XMLParseException("No state elements defined in " + getParserName() + " element");
} else if (states.size() < 2 ) {
throw new XMLParseException("Less than two state elements defined in " + getParserName() + " element");
}
GeneralDataType dataType = new GeneralDataType(states);
for (int i =0; i < xo.getChildCount(); i++) {
if (xo.getChild(i) instanceof XMLObject) {
XMLObject cxo = (XMLObject)xo.getChild(i);
if (cxo.getName().equals(ALIAS)) {
String alias = cxo.getStringAttribute(CODE);
//                    if (alias.length() != 1) {
//                        throw new XMLParseException("State alias codes in " + getParserName() + " element must be exactly one character");
//                    }
String state = cxo.getStringAttribute(STATE);
//                    if (state.length() != 1) {
//                        throw new XMLParseException("State codes in " + getParserName() + " element must be exactly one character");
//                    }
try {
dataType.addAlias(alias, state);
} catch (IllegalArgumentException iae) {
throw new XMLParseException(iae.getMessage() + "in " + getParserName() + " element");
}
} else if (cxo.getName().equals(AMBIGUITY)) {
String code = cxo.getStringAttribute(CODE);
//                    if (code.length() != 1) {
//                        throw new XMLParseException("State ambiguity codes in " + getParserName() + " element must be exactly one character");
//                    }
String[] ambiguities = cxo.getStringArrayAttribute(STATES);
if (ambiguities.length == 1) {
String codes = ambiguities[0];
if (codes.length() < 2) {
throw new XMLParseException("States for ambiguity code in " + getParserName() + " element are not ambiguous");
}
ambiguities = new String[codes.length()];
for (int j = 0; j < codes.length(); j++) {
ambiguities[j] = String.valueOf(codes.charAt(j));
}
}
try {
dataType.addAmbiguity(code, ambiguities);
} catch (IllegalArgumentException iae) {
throw new XMLParseException(iae.getMessage() + "in " + getParserName() + " element");
}
}
}
}
return dataType;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "Defines a general DataType for any number of states";
}
public String getExample() {
return "<!-- The XML for a nucleotide data type under this scheme would be -->\n"+
"<generalDataType id=\"nucleotides\">\n"+
"	<state code=\"A\"/>\n"+
"	<state code=\"C\"/>\n"+
"	<state code=\"G\"/>\n"+
"	<state code=\"T\"/>\n"+
"	<alias code=\"U\" state=\"T\"/>\n"+
"	<ambiguity code=\"R\" states=\"AG\"/>\n"+
"	<ambiguity code=\"Y\" states=\"CT\"/>\n"+
"	<ambiguity code=\"?\" states=\"ACGT\"/>\n"+
"	<ambiguity code=\"-\" states=\"ACGT\"/>\n"+
"</generalDataType>\n";
}
public Class getReturnType() { return DataType.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new ElementRule(Identifiable.class, 0, Integer.MAX_VALUE),
new ContentRule("<state code=\"X\"/>"),
new ContentRule("<alias code=\"Y\" state=\"X\"/>"),
new ContentRule("<ambiguity code=\"Z\" states=\"XY\"/>")
};
}
