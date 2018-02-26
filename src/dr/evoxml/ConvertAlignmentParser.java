
package dr.evoxml;

import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.ConvertAlignment;
import dr.evolution.datatype.*;
import dr.evoxml.util.DataTypeUtils;
import dr.xml.*;

import java.util.logging.Logger;

public class ConvertAlignmentParser extends AbstractXMLObjectParser {

    public final static String CONVERT = "convert";

    public String getParserName() { return CONVERT; }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        Alignment alignment = (Alignment)xo.getChild(Alignment.class);

	    // Old parser always returned UNIVERSAL type for codon conversion
	    DataType dataType = DataTypeUtils.getDataType(xo);

	    GeneticCode geneticCode = GeneticCode.UNIVERSAL;
	    if (dataType instanceof Codons) {
		    geneticCode = ((Codons)dataType).getGeneticCode();
	    }

        ConvertAlignment convert = new ConvertAlignment(dataType, geneticCode, alignment);
	    Logger.getLogger("dr.evoxml").info("Converted alignment, '" + xo.getId() + "', from " +
	            alignment.getDataType().getDescription() + " to " + dataType.getDescription());


        return convert;
    }

    public String getParserDescription() {
        return "Converts an alignment to the given data type.";
    }

    public Class getReturnType() { return Alignment.class; }

    public XMLSyntaxRule[] getSyntaxRules() { return rules; }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
        new ElementRule(Alignment.class),
        new StringAttributeRule(DataType.DATA_TYPE,
            "The type of sequence data",
            new String[] {Nucleotides.DESCRIPTION, AminoAcids.DESCRIPTION, Codons.DESCRIPTION, TwoStates.DESCRIPTION,
		            HiddenCodons.DESCRIPTION+"2",HiddenCodons.DESCRIPTION+"3"},
            false )
    };
}
