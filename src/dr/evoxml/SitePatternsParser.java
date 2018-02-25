package dr.evoxml;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.PatternList;
import dr.evolution.alignment.SitePatterns;
import dr.evolution.util.TaxonList;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class SitePatternsParser extends AbstractXMLObjectParser {
public static final String PATTERNS = "patterns";
public static final String FROM = "from";
public static final String TO = "to";
public static final String EVERY = "every";
public static final String TAXON_LIST = "taxonList";
public static final String STRIP = "strip";
public static final String UNIQUE = "unique";
public static final String CONSTANT_PATTERNS = "constantPatterns";
public String getParserName() {
return PATTERNS;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Alignment alignment = (Alignment) xo.getChild(Alignment.class);
TaxonList taxa = null;
int from = 0;
int to = -1;
int every = xo.getAttribute(EVERY, 1);
boolean strip = xo.getAttribute(STRIP, true);
boolean unique = xo.getAttribute(UNIQUE, true);
if (xo.hasAttribute(FROM)) {
from = xo.getIntegerAttribute(FROM) - 1;
if (from < 0)
throw new XMLParseException("illegal 'from' attribute in patterns element");
}
if (xo.hasAttribute(TO)) {
to = xo.getIntegerAttribute(TO) - 1;
if (to < 0 || to < from)
throw new XMLParseException("illegal 'to' attribute in patterns element");
}
if (every <= 0) throw new XMLParseException("illegal 'every' attribute in patterns element");
if (xo.hasChildNamed(TAXON_LIST)) {
taxa = (TaxonList) xo.getElementFirstChild(TAXON_LIST);
}
int[] constantPatternCounts = null;
if (xo.hasChildNamed(CONSTANT_PATTERNS)) {
Parameter param = (Parameter) xo.getElementFirstChild(CONSTANT_PATTERNS);
if (param.getDimension() != alignment.getStateCount()) {
throw new XMLParseException("The " + CONSTANT_PATTERNS + " parameter length should be equal to the number of states");
}
constantPatternCounts = new int[param.getDimension()];
int i = 0;
for (double value : param.getParameterValues()) {
constantPatternCounts[i] = (int)value;
i++;
}
}
if (from > alignment.getSiteCount())
throw new XMLParseException("illegal 'from' attribute in patterns element");
if (to > alignment.getSiteCount())
throw new XMLParseException("illegal 'to' attribute in patterns element");
SitePatterns patterns = new SitePatterns(alignment, taxa, from, to, every, strip, unique, constantPatternCounts);
int f = from + 1;
int t = to + 1; // fixed a *display* error by adding + 1 for consistency with f = from + 1
if (to == -1) t = alignment.getSiteCount();
if (xo.hasAttribute(XMLParser.ID)) {
final Logger logger = Logger.getLogger("dr.evoxml");
logger.info("Site patterns '" + xo.getId() + "' created from positions " +
Integer.toString(f) + "-" + Integer.toString(t) +
" of alignment '" + alignment.getId() + "'");
if (every > 1) {
logger.info("  only using every " + every + " site");
}
logger.info("  " + (unique ? "unique ": "") + "pattern count = " + patterns.getPatternCount());
}
return patterns;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules = {
AttributeRule.newIntegerRule(FROM, true, "The site position to start at, default is 1 (the first position)"),
AttributeRule.newIntegerRule(TO, true, "The site position to finish at, must be greater than <b>" + FROM + "</b>, default is length of given alignment"),
AttributeRule.newIntegerRule(EVERY, true, "Determines how many sites are selected. A value of 3 will select every third site starting from <b>" + FROM + "</b>, default is 1 (every site)"),
new ElementRule(TAXON_LIST,
new XMLSyntaxRule[]{new ElementRule(TaxonList.class)}, true),
new ElementRule(CONSTANT_PATTERNS,
new XMLSyntaxRule[]{new ElementRule(Parameter.class)}, true),
new ElementRule(Alignment.class),
AttributeRule.newBooleanRule(STRIP, true, "Strip out completely ambiguous sites"),
AttributeRule.newBooleanRule(UNIQUE, true, "Return a weight list of unique patterns"),
};
public String getParserDescription() {
return "A weighted list of the unique site patterns (unique columns) in an alignment.";
}
public Class getReturnType() {
return PatternList.class;
}
}
