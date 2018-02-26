
package dr.evoxml.util;

import dr.evolution.util.Units;
import dr.xml.StringAttributeRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;

public interface XMLUnits extends Units {

    final static String GENERATIONS = "generations";
    final static String DAYS = "days";
    final static String MONTHS = "months";
    final static String YEARS = "years";
    public final static String SUBSTITUTIONS = "substitutions";
    // Mutations has been replaced with substitutions...
    final static String MUTATIONS = "mutations";
    final static String UNKNOWN = "unknown";

    public final static String UNITS = "units";

    XMLSyntaxRule UNITS_RULE = new StringAttributeRule("units", "the units", UNIT_NAMES, false);
    XMLSyntaxRule[] SYNTAX_RULES = {UNITS_RULE};

    class Utils {

        public static Units.Type getUnitsAttr(XMLObject
                xo) throws XMLParseException {

            Units.Type units = dr.evolution.util.Units.Type.GENERATIONS;
            if (xo.hasAttribute(UNITS)) {
                String unitsAttr = (String) xo.getAttribute(UNITS);
                if (unitsAttr.equals(YEARS)) {
                    units = dr.evolution.util.Units.Type.YEARS;
                } else if (unitsAttr.equals(MONTHS)) {
                    units = dr.evolution.util.Units.Type.MONTHS;
                } else if (unitsAttr.equals(DAYS)) {
                    units = dr.evolution.util.Units.Type.DAYS;
                } else if (unitsAttr.equals(SUBSTITUTIONS) || unitsAttr.equals(MUTATIONS)) {
                    units = dr.evolution.util.Units.Type.SUBSTITUTIONS;
                }
            }
            return units;
        }

    }
}
