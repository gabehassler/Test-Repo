
package dr.app.beauti.clockModelsPanel;

import dr.app.beauti.options.PartitionClockModel;
import dr.app.beauti.types.ClockDistributionType;
import dr.app.beauti.util.PanelUtils;
import dr.app.util.OSType;
import jam.panels.OptionsPanel;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class PartitionClockModelPanel extends OptionsPanel {

    // Components
    private static final long serialVersionUID = -1645661616353099424L;

//    private JComboBox clockTypeCombo = new JComboBox(ClockType.values());
    private JComboBox clockDistributionCombo = new JComboBox(ClockDistributionType.values());

    protected final PartitionClockModel model;

    public PartitionClockModelPanel(final PartitionClockModel partitionModel) {

        super(12, (OSType.isMac() ? 6 : 24));

        this.model = partitionModel;

//        PanelUtils.setupComponent(clockTypeCombo);
//        clockTypeCombo.addItemListener(new ItemListener() {
//            public void itemStateChanged(ItemEvent ev) {
//                model.setClockType((ClockType) clockTypeCombo.getSelectedItem());
//                setupPanel();
//            }
//        });
//        clockTypeCombo.setToolTipText("<html>Select the type of molecular clock model.</html>");
//
//        clockTypeCombo.setSelectedItem(model.getClockType());

        PanelUtils.setupComponent(clockDistributionCombo);
        clockDistributionCombo.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent ev) {
                model.setClockDistributionType((ClockDistributionType) clockDistributionCombo.getSelectedItem());
                setupPanel();
            }
        });
        clockDistributionCombo.setToolTipText("<html>Select the distribution that describes the variation in rate.</html>");

        clockDistributionCombo.setSelectedItem(model.getClockDistributionType());

        setupPanel();
        setOpaque(false);
    }


    public void setupPanel() {
        removeAll();
//        addComponentWithLabel("Clock Type:", clockTypeCombo);

        switch (model.getClockType()) {
            case STRICT_CLOCK:
                break;

            case UNCORRELATED:
            case AUTOCORRELATED:
                addComponentWithLabel("Relaxed Distribution:", clockDistributionCombo);
                break;

            case RANDOM_LOCAL_CLOCK:
                break;

            default:
                throw new IllegalArgumentException("Unknown data type");

        }
    }

}
