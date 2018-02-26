package dr.app.beauti.siteModelsPanel;
import dr.app.beauti.options.PartitionSubstitutionModel;
import dr.app.beauti.util.PanelUtils;
import dr.evolution.datatype.AminoAcids;
import dr.evolution.datatype.DataType;
import dr.evolution.datatype.Nucleotides;
import dr.evolution.datatype.TwoStates;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
public class CloneModelDialog {
    private JFrame frame;
    JComboBox sourceModelCombo;
    OptionsPanel optionPanel;
    public CloneModelDialog(JFrame frame) {
        this.frame = frame;
        sourceModelCombo = new JComboBox();
        PanelUtils.setupComponent(sourceModelCombo);
        sourceModelCombo
                .setToolTipText("<html>Select the substitution model to act as a source<br>to copy to the other selected models.</html>");
        optionPanel = new OptionsPanel(12, 12);
        optionPanel.addSpanningComponent(new JLabel("<html>Select the substitution model to act as a source<br>to copy to the other selected models.</html>"));
        optionPanel.addComponentWithLabel("Source Model:", sourceModelCombo);
    }
    public int showDialog(List<PartitionSubstitutionModel> sourceModels) {
        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        sourceModelCombo.removeAllItems();
        for (PartitionSubstitutionModel model : sourceModels) {
            sourceModelCombo.addItem(model);
        }
        final JDialog dialog = optionPane.createDialog(frame, "Clone model settings");
        dialog.pack();
        dialog.setVisible(true);
        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }
        return result;
    }
    public PartitionSubstitutionModel getSourceModel() {
        return (PartitionSubstitutionModel)sourceModelCombo.getSelectedItem();
    }
}