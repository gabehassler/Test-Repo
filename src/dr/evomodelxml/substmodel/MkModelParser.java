
package dr.evomodelxml.substmodel;

import dr.evolution.datatype.DataType;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.GeneralSubstitutionModel;
import dr.inference.model.Parameter;
import dr.xml.*;

import java.util.logging.Logger;

public class MkModelParser extends AbstractXMLObjectParser {

    public static final String MK_SUBSTITUTION_MODEL = "mkSubstitutionModel";

    public String getParserName() {
        return MK_SUBSTITUTION_MODEL;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        XMLObject cxo = xo.getChild(GeneralSubstitutionModelParser.FREQUENCIES);
        FrequencyModel freqModel = (FrequencyModel) cxo.getChild(FrequencyModel.class);

        DataType dataType = freqModel.getDataType();
        int rateCount = ((dataType.getStateCount() - 1) * dataType.getStateCount()) / 2 - 1;
        Parameter ratesParameter = new Parameter.Default(rateCount, 1.0);

        Logger.getLogger("dr.evolution").info("Creating an Mk substitution model with data type: " + dataType.getType() + "on " + dataType.getStateCount() + " states.");

        int relativeTo = 1;

        return new GeneralSubstitutionModel(dataType, freqModel, ratesParameter, relativeTo);
    }

    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************

    public String getParserDescription() {
        return "An Mk model of substitution. This model can also accomodate arbitrary orderings of changes.";
    }

    public Class getReturnType() {
        return GeneralSubstitutionModel.class;
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            new ElementRule(GeneralSubstitutionModelParser.FREQUENCIES, FrequencyModel.class),
    };

}