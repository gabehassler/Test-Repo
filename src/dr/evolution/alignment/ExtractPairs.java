package dr.evolution.alignment;
import dr.evolution.datatype.DataType;
import dr.evolution.sequence.Sequence;
public class ExtractPairs {
    Alignment alignment;
    public ExtractPairs(Alignment alignment) {
        this.alignment = alignment;
    }
    public Alignment getPairAlignment(int x, int y) {
        SimpleAlignment pairAlignment = new SimpleAlignment();
        StringBuffer sequence0 = new StringBuffer();
        StringBuffer sequence1 = new StringBuffer();
        DataType dataType = alignment.getDataType();
        int stateCount = dataType.getStateCount();
        for (int i = 0; i < alignment.getSiteCount(); i++) {
            int s0 = alignment.getState(x,i);
            int s1 = alignment.getState(y,i);
            char c0 = dataType.getChar(s0);
            char c1 = dataType.getChar(s1);
            if (s0 < stateCount || s1 < stateCount) {
                sequence0.append(c0);
                sequence1.append(c1);
            }
        }
        // trim hanging ends on left
        int left = 0;
        while (
            (dataType.getState(sequence0.charAt(left)) >= stateCount) ||
            (dataType.getState(sequence1.charAt(left)) >= stateCount)) {
            left += 1;
        }
        // trim hanging ends on right
        int right = sequence0.length()-1;
        while (
            (dataType.getState(sequence0.charAt(right)) >= stateCount) ||
            (dataType.getState(sequence1.charAt(right)) >= stateCount)) {
            right -= 1;
        }
        if (right < left) return null;
        String sequenceString0 = sequence0.substring(left,right+1);
        String sequenceString1 = sequence1.substring(left,right+1);
        pairAlignment.addSequence(new Sequence(alignment.getTaxon(x),sequenceString0));
        pairAlignment.addSequence(new Sequence(alignment.getTaxon(y),sequenceString1));
        return pairAlignment;
    }
}
