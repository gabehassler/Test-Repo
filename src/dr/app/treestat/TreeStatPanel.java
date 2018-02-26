package dr.app.treestat;
import jam.framework.Exportable;
import javax.swing.*;
import java.awt.*;
public class TreeStatPanel extends javax.swing.JPanel implements Exportable {
	private static final long serialVersionUID = 2437334458007083790L;
	private JTabbedPane tabbedPane = new JTabbedPane();
	private TaxonSetsPanel taxonSetsPanel;
	//private CharactersPanel charactersPanel;
	private StatisticsPanel statisticsPanel;
//	final Icon traceIcon = new ImageIcon(Utils.getImage(this, "/images/trace-small-icon.gif"));
	public TreeStatPanel(TreeStatFrame frame, TreeStatData treeStatData) {
		taxonSetsPanel = new TaxonSetsPanel(frame, treeStatData);
		//charactersPanel = new CharactersPanel(frame, treeStatData);
		statisticsPanel = new StatisticsPanel(frame, treeStatData);
		tabbedPane.addTab("Taxon Sets", null, taxonSetsPanel);
		//tabbedPane.addTab("Characters", null, charactersPanel);
		tabbedPane.addTab("Statistics", null, statisticsPanel);
		setLayout(new BorderLayout());
		add(tabbedPane, BorderLayout.CENTER);
	}
	public void fireDataChanged() {
		taxonSetsPanel.dataChanged();
		//charactersPanel.dataChanged();
		statisticsPanel.dataChanged();
	}
    public void doCopy() {
    	//summaryPanel.copyToClipboard();
			case 0: summaryPanel.copyToClipboard(); break;
			case 1: densityPanel.copyToClipboard(); break;
			case 2: tracePanel.copyToClipboard(); break;
		}*/
	}
    public JComponent getExportableComponent() {
		JComponent exportable = null;
		Component comp = tabbedPane.getSelectedComponent();
		if (comp instanceof Exportable) {
			exportable = ((Exportable)comp).getExportableComponent();
		} else if (comp instanceof JComponent) {
			exportable = (JComponent)comp;
		}
		return exportable;
	}
	//************************************************************************
	// private methods
	//************************************************************************
}
