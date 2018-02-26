package dr.app.bfe;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
public class XMLViewer implements ActionListener, ListSelectionListener, TreeSelectionListener {
    private JPanel panel;
    private JTextField filenameField;
    private JButton browseButton;
    private JButton viewButton;
    private JTree xmlTree;
    private JButton expandButton;
    private JButton contractButton;
    private JList idElementList;
    public JFrame getFrame() {
        return frame;
    }
    private JFrame frame;
    JFileChooser fc;
    public XMLViewer() {
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        browseButton.addActionListener(this);
        viewButton.addActionListener(this);
        expandButton.addActionListener(this);
        contractButton.addActionListener(this);
        xmlTree.setModel(new XMLTreeModel(""));
        idElementList.addListSelectionListener(this);
        xmlTree.addTreeSelectionListener(this);
    }
    public XMLViewer(boolean standalone) {
        //super();
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        browseButton.addActionListener(this);
        viewButton.addActionListener(this);
        expandButton.addActionListener(this);
        contractButton.addActionListener(this);
        xmlTree.setModel(new XMLTreeModel(""));
        idElementList.addListSelectionListener(this);
        xmlTree.addTreeSelectionListener(this);
        if (standalone) {
            frame = new JFrame("XML Structure Viewer");
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
    }
    protected void view(String filename) {
        xmlTree.setModel(new XMLTreeModel(filename));
        idElementList.setModel(((XMLTreeModel) xmlTree.getModel()).getXmlModel());
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseButton) {
            int returnVal = fc.showOpenDialog(this.panel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                String z = null;
                try {
                    z = file.getCanonicalPath();
                    filenameField.setText(z);
                    view(filenameField.getText());
                } catch (IOException fe) {
                    System.err.println("something went wrong with: " + z);
                }
            }
        } else if (e.getSource() == viewButton) {
            view(filenameField.getText());
        } else if (e.getSource() == expandButton) {
            for (int i = 0; i < xmlTree.getRowCount(); i++) {
                xmlTree.expandRow(i);
            }
        } else if (e.getSource() == contractButton) {
            for (int i = xmlTree.getRowCount(); i > 0; i--) {
                xmlTree.collapseRow(i);
            }
        }
    }
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == idElementList && !e.getValueIsAdjusting()) {
            int index = idElementList.getSelectedIndex();
            if (index >= 0) {
                String selectedID = (String) idElementList.getModel().getElementAt(index);
                System.err.println("Element with id: " + selectedID + " selected");
                //TODO: add code to highlight the elements refering to this id in the XMLTree
                highlightID(selectedID);
            }
        }
    }
    public void highlightID(String id) {
        xmlTree.clearSelection();
        if (id == null || id.equals("")) {
            return;
        }
        for (int i = xmlTree.getRowCount(); i > 0; i--) {
            xmlTree.collapseRow(i);
        }
        TreePath rootPath = xmlTree.getPathForRow(0);
        highlightID(id, rootPath);
    }
    public void highlightID(String id, TreePath node) {
        if (((XMLTreeModel.ElementObject) node.getLastPathComponent()).getId().equals(id)) {
            xmlTree.expandPath(node);
            xmlTree.addSelectionPath(node);
        } else {
            XMLTreeModel.ElementObject parent = (XMLTreeModel.ElementObject) node.getLastPathComponent();
            for (XMLTreeModel.ElementObject child : parent.getChildren()) {
                highlightID(id, node.pathByAddingChild(child));
            }
        }
    }
    public void valueChanged(TreeSelectionEvent e) {
        idElementList.clearSelection();
    }
    public JList getIdElementList() {
        return idElementList;
    }
    public static void main(String[] args) {
        XMLViewer z = new XMLViewer(true);
    }
}
