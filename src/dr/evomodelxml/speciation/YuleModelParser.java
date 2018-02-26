package dr.evomodelxml.speciation;
import dr.evolution.util.Units;
import dr.evomodel.speciation.BirthDeathGernhard08Model;
import dr.evoxml.util.XMLUnits;
import dr.inference.model.Parameter;
import dr.xml.*;
import java.util.logging.Logger;
public class YuleModelParser extends AbstractXMLObjectParser {
    public static final String YULE_MODEL = "yuleModel";
    public static final String YULE = "yule";
    public static final String BIRTH_RATE = "birthRate";
    public String getParserName() {
        return YULE_MODEL;
    }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        final Units.Type units = XMLUnits.Utils.getUnitsAttr(xo);
        final XMLObject cxo = xo.getChild(BIRTH_RATE);
        final boolean conditonalOnRoot =  xo.getAttribute(BirthDeathModelParser.CONDITIONAL_ON_ROOT, false);
        final Parameter brParameter = (Parameter) cxo.getChild(Parameter.class);
        Logger.getLogger("dr.evomodel").info("Using Yule prior on tree");
        return new BirthDeathGernhard08Model(xo.getId(), brParameter, null, null,
                BirthDeathGernhard08Model.TreeType.UNSCALED, units, conditonalOnRoot);
    }
    //************************************************************************
    // AbstractXMLObjectParser implementation
    //************************************************************************
    public String getParserDescription() {
        return "A speciation model of a simple constant rate Birth-death process.";
    }
    public Class getReturnType() {
        return BirthDeathGernhard08Model.class;
    }
    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }
    private final XMLSyntaxRule[] rules = {
            AttributeRule.newBooleanRule(BirthDeathModelParser.CONDITIONAL_ON_ROOT, true),
            new ElementRule(BIRTH_RATE,
                    new XMLSyntaxRule[]{new ElementRule(Parameter.class)}),
            XMLUnits.SYNTAX_RULES[0]
    };
}