
package dr.evoxml;

import dr.evolution.util.Date;
import dr.evolution.util.Units;
import dr.evoxml.util.DateUnitsType;
import dr.evoxml.util.XMLUnits;
import dr.xml.*;

public class DateParser extends AbstractXMLObjectParser {

    public static final String VALUE = "value";
    public static final String UNITS = "units";
    public static final String ORIGIN = "origin";
    public static final String DIRECTION = "direction";
    
    public static final String FORWARDS = DateUnitsType.FORWARDS.getAttribute(); //"forwards";
    public static final String BACKWARDS = DateUnitsType.BACKWARDS.getAttribute(); //"backwards";

    public static final String YEARS = DateUnitsType.YEARS.getAttribute(); //"units";
    public static final String MONTHS = DateUnitsType.MONTHS.getAttribute(); //"units";
    public static final String DAYS = DateUnitsType.DAYS.getAttribute(); //"days";

    public static final String PRECISION = "precision";

    public String getParserName() {
        return Date.DATE;
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, java.util.Locale.UK);
        dateFormat.setLenient(true);

        if (xo.getChildCount() > 0) {
            throw new XMLParseException("No child elements allowed in date element.");
        }

        double value = 0.0;
        java.util.Date dateValue = null;

        if (xo.hasAttribute(VALUE)) {
            try {
                value = xo.getDoubleAttribute(VALUE);
            } catch (XMLParseException e) {
                String dateString = xo.getStringAttribute(VALUE);

                try {

                    dateValue = dateFormat.parse(dateString);

                } catch (Exception ex) {
                    throw new XMLParseException("value=" + dateString + " not recognised as a date, use DD/MM/YYYY");
                }
            }
        } else {
            throw new XMLParseException("Value attribute missing from date element.");
        }

        boolean backwards = false;

        if (xo.hasAttribute(DIRECTION)) {
            String direction = (String) xo.getAttribute(DIRECTION);
            if (direction.equals(BACKWARDS)) {
                backwards = true;
            }
        }

        Units.Type units = XMLUnits.Utils.getUnitsAttr(xo);

        Date date;

        if (xo.hasAttribute(ORIGIN)) {

            String originString = (String) xo.getAttribute(ORIGIN);
            java.util.Date origin;

            try {
                origin = dateFormat.parse(originString);
            } catch (Exception e) {
                throw new XMLParseException("origin=" + originString + " not recognised as a date, use DD/MM/YYYY");
            }

            if (dateValue != null) {
                date = new Date(dateValue, units, origin);
            } else {
                date = new Date(value, units, backwards, origin);
            }

        } else {

            // No origin specified so use default (1st Jan 1970)
            if (dateValue != null) {
                date = new Date(dateValue, units);
            } else {
                date = new Date(value, units, backwards);
            }
        }

        if (xo.hasAttribute(PRECISION)) {
            double precision = (Double)xo.getDoubleAttribute(PRECISION);
            date.setPrecision(precision);
        }


        return date;
    }

    public String getParserDescription() {
        return "Specifies a date on a given timescale";
    }

    public String getExample() {
        return
                "<!-- a date representing 10 years in the past                                 -->\n" +
                        "<date value=\"10.0\" units=\"years\" direction=\"backwards\"/>\n" +
                        "\n" +
                        "<!-- a date representing 300 days after Jan 1st 1989                          -->\n" +
                        "<date value=\"300.0\" origin=\"01/01/89\" units=\"days\" direction=\"forwards\"/>\n";
    }

    public XMLSyntaxRule[] getSyntaxRules() {
        return rules;
    }

    private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
            new StringAttributeRule(VALUE,
                    "The value of this date"),
            new StringAttributeRule(ORIGIN,
                    "The origin of this time scale, which must be a valid calendar date", "01/01/01", true),
            new StringAttributeRule(UNITS, "The units of the timescale", new String[]{YEARS, MONTHS, DAYS}, true),
            new StringAttributeRule(DIRECTION, "The direction of the timescale", new String[]{FORWARDS, BACKWARDS}, true),
            AttributeRule.newDoubleRule(PRECISION, true, "The precision to which the date is specified"),
    };

    public Class getReturnType() {
        return dr.evolution.util.Date.class;
    }
}
