package dr.evoxml;
import dr.evolution.distance.DistanceMatrix;
import dr.evolution.tree.*;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;
import dr.xml.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class RescaledTreeParser extends AbstractXMLObjectParser {
    //
    // Public stuff
    //
    public final static String RESCALED_TREE = "rescaledTree";
    public final static String CLADE = "clade";
    public final static String HEIGHT = "height";
    public String getParserName() { return RESCALED_TREE; }
    public Object parseXMLObject(XMLObject xo) throws XMLParseException {
        Tree tree = (Tree)xo.getChild(Tree.class);
        // make a mutable copy
        SimpleTree rescaledTree = new SimpleTree(tree);
        // First flag all internal nodes as unset....
        for (int i = 0; i < rescaledTree.getInternalNodeCount(); i++) {
            rescaledTree.setNodeHeight(rescaledTree.getInternalNode(i), Double.NEGATIVE_INFINITY);
        }
        for (int i = 0; i < xo.getChildCount(); i++) {
            if (xo.getChild(i) instanceof XMLObject) {
                XMLObject cxo = (XMLObject)xo.getChild(i);
                if (cxo.getName().equals(CLADE)) {
                    TaxonList taxa = (TaxonList)cxo.getChild(TaxonList.class);
                    Set<String> leafSet = new HashSet<String>();
                    for (Taxon taxon : taxa) {
                        leafSet.add(taxon.getId());
                    }
                    NodeRef mrca = Tree.Utils.getCommonAncestorNode(rescaledTree, leafSet);
                    if (mrca == null ||  Tree.Utils.getLeafCount(rescaledTree, mrca) != leafSet.size()) {
                        throw new XMLParseException("Clade defined by taxon Set, " + taxa.getId() + ", is not found in the guide tree");
                    }
                    if (cxo.hasAttribute(HEIGHT)) {
                        double height = cxo.getDoubleAttribute(HEIGHT);
                        rescaledTree.setNodeHeight(mrca, height);
                    }
                }
            }
        }
        if (xo.hasAttribute(HEIGHT)) {
            rescaledTree.setNodeHeight(rescaledTree.getRoot(), xo.getDoubleAttribute(HEIGHT));
        }
        interpolateHeights(rescaledTree, rescaledTree.getRoot());
        return rescaledTree;
    }
    private double interpolateHeights(MutableTree tree, NodeRef node) {
        if (!tree.isExternal(node)) {
            double maxHeight = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < tree.getChildCount(node); i++) {
                NodeRef child = tree.getChild(node, i);
                double h = interpolateHeights(tree, child);
                if (h > maxHeight) {
                    maxHeight = h;
                }
            }
            double height = tree.getNodeHeight(node);
            if (Double.isInfinite(height)) {
                int count = 1;
                NodeRef parent = tree.getParent(node);
                while (parent != null && Double.isInfinite(tree.getNodeHeight(parent))) {
                    parent = tree.getParent(parent);
                    count ++;
                }
                if (parent == null) {
                    height = maxHeight + count;
                } else {
                    height = (tree.getNodeHeight(parent) + maxHeight) / 2.0;
                }
            }
            tree.setNodeHeight(node, height);
            return height;
        } else {
            return tree.getNodeHeight(node);
        }
    }
    public String getParserDescription() {
        return "This element rescales a given tree with a set of clade heights.";
    }
    public Class getReturnType() { return Tree.class; }
    public XMLSyntaxRule[] getSyntaxRules() { return rules; }
    private XMLSyntaxRule[] rules = new XMLSyntaxRule[] {
            AttributeRule.newDoubleRule(HEIGHT, true),
            new ElementRule(Tree.class),
            new ElementRule(CLADE, new XMLSyntaxRule[] {
                    AttributeRule.newDoubleRule(HEIGHT, true),
                    new ElementRule(TaxonList.class)
            }, 0, Integer.MAX_VALUE)
    };
}