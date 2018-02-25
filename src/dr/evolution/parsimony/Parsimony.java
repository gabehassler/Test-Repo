package dr.evolution.parsimony;
import dr.evolution.alignment.PatternList;
import dr.evolution.tree.MutableTree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public class Parsimony {
public static int getParsimonySteps(Tree tree, PatternList patterns) {
int[] score = new int[] { 0 };
for (int i = 0; i < patterns.getPatternCount(); i++) {
String name = "state" + String.valueOf(i+1);
getParsimonyState(tree, tree.getRoot(), patterns, i, name, score);
}
return score[0];
}
public static void reconstructParsimonyStates(Tree tree, PatternList patterns) {
for (int i = 0; i < patterns.getPatternCount(); i++) {
String name = "state" + String.valueOf(i+1);
//int[] score = new int[] {0};
int[] root = getParsimonyState2(tree, tree.getRoot(), patterns, i, name, patterns.getDataType().getStateCount());
int bestIndex = 0;
int bestScore = root[0];
for (int j = 1; j < root.length; j++) {
if (root[j] < bestScore) {
bestScore = root[j];
bestIndex = i;
}
}
//System.out.println("Pattern " + i + " steps = " + score[0]);
reconstructParsimonyState2(tree, tree.getRoot(), name, "r" + name, bestIndex);
}
}
private static void reconstructParsimonyState2(Tree tree, NodeRef node, String setName, String attributeName, int state) {
if (tree.isRoot(node)) {
int[] scores = (int[])tree.getNodeAttribute(node, setName);
int bestIndex = 0;
int bestScore = scores[0];
for (int i = 1; i < scores.length; i++) {
if (scores[i] < bestScore) {
bestScore = scores[i];
bestIndex = i;
}
}
state = bestIndex;
}
int childCount = tree.getChildCount(node);
if (childCount > 0) {
int bestScore = 0;
// the child scores
int[][] childScores = new int[childCount][];
for (int i = 0; i < childCount; i++) {
childScores[i] = (int[])tree.getNodeAttribute(tree.getChild(node,i), setName);
}
int[] childIndices = new int[childCount];
int[] bestIndices = new int[childCount];
int stateCount = childScores[0].length;
int childrenCombinations = (int)Math.round(Math.pow(stateCount, childCount));
for (int i = 0; i < childrenCombinations; i++) {
int c = i;
for (int j = 0; j < childCount; j+=1) {
childIndices[j] = c % stateCount;
c /= stateCount;
}
int score = 0;
for (int j = 0; j < childCount; j+=1) {
score += childScores[j][childIndices[j]] + penalty(state, childIndices[j]);
}
if (i == 0) {
bestScore = score;
} else if (score < bestScore) {
bestScore = score;
for (int j = 0; j <childCount; j++) {
bestIndices[j] = childIndices[j];
}
}
}
for (int i = 0; i < childCount; i++) {
reconstructParsimonyState2(tree, tree.getChild(node, i), setName, attributeName, bestIndices[i]);
}
}
((MutableTree)tree).setNodeAttribute(node, attributeName, new Integer(state));
}
private static int[] getParsimonyState2(Tree tree, NodeRef node, PatternList patterns,
int patternIndex, String attributeName, int stateCount) {
if (tree.isExternal(node)) {
int state = patterns.getPatternState(patterns.getTaxonIndex(tree.getNodeTaxon(node).getId()), patternIndex);
int[] scores = new int[stateCount];
for (int i = 0; i < stateCount; i++) {
scores[i] = Integer.MAX_VALUE/20;
}
if (state < stateCount) {
scores[state] = 0;
} else {
scores[stateCount-1] = 0;
}
((MutableTree)tree).setNodeAttribute(node, attributeName, scores);
return scores;
} else {
int childCount = tree.getChildCount(node);
// the child scores
int[][] childScores = new int[childCount][];
for (int i = 0; i < childCount; i++) {
childScores[i] = getParsimonyState2(tree, tree.getChild(node, i), patterns, patternIndex, attributeName, stateCount);
}
int[] scores = new int[stateCount];
for (int parent = 0; parent < stateCount; parent++) {
int[] childIndices = new int[childCount];
int childrenCombinations = (int)Math.round(Math.pow(stateCount, childCount));
for (int i = 0; i < childrenCombinations; i++) {
int c = i;
for (int j = 0; j < childCount; j+=1) {
childIndices[j] = c % stateCount;
c /= stateCount;
}
int score = 0;
for (int j = 0; j < childCount; j+=1) {
score += childScores[j][childIndices[j]] + penalty(parent, childIndices[j]);
}
if (i == 0) {
scores[parent] = score;
} else if (score < scores[parent]) {
scores[parent] = score;
}
}
}
((MutableTree)tree).setNodeAttribute(node, attributeName, scores);
//((MutableTree)tree).setNodeAttribute(node, attributeName +".children", bestChildren);
//for (int i = 0; i < scores.length; i++) {
//	System.out.print(scores[i] + "\t");
//}
//System.out.println();
return scores;
}
}
private static final int penalty(int state1, int state2) {
if (state1 == state2) return 0;
return 1;
}
private static boolean[] getParsimonyState(Tree tree, NodeRef node, PatternList patterns,
int patternIndex, String attributeName, int[] score) {
if (tree.isExternal(node)) {
int state = patterns.getPatternState(patterns.getTaxonIndex(tree.getNodeTaxon(node).getId()), patternIndex);
((MutableTree)tree).setNodeAttribute(node, "r"+attributeName, new Integer(state));
return patterns.getDataType().getStateSet(state);
} else {
// the union state set
boolean[] uState = getParsimonyState(tree, tree.getChild(node, 0), patterns, patternIndex, attributeName, score);
int n = uState.length;
// the intersection state set
boolean[] iState = new boolean[n];
for (int j = 0; j < n; j++) {
iState[j] = uState[j];
}
// the cardinality of the intersection set
int iCard = 0;
for (int i = 1; i < tree.getChildCount(node); i++) {
boolean[] state = getParsimonyState(tree, tree.getChild(node, i), patterns,
patternIndex, attributeName, score);
for (int j = 0; j < uState.length; j++) {
uState[j] = state[j] || uState[j];
iState[j] = state[j] && iState[j];
if (iState[j]) iCard += 1;
}
}
if (score != null && iCard == 0) {
score[0] += 1;
}
if (attributeName != null) {
// Should take the intersection if non-empty AD 24/3/2004
if (iCard == 0) {
((MutableTree)tree).setNodeAttribute(node, attributeName, uState);
} else {
((MutableTree)tree).setNodeAttribute(node, attributeName, iState);
}
}
// Should take the intersection if non-empty AD 24/3/2004
if (iCard == 0) {
return uState;
} else {
return iState;
}
}
}
}