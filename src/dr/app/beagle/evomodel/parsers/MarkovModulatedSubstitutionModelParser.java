package dr.app.beagle.evomodel.parsers;
import dr.app.beagle.evomodel.sitemodel.SiteRateModel;
import dr.app.beagle.evomodel.substmodel.MarkovModulatedSubstitutionModel;
import dr.app.beagle.evomodel.substmodel.SubstitutionModel;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.NewHiddenNucleotides;
import dr.evoxml.util.DataTypeUtils;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public class MarkovModulatedSubstitutionModelParser extends AbstractXMLObjectParser {
    public static final String MARKOV_MODULATED_MODEL = "markovModulatedSubstitutionModel";
    //    public static final String HIDDEN_COUNT = "hiddenCount";
    public static final String SWITCHING_RATES = "switchingRates";
    //    public static final String DIAGONALIZATION = "diagonalization";
    public static final String RATE_SCALAR = "rateScalar";
    public static final String GEOMETRIC_RATES = "geometricRates";
    public String getParserName() {
        return MARKOV_MODULATED_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        DataType dataType = DataTypeUtils.getDataType(xo);
        System.err.println("dataType = " + dataType);
        NewHiddenNucleotides nucleotides;
        if (dataType instanceof NewHiddenNucleotides) {
            nucleotides = (NewHiddenNucleotides) dataType;
        } else {
            throw new XMLParseException("Must construct " + MARKOV_MODULATED_MODEL + " with hidden nucleotides");
        }
//
//        Parameter omegaParam = (Parameter) xo.getElementFirstChild(OMEGA);
//        Parameter kappaParam = (Parameter) xo.getElementFirstChild(KAPPA);
//        Parameter switchingParam = (Parameter) xo.getElementFirstChild(SWITCHING_RATES);
//        FrequencyModel freqModel = (FrequencyModel) xo.getChild(FrequencyModel.class);
//
//        EigenSystem eigenSystem;
//        if (xo.getAttribute(DIAGONALIZATION,"default").compareToIgnoreCase("colt") == 0)
//            eigenSystem = new ColtEigenSystem();
//        else
//            eigenSystem = new DefaultEigenSystem(dataType.getStateCount());
        Parameter switchingRates = (Parameter) xo.getElementFirstChild(SWITCHING_RATES);
        List<SubstitutionModel> substModels = new ArrayList<SubstitutionModel>();
        for (int i = 0; i < xo.getChildCount(); i++) {
            Object cxo = xo.getChild(i);
            if (cxo instanceof SubstitutionModel) {
                substModels.add((SubstitutionModel) cxo);
            }
        }
        boolean geometricRates = xo.getAttribute(GEOMETRIC_RATES, false);
        Parameter rateScalar = xo.hasChildNamed(RATE_SCALAR) ?
                (Parameter) xo.getChild(RATE_SCALAR).getChild(Parameter.class) : null;
        SiteRateModel siteRateModel = (SiteRateModel) xo.getChild(SiteRateModel.class);
        if (siteRateModel != null) {
            if (siteRateModel.getCategoryCount() != substModels.size()) {
                throw new XMLParseException(
                        "Number of gamma categories must equal number of substitution models in " + xo.getId());
            }
        }
        return new MarkovModulatedSubstitutionModel(xo.getId(), substModels, switchingRates, dataType, null,
                rateScalar, geometricRates, siteRateModel);
    }
    public String getParserDescription() {
        return "This element represents the a Markov-modulated substitution model.";
    }
    public Class getReturnType() {
        return MarkovModulatedSubstitutionModel.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            AttributeRule.newStringRule(DataType.DATA_TYPE),
//            AttributeRule.newStringRule(GeneticCode.GENETIC_CODE),
//            new ElementRule(OMEGA,
//                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
//            new ElementRule(KAPPA,
//                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(SWITCHING_RATES,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
//            new ElementRule(FrequencyModel.class),
//            AttributeRule.newStringRule(DIAGONALIZATION),
            new ElementRule(SubstitutionModel.class, 1, Integer.MAX_VALUE),
            AttributeRule.newBooleanRule(GEOMETRIC_RATES, true),
            new ElementRule(RATE_SCALAR,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}, true),
            new ElementRule(SiteRateModel.class, true),
    };
}
