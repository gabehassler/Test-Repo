
package dr.evolution.datatype;

 public class PairedDataType extends DataType {

	public static final String DESCRIPTION = "pairedDataType";
	public static final PairedDataType PAIRED_NUCLEOTIDES = new PairedDataType(Nucleotides.INSTANCE);
	public static final PairedDataType PAIRED_AMINO_ACIDS = new PairedDataType(AminoAcids.INSTANCE);
	public static final PairedDataType PAIRED_CODONS = new PairedDataType(Codons.UNIVERSAL);

	public PairedDataType(DataType baseDataType) {
		this.baseDataType = baseDataType;

		stateCount = baseDataType.getStateCount() * baseDataType.getStateCount();
		ambiguousStateCount = stateCount + 2;
	}

	public final int getState(int state1, int state2) {
        if (baseDataType.isAmbiguousState(state1) || baseDataType.isAmbiguousState(state2)) {
            return getUnknownState();
        }
		return (state1 * baseDataType.getStateCount()) + state2;
	}

	public final int getState(char c1, char c2) {
		return getState(baseDataType.getState(c1), baseDataType.getState(c2));
	}

	public final int getFirstState(int state) {
		return state / baseDataType.getStateCount();
	}

	public final int getSecondState(int state) {
		return state % baseDataType.getStateCount();
	}

    @Override
    public char[] getValidChars() {
        return null;
    }

    public final int getState(char c)
	{
		throw new IllegalArgumentException("Paired datatype cannot be expressed as char");
	}

	public int getUnknownState() {
		return stateCount;
	}

	public int getGapState() {
		return stateCount + 1;
	}

	public final char getChar(int state) {
		throw new IllegalArgumentException("Paired datatype cannot be expressed as char");
	}

	public final String getTriplet(int state) {
		throw new IllegalArgumentException("Paired datatype cannot be expressed as triplets");
	}

	public final int[] getTripletStates(int state) {
		throw new IllegalArgumentException("Paired datatype cannot be expressed as triplets");
	}

	public String getDescription() {
		return DESCRIPTION;
	}

	public int getType() {
		return -1;
	}

	// Private members

	private DataType baseDataType;

}
