
package dr.evoxml;

import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.HypermutantAlignment;
import dr.evolution.datatype.*;
import dr.xml.*;

import java.util.logging.Logger;

public class HypermutantAlignmentParser extends AbstractXMLObjectParser {
    public final static String HYPERMUTANT_ALIGNMENT = "hypermutantAlignment";
    public final static String CONTEXT_TYPE = "type";

    public String getParserName() { return HYPERMUTANT_ALIGNMENT; }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        Alignment alignment = (Alignment)xo.getChild(Alignment.class);

        if (alignment.getDataType().getType() != DataType.NUCLEOTIDES) {
            throw new XMLParseException("HypermutantAlignment can only convert nucleotide alignments");
        }

        String typeName = xo.getStringAttribute(CONTEXT_TYPE);
        HypermutantAlignment.APOBECType type = null;
        try {
            type = HypermutantAlignment.APOBECType.valueOf(typeName.toUpperCase());
        } catch(IllegalArgumentException iae) {
            throw new XMLParseException("Unrecognised hypermutation type: " + typeName);
        }

        HypermutantAlignment convert = new HypermutantAlignment(type, alignment);
        int mutatedCount = convert.getMutatedContextCount();
        int totalCount = mutatedCount + convert.getUnmutatedContextCount();

        Logger.getLogger("dr.evoxml").info("Converted alignment, '" + xo.getId() + "' to a hypermutant alignment targeting " + type.toString() + " contexts.\r" +
                "\tPotentially mutated contexts: " + mutatedCount + " out of a total of " + totalCount + " contexts");

        return convert;
    }

    public String getParserDescription() {
        return "Converts an alignment so that 'A's at specific APOBEC targeted contexts are set to an A/G ambiguity code.";
    }

    public Class getReturnType() { return Alignment.class; }

    public XMLSyntaxRule[] getSyntaxRules() { return rules; }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
            new ElementRule(Alignment.class),
            new StringAttributeRule(CONTEXT_TYPE,
                    "The type of APOBEC molecule being modelled",
                    new String[] {HypermutantAlignment.APOBECType.ALL.toString(), HypermutantAlignment.APOBECType.BOTH.toString(), HypermutantAlignment.APOBECType.HA3G.toString(), HypermutantAlignment.APOBECType.HA3F.toString()},
                    false )
    };
}