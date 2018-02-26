package dr.app.beauti.datapanel;
import dr.app.beauti.options.PartitionClockModel;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
public class SelectClockDialog {
    private JFrame frame;
    JComboBox treeCombo;
    JCheckBox copyCheck;
    JTextField nameField;
    OptionsPanel optionPanel;
    public SelectClockDialog(JFrame frame) {
        this.frame = frame;
        treeCombo = new JComboBox();
        copyCheck = new JCheckBox("Rename clock model partition to:");
        nameField = new JTextField();
        nameField.setColumns(20);
        nameField.setEnabled(false);
        optionPanel = new OptionsPanel(12, 12);
        optionPanel.addComponentWithLabel("Partition clock model:", treeCombo);
        optionPanel.addComponents(copyCheck, nameField);
        copyCheck.addItemListener(
                new ItemListener() {
                    public void itemStateChanged(ItemEvent ev) {
                        nameField.setEnabled(copyCheck.isSelected());
                    }
                }
        );
    }
    public int showDialog(Object[] models) {
        treeCombo.removeAllItems();
        for (Object model : models) {
            treeCombo.addItem(model);
        }
        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        final JDialog dialog = optionPane.createDialog(frame, "Create New Partition Clock Model");
        dialog.pack();
        dialog.setVisible(true);
        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }
        return result;
    }
    public PartitionClockModel getModel() {
        return (PartitionClockModel) treeCombo.getSelectedItem();
    }
    public boolean getMakeCopy() {
        return copyCheck.isSelected();
    }
    public String getName() {
        return nameField.getText();
    }
}