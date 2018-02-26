package dr.evomodelxml.speciation;
import dr.evolution.util.Units;
import dr.evomodel.speciation.BirthDeathGernhard08Model;
import dr.evoxml.util.XMLUnits;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class BirthDeathModelParser extends AbstractXMLObjectParser {
    public static final String BIRTH_DEATH_MODEL = "birthDeathModel";
    public static final String BIRTHDIFF_RATE = "birthMinusDeathRate";
    public static final String RELATIVE_DEATH_RATE = "relativeDeathRate";
    public static final String SAMPLE_PROB = "sampleProbability";
    public static final String TREE_TYPE = "type";
    public static final String CONDITIONAL_ON_ROOT = "conditionalOnRoot";
    public static final String BIRTH_DEATH = "birthDeath";
    public static final String MEAN_GROWTH_RATE_PARAM_NAME = BIRTH_DEATH + ".meanGrowthRate";
    public static final String RELATIVE_DEATH_RATE_PARAM_NAME = BIRTH_DEATH + "." + RELATIVE_DEATH_RATE;
    public String getParserName() {
        return BIRTH_DEATH_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        final Units.Type units = XMLUnits.Utils.getUnitsAttr(xo);
        final String s = xo.getAttribute(TREE_TYPE, BirthDeathGernhard08Model.TreeType.UNSCALED.toString());
        final BirthDeathGernhard08Model.TreeType treeType = BirthDeathGernhard08Model.TreeType.valueOf(s);
        final boolean conditonalOnRoot =  xo.getAttribute(CONDITIONAL_ON_ROOT, false);
        final Parameter birthParameter = (Parameter) xo.getElementFirstChild(BIRTHDIFF_RATE);
        final Parameter deathParameter = (Parameter) xo.getElementFirstChild(RELATIVE_DEATH_RATE);
        final Parameter sampleProbability = xo.hasChildNamed(SAMPLE_PROB) ?
                (Parameter) xo.getElementFirstChild(SAMPLE_PROB) : null;
        Logger.getLogger("dr.evomodel").info(xo.hasChildNamed(SAMPLE_PROB) ? getCitationRHO() : getCitation());
        final String modelName = xo.getId();
        return new BirthDeathGernhard08Model(modelName, birthParameter, deathParameter, sampleProbability,
                treeType, units, conditonalOnRoot);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public static String getCitationRHO() {
        return "Stadler, T; On incomplete sampling under birth-death models and connections to the sampling-based coalescent;\n" +
               "JOURNAL OF THEORETICAL BIOLOGY (2009) 261:58-66";
    }
    public static String getCitation() {
        return "Using birth-death model on tree: Gernhard T (2008) J Theor Biol, Volume 253, Issue 4, Pages 769-778 In press";
    }
    public String getParserDescription() {
        return "Gernhard (2008) model of speciation (equation at bottom of page 19 of draft).";
    }
    public Class getReturnType() {
        return BirthDeathGernhard08Model.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            AttributeRule.newStringRule(TREE_TYPE, true),
            AttributeRule.newBooleanRule(CONDITIONAL_ON_ROOT, true),
            new ElementRule(BIRTHDIFF_RATE, new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(RELATIVE_DEATH_RATE, new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            new ElementRule(SAMPLE_PROB, new XMLSyntaxRule[]{new ElementRule(Parameter.class)}, true),
            XMLUnits.SYNTAX_RULES[0]
    };
}