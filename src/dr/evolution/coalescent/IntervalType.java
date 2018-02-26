package dr.evolution.coalescent;
public enum IntervalType {
    SAMPLE("sample"),
    COALESCENT("coalescent"),
    MIGRATION("migration"),
    NOTHING("nothing");
    private IntervalType(String name) {
        this.name = name;
    }
    public String toString() {
        return name;
    }
    private final String name;
}