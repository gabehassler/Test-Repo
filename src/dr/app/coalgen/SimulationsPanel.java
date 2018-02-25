package dr.app.coalgen;
import jam.framework.Exportable;
import jam.panels.OptionsPanel;
import javax.swing.*;
import java.awt.*;
import dr.app.gui.components.WholeNumberField;
public class SimulationsPanel extends JPanel implements Exportable {
private final CoalGenFrame frame;
private final CoalGenData data;
private OptionsPanel optionPanel;
private final JLabel replicatesLabel = new JLabel("Number of replicates:");
private final WholeNumberField replicatesField = new WholeNumberField(1, Integer.MAX_VALUE);
public SimulationsPanel(final CoalGenFrame frame, final CoalGenData data) {
super();
this.frame = frame;
this.data = data;
setOpaque(false);
setLayout(new BorderLayout());
optionPanel = new OptionsPanel(12, 12, SwingConstants.CENTER);
add(optionPanel, BorderLayout.NORTH);
replicatesField.setColumns(8);
replicatesField.setValue(data.replicateCount);
optionPanel.addComponents(replicatesLabel, replicatesField);
}
public final void tracesChanged() {
replicatesLabel.setEnabled(data.traces == null);
replicatesField.setEnabled(data.traces == null);
if (data.traces != null) {
replicatesField.setValue(data.traces.getStateCount());
}
replicatesField.setValue(data.replicateCount);
}
public final void collectSettings() {
data.replicateCount = replicatesField.getValue();
}
public JComponent getExportableComponent() {
return this;
}
}