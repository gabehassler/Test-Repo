package dr.evoxml;
import dr.evolution.datatype.*;
import dr.evolution.sequence.Sequence;
import dr.evolution.util.Taxon;
import dr.xml.*;
import java.util.StringTokenizer;
public class SequenceParser extends AbstractXMLObjectParser {
public static final String SEQUENCE = "sequence";
public String getParserName() { return SEQUENCE; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Sequence sequence = new Sequence();
Taxon taxon = (Taxon)xo.getChild(Taxon.class);
DataType dataType = null;
if (xo.hasAttribute(DataType.DATA_TYPE)) {
String dataTypeStr = xo.getStringAttribute(DataType.DATA_TYPE);
if (dataTypeStr.equals(Nucleotides.DESCRIPTION)) {
dataType = Nucleotides.INSTANCE;
} else if (dataTypeStr.equals(AminoAcids.DESCRIPTION)) {
dataType = AminoAcids.INSTANCE;
} else if (dataTypeStr.equals(Codons.DESCRIPTION)) {
dataType = Codons.UNIVERSAL;
} else if (dataTypeStr.equals(TwoStates.DESCRIPTION)) {
dataType = TwoStates.INSTANCE;
}
}
StringBuffer seqBuf = new StringBuffer();
for (int i = 0; i < xo.getChildCount(); i++) {
Object child = xo.getChild(i);
if (child instanceof String) {
StringTokenizer st = new StringTokenizer((String)child);
while (st.hasMoreTokens()) {
seqBuf.append(st.nextToken());
}
}
}
// We really need to filter the input string to check for illegal characters.
// Perhaps sequence.setSequenceString could throw an exception if any characters
// don't fit the dataType.
String sequenceString = seqBuf.toString();
if (sequenceString.length() == 0) {
throw new XMLParseException("Sequence data missing from sequence element!");
}
if (dataType != null) {
sequence.setDataType(dataType);
}
sequence.setSequenceString(sequenceString);
sequence.setTaxon(taxon);
return sequence;
}
public String getParserDescription() {
return "A biomolecular sequence.";
}
public Class getReturnType() { return Sequence.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new ElementRule(Taxon.class),
new ElementRule(String.class, "A character string representing the aligned molecular sequence", "ACGACTAGCATCGAGCTTCG--GATAGCATGC")
};
}
