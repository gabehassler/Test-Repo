package dr.app.beagle.tools.parsers;
import dr.app.beagle.tools.BeagleSequenceSimulator;
import dr.app.beagle.tools.Partition;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.SimpleAlignment;
import dr.evolution.datatype.Codons;
import dr.evolution.datatype.Nucleotides;
import dr.xml.*;
import java.util.ArrayList;
import java.util.logging.Logger;
public class BeagleSequenceSimulatorParser extends AbstractXMLObjectParser {
public static final String BEAGLE_SEQUENCE_SIMULATOR = "beagleSequenceSimulator";
public static final String PARALLEL = "parallel";
public static final String OUTPUT_ANCESTRAL_SEQUENCES = "outputAncestralSequences";
public static final String OUTPUT = "output";
public String getParserName() {
return BEAGLE_SEQUENCE_SIMULATOR;
}
@Override
public String getParserDescription() {
return "Beagle sequence simulator";
}
@Override
public Class<Alignment> getReturnType() {
return Alignment.class;
}
@Override
public XMLSyntaxRule[] getSyntaxRules() {
return new XMLSyntaxRule[]{
AttributeRule.newBooleanRule(PARALLEL, true, "Whether to use multiple Beagle instances for simulation, default is false (sequential execution)."),
new StringAttributeRule(OUTPUT, "Possible output formats",
SimpleAlignment.OutputType.values(), //TODO: this should ignore upper/lower cas
false),
new ElementRule(Partition.class, 1, Integer.MAX_VALUE)
};
}// END: getSyntaxRules
@Override
public Object parseXMLObject(XMLObject xo) throws XMLParseException {
String msg = "";
boolean parallel = false;
boolean outputAncestralSequences = false;
if (xo.hasAttribute(PARALLEL)) {
parallel = xo.getBooleanAttribute(PARALLEL);
}
if (xo.hasAttribute(OUTPUT_ANCESTRAL_SEQUENCES)) {
outputAncestralSequences = xo.getBooleanAttribute(OUTPUT_ANCESTRAL_SEQUENCES);
}
SimpleAlignment.OutputType output = SimpleAlignment.OutputType.FASTA;
if (xo.hasAttribute(OUTPUT)) {
output = SimpleAlignment.OutputType.parseFromString(
xo.getStringAttribute(OUTPUT));
}
int siteCount = 0;
int to = 0;
for (int i = 0; i < xo.getChildCount(); i++) {
Partition partition = (Partition) xo.getChild(i);
to = partition.to + 1;
if (to > siteCount) {
siteCount = to;
}
}// END: partitions loop
ArrayList<Partition> partitionsList = new ArrayList<Partition>();
for (int i = 0; i < xo.getChildCount(); i++) {
Partition partition = (Partition) xo.getChild(i);
if (partition.from > siteCount) {
throw new XMLParseException(
"Illegal 'from' attribute in " + PartitionParser.PARTITION + " element");
}
if (partition.to > siteCount) {
throw new XMLParseException(
"Illegal 'to' attribute in " + PartitionParser.PARTITION + " element");
}
if (partition.to == -1) {
partition.to = siteCount - 1;
}
if (partition.getRootSequence() != null) {
//            	TODO: what about 'every'?
int partitionSiteCount = (partition.to - partition.from) +1;
//            	System.out.println("SCRAAAAAM:" + partitionSiteCount);
if (partition.getRootSequence().getLength() != 3 * partitionSiteCount && partition.getFreqModel().getDataType() instanceof Codons) {
throw new RuntimeException("Root codon sequence " + "for partition "+ (i+1) +" has "
+ partition.getRootSequence().getLength() + " characters "
+ "expecting " + 3 * partitionSiteCount + " characters");
} else if (partition.getRootSequence().getLength() != partitionSiteCount && partition.getFreqModel().getDataType() instanceof Nucleotides) {
throw new RuntimeException("Root nuleotide sequence "+ "for partition "+ (i+1) +" has "
+ partition.getRootSequence().getLength() + " characters "
+ "expecting " + partitionSiteCount + " characters");
}// END: dataType check
//                System.exit(-1);
}// END: ancestralSequence check
partitionsList.add(partition);
}// END: partitions loop
msg += "\n\t" + siteCount + ((siteCount > 1) ? " replications " : " replication");
if (msg.length() > 0) {
Logger.getLogger("dr.app.beagle.tools").info("Using Beagle Sequence Simulator: " + msg);
}
BeagleSequenceSimulator s = new BeagleSequenceSimulator(partitionsList);
SimpleAlignment alignment = s.simulate(parallel, outputAncestralSequences);
alignment.setOutputType(output);
return alignment;
}// END: parseXMLObject
}// END: class
