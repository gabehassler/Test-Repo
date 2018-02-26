package dr.evolution.alignment;
import dr.app.beauti.util.NexusApplicationImporter;
import dr.evolution.sequence.Sequence;
public class CharSetAlignment extends SimpleAlignment {
    public CharSetAlignment(NexusApplicationImporter.CharSet charset, Alignment parentAlignment) {
        setId(charset.getName());
        for (int i = 0; i < parentAlignment.getSequenceCount(); i++) {
            Sequence sequence = parentAlignment.getSequence(i);
            String sequenceString = parentAlignment.getAlignedSequenceString(i);
            String filteredSequence = filter(charset, sequenceString);
            addSequence(new Sequence(sequence.getTaxon(), filteredSequence));
        }
        setDataType(parentAlignment.getDataType());
    }
    private String filter(NexusApplicationImporter.CharSet charset, String sequenceString) {
        StringBuilder filtered = new StringBuilder();
        for (NexusApplicationImporter.CharSetBlock block : charset.getBlocks()) {
            int toSite = block.getToSite();
            if (toSite <= 0) {
                toSite = sequenceString.length();
            }
            for (int i = block.getFromSite(); i <= toSite; i += block.getEvery()) {
                // the -1 comes from the fact that charsets are indexed from 1 whereas strings are indexed from 0
                filtered.append(sequenceString.charAt(i - 1));
            }
        }
        return filtered.toString();
    }
}
