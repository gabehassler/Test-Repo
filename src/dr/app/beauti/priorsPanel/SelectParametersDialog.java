package dr.app.beauti.priorsPanel;
import dr.app.beauti.options.Parameter;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
public class SelectParametersDialog {
private final JFrame frame;
private JComboBox parameterCombo = new JComboBox();
OptionsPanel optionPanel = new OptionsPanel(12, 12);
public SelectParametersDialog(final JFrame frame) {
this.frame = frame;
}
public int showDialog(String message, List<Parameter> parameterList) {
optionPanel.removeAll();
if (message != null && !message.isEmpty()) {
optionPanel.addSpanningComponent(new JLabel(message));
}
parameterCombo.removeAllItems();
for (Parameter parameter : parameterList) {
parameterCombo.addItem(parameter);
}
optionPanel.addComponentWithLabel("Parameter:", parameterCombo);
JOptionPane optionPane = new JOptionPane(optionPanel,
JOptionPane.QUESTION_MESSAGE,
JOptionPane.OK_CANCEL_OPTION,
null,
null,
null);
optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
int result = JOptionPane.CANCEL_OPTION;
final JDialog dialog = optionPane.createDialog(frame, "Add Parameter");
dialog.pack();
dialog.setVisible(true);
Integer value = (Integer) optionPane.getValue();
if (value != null && value != -1) {
result = value;
}
return result;
}
public Parameter getSelectedParameter() {
return (Parameter)parameterCombo.getSelectedItem();
}
}