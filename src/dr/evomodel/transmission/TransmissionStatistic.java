package dr.evomodel.transmission;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.evomodel.tree.TreeStatistic;
import dr.inference.model.BooleanStatistic;
import dr.inference.model.Statistic;
import dr.xml.*;
import java.util.HashSet;
import java.util.Set;
public class TransmissionStatistic extends BooleanStatistic implements TreeStatistic {
// PUBLIC STUFF
public static final String TRANSMISSION_STATISTIC = "transmissionStatistic";
public TransmissionStatistic(String name, TransmissionHistoryModel transmissionHistoryModel, Tree virusTree) {
super(name);
this.transmissionHistoryModel = transmissionHistoryModel;
this.virusTree = virusTree;
setupHosts();
}
public TransmissionStatistic(String name, Tree hostTree, Tree virusTree) {
super(name);
this.hostTree = hostTree;
this.virusTree = virusTree;
setupHosts();
}
private void setupHosts() {
if (transmissionHistoryModel != null) {
hostCount = transmissionHistoryModel.getHostCount();
} else {
hostCount = hostTree.getTaxonCount();
}
donorHost = new int[hostCount];
donorHost[0] = -1;
transmissionTime = new double[hostCount];
transmissionTime[0] = java.lang.Double.POSITIVE_INFINITY;
if (transmissionHistoryModel != null) {
for (int i = 0; i < transmissionHistoryModel.getTransmissionEventCount(); i++)
{
TransmissionHistoryModel.TransmissionEvent event = transmissionHistoryModel.getTransmissionEvent(i);
int host1 = transmissionHistoryModel.getHostIndex(event.getDonor());
int host2 = transmissionHistoryModel.getHostIndex(event.getRecipient());
donorHost[host2] = host1;
transmissionTime[host2] = event.getTransmissionTime();
}
} else {
setupHostsTree(hostTree.getRoot());
}
}
private int setupHostsTree(NodeRef node) {
int host;
if (hostTree.isExternal(node)) {
host = node.getNumber();
} else {
// This traversal assumes that the first child is the donor
// and the second is the recipient
int host1 = setupHostsTree(hostTree.getChild(node, 0));
int host2 = setupHostsTree(hostTree.getChild(node, 1));
donorHost[host2] = host1;
transmissionTime[host2] = hostTree.getNodeHeight(node);
host = host1;
}
return host;
}
public void setTree(Tree tree) {
this.virusTree = tree;
}
public Tree getTree() {
return virusTree;
}
public String getDimensionName(int dim) {
String recipient = transmissionHistoryModel.getHost(dim).getId();
String donor = (donorHost[dim] == -1 ? "" : transmissionHistoryModel.getHost(donorHost[dim]).getId() + "->");
return "transmission(" + donor + recipient + ")";
}
public int getDimension() {
return hostCount;
}
public boolean getBoolean(int dim) {
Set<Integer> incompatibleSet = new HashSet<Integer>();
setupHosts();
isCompatible(virusTree.getRoot(), incompatibleSet);
return !incompatibleSet.contains(dim);
}
private int isCompatible(NodeRef node, Set<Integer> incompatibleSet) {
double height = virusTree.getNodeHeight(node);
int host;
if (virusTree.isExternal(node)) {
Taxon hostTaxon = (Taxon) virusTree.getTaxonAttribute(node.getNumber(), "host");
if (transmissionHistoryModel != null) {
host = transmissionHistoryModel.getHostIndex(hostTaxon);
} else {
host = hostTree.getTaxonIndex(hostTaxon);
}
if (host != -1 && height > transmissionTime[host]) {
// This means that the sequence was sampled
// before the host was infected so we should probably flag
// this as an error before we get to this point...
throw new RuntimeException("Sequence " + virusTree.getNodeTaxon(node) + ", was sampled ("+height+") before host, " + hostTaxon + ", was infected ("+transmissionTime[host]+")");
}
} else {
// Tree should be bifurcating...
int host1 = isCompatible(virusTree.getChild(node, 0), incompatibleSet);
int host2 = isCompatible(virusTree.getChild(node, 1), incompatibleSet);
if (host1 == host2) {
host = host1;
while (height > transmissionTime[host]) {
host = donorHost[host];
}
} else {
while (height > transmissionTime[host1]) {
host1 = donorHost[host1];
}
while (height > transmissionTime[host2]) {
host2 = donorHost[host2];
}
if (host1 != host2) {
if (transmissionTime[host1] < transmissionTime[host2]) {
incompatibleSet.add(host1);
host = host2;
} else {
incompatibleSet.add(host2);
host = host1;
}
} else {
host = host1;
}
}
}
return host;
}
// ****************************************************************
// Private and protected stuff
// ****************************************************************
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() {
return TRANSMISSION_STATISTIC;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
String name = xo.getStringAttribute("name");
Tree virusTree = (Tree) xo.getElementFirstChild("parasiteTree");
if (xo.getChild(TransmissionHistoryModel.class) != null) {
TransmissionHistoryModel history = (TransmissionHistoryModel) xo.getChild(TransmissionHistoryModel.class);
return new TransmissionStatistic(name, history, virusTree);
} else {
Tree hostTree = (Tree) xo.getElementFirstChild("hostTree");
return new TransmissionStatistic(name, hostTree, virusTree);
}
}
public String getParserDescription() {
return "A statistic that returns true if the given parasite tree is compatible with the host tree.";
}
public Class getReturnType() {
return Statistic.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
new StringAttributeRule("name", "A name for this statistic for the purpose of logging"),
new XORRule(
new ElementRule("hostTree",
new XMLSyntaxRule[]{new ElementRule(Tree.class)}),
new ElementRule(TransmissionHistoryModel.class,
"This describes the transmission history of the patients.")
),
new ElementRule("parasiteTree",
new XMLSyntaxRule[]{new ElementRule(Tree.class)})
};
};
private Tree hostTree = null;
private TransmissionHistoryModel transmissionHistoryModel = null;
private Tree virusTree = null;
private int hostCount;
private int[] donorHost;
private double[] transmissionTime;
}