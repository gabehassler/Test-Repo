package dr.evoxml;
import dr.evolution.alignment.PatternList;
import dr.evolution.alignment.Patterns;
import dr.evolution.alignment.SiteList;
import dr.evolution.alignment.SitePatterns;
import dr.xml.*;
import java.util.logging.Logger;
public class MaskedPatternsParser extends AbstractXMLObjectParser {
public static final String MASKED_PATTERNS = "maskedPatterns";
public static final String MASK = "mask";
public static final String NEGATIVE = "negative";
public String getParserName() { return MASKED_PATTERNS; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
SiteList siteList = (SiteList)xo.getChild(SiteList.class);
boolean negativeMask = xo.getBooleanAttribute(NEGATIVE);
String maskString = (String)xo.getElementFirstChild(MASK);
boolean[] mask = new boolean[siteList.getSiteCount()];
int k = 0;
for (char c : maskString.toCharArray()) {
if (Character.isDigit(c)) {
if (k >= mask.length) {
break;
}
mask[k] = (c == '0' ? negativeMask : !negativeMask);
k++;
}
}
if (k != mask.length) {
throw new XMLParseException("The mask needs to be the same length as the alignment (spaces are ignored)");
}
SitePatterns patterns = new SitePatterns(siteList, mask, false, false);
if (patterns == null) {
throw new XMLParseException("The mask needs include at least one pattern");
}
if (xo.hasAttribute(XMLParser.ID)) {
final Logger logger = Logger.getLogger("dr.evoxml");
logger.info("Site patterns '" + xo.getId() + "' created by masking alignment with id '" + siteList.getId() + "'");
logger.info("  pattern count = " + patterns.getPatternCount());
}
return patterns;
}
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private final XMLSyntaxRule[] rules = {
AttributeRule.newBooleanRule(NEGATIVE, true),
new ElementRule(SiteList.class),
new ElementRule(MASK, String.class, "A parameter of 1s and 0s that represent included and excluded sites")
};
public String getParserDescription() {
return "A weighted list of the unique site patterns (unique columns) in an alignment.";
}
public Class getReturnType() { return PatternList.class; }
}
