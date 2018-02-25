package dr.evolution.datatype;
public final class AminoAcids extends DataType
{
public static final String DESCRIPTION = "amino acid";
public static final int TYPE = AMINO_ACIDS;
public static final AminoAcids INSTANCE = new AminoAcids();
public static final char STOP_CHARACTER = '*';
public static final int STOP_STATE = 23;
public static final int UNKNOWN_STATE = 24;
public static final int GAP_STATE = 25;
public static final int AMINOACIDS = 1;
public static final char[] AMINOACID_CHARS= {
'A','C','D','E','F','G','H','I','K','L','M','N','P','Q','R', 
'S','T','V','W','Y','B','Z','X',AminoAcids.STOP_CHARACTER,DataType.UNKNOWN_CHARACTER,DataType.GAP_CHARACTER
};
private static final String[] AMINOACID_TRIPLETS = {
//		A		C		D		E		F		G		H		I		K
"Ala",  "Cys",  "Asp",  "Glu",  "Phe",  "Gly",  "His",  "Ile",  "Lys",  
//		L		M		N		P		Q		R		S		T		V
"Leu",  "Met",  "Asn",  "Pro",  "Gln",  "Arg",  "Ser",  "Thr",  "Val",
//		W		Y		B		Z		X		*		?		-
"Trp",  "Tyr",  "Asx",  "Glx",  " X ",  " * ",  " ? ",  " - "
};
public static final int[] AMINOACID_STATES = {
25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,	// 0-15
25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,	// 16-31
//                                 *        -
25,25,25,25,25,25,25,25,25,25,23,25,25,25,25,25,	// 32-47
//                                                ?
25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,24,	// 48-63
//		A  B  C  D  E  F  G  H  I  j  K  L  M  N  o
25, 0,20, 1, 2, 3, 4, 5, 6, 7,24, 8, 9,10,11,24,	// 64-79
//	 P  Q  R  S  T  u  V  W  X  Y  Z
12,13,14,15,16,24,17,18,22,19,21,25,25,25,25,25,	// 80-95
//		A  B  C  D  E  F  G  H  I  j  K  L  M  N  o
25, 0,20, 1, 2, 3, 4, 5, 6, 7,24, 8, 9,10,11,24,	// 96-111
//	 P  Q  R  S  T  u  V  W  X  Y  Z
12,13,14,15,16,24,17,18,22,19,21,25,25,25,25,25		// 112-127
};
private static final String[] AMINOACID_AMBIGUITIES = {
//	   A	C	 D	  E	   F	G	 H	  I	   K
"A", "C", "D", "E", "F", "G", "H", "I", "K",  
//	   L	M	 N	  P	   Q	R	 S	  T	   V
"L", "M", "N", "P", "Q", "R", "S", "T", "V",
//	   W	Y	 B	   Z
"W", "Y", "DN", "EQ", 
//	   X					   *	?						-
"ACDEFGHIKLMNPQRSTVWY", "*", "ACDEFGHIKLMNPQRSTVWY", "ACDEFGHIKLMNPQRSTVWY"
};
private AminoAcids() {
super();
stateCount = 20;
ambiguousStateCount = 26;
}
@Override
public char[] getValidChars() {
return AMINOACID_CHARS;
}
public int getState(final char c) {
return AMINOACID_STATES[c];
}
public static int getStopState() {
return STOP_STATE;
}
public int getUnknownState() {
return AminoAcids.UNKNOWN_STATE;
}
public int getGapState() {
return AminoAcids.GAP_STATE;
}
public char getChar(final int state) {
return AminoAcids.AMINOACID_CHARS[state];
}
public String getTriplet(final int state) {
return AminoAcids.AMINOACID_TRIPLETS[state];
}
public int[] getStates(final int state) {
final String stateString = AminoAcids.AMINOACID_AMBIGUITIES[state];
final int[] states = new int[stateString.length()];
for (int i = 0; i < stateString.length(); i++) {
states[i] = getState(stateString.charAt(i));
}
return states;
}
public boolean[] getStateSet(final int state) {
final boolean[] stateSet = new boolean[stateCount];
for (int i = 0; i < stateCount; i++) {
stateSet[i] = false;
}
final int len = AminoAcids.AMINOACID_AMBIGUITIES[state].length();
for (int i = 0; i < len; i++) {
stateSet[getState(AMINOACID_AMBIGUITIES[state].charAt(i))] = true;
}
return stateSet;
}
public String getDescription() {
return DESCRIPTION;
}
public int getType() {
return TYPE;
}
public boolean isStopChar(final char c) {
return isStopState(getState(c));
}
public boolean isStopState(final int state) {
return state == getStopState();
}
}
