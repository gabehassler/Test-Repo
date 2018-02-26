package dr.evolution.datatype;
import dr.util.Identifiable;
import java.util.*;
public class GeneralDataType extends DataType implements Identifiable {
    public static final String GENERAL_DATA_TYPE = "generalDataType";
    public static final String DESCRIPTION = GENERAL_DATA_TYPE;
    public static final int TYPE = GENERAL;
    public static final GeneralDataType INSTANCE = new GeneralDataType();
    // for BEAUti trait PartitionSubstitutionModel
    public GeneralDataType() {}
    public GeneralDataType(final String[] stateCodes) {
        for (int i = 0; i < stateCodes.length; i++) {
            State state = new State(i, stateCodes[i]);
            states.add(state);
            stateMap.put(stateCodes[i], state);
        }
        stateCount = states.size();
        this.ambiguousStateCount = 0;
    }
    public GeneralDataType(final Collection<String> stateCodes) {
        int i = 0;
        for (String code : stateCodes) {
            State state = new State(i, code);
            states.add(state);
            stateMap.put(code, state);
            i++;
        }
        stateCount = states.size();
        this.ambiguousStateCount = 0;
    }
    public void addAlias(String alias, String code) {
        State state =stateMap.get(code);
        if (state == null) {
            throw new IllegalArgumentException("DataType doesn't contain the state, " + code);
        }
        stateMap.put(alias, state);
    }
    public void addAmbiguity(String code, String[] ambiguousStates) {
        int n = ambiguousStateCount + stateCount;
        int[] indices = new int[ambiguousStates.length];
        int i = 0;
        for (String stateCode : ambiguousStates) {
            State state =stateMap.get(stateCode);
            if (state == null) {
                throw new IllegalArgumentException("DataType doesn't contain the state, " + stateCode);
            }
            indices[i] = state.number;
            i++;
        }
        State state = new State(n, code, indices);
        states.add(state);
        ambiguousStateCount++;
        stateMap.put(code, state);
    }
    @Override
    public char[] getValidChars() {
        return null;
    }
    public int getState(String code) {
        if (code.equals("?")) {
            return getUnknownState();
        }
        if (!stateMap.containsKey(code)) {
            return -1;
        }
        return stateMap.get(code).number;
    }
    public int getState(char c) {
        return getState(String.valueOf(c));
    }
    public int getUnknownState() {
        return stateCount + ambiguousStateCount;
    }
    public int getGapState() {
        return getUnknownState();
    }
    public String getCode(int state) {
        return states.get(state).code;
    }
    public int[] getStates(int state) {
        return states.get(state).ambiguities;
    }
    public boolean[] getStateSet(int state) {
        if (state >= states.size()) {
            throw new IllegalArgumentException("invalid state index");
        }
        State s = states.get(state);
        boolean[] stateSet = new boolean[stateCount];
        for (int i = 0; i < stateCount; i++)
            stateSet[i] = false;
        for (int i = 0, n = s.ambiguities.length; i < n; i++) {
            stateSet[s.ambiguities[i]] = true;
        }
        return stateSet;
    }
    public String getDescription() {
        if (id != null) {
            return id;
        } else {
            return DESCRIPTION;
        }
    }
    public int getType() {
        return TYPE;
    }
    // **************************************************************
    // Identifiable IMPLEMENTATION
    // **************************************************************
    private String id = null;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    private List<State> states = new ArrayList<State>();
    private Map<String, State> stateMap = new TreeMap<String, State>();
    private class State {
        int number;
        String code;
        int[] ambiguities;
        State(int number, String code) {
            this.number = number;
            this.code = code;
            this.ambiguities = new int[]{number};
        }
        State(int number, String code, int[] ambiguities) {
            this.number = number;
			this.code = code;
			this.ambiguities = ambiguities;
		}
	}
}
