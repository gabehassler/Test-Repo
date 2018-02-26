package dr.evolution.datatype;
public class HiddenNucleotides extends DataType {
    public static final String DESCRIPTION = "hiddenNucleotide";
    public static final HiddenNucleotides INSTANCE = new HiddenNucleotides(2);
    public HiddenNucleotides(int numHiddenStates) {
        hiddenClassCount = numHiddenStates;
        stateCount = 4 * hiddenClassCount;
        ambiguousStateCount = stateCount + 6;
    }
    @Override
    public char[] getValidChars() {
        return null;
    }
    public int getState(char c) {
        switch (c) {
            case'A':
            case'a':
                return stateCount;
            case'C':
            case'c':
                return stateCount + 1;
            case'G':
            case'g':
                return stateCount + 2;
            case'T':
            case't':
            case'U':
            case'u':
                return stateCount + 3;
            case'-':
            case'?':
                return getGapState();
            default: {
                int state = (int) c - '0';
                if (c > '?') state -= 1;
                if (c > 'A') state -= 1;
                if (c > 'C') state -= 1;
                if (c > 'G') state -= 1;
                if (c > 'T') state -= 1;
                if (c > 'U') state -= 1;
                if (c > 'a') state -= 1;
                if (c > 'g') state -= 1;
                if (c > 'c') state -= 1;
                if (c > 't') state -= 1;
                if (c > 'u') state -= 1;
                return state;
            }
        }
    }
    public char getChar(int state) {
        if (state >= stateCount) {
            switch (state - stateCount) {
                case 0:
                    return 'A';
                case 1:
                    return 'C';
                case 2:
                    return 'G';
                case 3:
                    return 'T';
                default:
                    return '-';
            }
        } else {
            char c = (char) (state + '0');
            if (c >= '?') c += 1;
            if (c >= 'A') c += 1;
            if (c >= 'C') c += 1;
            if (c >= 'G') c += 1;
            if (c >= 'T') c += 1;
            if (c >= 'U') c += 1;
            if (c >= 'a') c += 1;
            if (c >= 'g') c += 1;
            if (c >= 'c') c += 1;
            if (c >= 't') c += 1;
            if (c >= 'u') c += 1;
            return c;
        }
    }
    public int[] getStates(int state) {
        if (state >= stateCount && state <= stateCount + 3) {
            int[] states = new int[hiddenClassCount];
            for (int i = 0; i < hiddenClassCount; i++) {
                states[i] = state % 4 + (i * 4);
            }
            return states;
        } else throw new IllegalArgumentException();
    }
    public boolean[] getStateSet(int state) {
        boolean[] stateSet = new boolean[stateCount];
        for (int i = 0; i < stateCount; i++) {
            stateSet[i] = false;
        }
        if (!isAmbiguousState(state)) {
            stateSet[state] = true;
        } else if (state < (stateCount + 4)) {
            for (int i = 0; i < stateCount; i++) {
                if ((i % 4) == (state % 4)) {
                    stateSet[i] = true;
                }
            }
        } else {
            for (int i = 0; i < stateCount; i++) {
                stateSet[i] = true;
            }
        }
        return stateSet;
    }
    public int getUnknownState() {
        return stateCount + 4;
    }
    public int getGapState() {
        return stateCount + 5;
    }
    public boolean isAmbiguousChar(char c) {
        return isAmbiguousState(getState(c));
    }
    public boolean isUnknownChar(char c) {
        return isUnknownState(getState(c));
    }
    public boolean isGapChar(char c) {
        return isGapState(getState(c));
    }
    public boolean isAmbiguousState(int state) {
        return (state >= stateCount);
    }
    public boolean isUnknownState(int state) {
        return (state == getUnknownState());
    }
    public boolean isGapState(int state) {
        return (state == getGapState());
    }
    public int getType() {
        return 999;
    }
    public String getDescription() {
        return "Hidden-state Nucleotides";
    }
    private int hiddenClassCount;
    public int getHiddenClassCount() {
        return hiddenClassCount;
    }
}
