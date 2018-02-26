package dr.evolution.datatype;
public class MutationDeathType extends DataType {
    protected static String DESCRIPTION = "MutationDeathType";
    protected static int UNKNOWN_STATE = 128;
    protected int[][] codes;
    protected char[] stateCodes;
    public int DEATHSTATE;
    public MutationDeathType(char deathCode, char extantCode) { // Constructor for pure death type
        super();
        initialize_internals();
        codes[extantCode] = new int[]{0};
        stateCodes[0] = extantCode;
        codes[deathCode] = new int[]{1};
        stateCodes[1] = deathCode;
        stateCount = 2;
        DEATHSTATE = 1;
        ambiguousStateCount = 0;
    }
    public MutationDeathType(DataType x, char deathCode) { // constructor for extention type
        super();
        int i;
        char stateCode;
        initialize_internals();
        for (i = 0; i < x.getStateCount(); ++i) {  /* Copy unique codes */
            stateCode = x.getCode(i).charAt(0);
            this.codes[stateCode] = new int[]{i};
            stateCodes[i] = stateCode;
        }
        this.codes[deathCode] = new int[]{i}; /* Append the state space with the death state */
        stateCodes[i] = deathCode;
        DEATHSTATE = i;
        stateCount = i + 1;
        for (i = 0; i < 128; ++i) {
            int state = x.getState((char) i);
            if (state > 0 && state < 128 && x.isAmbiguousState(state) && i != deathCode) {
                if (!x.isUnknownState(state)) {
                    int[] states = x.getStates(state);
                    this.codes[i] = new int[states.length];
                    System.arraycopy(states, 0, this.codes[i], 0, states.length);
                }
            }
        }
        ambiguousStateCount = x.getAmbiguousStateCount() + 1;
    }
    public void addAmbiguity(char ambiguityCode, String s) {
        if (s.length() == 0) {
            this.codes[ambiguityCode] = new int[stateCount];
            for (int i = 0; i < stateCount; ++i) {
                this.codes[ambiguityCode][i] = i;
            }
        } else {
            this.codes[ambiguityCode] = new int[s.length()];
            for (int i = 0; i < s.length(); ++i) {
                this.codes[ambiguityCode][i] = getState(s.charAt(i));
            }
        }
        ambiguousStateCount += 1;
    }
    private void initialize_internals() {
        this.codes = new int[128][]; /* stores states (w/ ambiguities) corresponding to codes */
        this.stateCodes = new char[128]; /* Stores characters corresponding to unique state codes*/
    }
    @Override
    public char[] getValidChars() {
        return null;
    }
    public int getState(char c) {
        if (codes[c] != null && codes[c].length == 1) {
            return codes[c][0];
        } else {
            return c;
        }
    }
    public int getUnknownState() {
        return UNKNOWN_STATE;
    }
    public char getChar(int state) {
        if (state < stateCount)
            return stateCodes[state];
        return super.getChar(state);
    }
    public int[] getStates(int state) {
        if (state < stateCount)
            return codes[stateCodes[state]];
        else
            return codes[state];
    }
    public boolean[] getStateSet(int state) {
        boolean[] stateSet = new boolean[stateCount];
        int states[];
        int i;
        for (i = 0; i < stateCount; ++i)
            stateSet[i] = false;
        states = getStates(state);
        for (i = 0; states != null && i < states.length; ++i) {
            stateSet[states[i]] = true;
        }
        return stateSet;
    }
    public boolean isAmbiguousChar(char c) {
        return codes[c] != null && codes[c].length > 1;
    }
    public boolean isUnknownChar(char c) {
        return codes[c] == null;
    }
    public boolean isAmbiguousState(int state) {
        return state >= stateCount;
    }
    public boolean isUnknownState(int state) {
        return state >= stateCount && codes[state] == null;
    }
    public String getDescription() {
        return DESCRIPTION;
    }
    public int getType() {
        return 314;
    }
}
