package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.TN93;
import dr.evomodel.substmodel.NucModelType;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class TN93Parser extends AbstractXMLObjectParser {
    public static final String TN93_MODEL = NucModelType.TN93.getXMLName();
    public static final String KAPPA1 = "kappa1";
    public static final String KAPPA2 = "kappa2";
    public static final String FREQUENCIES = "frequencies";
    public String getParserName() {
        return TN93_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Parameter kappa1Param = (Parameter) xo.getElementFirstChild(KAPPA1);
        Parameter kappa2Param = (Parameter) xo.getElementFirstChild(KAPPA2);
        FrequencyModel freqModel = (FrequencyModel) xo.getElementFirstChild(FREQUENCIES);
        Logger.getLogger("dr.evomodel").info("Creating TN93 substitution model. Initial kappa = "
                + kappa1Param.getValue(0) + "," + kappa2Param.getValue(0));
        return new TN93(kappa1Param, kappa2Param, freqModel);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return "This element represents an instance of the TN93 (Tamura and Nei 1993) model of nucleotide evolution.";
    }
    public Class getReturnType() {
        return TN93.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules;{
        rules = new XMLSyntaxRule[]{
                new ElementRule(FREQUENCIES,
                        new XMLSyntaxRule[]{new ElementRule(FrequencyModel.class)}),
                new ElementRule(KAPPA1,
                        new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
                new ElementRule(KAPPA2,
                        new XMLSyntaxRule[]{new ElementRule(Parameter.class)})
        };
    }
}
