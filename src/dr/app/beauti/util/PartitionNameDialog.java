package dr.app.beauti.util;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
public class PartitionNameDialog {
    private JFrame frame;
    private final OptionsPanel optionPanel;
    JLabel descriptionText = new JLabel();
    private final JTextField valueText = new JTextField(16);
    public PartitionNameDialog(JFrame frame) {
        this.frame = frame;
        optionPanel = new OptionsPanel(12, 12);
        optionPanel.addSpanningComponent(descriptionText);
        optionPanel.addComponentWithLabel("New Name: ", valueText);
    }
    public void setDescription(String description) {
        descriptionText.setText(description);
    }
    public int showDialog() {
        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        final JDialog dialog = optionPane.createDialog(frame, "Rename partition");
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
        return valueText.getText().trim();
    }
}