
package dr.evoxml;

import dr.evolution.util.Taxa;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import dr.xml.*;

public class TaxaParser extends AbstractXMLObjectParser {

    public static final String TAXA = "taxa";

    public String getParserName() { return TAXA; }

    public String getExample() {
        return "<!-- A list of six taxa -->\n"+
                "<taxa id=\"greatApes\">\n"+
                "	<taxon id=\"human\"/>\n"+
                "	<taxon id=\"chimp\"/>\n"+
                "	<taxon id=\"bonobo\"/>\n"+
                "	<taxon id=\"gorilla\"/>\n"+
                "	<taxon id=\"orangutan\"/>\n"+
                "	<taxon id=\"siamang\"/>\n"+
                "</taxa>\n" +
                "\n" +
                "<!-- A list of three taxa by references to above taxon objects -->\n"+
                "<taxa id=\"humanAndChimps\">\n"+
                "	<taxon idref=\"human\"/>\n"+
                "	<taxon idref=\"chimp\"/>\n"+
                "	<taxon idref=\"bonobo\"/>\n"+
                "</taxa>\n";
    }

    public Object parseXMLObject(XMLObject xo) throws XMLParseException {

        Taxa taxonList = new Taxa();

        for (int i = 0; i < xo.getChildCount(); i++) {
            Object child = xo.getChild(i);
            if (child instanceof Taxon) {
                Taxon taxon = (Taxon)child;
                taxonList.addTaxon(taxon);
            } else if (child instanceof TaxonList) {
                TaxonList taxonList1 = (TaxonList)child;
                for (int j = 0; j < taxonList1.getTaxonCount(); j++) {
                    taxonList.addTaxon(taxonList1.getTaxon(j));
                }
            } else {
                throwUnrecognizedElement(xo);
            }
        }
        return taxonList;
    }

    public XMLSyntaxRule[] getSyntaxRules() { return rules; }

    private final XMLSyntaxRule[] rules = {
        new OrRule(
            new ElementRule(Taxa.class, 1, Integer.MAX_VALUE),
            new ElementRule(Taxon.class, 1, Integer.MAX_VALUE)
        )
    };

    public String getParserDescription() {
        return "Defines a set of taxon objects.";
    }

    public Class getReturnType() { return Taxa.class; }
}


