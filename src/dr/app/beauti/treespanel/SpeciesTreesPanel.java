package dr.app.beauti.treespanel;
import dr.app.beauti.options.PartitionTreePrior;
import dr.app.beauti.types.TreePriorType;
import dr.app.beauti.types.PopulationSizeModelType;
import dr.app.beauti.util.PanelUtils;
import dr.app.util.OSType;
import jam.panels.OptionsPanel;
import javax.swing.*;
import java.awt.event.*;
import java.util.EnumSet;
public class SpeciesTreesPanel extends OptionsPanel {
	private static final long serialVersionUID = -2768091530149898538L;
	private JComboBox treePriorCombo = new JComboBox(EnumSet.range(TreePriorType.SPECIES_YULE, TreePriorType.SPECIES_BIRTH_DEATH).toArray());
    private JComboBox populationSizeCombo = new JComboBox(PopulationSizeModelType.values());
    private final PartitionTreePrior partitionTreePrior;
//    private boolean settingOptions = false;
    public SpeciesTreesPanel(final PartitionTreePrior partitionTreePrior) {
    	super(12, (OSType.isMac() ? 6 : 24));
    	this.partitionTreePrior = partitionTreePrior;
    	PanelUtils.setupComponent(treePriorCombo);
    	addComponentWithLabel("Species Tree Prior:", treePriorCombo);
        treePriorCombo.addItemListener(
                new ItemListener() {
                    public void itemStateChanged(ItemEvent ev) {
                    	partitionTreePrior.setNodeHeightPrior( (TreePriorType) treePriorCombo.getSelectedItem());
                    }
                }
        );
        PanelUtils.setupComponent(populationSizeCombo);
    	addComponentWithLabel("Population Size Model:", populationSizeCombo);
        populationSizeCombo.addItemListener(
                new ItemListener() {
                    public void itemStateChanged(ItemEvent ev) {
                    	partitionTreePrior.setPopulationSizeModel((PopulationSizeModelType) populationSizeCombo.getSelectedItem());
                    }
                }
        );
//        addSeparator();
        addLabel("Note: *BEAST only needs to select the prior for species tree.");
        validate();
        repaint();
    }
    public void setOptions() {
//        settingOptions = true;
        treePriorCombo.setSelectedItem(partitionTreePrior.getNodeHeightPrior());
        populationSizeCombo.setSelectedItem(partitionTreePrior.getPopulationSizeModel());
//        settingOptions = false;
        validate();
        repaint();
    }
    public void getOptions() {
    }
	// @Override
	public JComponent getExportableComponent() {
		// TODO Auto-generated method stub
		return null;
	}
}