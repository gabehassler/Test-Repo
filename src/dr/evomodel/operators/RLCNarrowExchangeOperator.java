package dr.evomodel.operators;
import dr.evolution.tree.MutableTree;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.inference.operators.OperatorFailedException;
import dr.inference.operators.SimpleMCMCOperator;
import dr.math.MathUtils;
import dr.xml.*;
import java.util.ArrayList;
import java.util.List;
public class RLCNarrowExchangeOperator extends SimpleMCMCOperator {
    public static final String NARROW_EXCHANGE = "narrowExchangeRLC";
    private static final int MAX_TRIES = 10000;
    private final TreeModel tree;
    public RLCNarrowExchangeOperator(TreeModel tree, double weight) {
        this.tree = tree;
        setWeight(weight);
    }
    public double doOperation() throws OperatorFailedException {
        int tipCount = tree.getExternalNodeCount();
        narrow();
        if (tree.getExternalNodeCount() != tipCount) {
            throw new RuntimeException("Lost some tips in narrow exchange RLC");
        }
        return 0.0;
    }
    public void narrow() throws OperatorFailedException {
        final int nNodes = tree.getNodeCount();
        final NodeRef root = tree.getRoot();
        for (int tries = 0; tries < MAX_TRIES; ++tries) {
            NodeRef i = tree.getNode(MathUtils.nextInt(nNodes));
            while (root == i || tree.getParent(i) == root) {
                i = tree.getNode(MathUtils.nextInt(nNodes));
            }
            final NodeRef iParent = tree.getParent(i);
            final NodeRef iGrandParent = tree.getParent(iParent);
            NodeRef iUncle = tree.getChild(iGrandParent, 0);
            if (iUncle == iParent) {
                iUncle = tree.getChild(iGrandParent, 1);
            }
            assert tree.getNodeHeight(i) < tree.getNodeHeight(iGrandParent);
            if (tree.getNodeHeight(iUncle) < tree.getNodeHeight(iParent)) {
                NodeRef iSister = tree.getChild(iParent, 0);
                if (iSister == i) {
                    iSister = tree.getChild(iParent, 1);
                }
                eupdate(i, iUncle, iParent, iGrandParent, iSister);
                tree.pushTreeChangedEvent(iParent);
                tree.pushTreeChangedEvent(iGrandParent);
                return;
            }
        }
        //System.out.println("tries = " + tries);
        throw new OperatorFailedException("Couldn't find valid narrow move on this tree!!");
    }
    public String getOperatorName() {
        return "Narrow Exchange RLC";
    }
    private void eupdate(NodeRef i, NodeRef j, NodeRef iP, NodeRef jP, NodeRef iS) throws OperatorFailedException {
        tree.beginTreeEdit();
        tree.removeChild(iP, i);
        tree.removeChild(jP, j);
        tree.addChild(jP, i);
        tree.addChild(iP, j);
        tree.endTreeEdit();
        List<NodeRef> nodes = new ArrayList<NodeRef>();
        nodes.add(i);
        nodes.add(iP);
        nodes.add(j);
        nodes.add(jP);
        nodes.add(iS);
        NodeRef a = nodes.remove(MathUtils.nextInt(nodes.size()));
        NodeRef b = nodes.get(MathUtils.nextInt(nodes.size()));
        //swap traits in these two nodes
        double changedA = tree.getNodeTrait(a, "trait");
        double changedB = tree.getNodeTrait(a, "trait");
        tree.setNodeTrait(a, "trait", changedB);
        tree.setNodeTrait(b, "trait", changedA);
    }
    public double getMinimumAcceptanceLevel() {
        return 0.05;
    }
    public double getMinimumGoodAcceptanceLevel() {
        return 0.05;
    }
    public String getPerformanceSuggestion() {
        if (Utils.getAcceptanceProbability(this) < getMinimumAcceptanceLevel()) {
            return "";
        } else if (Utils.getAcceptanceProbability(this) > getMaximumAcceptanceLevel()) {
            return "";
        } else {
            return "";
        }
    }
    public static XMLObjectParser NARROW_EXCHANGE_PARSER = new AbstractXMLObjectParser() {
        public String getParserName() {
            return NARROW_EXCHANGE;
        }
        public Object parseXMLObject(XMLObject xo) throws XMLParseException {
            TreeModel treeModel = (TreeModel) xo.getChild(TreeModel.class);
            double weight = xo.getDoubleAttribute("weight");
            return new RLCNarrowExchangeOperator(treeModel, weight);
        }
        //************************************************************************
        // AbstractXMLObjectParser implementation
        //************************************************************************
        public String getParserDescription() {
            return "This element represents a narrow exchange operator. " +
                    "This operator swaps a random subtree with its uncle.";
        }
        public Class getReturnType() {
            return RLCNarrowExchangeOperator.class;
        }
        public XMLSyntaxRule[] getSyntaxRules() {
            return rules;
        }
        private final XMLSyntaxRule[] rules = {
                AttributeRule.newDoubleRule("weight"),
                new ElementRule(TreeModel.class)
        };
    };
}
