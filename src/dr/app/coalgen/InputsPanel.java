
package dr.app.coalgen;

import jam.framework.Exportable;
import jam.panels.OptionsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class InputsPanel extends JPanel implements Exportable {

    private final CoalGenFrame frame;
    private final CoalGenData data;

    private OptionsPanel optionPanel;

    private final JButton logFileButton = new JButton("Choose File...");
    private final JTextField logFileNameText = new JTextField("not selected", 16);

    private final JButton treesFileButton = new JButton("Choose File...");
    private final JTextField treesFileNameText = new JTextField("not selected", 16);


    public InputsPanel(final CoalGenFrame frame, final CoalGenData data) {

        super();

        this.frame = frame;
        this.data = data;

        setOpaque(false);
        setLayout(new BorderLayout());

        optionPanel = new OptionsPanel(12, 12, SwingConstants.CENTER);
        add(optionPanel, BorderLayout.NORTH);

        logFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                FileDialog dialog = new FileDialog(frame,
                        "Select input log file...",
                        FileDialog.LOAD);

                dialog.setVisible(true);
                if (dialog.getFile() == null) {
                    // the dialog was cancelled...
                    return;
                }

                logFileButton.setEnabled(false);

                File file = new File(dialog.getDirectory(), dialog.getFile());
                try {
                    frame.readFromFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        logFileNameText.setEditable(false);

        treesFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                FileDialog dialog = new FileDialog(frame,
                        "Select input trees file...",
                        FileDialog.LOAD);

                dialog.setVisible(true);
                if (dialog.getFile() == null) {
                    // the dialog was cancelled...
                    return;
                }

                data.treesFile = new File(dialog.getDirectory(), dialog.getFile());
                treesFileNameText.setText(data.treesFile.getName());

                frame.fireTracesChanged();
            }
        });
        treesFileNameText.setEditable(false);

        JPanel panel1 = new JPanel(new BorderLayout(0, 0));
        panel1.setOpaque(false);
        panel1.add(logFileNameText, BorderLayout.CENTER);
        panel1.add(logFileButton, BorderLayout.EAST);
        optionPanel.addComponentWithLabel("Log File: ", panel1);

        JPanel panel2 = new JPanel(new BorderLayout(0, 0));
        panel2.setOpaque(false);
        panel2.add(treesFileNameText, BorderLayout.CENTER);
        panel2.add(treesFileButton, BorderLayout.EAST);
        optionPanel.addComponentWithLabel("Input Trees File: ", panel2);
    }

    public final void tracesChanged() {
        logFileButton.setEnabled(true);
        if (data.logFile != null) {
            logFileNameText.setText(data.logFile.getName());
        }
    }

    public void collectSettings() {

    }

    public JComponent getExportableComponent() {
        return this;
    }
}