package dr.app.beauti.generator;
import dr.app.beauti.components.ComponentFactory;
import dr.app.beauti.options.*;
import dr.app.beauti.types.PriorType;
import dr.app.beauti.types.TreePriorType;
import dr.app.beauti.util.XMLWriter;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxa;
import dr.evomodelxml.coalescent.CoalescentSimulatorParser;
import dr.evomodelxml.coalescent.OldCoalescentSimulatorParser;
import dr.evomodelxml.coalescent.ConstantPopulationModelParser;
import dr.evomodelxml.coalescent.ExponentialGrowthModelParser;
import dr.evoxml.*;
import dr.util.Attribute;
import dr.xml.XMLParser;
import java.util.ArrayList;
import java.util.List;
public class InitialTreeGenerator extends Generator {
final static public String STARTING_TREE = "startingTree";
public InitialTreeGenerator(BeautiOptions options, ComponentFactory[] components) {
super(options, components);
}
public void writeStartingTree(PartitionTreeModel model, XMLWriter writer) {
setModelPrefix(model.getPrefix()); // only has prefix, if (options.getPartitionTreeModels().size() > 1)
switch (model.getStartingTreeType()) {
case USER:
case UPGMA:
Parameter rootHeight = model.getParameter("treeModel.rootHeight");
// generate a rescaled starting tree
writer.writeComment("Construct a starting tree that is compatible with specified clade heights");
Attribute[] attributes = (rootHeight.priorType != PriorType.NONE_TREE_PRIOR ?
new Attribute[] {
new Attribute.Default<String>(XMLParser.ID, modelPrefix + STARTING_TREE),
new Attribute.Default<String>(RescaledTreeParser.HEIGHT, "" + rootHeight.initial)
} :
new Attribute[] {
new Attribute.Default<String>(XMLParser.ID, modelPrefix + STARTING_TREE)
});
writer.writeOpenTag(RescaledTreeParser.RESCALED_TREE, attributes);
writeSourceTree(model, writer);
if (options.taxonSets != null && options.taxonSets.size() > 0 && !options.useStarBEAST) {
for (Taxa taxa : options.taxonSets) {
Double height = options.taxonSetsHeights.get(taxa);
if (height != null) {
writer.writeOpenTag(RescaledTreeParser.CLADE, new Attribute.Default<String>(RescaledTreeParser.HEIGHT, height.toString()));
writer.writeTag("taxa", new Attribute.Default<String>(XMLParser.IDREF, taxa.getId()), true);
writer.writeCloseTag(RescaledTreeParser.CLADE);
} else if (options.taxonSetsMono.get(taxa)) {
// if monophyly is enforced then placing this clade element here will force BEAST to check
// the clade exists in the tree.
writer.writeOpenTag(RescaledTreeParser.CLADE);
writer.writeTag("taxa", new Attribute.Default<String>(XMLParser.IDREF, taxa.getId()), true);
writer.writeCloseTag(RescaledTreeParser.CLADE);
}
}
}
writer.writeCloseTag(RescaledTreeParser.RESCALED_TREE);
break;
case RANDOM:
// generate a coalescent tree
String simulatorId = modelPrefix + STARTING_TREE;
String taxaId = TaxaParser.TAXA;
AbstractPartitionData partition = options.getDataPartitions(model).get(0);
if (!options.hasIdenticalTaxa()) {
taxaId = partition.getPartitionTreeModel().getPrefix() + TaxaParser.TAXA;
}
if (partition instanceof PartitionPattern && ((PartitionPattern) partition).getPatterns().hasMask()) {
taxaId = partition.getPrefix() + TaxaParser.TAXA;
}
writer.writeComment("Generate a random starting tree under the coalescent process");
if (options.taxonSets != null && options.taxonSets.size() > 0 && !options.useStarBEAST) { // need !options.useStarBEAST,
writeSubTree(simulatorId, taxaId, options.taxonList, model, writer);
} else {
writer.writeOpenTag(
CoalescentSimulatorParser.COALESCENT_SIMULATOR,
new Attribute[]{
new Attribute.Default<String>(XMLParser.ID, simulatorId)
}
);
writeTaxaRef(taxaId, model, writer);
writeInitialDemoModelRef(model, writer);
writer.writeCloseTag(CoalescentSimulatorParser.COALESCENT_SIMULATOR);
}
break;
default:
throw new IllegalArgumentException("Unknown StartingTreeType");
}
}
public void writeSourceTree(PartitionTreeModel model, XMLWriter writer) {
switch (model.getStartingTreeType()) {
case USER:
if (model.isNewick()) {
writeNewickTree(model.getUserStartingTree(), writer);
} else {
writeSimpleTree(model.getUserStartingTree(), writer);
}
break;
case UPGMA:
// generate a upgma starting tree
writer.writeComment("Construct a rough-and-ready UPGMA tree as an starting tree");
writer.writeOpenTag(UPGMATreeParser.UPGMA_TREE);
writer.writeOpenTag(
DistanceMatrixParser.DISTANCE_MATRIX,
new Attribute[]{
new Attribute.Default<String>(DistanceMatrixParser.CORRECTION, "JC")
}
);
writer.writeOpenTag(SitePatternsParser.PATTERNS);
writer.writeComment("To generate UPGMA starting tree, only use the 1st aligment, "
+ "which may be 1 of many aligments using this tree.");
writer.writeIDref(AlignmentParser.ALIGNMENT, options.getDataPartitions(model).get(0).getTaxonList().getId());
// alignment has no gene prefix
writer.writeCloseTag(SitePatternsParser.PATTERNS);
writer.writeCloseTag(DistanceMatrixParser.DISTANCE_MATRIX);
writer.writeCloseTag(UPGMATreeParser.UPGMA_TREE);
break;
case RANDOM:
throw new IllegalArgumentException("Shouldn't be here");
default:
throw new IllegalArgumentException("Unknown StartingTreeType");
}
}
private void writeTaxaRef(String taxaId, PartitionTreeModel model, XMLWriter writer) {
Attribute[] taxaAttribute = {new Attribute.Default<String>(XMLParser.IDREF, taxaId)};
if (options.taxonSets != null && options.taxonSets.size() > 0 && !options.useStarBEAST) { // need !options.useStarBEAST,
// *BEAST case is in STARBEASTGenerator.writeStartingTreeForCalibration(XMLWriter writer)
writer.writeOpenTag(OldCoalescentSimulatorParser.CONSTRAINED_TAXA);
writer.writeTag(TaxaParser.TAXA, taxaAttribute, true);
for (Taxa taxa : options.taxonSets) {
if (options.taxonSetsTreeModel.get(taxa).equals(model)) {
Parameter statistic = options.getStatistic(taxa);
Attribute mono = new Attribute.Default<Boolean>(
OldCoalescentSimulatorParser.IS_MONOPHYLETIC, options.taxonSetsMono.get(taxa));
writer.writeOpenTag(OldCoalescentSimulatorParser.TMRCA_CONSTRAINT, mono);
writer.writeIDref(TaxaParser.TAXA, taxa.getId());
if (model.getPartitionTreePrior().getNodeHeightPrior() == TreePriorType.YULE_CALIBRATION
&& statistic.priorType == PriorType.UNIFORM_PRIOR) {
writeDistribution(statistic, false, writer);
}
writer.writeCloseTag(OldCoalescentSimulatorParser.TMRCA_CONSTRAINT);
}
}
writer.writeCloseTag(OldCoalescentSimulatorParser.CONSTRAINED_TAXA);
} else {
writer.writeTag(TaxaParser.TAXA, taxaAttribute, true);
}
}
private void writeSubTree(String treeId, String taxaId, Taxa taxa, PartitionTreeModel model, XMLWriter writer) {
Double height = options.taxonSetsHeights.get(taxa);
if (height == null) {
height = Double.NaN;
}
Attribute[] attributes = new Attribute[] {};
if (treeId != null) {
if (Double.isNaN(height)) {
attributes = new Attribute[] {
new Attribute.Default<String>(XMLParser.ID, treeId)
};
} else {
attributes = new Attribute[] {
new Attribute.Default<String>(XMLParser.ID, treeId),
new Attribute.Default<String>(CoalescentSimulatorParser.HEIGHT, "" + height)
};
}
} else {
if (!Double.isNaN(height)) {
attributes = new Attribute[] {
new Attribute.Default<String>(CoalescentSimulatorParser.HEIGHT, "" + height)
};
}
}
// construct a subtree
writer.writeOpenTag(
CoalescentSimulatorParser.COALESCENT_SIMULATOR,
attributes
);
List<Taxa> subsets = new ArrayList<Taxa>();
//        Taxa remainingTaxa = new Taxa(taxa);
for (Taxa taxa2 : options.taxonSets) {
boolean sameTree = model.equals(options.taxonSetsTreeModel.get(taxa2));
boolean isMono = options.taxonSetsMono.get(taxa2);
boolean hasHeight = options.taxonSetsHeights.get(taxa2) != null;
boolean isSubset = taxa.containsAll(taxa2);
if (sameTree && (isMono || hasHeight) && taxa2 != taxa && isSubset) {
subsets.add(taxa2);
}
}
List<Taxa> toRemove = new ArrayList<Taxa>();
for (Taxa taxa3 : subsets) {
boolean isSubSubSet = false;
for (Taxa taxa4 : subsets) {
if (!taxa4.equals(taxa3) && taxa4.containsAll(taxa3)) {
isSubSubSet = true;
}
}
if (isSubSubSet) {
toRemove.add(taxa3);
}
}
subsets.removeAll(toRemove);
for (Taxa taxa5 : subsets) {
//            remainingTaxa.removeTaxa(taxa5);
writeSubTree(null, null, taxa5, model, writer);
}
if (taxaId == null) {
writer.writeIDref(TaxaParser.TAXA, taxa.getId());
} else {
writer.writeIDref(TaxaParser.TAXA, taxaId);
}
writeInitialDemoModelRef(model, writer);
writer.writeCloseTag(CoalescentSimulatorParser.COALESCENT_SIMULATOR);
}
private void writeInitialDemoModelRef(PartitionTreeModel model, XMLWriter writer) {
PartitionTreePrior prior = model.getPartitionTreePrior();
if (prior.getNodeHeightPrior() == TreePriorType.CONSTANT || options.useStarBEAST) {
writer.writeIDref(ConstantPopulationModelParser.CONSTANT_POPULATION_MODEL, prior.getPrefix() + "constant");
} else if (prior.getNodeHeightPrior() == TreePriorType.EXPONENTIAL) {
writer.writeIDref(ExponentialGrowthModelParser.EXPONENTIAL_GROWTH_MODEL, prior.getPrefix() + "exponential");
} else {
writer.writeIDref(ConstantPopulationModelParser.CONSTANT_POPULATION_MODEL, prior.getPrefix() + "initialDemo");
}
}
private void writeNewickTree (Tree tree, XMLWriter writer) {
writer.writeComment("The user-specified starting tree in a newick tree format.");
writer.writeOpenTag(
NewickParser.NEWICK,
new Attribute[]{
//                        new Attribute.Default<String>(XMLParser.ID, modelPrefix + STARTING_TREE),
//                        new Attribute.Default<String>(DateParser.UNITS, options.datesUnits.getAttribute()),
new Attribute.Default<Boolean>(SimpleTreeParser.USING_DATES, options.clockModelOptions.isTipCalibrated())
}
);
writer.writeText(Tree.Utils.newick(tree));
writer.writeCloseTag(NewickParser.NEWICK);
}
//    private void writeNewickNode(Tree tree, NodeRef node, XMLWriter writer) {
//        if (tree.getChildCount(node) > 0)
//            writer.writeText("(");
//        if (tree.getChildCount(node) == 0)
//            writer.writeText(tree.getNodeTaxon(node).getId() + " : " + tree.getBranchLength(node));
//
//        for (int i = 0; i < tree.getChildCount(node); i++) {
//            if (i > 0) writer.writeText(", ");
//            writeNewickNode(tree, tree.getChild(node, i), writer);
//
//        }
//        if (tree.getChildCount(node) > 0)
//            writer.writeText(")");
//    }
private void writeSimpleTree(Tree tree, XMLWriter writer) {
writer.writeComment("The user-specified starting tree in a simple tree format.");
writer.writeOpenTag(
SimpleTreeParser.SIMPLE_TREE,
new Attribute[]{
//                        new Attribute.Default<String>(XMLParser.ID, modelPrefix + STARTING_TREE),
//                        new Attribute.Default<String>(DateParser.UNITS, options.datesUnits.getAttribute()),
new Attribute.Default<Object>(DateParser.UNITS, options.units.toString()),
new Attribute.Default<Boolean>(SimpleTreeParser.USING_DATES, options.clockModelOptions.isTipCalibrated())
}
);
writeSimpleNode(tree, tree.getRoot(), writer);
writer.writeCloseTag(SimpleTreeParser.SIMPLE_TREE);
}
private void writeSimpleNode(Tree tree, NodeRef node, XMLWriter writer) {
writer.writeOpenTag(
SimpleNodeParser.NODE,
new Attribute[]{new Attribute.Default<Double>(SimpleNodeParser.HEIGHT, tree.getNodeHeight(node))}
);
if (tree.getChildCount(node) == 0) {
writer.writeIDref(TaxonParser.TAXON, tree.getNodeTaxon(node).getId());
}
for (int i = 0; i < tree.getChildCount(node); i++) {
writeSimpleNode(tree, tree.getChild(node, i), writer);
}
writer.writeCloseTag(SimpleNodeParser.NODE);
}
}