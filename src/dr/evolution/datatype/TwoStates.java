package dr.evolution.datatype;
public class TwoStates extends DataType {
	public static final String DESCRIPTION = "binary";
	public static final int TYPE = TWO_STATES;
	public static final TwoStates INSTANCE = new TwoStates();
    public static final int ZERO_STATE = 0;
	public static final int ONE_STATE = 1;
	public static final int UNKNOWN_STATE = 2;
	public static final int GAP_STATE = 3;
	public static final char[] TWOSTATE_CHARS = 
		{ '0','1', UNKNOWN_CHARACTER,GAP_CHARACTER};
	public static final String[] TWOSTATE_AMBIGUITIES = {
	//	 0    1	   ?     -
		"0", "1", "01", "01"
	};
	private TwoStates() {
		stateCount = 2;
		ambiguousStateCount = 4;
	}
    @Override
    public char[] getValidChars() {
        return TWOSTATE_CHARS;
    }
    // Get state corresponding to character c
	public int getState(char c)
	{
		switch (c)
		{
 			case '0': 
				return 0;
			case '1': 
				return 1;
			case UNKNOWN_CHARACTER:
				return 2;
			case GAP_CHARACTER:
				return 3;
			default:
				return 2;
		}
	}
	public char getChar(int state) {
		return TWOSTATE_CHARS[state];
	}
	public int[] getStates(int state) {
		String stateString = TWOSTATE_AMBIGUITIES[state];
		int[] states = new int[stateString.length()];
		for (int i = 0; i < stateString.length(); i++) {
			states[i] = getState(stateString.charAt(i));
		}
		return states;
	}
	public boolean[] getStateSet(int state) {
		boolean[] stateSet = new boolean[stateCount];
		if (state < 2) {
			stateSet[1-state] = false;
			stateSet[state] = true;
		} else {
			stateSet[0] = true;
			stateSet[1] = true;
		}
		return stateSet;
	}
	public String getDescription() {
		return DESCRIPTION;
	}
	public int getType() {
		return TYPE;
	}
}
