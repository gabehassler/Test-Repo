package dr.app.beauti.datapanel;
import dr.app.beauti.options.PartitionSubstitutionModel;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class SelectModelDialog {
    private JFrame frame;
    JComboBox modelCombo;
    JCheckBox copyCheck;
    JTextField nameField;
    OptionsPanel optionPanel;
    public SelectModelDialog(JFrame frame) {
        this.frame = frame;
        modelCombo = new JComboBox();
        copyCheck = new JCheckBox("Rename substitution model partition to:");
        nameField = new JTextField();
        nameField.setColumns(20);
        nameField.setEnabled(false);
        optionPanel = new OptionsPanel(12, 12);
        optionPanel.addComponentWithLabel("Partition Model:", modelCombo);
        optionPanel.addComponents(copyCheck, nameField);
        copyCheck.addItemListener(
                new java.awt.event.ItemListener() {
                    public void itemStateChanged(java.awt.event.ItemEvent ev) {
                        nameField.setEnabled(copyCheck.isSelected());
                    }
                }
        );
    }
    public int showDialog(Object[] models) {
        modelCombo.removeAllItems();
        for (Object model : models) {
            modelCombo.addItem(model);
        }
        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        final JDialog dialog = optionPane.createDialog(frame, "Create New Model");
        dialog.pack();
        dialog.setVisible(true);
        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }
        return result;
    }
    public PartitionSubstitutionModel getModel() {
        return (PartitionSubstitutionModel)modelCombo.getSelectedItem();
    }
    public boolean getMakeCopy() {
        return copyCheck.isSelected();
    }
    public String getName() {
        return nameField.getText();
    }
}