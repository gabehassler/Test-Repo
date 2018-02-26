package dr.app.beauti.traitspanel;
import dr.app.beauti.BeautiFrame;
import dr.app.beauti.options.STARBEASTOptions;
import dr.app.beauti.options.TraitData;
import dr.app.beauti.util.TextUtil;
import jam.panels.OptionsPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
public class CreateTraitDialog {
    private final BeautiFrame frame;
    private JTextField nameField;
    private JComboBox typeCombo;
    private final JRadioButton createRadio = new JRadioButton("Create a new trait", true);
    private final JRadioButton importRadio = new JRadioButton("Import trait(s) from a mapping file", false);
    private final JButton exampleButton = new JButton("Show example of mapping file format");
    private final JCheckBox createTraitPartitionCheck = new JCheckBox("Create a corresponding data partition", true);
    private String message = null;
    private boolean isSpeciesTrait = false;
    public static final int OK_IMPORT = 10;
    OptionsPanel optionPanel;
    public CreateTraitDialog(final BeautiFrame frame) {
        this.frame = frame;
        nameField = new JTextField("untitled_trait");
        nameField.setColumns(20);
//        nameCombo = new JComboBox(TraitData.Traits.values());
        typeCombo = new JComboBox(TraitData.TraitType.values());
        optionPanel = new OptionsPanel(12, 12);
        ButtonGroup group = new ButtonGroup();
        group.add(createRadio);
        group.add(importRadio);
        ItemListener listener = new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                nameField.setEnabled(createRadio.isSelected());
                typeCombo.setEnabled(createRadio.isSelected());
                exampleButton.setEnabled(importRadio.isSelected());
            }
        };
        createRadio.addItemListener(listener);
        importRadio.addItemListener(listener);
        exampleButton.setEnabled(false);
        exampleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                JScrollPane scrollPane = TextUtil.createHTMLScrollPane(STARBEASTOptions.EXAMPLE_FORMAT, new Dimension(400,300));
                JOptionPane.showMessageDialog(frame, scrollPane,
                    "Example of mapping file format",
                    JOptionPane.PLAIN_MESSAGE);
            }
        });
    }
    public void setTraitName(String traitName) {
        nameField.setText(traitName);
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setSpeciesTrait(final boolean isSpeciesTrait) {
        this.isSpeciesTrait = isSpeciesTrait;
    }
    public int showDialog() {
        setupPanel();
        JOptionPane optionPane = new JOptionPane(optionPanel,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        boolean done;
        int result = JOptionPane.CANCEL_OPTION;
        do {
            final JDialog dialog = optionPane.createDialog(frame, "Create or Import Trait(s)");
            dialog.pack();
            dialog.setVisible(true);
            Integer value = (Integer) optionPane.getValue();
            if (value != null && value != -1) {
                result = value;
            }
            done = true;
            if (result == JOptionPane.OK_OPTION && createRadio.isSelected()) {
                done = frame.validateTraitName(getName());
            }
        } while (!done);
        if (importRadio.isSelected() && result == JOptionPane.OK_OPTION) result = OK_IMPORT;
        return result;
    }
    private void setupPanel() {
        optionPanel.removeAll();
        if (message != null && !message.isEmpty()) {
            optionPanel.addSpanningComponent(new JLabel(message));
        }
        optionPanel.addComponent(createRadio);
        JLabel label = optionPanel.addComponentWithLabel("Name:", nameField);
        if (isSpeciesTrait) {
            label.setEnabled(false);
            nameField.setEnabled(false);
        }
        optionPanel.addComponent(importRadio);
        optionPanel.addComponent(exampleButton);
        exampleButton.putClientProperty("Quaqua.Button.style", "help");
        if (!isSpeciesTrait) {
            optionPanel.addComponentWithLabel("Type:", typeCombo);
            optionPanel.addComponent(createTraitPartitionCheck);
        }
        // if create "species" partition for *BEAST, then everything goes wrong
        createTraitPartitionCheck.setSelected(!isSpeciesTrait);
    }
    public String getName() {
        return nameField.getText();
//        return nameCombo.getSelectedItem().toString();
    }
    public TraitData.TraitType getType() {
        return (TraitData.TraitType) typeCombo.getSelectedItem();
    }
    public boolean createTraitPartition() {
        return createTraitPartitionCheck.isSelected();
    }
}