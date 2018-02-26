package dr.app.beauti.alignmentviewer;

import jebl.evolution.sequences.AminoAcids;

import java.awt.*;

public class AminoAcidDecorator implements StateDecorator {
    Paint[] paints = new Paint[AminoAcids.getStateCount()];

    public AminoAcidDecorator() {
        paints[AminoAcids.A_STATE.getIndex()] = new Color(204, 255, 255);
        paints[AminoAcids.C_STATE.getIndex()] = new Color(0, 255, 255);
        paints[AminoAcids.D_STATE.getIndex()] = new Color(255, 204, 153);
        paints[AminoAcids.E_STATE.getIndex()] = new Color(255, 204, 0);
        paints[AminoAcids.F_STATE.getIndex()] = new Color(0, 204, 255);
        paints[AminoAcids.G_STATE.getIndex()] = new Color(0, 255, 0);
        paints[AminoAcids.H_STATE.getIndex()] = new Color(255, 255, 153);
        paints[AminoAcids.I_STATE.getIndex()] = new Color(0, 0, 128);
        paints[AminoAcids.K_STATE.getIndex()] = new Color(198, 66, 0);
        paints[AminoAcids.L_STATE.getIndex()] = new Color(51, 102, 255);
        paints[AminoAcids.M_STATE.getIndex()] = new Color(153, 204, 255);
        paints[AminoAcids.N_STATE.getIndex()] = new Color(255, 153, 0);
        paints[AminoAcids.P_STATE.getIndex()] = new Color(255, 255, 0);
        paints[AminoAcids.Q_STATE.getIndex()] = new Color(255, 102, 0);
        paints[AminoAcids.R_STATE.getIndex()] = new Color(230, 6, 6);
        paints[AminoAcids.S_STATE.getIndex()] = new Color(204, 255, 153);
        paints[AminoAcids.T_STATE.getIndex()] = new Color(0, 255, 153);
        paints[AminoAcids.V_STATE.getIndex()] = new Color(0, 0, 255);
        paints[AminoAcids.W_STATE.getIndex()] = new Color(204, 153, 255);
        paints[AminoAcids.Y_STATE.getIndex()] = new Color(204, 255, 204);
        paints[AminoAcids.B_STATE.getIndex()] = Color.DARK_GRAY;
        paints[AminoAcids.Z_STATE.getIndex()] = Color.DARK_GRAY;
        paints[AminoAcids.X_STATE.getIndex()] = Color.GRAY;
        paints[AminoAcids.UNKNOWN_STATE.getIndex()] = Color.GRAY;
        paints[AminoAcids.STOP_STATE.getIndex()] = Color.GRAY;
        paints[AminoAcids.GAP_STATE.getIndex()] = Color.GRAY;
    };

    public Paint getStatePaint(int stateIndex) {
        return paints[stateIndex];
    }
}
