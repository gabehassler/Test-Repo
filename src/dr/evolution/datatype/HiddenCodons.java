
package dr.evolution.datatype;

public class HiddenCodons extends Codons implements HiddenDataType {
		
	public static final String DESCRIPTION = "hiddenCodon";

	public static final HiddenCodons UNIVERSAL_HIDDEN_2 = new HiddenCodons(GeneticCode.UNIVERSAL,2);
	public static final HiddenCodons UNIVERSAL_HIDDEN_3 = new HiddenCodons(GeneticCode.UNIVERSAL,3);
	
	private HiddenCodons(GeneticCode geneticCode, int hiddenClassCount) {
		super(geneticCode);
		this.hiddenClassCount = hiddenClassCount;
	}

	public boolean[] getStateSet(int state) {

	    boolean[] stateSet = new boolean[stateCount*hiddenClassCount];

	    if (!isAmbiguousState(state)) {
		    for(int h=0; h<hiddenClassCount; h++)
	            stateSet[h*stateCount + state] = true;
	    } else {
	        for (int i = 0; i < stateCount; i++) {
	            stateSet[i] = true;
	        }
	    }

	    return stateSet;
	}

	public int getStateCount() {
        return stateCount*hiddenClassCount;
    }

	private int hiddenClassCount;

    public int getHiddenClassCount() {
        return hiddenClassCount;
    }
}
