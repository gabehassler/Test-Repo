package dr.app.beauti.types;
public enum StartingTreeType {
    RANDOM("randomly generated"),
    UPGMA("UPGMA generated"),
    USER("user-specified");
    StartingTreeType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public String toString() {
        return name;
    }
    private final String name;
}