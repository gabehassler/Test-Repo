
package dr.app.beauti.traitspanel;

import dr.app.beauti.options.TraitGuesser;
import jam.panels.OptionsPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class TraitValueDialog {

    private JFrame frame;
    private final OptionsPanel optionPanel;

    JLabel descriptionText = new JLabel();

    private final JTextField valueText = new JTextField(16);

    public TraitValueDialog(JFrame frame) {
        this.frame = frame;

        optionPanel = new OptionsPanel(12, 12);

        optionPanel.addSpanningComponent(descriptionText);
        optionPanel.addComponentWithLabel("Value: ", valueText);
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

        final JDialog dialog = optionPane.createDialog(frame, "Set Trait Value for Taxa");
        dialog.pack();

        dialog.setVisible(true);

        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }

        return result;
    }

    public String getTraitValue() {
        return valueText.getText();
    }
}