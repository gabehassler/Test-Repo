
package dr.evomodel.substmodel;

import dr.util.Citation;
import jebl.evolution.substmodel.AminoAcidModel;

public enum AminoAcidModelType {

    BLOSUM_62("Blosum62", "blosum62", Blosum62.INSTANCE),
    DAYHOFF("Dayhoff", "dayhoff", Dayhoff.INSTANCE),
    JTT("JTT", dr.evomodel.substmodel.JTT.INSTANCE),
    MT_REV_24("mtREV", MTREV.INSTANCE),
    CP_REV_45("cpREV", CPREV.INSTANCE),
    WAG("WAG", dr.evomodel.substmodel.WAG.INSTANCE),
    LG("LG", dr.evomodel.substmodel.LG.INSTANCE),
    FLU("FLU", dr.evomodel.substmodel.FLU.INSTANCE);

    AminoAcidModelType(String displayName, EmpiricalRateMatrix matrix) {
        this(displayName, displayName, matrix);
    }

    AminoAcidModelType(String displayName, String xmlName, EmpiricalRateMatrix matrix) {
        this.displayName = displayName;
        this.xmlName = xmlName;
        this.matrix = matrix;
    }

    public String toString() {
        return displayName;
    }


    public String getXMLName() {
        return xmlName;
    }

    public EmpiricalRateMatrix getRateMatrixInstance() {
        return matrix;
    }

    public Citation getCitation() {
        return matrix.getCitations().get(0);
    }

    public static String[] xmlNames() {

        AminoAcidModelType[] values = values();

        String[] xmlNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            xmlNames[i] = values[i].getXMLName();
        }
        return xmlNames;
    }

    private final String displayName, xmlName;
    private final EmpiricalRateMatrix matrix;
}
