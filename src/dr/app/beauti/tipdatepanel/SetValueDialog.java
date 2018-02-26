
package dr.app.beauti.tipdatepanel;

import jam.panels.OptionsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


public class SetValueDialog {

    private JFrame frame;
    private final OptionsPanel optionPanel;
    private String title = "Set Values for Taxa";

    JLabel descriptionText = new JLabel();

    private final JTextField valueText = new JTextField(16);

    public SetValueDialog(JFrame frame, String title) {
        this.frame = frame;

        this.title = title;

        optionPanel = new OptionsPanel(12, 12);

        optionPanel.addSpanningComponent(descriptionText);
        optionPanel.addComponentWithLabel("Value: ", valueText);
    }

    public void setDescription(String description) {
        descriptionText.setText(description);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int showDialog() {

        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));

        final JDialog dialog = optionPane.createDialog(frame, title);
        dialog.pack();

        dialog.setVisible(true);

        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }

        return result;
    }

    public String getValue() {
        return valueText.getText();
    }
}