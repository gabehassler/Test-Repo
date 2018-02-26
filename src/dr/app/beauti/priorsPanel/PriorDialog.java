package dr.app.beauti.priorsPanel;
import dr.app.beauti.options.Parameter;
import dr.app.util.OSType;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
public class PriorDialog implements AbstractPriorDialog {
    private final JFrame frame;
    private final PriorSettingsPanel priorSettingsPanel;
    private Parameter parameter;
    public PriorDialog(JFrame frame) {
        this.frame = frame;
         priorSettingsPanel = new PriorSettingsPanel(frame);
    }
    public void setParameter(final Parameter parameter) {
        this.parameter = parameter;
        priorSettingsPanel.setParameter(parameter);
    }
    public int showDialog() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.add(new JLabel("Select prior distribution for " + parameter.getName()), BorderLayout.NORTH);
        panel.add(priorSettingsPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setOpaque(false);
        JOptionPane optionPane = new JOptionPane(scrollPane,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION,
                null,
                null,
                null);
        optionPane.setBorder(new EmptyBorder(12, 12, 12, 12));
        final JDialog dialog = optionPane.createDialog(frame, "Prior for Parameter " + parameter.getName());
        priorSettingsPanel.setDialog(dialog);
        if (OSType.isMac()) {
            dialog.setMinimumSize(new Dimension(dialog.getBounds().width, 300));
        } else {
            Toolkit tk = Toolkit.getDefaultToolkit();
            Dimension d = tk.getScreenSize();
            if (d.height < 700 && priorSettingsPanel.getHeight() > 450) {
                dialog.setSize(new java.awt.Dimension(priorSettingsPanel.getWidth() + 100, 550));
            } else {
                // setSize because optionsPanel is shrunk in dialog
                dialog.setSize(new java.awt.Dimension(priorSettingsPanel.getWidth() + 100, priorSettingsPanel.getHeight() + 100));
            }
//            System.out.println("panel width = " + panel.getWidth());
//            System.out.println("panel height = " + panel.getHeight());
        }
        dialog.pack();
        dialog.setResizable(true);
        dialog.setVisible(true);
        int result = JOptionPane.CANCEL_OPTION;
        Integer value = (Integer) optionPane.getValue();
        if (value != null && value != -1) {
            result = value;
        }
        return result;
    }
    public void getArguments(Parameter parameter) {
            priorSettingsPanel.getArguments(parameter);
    }
    public boolean hasInvalidInput(boolean showError) {
        return priorSettingsPanel.hasInvalidInput(showError);
    }
}