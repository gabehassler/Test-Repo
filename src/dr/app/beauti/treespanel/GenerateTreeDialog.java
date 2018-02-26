
package dr.app.beauti.treespanel;

import dr.app.beauti.options.AbstractPartitionData;
import dr.app.beauti.options.BeautiOptions;
import dr.app.beauti.options.PartitionData;
import jam.panels.OptionsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class GenerateTreeDialog {

    private JFrame frame;

    public static enum MethodTypes {
            NJ,
            UPGMA
    }


    JTextField nameField;
    JComboBox partitionCombo;
    JComboBox methodCombo;

    OptionsPanel optionPanel;

    public GenerateTreeDialog(JFrame frame) {
        this.frame = frame;

        nameField = new JTextField();
        nameField.setColumns(20);

        methodCombo = new JComboBox(MethodTypes.values());
        partitionCombo = new JComboBox();

        optionPanel = new OptionsPanel(12, 12);
        optionPanel.addComponentWithLabel("Name:", nameField);
        optionPanel.addComponentWithLabel("Data Partition:", partitionCombo);
        optionPanel.addComponentWithLabel("Construction Method:", methodCombo);


    }

    public int showDialog(BeautiOptions options) {

        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        partitionCombo.removeAllItems();
        for (AbstractPartitionData partition : options.dataPartitions) {
            partitionCombo.addItem(partition);
        }
        final JDialog dialog = optionPane.createDialog(frame, "Construct New Tree");
        dialog.pack();

        dialog.setVisible(true);

        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }

        return result;
    }

    public String getName() {
        return nameField.getText();
    }

    public PartitionData getDataPartition() {
        return (PartitionData)partitionCombo.getSelectedItem();
    }
    public MethodTypes getMethodType() {
        return (MethodTypes)methodCombo.getSelectedItem();
    }
}