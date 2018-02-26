
package dr.evomodel.operators;

import dr.evolution.tree.MutableTree;
import dr.evolution.tree.NodeRef;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.operators.FNPRParser;
import dr.inference.operators.OperatorFailedException;
import dr.math.MathUtils;

public class FNPR extends AbstractTreeOperator {

    private TreeModel tree = null;
    public FNPR(TreeModel tree, double weight) {
        this.tree = tree;
        setWeight(weight);
        // distances = new int[tree.getNodeCount()];
    }

    @Override
    public double doOperation() throws OperatorFailedException {
        NodeRef iGrandfather, iBrother;
        double heightFather;
        final int tipCount = tree.getExternalNodeCount();

        final int nNodes = tree.getNodeCount();
        final NodeRef root = tree.getRoot();

        NodeRef i;

        int MAX_TRIES = 1000;

        for (int tries = 0; tries < MAX_TRIES; ++tries) {
           // get a random node whose father is not the root - otherwise
           // the operation is not possible
           do {
              i = tree.getNode(MathUtils.nextInt(nNodes));
           } while (root == i || tree.getParent(i) == root);

           // int childIndex = (MathUtils.nextDouble() >= 0.5 ? 1 : 0);
           // int otherChildIndex = 1 - childIndex;
           // NodeRef iOtherChild = tree.getChild(i, otherChildIndex);

           NodeRef iFather = tree.getParent(i);
           iGrandfather = tree.getParent(iFather);
           iBrother = getOtherChild(tree, iFather, i);
           heightFather = tree.getNodeHeight(iFather);

           // NodeRef newChild = getRandomNode(possibleChilds, iFather);
           NodeRef newChild = tree.getNode(MathUtils.nextInt(nNodes));

           if (tree.getNodeHeight(newChild) < heightFather
                 && root != newChild
                 && tree.getNodeHeight(tree.getParent(newChild)) > heightFather
                 && newChild != iFather
                 && tree.getParent(newChild) != iFather) {
              NodeRef newGrandfather = tree.getParent(newChild);

              tree.beginTreeEdit();

              // prune
              tree.removeChild(iFather, iBrother);
              tree.removeChild(iGrandfather, iFather);
              tree.addChild(iGrandfather, iBrother);

              // reattach
              tree.removeChild(newGrandfather, newChild);
              tree.addChild(iFather, newChild);
              tree.addChild(newGrandfather, iFather);

              // ****************************************************

              tree.endTreeEdit();

              tree.pushTreeChangedEvent(i);

              assert tree.getExternalNodeCount() == tipCount;

              return 0.0;
           }
        }

        throw new OperatorFailedException("Couldn't find valid SPR move on this tree!");
     }

    @Override
    public String getOperatorName() {
        return FNPRParser.FNPR;
    }

    public double getTargetAcceptanceProbability() {

        return 0.0234;
    }

    public double getMaximumAcceptanceLevel() {

        return 0.04;
    }

    public double getMaximumGoodAcceptanceLevel() {

        return 0.03;
    }

    public double getMinimumAcceptanceLevel() {

        return 0.005;
    }

    public double getMinimumGoodAcceptanceLevel() {

        return 0.01;
    }

    public String getPerformanceSuggestion() {
        return "";
    }

}
