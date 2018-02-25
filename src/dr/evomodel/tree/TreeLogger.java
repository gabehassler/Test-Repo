package dr.evomodel.tree;
import dr.app.tools.NexusExporter;
import dr.evolution.tree.*;
import dr.inference.loggers.LogFormatter;
import dr.inference.loggers.MCLogger;
import java.text.NumberFormat;
import java.util.*;
public class TreeLogger extends MCLogger {
private Tree tree;
private BranchRates branchRates = null;
private TreeAttributeProvider[] treeAttributeProviders;
private TreeTraitProvider[] treeTraitProviders;
private boolean nexusFormat = false;
public boolean usingRates = false;
public boolean substitutions = false;
private final Map<String, Integer> idMap = new HashMap<String, Integer>();
private final List<String> taxaIds = new ArrayList<String>();
private boolean mapNames = true;
boolean normaliseMeanRate = false;*/
private NumberFormat format;
private LogUpon condition = null;
public interface LogUpon {
boolean logNow(long state);
}
public TreeLogger(Tree tree, LogFormatter formatter, int logEvery, boolean nexusFormat,
boolean sortTranslationTable, boolean mapNames) {
this(tree, null, null, null, formatter, logEvery, nexusFormat, sortTranslationTable, mapNames, null, null/*, Double.NaN*/);
}
public TreeLogger(Tree tree, LogFormatter formatter, int logEvery, boolean nexusFormat,
boolean sortTranslationTable, boolean mapNames, NumberFormat format) {
this(tree, null, null, null, formatter, logEvery, nexusFormat, sortTranslationTable, mapNames, format, null/*, Double.NaN*/);
}
public TreeLogger(Tree tree, BranchRates branchRates,
TreeAttributeProvider[] treeAttributeProviders,
TreeTraitProvider[] treeTraitProviders,
LogFormatter formatter, int logEvery, boolean nexusFormat,
boolean sortTranslationTable, boolean mapNames, NumberFormat format,
TreeLogger.LogUpon condition) {
super(formatter, logEvery, false);
this.condition = condition;
if(!Double.isNaN(normaliseMeanRateTo)) {
normaliseMeanRate = true;
}*/
this.nexusFormat = nexusFormat;
// if not NEXUS, can't map names
this.mapNames = mapNames && nexusFormat;
this.branchRates = branchRates;
this.treeAttributeProviders = treeAttributeProviders;
this.treeTraitProviders = treeTraitProviders;
if (this.branchRates != null) {
this.substitutions = true;
}
this.tree = tree;
for (int i = 0; i < tree.getTaxonCount(); i++) {
taxaIds.add(tree.getTaxon(i).getId());
}
if (sortTranslationTable) {
Collections.sort(taxaIds);
}
int k = 1;
for (String taxaId : taxaIds) {
idMap.put(taxaId, k);
k += 1;
}
this.format = format;
}
public void startLogging() {
if (nexusFormat) {
int taxonCount = tree.getTaxonCount();
logLine("#NEXUS");
logLine("");
logLine("Begin taxa;");
logLine("\tDimensions ntax=" + taxonCount + ";");
logLine("\tTaxlabels");
for (String taxaId : taxaIds) {
logLine("\t\t" + cleanTaxonName(taxaId));
}
logLine("\t\t;");
logLine("End;");
logLine("");
logLine("Begin trees;");
if (mapNames) {
// This is needed if the trees use numerical taxon labels
logLine("\tTranslate");
int k = 1;
for (String taxaId : taxaIds) {
if (k < taxonCount) {
logLine("\t\t" + k + " " + cleanTaxonName(taxaId) + ",");
} else {
logLine("\t\t" + k + " " + cleanTaxonName(taxaId));
}
k += 1;
}
logLine("\t\t;");
}
}
}
private String cleanTaxonName(String taxaId) {
if (taxaId.matches(NexusExporter.SPECIAL_CHARACTERS_REGEX)) {
if (taxaId.contains("\'")) {
if (taxaId.contains("\"")) {
throw new RuntimeException("Illegal taxon name - contains both single and double quotes");
}
return "\"" + taxaId + "\"";
}
return "\'" + taxaId + "\'";
}
return taxaId;
}
public void log(long state) {
NormaliseMeanTreeRate.analyze(tree, normaliseMeanRateTo);
}*/
final boolean doIt = condition != null ? condition.logNow(state) :
(logEvery < 0 || ((state % logEvery) == 0));
if ( doIt ) {
StringBuffer buffer = new StringBuffer("tree STATE_");
buffer.append(state);
if (treeAttributeProviders != null) {
boolean hasAttribute = false;
for (TreeAttributeProvider tap : treeAttributeProviders) {
String[] attributeLabel = tap.getTreeAttributeLabel();
String[] attributeValue = tap.getAttributeForTree(tree);
for (int i = 0; i < attributeLabel.length; i++) {
if (!hasAttribute) {
buffer.append(" [&");
hasAttribute = true;
} else {
buffer.append(",");
}
buffer.append(attributeLabel[i]);
buffer.append("=");
buffer.append(attributeValue[i]);
}
}
if (hasAttribute) {
buffer.append("]");
}
}
buffer.append(" = [&R] ");
if (substitutions) {
Tree.Utils.newick(tree, tree.getRoot(), false, Tree.BranchLengthType.LENGTHS_AS_SUBSTITUTIONS,
format, branchRates, treeTraitProviders, idMap, buffer);
} else {
Tree.Utils.newick(tree, tree.getRoot(), !mapNames, Tree.BranchLengthType.LENGTHS_AS_TIME,
format, null, treeTraitProviders, idMap, buffer);
}
buffer.append(";");
logLine(buffer.toString());
}
}
public void stopLogging() {
logLine("End;");
super.stopLogging();
}
public Tree getTree() {
return tree;
}
public void setTree(Tree tree) {
this.tree = tree;
}
}