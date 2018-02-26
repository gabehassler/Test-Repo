package dr.evomodel.substmodel;
public enum NucModelType {
    JC, HKY, GTR, TN93;
    public final String getXMLName() {
        return name() + "Model";
    }
    public String toString() {
        return name();
    }
}
