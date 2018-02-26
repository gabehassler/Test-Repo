package dr.app.beagle.evomodel.parsers;
import dr.inference.model.Parameter;
import dr.xml.*;
import dr.app.beagle.evomodel.substmodel.FrequencyModel;
import dr.app.beagle.evomodel.substmodel.GY94CodonModel;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.GeneticCode;
public class GY94CodonModelParser extends AbstractXMLObjectParser {
    public static final String YANG_CODON_MODEL = "yangCodonModel";
    public static final String OMEGA = "omega";
    public static final String KAPPA = "kappa";
    public String getParserName() { return YANG_CODON_MODEL; }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Codons codons = Codons.UNIVERSAL;
        if (xo.hasAttribute(GeneticCode.GENETIC_CODE)) {
            String codeStr = xo.getStringAttribute(GeneticCode.GENETIC_CODE);
            codons = Codons.findByName(codeStr);
        }
        Parameter omegaParameter = (Parameter)xo.getElementFirstChild(OMEGA);
        int dim = omegaParameter.getDimension();
        double value = omegaParameter.getParameterValue(dim - 1); 
        if(value < 0) {
        	throw new RuntimeException("Negative Omega parameter value " + value);
        }//END: negative check
        Parameter kappaParameter = (Parameter)xo.getElementFirstChild(KAPPA);
        dim = kappaParameter.getDimension();
        value = kappaParameter.getParameterValue(dim - 1);
        if(value < 0) {
        	throw new RuntimeException("Negative kappa parameter value value " + value);
        }//END: negative check
        FrequencyModel freqModel = (FrequencyModel)xo.getChild(FrequencyModel.class);
        GY94CodonModel codonModel = new GY94CodonModel(codons, omegaParameter, kappaParameter, freqModel);
//            codonModel.printRateMap();
        return codonModel;
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return "This element represents the Yang model of codon evolution.";
    }
    public Class getReturnType() { return GY94CodonModel.class; }
    public XMLSyntaxRule[] getSyntaxRules() { return rules; }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
            new StringAttributeRule(GeneticCode.GENETIC_CODE,
                    "The genetic code to use",
                    GeneticCode.GENETIC_CODE_NAMES, true),
            new ElementRule(OMEGA,
                    new XMLSyntaxRule[] { new ElementRule(Parameter.class) }),
            new ElementRule(KAPPA,
                    new XMLSyntaxRule[] { new ElementRule(Parameter.class) }),
            new ElementRule(FrequencyModel.class)
    };
}