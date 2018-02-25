package dr.evomodelxml.speciation;
import dr.evolution.util.Taxon;
import dr.evomodel.speciation.AlloppSpeciesBindings;
import dr.xml.AbstractXMLObjectParser;
import dr.xml.ElementRule;
import dr.xml.XMLObject;
import dr.xml.XMLParseException;
import dr.xml.XMLSyntaxRule;
public class AlloppSpeciesBindingsIndividualParser extends
AbstractXMLObjectParser {
public static final String INDIVIDUAL = "individual";
public String getParserName() {
return INDIVIDUAL;
}
@Override
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
Taxon[] taxa = new Taxon[xo.getChildCount()];
for (int nt = 0; nt < taxa.length; ++nt) {
taxa[nt] = (Taxon) xo.getChild(nt);
}
return new AlloppSpeciesBindings.Individual(xo.getId(), taxa);
}
@Override
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[]{
new ElementRule(Taxon.class, 1, Integer.MAX_VALUE)
};
}
@Override
public String getParserDescription() {
return "Individual specimen from a species, possibly containing multiple genomes.";
}
@Override
public Class getReturnType() {
return AlloppSpeciesBindings.Individual.class;
}
}
