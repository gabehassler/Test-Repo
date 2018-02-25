package dr.evoxml;
import dr.evolution.alignment.PatternList;
import dr.evolution.alignment.Patterns;
import dr.evolution.alignment.SiteList;
import dr.xml.*;
import java.util.logging.Logger;
public class PatternSubSetParser extends AbstractXMLObjectParser {
public static final String PATTERNS_SUB_SET = "patternSubSet";
public static final String SUB_SET = "subSet";
public static final String SUB_SET_COUNT = "subSetCount";
public String getParserName() {
return PATTERNS_SUB_SET;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
SiteList patterns = (SiteList) xo.getChild(SiteList.class);
int subSet = 0;
int subSetCount = 0;
if (xo.hasAttribute(SUB_SET)) {
subSet = xo.getIntegerAttribute(SUB_SET) - 1;
if (subSet < 0)
throw new XMLParseException("illegal 'subSet' attribute in patterns element");
}
if (xo.hasAttribute(SUB_SET_COUNT)) {
subSetCount = xo.getIntegerAttribute(SUB_SET_COUNT);
if (subSetCount < 0)
throw new XMLParseException("illegal 'subSetCount' attribute in patterns element");
}
Patterns subPatterns = new Patterns(patterns, 0, 0, 1, subSet, subSetCount);
if (xo.hasAttribute(XMLParser.ID)) {
final Logger logger = Logger.getLogger("dr.evoxml");
logger.info("Pattern subset '" + xo.getId() + "' created from '" + patterns.getId() +"' ("+(subSet+1)+"/"+subSetCount+")");
logger.info("  pattern count = " + subPatterns.getPatternCount());
}
return subPatterns;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newIntegerRule(SUB_SET, true, "Which subset of patterns to use (out of subSetCount)"),
AttributeRule.newIntegerRule(SUB_SET_COUNT, true, "The number of subsets"),
new ElementRule(SiteList.class)
};
public String getParserDescription() {
return "A weighted list of the unique site patterns (unique columns) in an alignment.";
}
public Class getReturnType() {
return PatternList.class;
}
}
