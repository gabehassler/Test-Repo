package dr.evoxml;
import dr.evolution.io.Importer;
import dr.evolution.io.NewickImporter;
import dr.evolution.tree.*;
import dr.evolution.util.Taxon;
import dr.evolution.util.Units;
import dr.evoxml.util.XMLUnits;
import dr.xml.*;
import java.io.IOException;
public class NewickParser extends AbstractXMLObjectParser {
public static final String NEWICK = "newick";
public static final String UNITS = "units";
public static final String RESCALE_HEIGHT = "rescaleHeight";
public static final String RESCALE_LENGTH = "rescaleLength";
public static final String USING_DATES = SimpleTreeParser.USING_DATES;
public static final String USING_HEIGHTS = "usingHeights";
public NewickParser() {
rules = new XMLSyntaxRule[]{
AttributeRule.newBooleanRule(USING_DATES, true),
AttributeRule.newBooleanRule(USING_HEIGHTS, true),
AttributeRule.newDoubleRule(RESCALE_HEIGHT, true, "Attempt to rescale the tree to the given root height"),
AttributeRule.newDoubleRule(RESCALE_LENGTH, true, "Attempt to rescale the tree to the given total length"),
new StringAttributeRule(UNITS, "The branch length units of this tree", Units.UNIT_NAMES, true),
new ElementRule(String.class, "The NEWICK format tree. Tip labels are taken to be Taxon IDs")
};
}
public String getParserName() {
return NEWICK;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
final Units.Type units = XMLUnits.Utils.getUnitsAttr(xo);
//        boolean usingDates = xo.getAttribute(USING_DATES, true);
boolean usingDates = true;
if (xo.hasAttribute(USING_DATES)) {
usingDates = xo.getAttribute(USING_DATES, true);
}
boolean usingHeights = false;
if (xo.hasAttribute(USING_HEIGHTS)) {
usingHeights = xo.getAttribute(USING_HEIGHTS, true);
}
//        System.out.println("UsingDates=" + usingDates + " usingHeights= " + usingHeights);
if (usingDates && usingHeights) {
throw new XMLParseException("Unable to use both dates and node heights. Specify value of usingDates attribute.");
}
//		else if (!usingDates && !usingHeights) {
//			System.out.println("Tree is assumed to be ultrametric");
//		}
StringBuffer buffer = new StringBuffer();
for (int i = 0; i < xo.getChildCount(); i++) {
if (xo.getChild(i) instanceof String) {
buffer.append((String) xo.getChild(i));
} else {
throw new XMLParseException("illegal element in newick element");
}
}
java.io.Reader reader = new java.io.StringReader(buffer.toString());
NewickImporter importer = new NewickImporter(reader);
FlexibleTree tree;
try {
tree = (FlexibleTree) importer.importTree(null);
} catch (IOException ioe) {
throw new XMLParseException("error parsing tree in newick element");
} catch (NewickImporter.BranchMissingException bme) {
throw new XMLParseException("branch missing in tree in newick element");
} catch (Importer.ImportException ime) {
throw new XMLParseException("error parsing tree in newick element - " + ime.getMessage());
}
if (tree == null) {
throw new XMLParseException("Failed to read tree");
}
tree.setUnits(units);
for (int i = 0; i < tree.getTaxonCount(); i++) {
FlexibleNode node = (FlexibleNode) tree.getExternalNode(i);
String id = node.getTaxon().getId();
Taxon taxon = null;
try {
Object obj = getStore().getObjectById(id);
if (obj instanceof Taxon) {
taxon = (Taxon) obj;
}
} catch (ObjectNotFoundException e) { /**/}
if (taxon != null) {
node.setTaxon(taxon);
} else {
throw new XMLParseException("unknown taxon, " + id + ", in newick tree");
}
}
if (usingDates) {
for (int i = 0; i < tree.getTaxonCount(); i++) {
NodeRef node = tree.getExternalNode(i);
dr.evolution.util.Date date = (dr.evolution.util.Date) tree.getTaxonAttribute(i, dr.evolution.util.Date.DATE);
if (date == null) {
date = (dr.evolution.util.Date) tree.getNodeAttribute(tree.getExternalNode(i), dr.evolution.util.Date.DATE);
}
double height = 0.0;
double nodeHeight = tree.getNodeHeight(node);
if (date != null) {
height = Taxon.getHeightFromDate(date);
}
if (Math.abs(nodeHeight - height) > 1e-5) {
System.out.println("  Changing height of node " + tree.getTaxon(node.getNumber()) + " from " + nodeHeight + " to " + height);
tree.setNodeHeight(node, height);
}
}
for (int i = 0; i < tree.getInternalNodeCount(); i++) {
dr.evolution.util.Date date = (dr.evolution.util.Date) tree.getNodeAttribute(tree.getInternalNode(i), dr.evolution.util.Date.DATE);
if (date != null) {
double height = Taxon.getHeightFromDate(date);
tree.setNodeHeight(tree.getInternalNode(i), height);
}
}// END: i loop
MutableTree.Utils.correctHeightsForTips(tree);
} else if (!usingDates && !usingHeights) {
System.out.println("Tree is assumed to be ultrametric");
// not using dates or heights
for (int i = 0; i < tree.getTaxonCount(); i++) {
final NodeRef leaf = tree.getExternalNode(i);
final double h = tree.getNodeHeight(leaf);
if (h != 0.0) {
double zero = 0.0;
System.out.println("  Changing height of leaf node " + tree.getTaxon(leaf.getNumber()) + " from " + h + " to " + zero);
tree.setNodeHeight(leaf, zero);
}
}// END: i loop
} else {
System.out.println("Using node heights.");
}// END: usingDates check
if (xo.hasAttribute(RESCALE_HEIGHT)) {
double rescaleHeight = xo.getDoubleAttribute(RESCALE_HEIGHT);
double scale = rescaleHeight / tree.getNodeHeight(tree.getRoot());
for (int i = 0; i < tree.getInternalNodeCount(); i++) {
NodeRef n = tree.getInternalNode(i);
tree.setNodeHeight(n, tree.getNodeHeight(n) * scale);
}
}
if (xo.hasAttribute(RESCALE_LENGTH)) {
double rescaleLength = xo.getDoubleAttribute(RESCALE_LENGTH);
double scale = rescaleLength / Tree.Utils.getTreeLength(tree, tree.getRoot());
for (int i = 0; i < tree.getInternalNodeCount(); i++) {
NodeRef n = tree.getInternalNode(i);
tree.setNodeHeight(n, tree.getNodeHeight(n) * scale);
}
}
//System.out.println("Constructed newick tree = " + Tree.Utils.uniqueNewick(tree, tree.getRoot()));
System.err.println(tree.toString());
return tree;
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "Constructs a tree from a NEWICK format tree description";
}
public String getExample() {
return "<" + getParserName() + " " + UNITS + "=\"" + Units.Utils.getDefaultUnitName(Units.Type.YEARS) + "\">" + " ((A:1.0, B:1.0):1.0,(C:2.0, D:2.0):1.0); </" + getParserName() + ">";
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private final XMLSyntaxRule[] rules;
public Class getReturnType() {
return Tree.class;
}
}