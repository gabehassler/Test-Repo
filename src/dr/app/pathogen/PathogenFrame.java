
package dr.app.pathogen;

import dr.evolution.io.*;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.util.TaxonList;
import dr.app.tools.NexusExporter;
import dr.stats.Regression;
import dr.util.NumberFormatter;
import figtree.treeviewer.TreeViewer;
import jam.framework.DocumentFrame;
import jam.framework.Exportable;
import jam.panels.SearchPanel;
import jam.panels.SearchPanelListener;
import jam.toolbar.Toolbar;
import jam.toolbar.ToolbarAction;
import jam.toolbar.ToolbarButton;
import jam.util.IconUtils;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;

public class PathogenFrame extends DocumentFrame {

    private static final long serialVersionUID = 2114148696789612509L;

    private JTabbedPane tabbedPane = new JTabbedPane();
    private JLabel statusLabel = new JLabel("No data loaded");

    private PathogenPanel pathogenPanel;

    TaxonList taxa = null;
    java.util.List<Tree> trees = new ArrayList<Tree>();

    public PathogenFrame(String title) {
        super();

        setTitle(title);

        // Prevent the application to close in requestClose()
        // after a user cancel or a failure in beast file generation
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        getOpenAction().setEnabled(true);
        getSaveAction().setEnabled(false);
        getSaveAsAction().setEnabled(false);

        getFindAction().setEnabled(false);

        getCutAction().setEnabled(false);
        getPasteAction().setEnabled(false);
        getDeleteAction().setEnabled(false);
        getSelectAllAction().setEnabled(false);

        getCopyAction().setEnabled(false);

        getZoomWindowAction().setEnabled(false);
    }

    public void initializeComponents() {

        pathogenPanel = new PathogenPanel(this, taxa, trees.get(0));

        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.add(pathogenPanel, BorderLayout.CENTER);

        statusLabel.setBorder(new BorderUIResource.EmptyBorderUIResource(new Insets(0, 12, 6, 12)));
        panel.add(statusLabel, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(panel, BorderLayout.CENTER);

        setSize(new Dimension(1024, 768));

        setStatusMessage();
    }

    public void timeScaleChanged() {
        pathogenPanel.timeScaleChanged();
        setStatusMessage();
    }

    protected boolean readFromFile(File file) throws IOException {
        Reader reader = new FileReader(file);

        BufferedReader bufferedReader = new BufferedReader(reader);
        String line = bufferedReader.readLine();
        while (line != null && line.length() == 0) {
            line = bufferedReader.readLine();
        }

        boolean isNexus = (line != null && line.toUpperCase().contains("#NEXUS"));

        reader = new FileReader(file);

        Tree tree = null;
        try {
            if (isNexus) {
                NexusImporter importer = new NexusImporter(reader);
                tree = importer.importTree(taxa);
            } else {
                NewickImporter importer = new NewickImporter(reader);
                tree = importer.importTree(taxa);
            }

        } catch (Importer.ImportException ime) {
            JOptionPane.showMessageDialog(this, "Error parsing imported file: " + ime,
                    "Error reading file",
                    JOptionPane.ERROR_MESSAGE);
            ime.printStackTrace();
            return false;
        } catch (IOException ioex) {
            JOptionPane.showMessageDialog(this, "File I/O Error: " + ioex,
                    "File I/O Error",
                    JOptionPane.ERROR_MESSAGE);
            ioex.printStackTrace();
            return false;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Fatal exception: " + ex,
                    "Error reading file",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }


        if (tree == null) {
            JOptionPane.showMessageDialog(this, "The file is not in a suitable format or contains no trees.",
                    "Error reading file",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        FlexibleTree binaryTree = new FlexibleTree(tree, true);
        binaryTree.resolveTree();
        trees.add(binaryTree);
        if (taxa == null) {
            taxa = binaryTree;
        }

        getExportTreeAction().setEnabled(true);
        getExportDataAction().setEnabled(true);

        return true;
    }

    protected boolean writeToFile(File file) throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void doExportTree() {
        FileDialog dialog = new FileDialog(this,
                "Export Tree File...",
                FileDialog.SAVE);

        dialog.setVisible(true);
        if (dialog.getFile() != null) {
            File file = new File(dialog.getDirectory(), dialog.getFile());

            PrintStream ps = null;
            try {
                ps = new PrintStream(file);
                writeTreeFile(ps, false);
                ps.close();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Error writing tree file: " + ioe.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void doExportTimeTree() {
        FileDialog dialog = new FileDialog(this,
                "Export Time Tree File...",
                FileDialog.SAVE);

        dialog.setVisible(true);
        if (dialog.getFile() != null) {
            File file = new File(dialog.getDirectory(), dialog.getFile());

            PrintStream ps = null;
            try {
                ps = new PrintStream(file);
                writeTimeTreeFile(ps);
                ps.close();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Error writing tree file: " + ioe.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    protected void writeTimeTreeFile(PrintStream ps) throws IOException {

        FlexibleTree tree = new FlexibleTree(pathogenPanel.getTreeAsViewed());

        Regression r = pathogenPanel.getTemporalRooting().getRootToTipRegression(pathogenPanel.getTreeAsViewed());

        for (int i = 0; i < tree.getInternalNodeCount(); i++) {
            NodeRef node = tree.getInternalNode(i);
            double height = tree.getNodeHeight(node);
            tree.setNodeHeight(node, height/r.getGradient());
        }

        TreeUtils.setHeightsFromDates(tree);

        NexusExporter nexusExporter = new NexusExporter(new PrintStream(ps));
        nexusExporter.exportTree(tree);
    }


    protected void writeTreeFile(PrintStream ps, boolean newickFormat) throws IOException {

        Tree tree = pathogenPanel.getTreeAsViewed();

//        if (newickFormat) {
//            NewickExporter newickExporter = new NewickExporter(ps);
//            newickExporter.exportTree(tree);
//        } else {
        NexusExporter nexusExporter = new NexusExporter(new PrintStream(ps));
        nexusExporter.exportTree(tree);
//        }
    }

//    protected void doExportGraphic() {
//        ExportDialog export = new ExportDialog();
//        export.showExportDialog( this, "Export view as ...", treeViewer.getContentPane(), "export" );
//    }

    protected void doExportData() {
        FileDialog dialog = new FileDialog(this,
                "Export Data File...",
                FileDialog.SAVE);

        dialog.setVisible(true);
        if (dialog.getFile() != null) {
            File file = new File(dialog.getDirectory(), dialog.getFile());

            Writer writer = null;
            try {
                writer = new PrintWriter(file);
                pathogenPanel.writeDataFile(writer);
                writer.close();
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(this, "Error writing data file: " + ioe.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    private void setStatusMessage() {
        Tree tree = pathogenPanel.getTree();
        if (tree != null) {
            String message = "";
            message += "Tree loaded, " + tree.getTaxonCount() + " taxa";

            TemporalRooting tr = pathogenPanel.getTemporalRooting();
            if (tr.isContemporaneous()) {
                message += ", contemporaneous tips";
            } else {
                NumberFormatter nf = new NumberFormatter(3);
                message += ", dated tips with range " + nf.format(tr.getDateRange());
            }
            statusLabel.setText(message);
        }
    }

    public JComponent getExportableComponent() {

        JComponent exportable = null;
        Component comp = tabbedPane.getSelectedComponent();

        if (comp instanceof Exportable) {
            exportable = ((Exportable) comp).getExportableComponent();
        } else if (comp instanceof JComponent) {
            exportable = (JComponent) comp;
        }

        return exportable;
    }

    @Override
    public void doCopy() {
        StringWriter writer = new StringWriter();
        PrintWriter pwriter = new PrintWriter(writer);

        for (String tip : pathogenPanel.getSelectedTips()) {
            pwriter.println(tip);
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(writer.toString());
        clipboard.setContents(selection, selection);
    }

    public Action getExportTreeAction() {
        return exportTreeAction;
    }

//    public Action getExportGraphicAction() {
//        return exportGraphicAction;
//    }

    public Action getExportDataAction() {
        return exportDataAction;
    }

    protected AbstractAction exportTreeAction = new AbstractAction("Export Tree...") {
        public void actionPerformed(ActionEvent ae) {
            doExportTree();
        }
    };

//    protected AbstractAction exportGraphicAction = new AbstractAction("Export Graphic...") {
//        public void actionPerformed(ActionEvent ae) {
//            doExportGraphic();
//        }
//    };

    protected AbstractAction exportDataAction = new AbstractAction("Export Data...") {
        public void actionPerformed(ActionEvent ae) {
            doExportData();
        }
    };

    public Action getExportTimeTreeAction() {
        return exportTimeTreeAction;
    }

    protected AbstractAction exportTimeTreeAction = new AbstractAction("Export Time Tree...") {
        public void actionPerformed(ActionEvent ae) {
            doExportTimeTree();
        }
    };


}