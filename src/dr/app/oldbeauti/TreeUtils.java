package dr.app.oldbeauti;
import dr.evolution.tree.MutableTree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Date;
import dr.evolution.util.Taxon;
import dr.evolution.util.TimeScale;
import dr.stats.Variate;
public class TreeUtils {
public static void getRootToTipDistances(Tree tree, Variate distances) {
double rootHeight = tree.getNodeHeight(tree.getRoot());
double height;
for (int i = 0; i < tree.getExternalNodeCount(); i++) {
height = tree.getNodeHeight(tree.getExternalNode(i));
distances.add(rootHeight - height);
}
}
public static void getTipDates(Tree tree, Variate dates) {
for (int i = 0; i < tree.getExternalNodeCount(); i++) {
Taxon taxon = tree.getNodeTaxon(tree.getExternalNode(i));
Object date = taxon.getAttribute("date");
if (date != null) {
if (date instanceof Date) {
dates.add(((Date) date).getTimeValue());
} else {
try {
dates.add(Double.parseDouble(date.toString()));
} catch (NumberFormatException nfe) {
dates.add(0.0);
}
}
} else {
dates.add(0.0);
}
}
}
public static void getRootToTipDistances(Tree tree, NodeRef node, double rootHeight,
Variate distances) {
if (tree.isExternal(node)) {
double height = tree.getNodeHeight(node);
distances.add(rootHeight - height);
} else {
getRootToTipDistances(tree, tree.getChild(node, 0), rootHeight, distances);
getRootToTipDistances(tree, tree.getChild(node, 1), rootHeight, distances);
}
}
public static void getTipDates(Tree tree, NodeRef node, Variate dates) {
if (tree.isExternal(node)) {
String date = (String) tree.getNodeTaxon(node).getAttribute("date");
if (date != null) {
dates.add(Double.parseDouble(date));
} else {
dates.add(0.0);
}
} else {
getTipDates(tree, tree.getChild(node, 0), dates);
getTipDates(tree, tree.getChild(node, 1), dates);
}
}
public static double guessDate(String s) {
int i = s.length();
char c;
do {
i--;
c = s.charAt(i);
} while (i >= 0 && (Character.isDigit(c) || c == '.'));
if (i == s.length()) {
return 0.0;
}
return Double.parseDouble(s.substring(i + 1));
}
public static void setHeightsFromDates(MutableTree tree) {
dr.evolution.util.Date mostRecent = null;
for (int i = 0; i < tree.getExternalNodeCount(); i++) {
Taxon taxon = tree.getNodeTaxon(tree.getExternalNode(i));
dr.evolution.util.Date date = (dr.evolution.util.Date) taxon.getAttribute("date");
if (date != null) {
if ((mostRecent == null) || date.after(mostRecent)) {
mostRecent = date;
}
}
}
TimeScale timeScale = new TimeScale(mostRecent.getUnits(), true, mostRecent.getAbsoluteTimeValue());
for (int i = 0; i < tree.getExternalNodeCount(); i++) {
NodeRef node = tree.getExternalNode(i);
Taxon taxon = tree.getNodeTaxon(node);
dr.evolution.util.Date date = (dr.evolution.util.Date) taxon.getAttribute("date");
if (date != null) {
double height = timeScale.convertTime(date.getTimeValue(), date);
tree.setNodeHeight(node, height);
} else {
tree.setNodeHeight(node, 0.0);
}
}
adjustInternalHeights(tree, tree.getRoot());
if (mostRecent != null) {
tree.setUnits(mostRecent.getUnits());
}
}
// **************************************************************
// Private static methods
// **************************************************************
private static void adjustInternalHeights(MutableTree tree, NodeRef node) {
if (!tree.isExternal(node)) {
// pre-order recursion
for (int i = 0; i < tree.getChildCount(node); i++) {
adjustInternalHeights(tree, tree.getChild(node, i));
}
}
NodeRef parent = tree.getParent(node);
if (parent != null) {
if (tree.getNodeHeight(parent) < tree.getNodeHeight(node)) {
tree.setNodeHeight(parent, tree.getNodeHeight(node));
}
}
}
}
