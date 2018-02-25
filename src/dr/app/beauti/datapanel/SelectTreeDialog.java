package dr.app.beauti.datapanel;
import dr.app.beauti.options.PartitionTreeModel;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
public class SelectTreeDialog {
private JFrame frame;
JComboBox treeCombo;
JCheckBox copyCheck;
JTextField nameField;
OptionsPanel optionPanel;
public SelectTreeDialog(JFrame frame) {
this.frame = frame;
treeCombo = new JComboBox();
copyCheck = new JCheckBox("Rename tree model partition to:");
nameField = new JTextField();
nameField.setColumns(20);
nameField.setEnabled(false);
optionPanel = new OptionsPanel(12, 12);
optionPanel.addComponentWithLabel("Partition tree:", treeCombo);
optionPanel.addComponents(copyCheck, nameField);
copyCheck.addItemListener(
new ItemListener() {
public void itemStateChanged(ItemEvent ev) {
nameField.setEnabled(copyCheck.isSelected());
}
}
);
}
public int showDialog(Object[] trees) {
treeCombo.removeAllItems();
for (Object tree : trees) {
treeCombo.addItem(tree);
}
JOptionPane optionPane = new JOptionPane(optionPanel,
JOptionPane.QUESTION_MESSAGE,
JOptionPane.OK_CANCEL_OPTION,
null,
null,
null);
optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
final JDialog dialog = optionPane.createDialog(frame, "Create New Partition Tree");
dialog.pack();
dialog.setVisible(true);
int result = JOptionPane.CANCEL_OPTION;
Integer value = (Integer) optionPane.getValue();
if (value != null && value != -1) {
result = value;
}
return result;
}
public PartitionTreeModel getTree() {
return (PartitionTreeModel) treeCombo.getSelectedItem();
}
public boolean getMakeCopy() {
return copyCheck.isSelected();
}
public String getName() {
return nameField.getText();
}
}