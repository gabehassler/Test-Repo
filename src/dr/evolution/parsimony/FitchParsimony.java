
package dr.evolution.parsimony;

import dr.evolution.alignment.PatternList;
import dr.evolution.alignment.Patterns;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.tree.FlexibleNode;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;

public class FitchParsimony implements ParsimonyCriterion {

    private final int stateCount;
    private final boolean gapsAreStates;

    private boolean[][][] stateSets;
    private int[][] states;

    private Tree tree = null;
    private final PatternList patterns;

    private boolean hasCalculatedSteps = false;
    private boolean hasRecontructedStates = false;

    private final double[] siteScores;

    public PatternList getPatterns() {
        return patterns;
    }

    public FitchParsimony(PatternList patterns, boolean gapsAreStates) {
        if (patterns == null) {
            throw new IllegalArgumentException("The patterns cannot be null");
        }

        this.gapsAreStates = gapsAreStates;

        if (gapsAreStates) {
            stateCount = patterns.getDataType().getStateCount() + 1;
        } else {
            stateCount = patterns.getDataType().getStateCount();

        }

        this.patterns = patterns;
        this.siteScores = new double[patterns.getPatternCount()];
    }

    public double[] getSiteScores(Tree tree) {

        if (tree == null) {
            throw new IllegalArgumentException("The tree cannot be null");
        }

        if (this.tree == null || this.tree != tree) {

            initialize(tree);
        }

        if (!hasCalculatedSteps) {
            for (int i = 0; i < siteScores.length; i++) {
                siteScores[i] = 0;
            }
            calculateSteps(tree, tree.getRoot(), patterns);
            hasCalculatedSteps = true;
        }


        return siteScores;
    }

    public double getScore(Tree tree) {

        getSiteScores(tree);

        double score = 0;

        for (int i = 0; i < patterns.getPatternCount(); i++) {
            score += siteScores[i] * patterns.getPatternWeight(i);
        }
        return score;
    }

    public int[] getStates(Tree tree, NodeRef node) {

        if (!Tree.Utils.isBinary(tree)) {
            throw new IllegalArgumentException("The Fitch algorithm can only reconstruct ancestral states on binary trees");
        }

        getSiteScores(tree);

        if (!hasRecontructedStates) {
            reconstructStates(tree, tree.getRoot(), null);
            hasRecontructedStates = true;
        }

        return states[node.getNumber()];
    }

    public void initialize(Tree tree) {

        this.tree = tree;

        hasCalculatedSteps = false;
        hasRecontructedStates = false;

        stateSets = new boolean[tree.getNodeCount()][patterns.getPatternCount()][];
        states = new int[tree.getNodeCount()][patterns.getPatternCount()];

        for (int i = 0; i < patterns.getPatternCount(); i++) {
            int[] pattern = patterns.getPattern(i);
            for (int j = 0; j < tree.getExternalNodeCount(); j++) {
                NodeRef node = tree.getExternalNode(j);
                int state = pattern[patterns.getTaxonIndex(tree.getNodeTaxon(node).getId())];
                if (gapsAreStates) {
                    stateSets[j][i] = new boolean[stateCount];
                    if (patterns.getDataType().isGapState(state)) {
                        stateSets[j][i][stateCount - 1] = true;
                    } else {
                        boolean[] stateSet = patterns.getDataType().getStateSet(state);
                        for (int k = 0; k < stateSet.length; k++) {
                            stateSets[j][i][k] = stateSet[k];
                        }
                    }
                } else {
                    stateSets[j][i] = patterns.getDataType().getStateSet(state);
                }
            }
        }
    }

    private void calculateSteps(Tree tree, NodeRef node, PatternList patterns) {

        if (!tree.isExternal(node)) {

            for (int i = 0; i < tree.getChildCount(node); i++) {
                calculateSteps(tree, tree.getChild(node, i), patterns);
            }

            for (int i = 0; i < patterns.getPatternCount(); i++) {
                boolean[] uState = stateSets[tree.getChild(node, 0).getNumber()][i];
                boolean[] iState = stateSets[tree.getChild(node, 0).getNumber()][i];

                for (int j = 1; j < tree.getChildCount(node); j++) {
                    uState = union(uState, stateSets[tree.getChild(node, j).getNumber()][i]);
                    iState = intersection(iState, stateSets[tree.getChild(node, j).getNumber()][i]);
                }

                if (size(iState) > 0) {
                    stateSets[node.getNumber()][i] = iState;
                } else {
                    stateSets[node.getNumber()][i] = uState;
                    siteScores[i]++;
                }
            }
        }
    }

    private void reconstructStates(Tree tree, NodeRef node, int[] parentStates) {

        for (int i = 0; i < patterns.getPatternCount(); i++) {

            if (parentStates != null && stateSets[node.getNumber()][i][parentStates[i]]) {
                states[node.getNumber()][i] = parentStates[i];
            } else {
                states[node.getNumber()][i] = firstIndex(stateSets[node.getNumber()][i]);
            }
        }

        for (int i = 0; i < tree.getChildCount(node); i++) {
            reconstructStates(tree, tree.getChild(node, i), states[node.getNumber()]);
        }
    }

    private static boolean[] union(boolean[] s1, boolean[] s2) {

        boolean[] union = new boolean[s1.length];
        for (int i = 0; i < union.length; i++) {
            union[i] = s1[i] || s2[i];
        }
        return union;
    }

    private static boolean[] intersection(boolean[] s1, boolean[] s2) {

        boolean[] intersection = new boolean[s1.length];
        for (int i = 0; i < intersection.length; i++) {
            intersection[i] = s1[i] && s2[i];
        }
        return intersection;
    }

    private static int firstIndex(boolean[] s1) {

        for (int i = 0; i < s1.length; i++) {
            if (s1[i]) {
                return i;
            }
        }
        return -1;
    }

    private static int size(boolean[] s1) {

        int count = 0;
        for (int i = 0; i < s1.length; i++) {
            if (s1[i]) count += 1;
        }
        return count;
    }

    public static void main(String[] argv) {
        FlexibleNode tip1 = new FlexibleNode(new Taxon("tip1"));
        FlexibleNode tip2 = new FlexibleNode(new Taxon("tip2"));
        FlexibleNode tip3 = new FlexibleNode(new Taxon("tip3"));
        FlexibleNode tip4 = new FlexibleNode(new Taxon("tip4"));
        FlexibleNode tip5 = new FlexibleNode(new Taxon("tip5"));

        FlexibleNode node1 = new FlexibleNode();
        node1.addChild(tip1);
        node1.addChild(tip2);

        FlexibleNode node2 = new FlexibleNode();
        node2.addChild(tip4);
        node2.addChild(tip5);

        FlexibleNode node3 = new FlexibleNode();
        node3.addChild(tip3);
        node3.addChild(node2);

        FlexibleNode root = new FlexibleNode();
        root.addChild(node1);
        root.addChild(node3);

        FlexibleTree tree = new FlexibleTree(root);

        Patterns patterns = new Patterns(Nucleotides.INSTANCE, tree);
        patterns.addPattern(new int[]{1, 0, 1, 2, 2});

        FitchParsimony fitch = new FitchParsimony(patterns, false);

        System.out.println("No. Steps = " + fitch.getScore(tree));
        System.out.println(" state(node1) = " + fitch.getStates(tree, node1)[0]);
        System.out.println(" state(node2) = " + fitch.getStates(tree, node2)[0]);
        System.out.println(" state(node3) = " + fitch.getStates(tree, node3)[0]);
        System.out.println(" state(root) = " + fitch.getStates(tree, root)[0]);


        System.out.println("\nParsimony static methods:");
        System.out.println("No. Steps = " + Parsimony.getParsimonySteps(tree, patterns));
        Parsimony.reconstructParsimonyStates(tree, patterns);
        System.out.println(" state(node1) = " + tree.getNodeAttribute(node1, "rstate1"));
        System.out.println(" state(node2) = " + tree.getNodeAttribute(node2, "rstate1"));
        System.out.println(" state(node3) = " + tree.getNodeAttribute(node3, "rstate1"));
        System.out.println(" state(root) = " + tree.getNodeAttribute(root, "rstate1"));
    }
}