package dr.app.beauti.datapanel;
import dr.app.beauti.options.PartitionSubstitutionModel;
import dr.app.beauti.options.TraitData;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.Collection;
public class SelectTraitDialog {
private JFrame frame;
JComboBox traitCombo;
JCheckBox copyCheck;
JTextField nameField;
OptionsPanel optionPanel;
public SelectTraitDialog(JFrame frame) {
this.frame = frame;
traitCombo = new JComboBox();
copyCheck = new JCheckBox("Name trait partition:");
nameField = new JTextField();
nameField.setColumns(20);
optionPanel = new OptionsPanel(12, 12);
copyCheck.addItemListener(
new java.awt.event.ItemListener() {
public void itemStateChanged(java.awt.event.ItemEvent ev) {
nameField.setEnabled(copyCheck.isSelected());
}
}
);
}
public int showDialog(Collection<TraitData> traits, String defaultName) {
optionPanel.removeAll();
if (traits == null) {
optionPanel.addSpanningComponent(new JLabel("Create a new data partition using the selected trait(s)."));
optionPanel.addComponentWithLabel("Name trait partition:", nameField);
nameField.setText(defaultName != null ? defaultName : "untitled_traits");
nameField.setEnabled(true);
nameField.selectAll();
} else {
traitCombo.removeAllItems();
for (Object model : traits) {
traitCombo.addItem(model);
}
optionPanel.addSpanningComponent(new JLabel("Create a new data partition using the following trait."));
optionPanel.addComponentWithLabel("Trait:", traitCombo);
optionPanel.addComponents(copyCheck, nameField);
nameField.setEnabled(copyCheck.isSelected());
}
JOptionPane optionPane = new JOptionPane(optionPanel,
JOptionPane.QUESTION_MESSAGE,
JOptionPane.OK_CANCEL_OPTION,
null,
null,
null);
optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
final JDialog dialog = optionPane.createDialog(frame, "Create New Partition");
dialog.pack();
int result;
boolean isValid;
do {
dialog.setVisible(true);
isValid = true;
result = JOptionPane.CANCEL_OPTION;
Integer value = (Integer) optionPane.getValue();
if (value != null && value != -1) {
result = value;
}
if (result != JOptionPane.CANCEL_OPTION) {
String name = getName().trim();
if (name.isEmpty()) {
isValid = false;
}
}
} while (!isValid);
return result;
}
public TraitData getTrait() {
return (TraitData) traitCombo.getSelectedItem();
}
public boolean getMakeCopy() {
return copyCheck.isSelected();
}
public String getName() {
return nameField.getText();
}
}