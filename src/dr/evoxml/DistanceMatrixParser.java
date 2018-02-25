package dr.evoxml;
import dr.evolution.alignment.PatternList;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.distance.DistanceMatrix;
import dr.evolution.distance.F84DistanceMatrix;
import dr.evolution.distance.JukesCantorDistanceMatrix;
import dr.evolution.distance.SMMDistanceMatrix;
import dr.xml.*;
import java.util.logging.Logger;
public class DistanceMatrixParser extends AbstractXMLObjectParser {
public static final String DISTANCE_MATRIX = "distanceMatrix";
public static final String CORRECTION = "correction";
public String getParserName() { return DISTANCE_MATRIX; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
PatternList patterns = (PatternList)xo.getChild(PatternList.class);
DistanceMatrix matrix = null;
String type = xo.getStringAttribute(CORRECTION);
if (type.equals(Nucleotides.JC)) {
Logger.getLogger("dr.evoxml").info("Creating Jukes-Cantor distance matrix");
matrix = new JukesCantorDistanceMatrix(patterns);
} else if (type.equals(Nucleotides.F84)) {
Logger.getLogger("dr.evoxml").info("Creating F84 distance matrix");
matrix = new F84DistanceMatrix(patterns);
} else if (type.equals("SMM")){
Logger.getLogger("dr.evoxml").info("Creating SMM distance matrix");
matrix = new SMMDistanceMatrix(patterns);
} else {
matrix = new DistanceMatrix(patterns);
}
return matrix;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
new StringAttributeRule(CORRECTION,
"The type of distance correction used",
new String[] { "none", Nucleotides.JC, Nucleotides.F84, "SMM" }, false),
new ElementRule(PatternList.class)
};
public String getParserDescription() {
return "Constructs a distance matrix from a pattern list or alignment";
}
public Class getReturnType() { return DistanceMatrix.class; }
}
