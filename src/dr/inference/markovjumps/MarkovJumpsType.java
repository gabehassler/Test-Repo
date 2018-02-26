
package dr.inference.markovjumps;


public enum MarkovJumpsType {

    HISTORY("history"),
    COUNTS("counts"),
    REWARDS("rewards");

    MarkovJumpsType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    private final String text;

    public static MarkovJumpsType parseFromString(String text) {
        for (MarkovJumpsType scheme : MarkovJumpsType.values()) {
            if (scheme.getText().compareToIgnoreCase(text) == 0)
                return scheme;
        }
        return null;
    }
}
