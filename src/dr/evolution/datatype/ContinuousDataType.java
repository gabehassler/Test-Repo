
package dr.evolution.datatype;

public class ContinuousDataType extends DataType {

    public static final String DESCRIPTION = "continuous";
    public static final ContinuousDataType INSTANCE = new ContinuousDataType();

    public ContinuousDataType(){
        stateCount = 0;
        ambiguousStateCount = 0;
    }

    @Override
    public char[] getValidChars() {
        return null;
    }

    public String getDescription() {
		return DESCRIPTION;
	}

    public int getType(){
        return CONTINUOUS;
    }

}
