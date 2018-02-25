package dr.app.seqgen;
import dr.app.bss.Utils;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.io.NewickImporter;
import dr.evolution.sequence.Sequence;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evomodel.branchratemodel.BranchRateModel;
import dr.evomodel.branchratemodel.DefaultBranchRateModel;
import dr.evomodel.sitemodel.GammaSiteModel;
import dr.evomodel.sitemodel.SiteModel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.HKY;
import dr.evomodel.substmodel.SubstitutionEpochModel;
import dr.inference.model.Parameter;
import dr.math.MathUtils;
import dr.xml.*;
public class SequenceSimulator {
private static final boolean DEBUG = false;
protected int m_sequenceLength;
protected Tree m_tree;
protected SiteModel m_siteModel;
protected BranchRateModel m_branchRateModel;
int m_categoryCount;
int m_stateCount;
static boolean has_ancestralSequence = false;
protected Sequence ancestralSequence;
protected double[][] m_probabilities;
SequenceSimulator(Tree tree, SiteModel siteModel, BranchRateModel branchRateModel, int sequenceLength) {
m_tree = tree;
m_siteModel = siteModel;
m_branchRateModel = branchRateModel;
m_sequenceLength = sequenceLength;
m_stateCount = m_siteModel.getFrequencyModel().getDataType().getStateCount();
m_categoryCount = m_siteModel.getCategoryCount();
m_probabilities = new double[m_categoryCount][m_stateCount * m_stateCount];
} // c'tor
Sequence intArray2Sequence(int [] seq, NodeRef node) {
StringBuilder sSeq = new StringBuilder();
for (int i  = 0; i < m_sequenceLength; i++) {
sSeq.append(m_siteModel.getFrequencyModel().getDataType().getCode(seq[i]));
}
return new Sequence(m_tree.getNodeTaxon(node), sSeq.toString());
} // intArray2Sequence
void setAncestralSequence(Sequence seq) {
ancestralSequence = seq;
has_ancestralSequence = true;
}
int[] sequence2intArray(Sequence seq) {
if(seq.getLength() != m_sequenceLength) {
throw new RuntimeException("Ancestral sequence length has "
+ seq.getLength() + " characters " + "expecting "
+ m_sequenceLength + " characters");
}
int array[] = new int[m_sequenceLength];
for (int i = 0; i < m_sequenceLength; i++) {
array[i] = m_siteModel.getFrequencyModel().getDataType().getState(
seq.getChar(i));
}
return array;
}//END: sequence2intArray
public Alignment simulate() {
NodeRef root = m_tree.getRoot();
double[] categoryProbs = m_siteModel.getCategoryProportions();
int[] category = new int[m_sequenceLength];
for (int i = 0; i < m_sequenceLength; i++) {
category[i] = MathUtils.randomChoicePDF(categoryProbs);
}
int[] seq = new int[m_sequenceLength];
if (has_ancestralSequence) {
seq = sequence2intArray(ancestralSequence);
} else {
FrequencyModel frequencyModel = m_siteModel.getFrequencyModel();
for (int i = 0; i < m_sequenceLength; i++) {
seq[i] = MathUtils.randomChoicePDF(frequencyModel
.getFrequencies());
}
}
if (DEBUG) {
synchronized (this) {
System.out.println();
System.out.println("root Sequence:");
Utils.printArray(seq);
}
}
SimpleAlignment alignment = new SimpleAlignment();
alignment.setReportCountStatistics(false);
alignment.setDataType(m_siteModel.getFrequencyModel().getDataType());
traverse(root, seq, category, alignment);
return alignment;
} // END: simulate
void traverse(NodeRef node, int [] parentSequence, int [] category, SimpleAlignment alignment) {
if (DEBUG) {
System.out.println();
System.out.println("I'm at: " + node.toString());
System.out.println();
}
for (int iChild = 0; iChild < m_tree.getChildCount(node); iChild++) {
NodeRef child = m_tree.getChild(node, iChild);
for (int i = 0; i < m_categoryCount; i++) {
getTransitionProbabilities(m_tree, child, i, m_probabilities[i]);
}
if (DEBUG) {
System.out.println("Going to child " + iChild + ": " + child.toString());
System.out.println("Child finite transition probs matrix:");
Utils.print2DArray(m_probabilities, 4);
System.out.println();
}// END: if DEBUG
int [] seq = new int[m_sequenceLength];
double [] cProb = new double[m_stateCount];
for (int i  = 0; i < m_sequenceLength; i++) {
//System.out.println("seqChar " + parentSequence[i]);
System.arraycopy(m_probabilities[category[i]], parentSequence[i] * m_stateCount, cProb, 0, m_stateCount);
if (DEBUG) {
System.out.println("site:" + i);
System.out.println("site probs:");
Utils.printArray(cProb);
}// END: if DEBUG
seq[i] = MathUtils.randomChoicePDF(cProb);
}
if (DEBUG) {
//        	    seq = new int[]{1, 3, 2, 3, 0, 1, 0, 1, 0, 2, 2, 0, 1, 3, 3, 3, 0, 1, 2, 1, 3, 1, 1, 1, 1, 3, 0, 0, 3, 2, 3, 2, 3, 2, 1, 2, 1, 3, 2, 3, 3, 0, 2, 2, 3, 2, 3, 2, 3, 1, 2, 0, 2, 1, 3, 2, 3, 1, 1, 1, 1, 0, 2, 3, 1, 0, 2, 1, 2, 1, 3, 0, 0, 0, 0, 0, 2, 0, 2, 3, 1, 0, 1, 3, 0, 2, 1, 2, 1, 3, 0, 0, 3, 2, 2, 0, 1, 0, 0, 3  };
System.out.println("Simulated sequence:");
Utils.printArray(seq);
}
if (m_tree.getChildCount(child) == 0) {
if (DEBUG) {
System.out.println("Simulated sequence (translated):");
System.out.println(intArray2Sequence(seq, child).getSequenceString());
}
alignment.addSequence(intArray2Sequence(seq, child));
}
traverse(m_tree.getChild(node, iChild), seq, category, alignment);
}//END: child nodes loop
} // traverse
void getTransitionProbabilities(Tree tree, NodeRef node, int rateCategory, double[] probs) {
NodeRef parent = tree.getParent(node);
final double branchRate = m_branchRateModel.getBranchRate(tree, node);
// Get the operational time of the branch
final double branchTime = branchRate * (tree.getNodeHeight(parent) - tree.getNodeHeight(node));
if (branchTime < 0.0) {
throw new RuntimeException("Negative branch length: " + branchTime);
}
double branchLength = m_siteModel.getRateForCategory(rateCategory) * branchTime;
if (m_siteModel.getSubstitutionModel() instanceof SubstitutionEpochModel) {
((SubstitutionEpochModel)m_siteModel.getSubstitutionModel()).getTransitionProbabilities(tree.getNodeHeight(node),
tree.getNodeHeight(parent),branchLength, probs);
return;
}
m_siteModel.getSubstitutionModel().getTransitionProbabilities(branchLength, probs);
} // getTransitionProbabilities
public static void printUsageAndExit() {
System.err.println("Usage: java " + SequenceSimulator.class.getName() + " <nr of instantiations>");
System.err.println("where <nr of instantiations> is the number of instantiations to be replciated");
System.exit(0);
} // printUsageAndExit
public static final String SEQUENCE_SIMULATOR = "sequenceSimulator";
public static final String SITE_MODEL = SiteModel.SITE_MODEL;
public static final String TREE = "tree";
//    public static final String BRANCH_RATE_MODEL = "branchRateModel";
public static final String REPLICATIONS = "replications";
public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {
public String getParserName() {
return SEQUENCE_SIMULATOR;
}
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
int nReplications = xo.getIntegerAttribute(REPLICATIONS);
Tree tree = (Tree) xo.getChild(Tree.class);
SiteModel siteModel = (SiteModel) xo.getChild(SiteModel.class);
BranchRateModel rateModel = (BranchRateModel)xo.getChild(BranchRateModel.class);
Sequence ancestralSequence = (Sequence)xo.getChild(Sequence.class);
if (rateModel == null)
rateModel = new DefaultBranchRateModel();
SequenceSimulator s = new SequenceSimulator(tree, siteModel, rateModel, nReplications);
if(ancestralSequence != null) {
s.setAncestralSequence(ancestralSequence);
}
return s.simulate();
}
//************************************************************************
// AbstractXMLObjectParser implementation
//************************************************************************
public String getParserDescription() {
return "A SequenceSimulator that generates random sequences for a given tree, sitemodel and branch rate model";
}
public Class getReturnType() {
return Alignment.class;
}
public XMLSyntaxRule[] getSyntaxRules() {
return rules;
}
private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{
new ElementRule(Tree.class),
new ElementRule(SiteModel.class),
new ElementRule(BranchRateModel.class, true),
new ElementRule(Sequence.class, true),
AttributeRule.newIntegerRule(REPLICATIONS)
};
};
static SiteModel getDefaultSiteModel() {
Parameter kappa = new Parameter.Default(1, 2);
Parameter freqs = new Parameter.Default(new double[]{0.25, 0.25, 0.25, 0.25});
FrequencyModel f = new FrequencyModel(Nucleotides.INSTANCE, freqs);
HKY hky = new HKY(kappa, f);
return new GammaSiteModel(hky);
} // getDefaultSiteModel
public static void main(String [] args) {
try {
int nReplications = 10;
// create tree
NewickImporter importer = new NewickImporter("((A:1.0,B:1.0)AB:1.0,(C:1.0,D:1.0)CD:1.0)ABCD;");
Tree tree =  importer.importTree(null);
// create site model
SiteModel siteModel = getDefaultSiteModel();
// create branch rate model
BranchRateModel branchRateModel = new DefaultBranchRateModel();
// feed to sequence simulator and generate leaves
SequenceSimulator treeSimulator = new SequenceSimulator(tree, siteModel, branchRateModel, nReplications);
Sequence ancestralSequence = new Sequence();
ancestralSequence.appendSequenceString("TCAGGTCAAG");
treeSimulator.setAncestralSequence(ancestralSequence);
System.out.println(treeSimulator.simulate().toString());
} catch (Exception e) {
e.printStackTrace();
}//END: try-catch block
} // END: main
//    public static void main(String [] args) {
//    	try {
//    		if (args.length == 0) {
//    			printUsageAndExit();
//    		}
//    		int nReplications = Integer.parseInt(args[0]);
//
//    		// create tree
//    		NewickImporter importer = new NewickImporter("((A:1.0,B:1.0)AB:1.0,(C:1.0,D:1.0)CD:1.0)ABCD;");
//    		Tree tree =  importer.importTree(null);
//    		// create site model
//    		SiteModel siteModel = getDefaultSiteModel();
//    		// create branch rate model
//    		BranchRateModel branchRateModel = new DefaultBranchRateModel();
//
//    		// feed to sequence simulator and generate leaves
//    		SequenceSimulator treeSimulator = new SequenceSimulator(tree, siteModel, branchRateModel, nReplications);
//    		System.err.println(treeSimulator.simulate().toString());
//    		treeSimulator.simulate();
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//	} // main
} // class SequenceSimulator