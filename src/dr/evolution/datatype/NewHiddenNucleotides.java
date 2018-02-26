package dr.evolution.datatype;
public class NewHiddenNucleotides extends Nucleotides implements HiddenDataType {
    public static final String DESCRIPTION = "hiddenNucleotide";
    public static final NewHiddenNucleotides NUCLEOTIDE_HIDDEN_1 = new NewHiddenNucleotides(1);
    public static final NewHiddenNucleotides NUCLEOTIDE_HIDDEN_2 = new NewHiddenNucleotides(2);
    public static final NewHiddenNucleotides NUCLEOTIDE_HIDDEN_3 = new NewHiddenNucleotides(3);
    private NewHiddenNucleotides(int hiddenClassCount) {
        super();
        this.hiddenClassCount = hiddenClassCount;
    }
    public boolean[] getStateSet(int state) {
        boolean[] stateSet = new boolean[stateCount * hiddenClassCount];
        if (!isAmbiguousState(state)) {
            for (int h = 0; h < hiddenClassCount; h++)
                stateSet[h * stateCount + state] = true;
        } else {
            for (int i = 0; i < stateCount; i++) {
                stateSet[i] = true;
            }
        }
        return stateSet;
    }
    public int getStateCount() {
        return stateCount * hiddenClassCount;
    }
    private int hiddenClassCount;
    public int getHiddenClassCount() {
        return hiddenClassCount;
    }
}