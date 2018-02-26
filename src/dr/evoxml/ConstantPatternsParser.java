package dr.evoxml;
import dr.evolution.alignment.*;
import dr.evolution.datatype.DataType;
import dr.evolution.util.TaxonList;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class ConstantPatternsParser extends AbstractXMLObjectParser {
    public static final String CONSTANT_PATTERNS = "constantPatterns";
    private static final String COUNTS = "counts";
    public String getParserName() {
        return CONSTANT_PATTERNS;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        PatternList source = (PatternList)xo.getChild(PatternList.class);
        Parameter constantPatternCounts = (Parameter) xo.getElementFirstChild(COUNTS);
        Patterns patterns = new Patterns(source.getDataType(), source);
        for (int i = 0; i < source.getDataType().getStateCount(); i++) {
            int[] pattern = new int[patterns.getPatternLength()];
            for (int j = 0; j < pattern.length; j++) {
                pattern[j] = i;
            }
            patterns.addPattern(pattern, constantPatternCounts.getParameterValue(i));
        }
        return patterns;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            new ElementRule(PatternList.class),
            new ElementRule(COUNTS,
                    new XMLSyntaxRule[]{
                            new ElementRule(Parameter.class)
                    })
    };
    public String getParserDescription() {
        return "Creates a set of patterns for constant sites with weights as provided.";
    }
    public Class getReturnType() {
        return PatternList.class;
    }
}
