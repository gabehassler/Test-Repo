package dr.evoxml;
import dr.evolution.distance.DistanceMatrix;
import dr.evolution.tree.NeighborJoiningTree;
import dr.evolution.tree.Tree;
import dr.xml.*;
public class NeighborJoiningParser extends AbstractXMLObjectParser {
//
// Public stuff
//
public final static String NEIGHBOR_JOINING_TREE = "neighborJoiningTree";
public String getParserName() { return NEIGHBOR_JOINING_TREE; }
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
DistanceMatrix distances = (DistanceMatrix)xo.getChild(DistanceMatrix.class);
return new NeighborJoiningTree(distances);
}
public String getParserDescription() {
return "This element returns a neighbour-joining tree generated from the given distances.";
}
public Class getReturnType() { return Tree.class; }
public XMLSyntaxRule[] getSyntaxRules() { return rules; }
private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
new ElementRule(DistanceMatrix.class)
};
}