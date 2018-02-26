package dr.evoxml;
import dr.evolution.alignment.PatternList;
import dr.evolution.alignment.Patterns;
import dr.evolution.datatype.DataType;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import dr.evoxml.util.DataTypeUtils;
import dr.xml.*;
import java.util.logging.Logger;
public class AttributePatternsParser extends AbstractXMLObjectParser {
    public static final String ATTRIBUTE = "attribute";
    public static final String SECONDARY_ATTRIBUTE = "secondary";
    public static final String PATTERNS = "Patterns";
    public static final String ATTRIBUTE_PATTERNS = ATTRIBUTE + PATTERNS;
    public String getParserName() { return ATTRIBUTE_PATTERNS; }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        String attributeName = xo.getStringAttribute(ATTRIBUTE);
        String secondaryAttributeName = xo.getAttribute(SECONDARY_ATTRIBUTE, (String)null);
        TaxonList taxa = (TaxonList)xo.getChild(TaxonList.class);
        DataType dataType = DataTypeUtils.getDataType(xo);
        if (dataType == null) {
            throw new XMLParseException("dataType expected for attributePatterns element");
        }
        Patterns patterns = new Patterns(dataType, taxa);
        int[] pattern = new int[taxa.getTaxonCount()];
        boolean attributeFound = false;
        for (int i = 0; i < taxa.getTaxonCount(); i++) {
            Taxon taxon = taxa.getTaxon(i);
            if (secondaryAttributeName == null || secondaryAttributeName.isEmpty()) {
                Object value = taxon.getAttribute(attributeName);
                if (value != null) {
                    int state = dataType.getState(value.toString());
                    if (state < 0) {
                        throw new XMLParseException("State for attribute, " + attributeName + ", in taxon, " + taxon.getId() + ", is unknown: " + value.toString());
                    }
                    pattern[i] = state;
                    attributeFound = true;
                } else {
                    pattern[i] = dataType.getUnknownState();
                }
            } else {
                Object value1 = taxon.getAttribute(attributeName);
                Object value2 = taxon.getAttribute(secondaryAttributeName);
                if (value1 != null && value2 != null) {
                    String code = value1.toString() + CompositeDataTypeParser.COMPOSITE_STATE_SEPARATOR + value2.toString();
                    int state = dataType.getState(code);
                    if (state < 0) {
                        throw new XMLParseException("State for attributes, " + attributeName + " & " + secondaryAttributeName + ", in taxon, " + taxon.getId() + ", is unknown: " + code);
                    }
                    pattern[i] = state;
                    attributeFound = true;
                } else {
                    pattern[i] = dataType.getUnknownState();
                }
            }
        }
        if (!attributeFound) {
            throw new XMLParseException("The attribute, " + attributeName + " was missing in all taxa. Check the name of the attribute.");
        }
        patterns.addPattern(pattern);
        if (xo.hasAttribute(XMLParser.ID)) {
		    Logger.getLogger("dr.evoxml").info("Read attribute patterns, '" + xo.getId() + "' for attribute, "+ attributeName);
	    } else {
            Logger.getLogger("dr.evoxml").info("Read attribute patterns for attribute, "+ attributeName);
	    }
        return patterns;
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public XMLSyntaxRule[] getSyntaxRules() { return rules; }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
            new XORRule(
                new StringAttributeRule(
                    DataType.DATA_TYPE,
                    "The data type",
                    DataType.getRegisteredDataTypeNames(), false),
                new ElementRule(DataType.class)
                ),
            AttributeRule.newStringRule(ATTRIBUTE),
            AttributeRule.newStringRule(SECONDARY_ATTRIBUTE, true),
            new ElementRule(TaxonList.class, "The taxon set")
    };
    public String getParserDescription() {
        return "A site pattern defined by an attribute in a set of taxa.";
    }
    public Class getReturnType() { return PatternList.class; }
}