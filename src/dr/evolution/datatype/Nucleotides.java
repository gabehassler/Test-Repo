
package dr.evolution.datatype;

public class Nucleotides extends DataType {

    public static final String JC = "JC";
	public static final String F84 = "F84";
	public static final String HKY = "HKY";
	public static final String GTR = "GTR";
	
	public static final String DESCRIPTION = "nucleotide";
	public static final int TYPE = NUCLEOTIDES;
	public static final Nucleotides INSTANCE = new Nucleotides();

    public static final int A_STATE = 0;
	public static final int C_STATE = 1;
	public static final int G_STATE = 2;
	public static final int UT_STATE = 3;

    public static final int R_STATE = 5; // A or G
    public static final int Y_STATE = 6; // C or T

	public static final int UNKNOWN_STATE = 16;
	public static final int GAP_STATE = 17;
	
	public static final char[] NUCLEOTIDE_CHARS = 
		{ 'A','C','G','T','U','K','M','R','S','W','Y','B','D','H','V','N', UNKNOWN_CHARACTER,GAP_CHARACTER};

	public static final int NUCLEOTIDE_STATES[] = {
		17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,	// 0-15
		17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,	// 16-31
	//                                          -
		17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,	// 32-47
	//                                                ?
		17,17,17,17,17,17,17,17,17,17,17,17,17,17,17,16,	// 48-63
	//	    A  B  C  D  e  f  G  H  i  j  K  l  M  N  o
		17, 0,11, 1,12,16,16, 2,13,16,16,10,16, 7,15,16,	// 64-79
	//	 p  q  R  S  T  U  V  W  x  Y  z
		16,16, 5, 9, 3, 3,14, 8,16, 6,16,17,17,17,17,17,	// 80-95
	//	    A  B  C  D  e  f  G  H  i  j  K  l  M  N  o
		17, 0,11, 1,12,16,16, 2,13,16,16,10,16, 7,15,16,	// 96-111
	//	 p  q  R  S  T  U  V  W  x  Y  z
		16,16, 5, 9, 3, 3,14, 8,16, 6,16,17,17,17,17,17		// 112-127
	};

	public static final String[] NUCLEOTIDE_AMBIGUITIES = {
	//	 A    C	   G    T    U    R     Y     M     W     S     K
		"A", "C", "G", "T", "T", "AG", "CT", "AC", "AT", "CG", "GT",
	//   B      D      H      V      N       ?       -
		"CGT", "AGT", "ACT", "ACG", "ACGT", "ACGT", "ACGT"
	};

	protected Nucleotides() {
		stateCount = 4;
		ambiguousStateCount = 18;
	}

    @Override
    public char[] getValidChars() {
        return NUCLEOTIDE_CHARS;
    }

	public int getState(char c) {
		return NUCLEOTIDE_STATES[c];
	}
	
	public int getUnknownState() {
		return UNKNOWN_STATE;
	}

	public int getGapState() {
		return GAP_STATE;
	}

	public char getChar(int state) {
		return NUCLEOTIDE_CHARS[state];
	}

	public int[] getStates(int state) {

		String stateString = NUCLEOTIDE_AMBIGUITIES[state];
		int[] states = new int[stateString.length()];
		for (int i = 0; i < stateString.length(); i++) {
			states[i] = getState(stateString.charAt(i));
		}

		return states;
	}
	
	public boolean[] getStateSet(int state) {
	
		boolean[] stateSet = new boolean[stateCount];
		for (int i = 0; i < stateCount; i++)
			stateSet[i] = false;
			
		int len = NUCLEOTIDE_AMBIGUITIES[state].length();
		for (int i = 0; i < len; i++)
			stateSet[getState(NUCLEOTIDE_AMBIGUITIES[state].charAt(i))] = true;
			
		return stateSet;
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	public int getType() {
		return TYPE;
	}

}
