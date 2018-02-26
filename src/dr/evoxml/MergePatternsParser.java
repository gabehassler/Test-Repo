
package dr.evoxml;

import dr.evolution.alignment.*;
import dr.xml.*;

import java.util.logging.Logger;

public class MergePatternsParser extends AbstractXMLObjectParser {

    public static final String MERGE_PATTERNS = "mergePatterns";

    public String getParserName() { return MERGE_PATTERNS; }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

	    PatternList patternList = (PatternList)xo.getChild(0);
	    Patterns patterns = new Patterns(patternList);
	    for (int i = 1; i < xo.getChildCount(); i++) {
		    patterns.addPatterns((PatternList)xo.getChild(i));
	    }

        if (xo.hasAttribute(XMLParser.ID)) {
            final Logger logger = Logger.getLogger("dr.evoxml");
            logger.info("Site patterns '" + xo.getId() + "' created by merging " + xo.getChildCount() + " pattern lists");
            logger.info("  pattern count = " + patterns.getPatternCount());
        }

        return patterns;
    }

    public XMLSyntaxRule[] getSyntaxRules() { return rules; }

    private final XMLSyntaxRule[] rules = {
        new ElementRule(PatternList.class, 1, Integer.MAX_VALUE)
    };

    public String getParserDescription() {
        return "A weighted list of the unique site patterns (unique columns) in an alignment.";
    }

    public Class getReturnType() { return PatternList.class; }

}
