package dr.app.gui.tree;
import dr.evolution.tree.Tree;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
public class JTreePanel extends JPanel {
private static final long serialVersionUID = 914666063619563914L;
public JTreePanel(JTreeDisplay treeDisplay) {
setOpaque(false);
setLayout(new BorderLayout(0,0));
this.treeDisplay = treeDisplay;
if (treeDisplay != null) {
add(treeDisplay, BorderLayout.CENTER);
}
JToolBar toolBar = new JToolBar();
toolBar.setOpaque(true);
toolBar.setBorder(BorderFactory.createCompoundBorder(
BorderFactory.createMatteBorder(0,0,1,0,Color.gray),
BorderFactory.createEmptyBorder(6,6,6,6)));
Icon firstIcon = null;
Icon prevIcon = null;
Icon nextIcon = null;
Icon lastIcon = null;
try {
firstIcon = new ImageIcon(dr.app.util.Utils.getImage(this, "/images/first.png"));
prevIcon = new ImageIcon(dr.app.util.Utils.getImage(this, "/images/prev.png"));
nextIcon = new ImageIcon(dr.app.util.Utils.getImage(this, "/images/next.png"));
lastIcon = new ImageIcon(dr.app.util.Utils.getImage(this, "/images/last.png"));
} catch (Exception e) { }
//		firstButton = new JButton(firstIcon);
firstButton = new JButton("First Tree");
firstButton.putClientProperty("JButton.buttonType", "toolbar");
firstButton.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e) {
firstTree();
}
});
firstButton.setOpaque(false);
firstButton.setEnabled(true);
//		prevButton = new JButton(prevIcon);
prevButton = new JButton("Previous Tree");
prevButton.putClientProperty("JButton.buttonType", "toolbar");
prevButton.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e) {
prevTree();
}
});
prevButton.setOpaque(false);
prevButton.setEnabled(true);
//		nextButton = new JButton(nextIcon);
nextButton = new JButton("Next Tree");
nextButton.putClientProperty("JButton.buttonType", "toolbar");
nextButton.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e) {
nextTree();
}
});
nextButton.setOpaque(false);
nextButton.setEnabled(true);
//		lastButton = new JButton(lastIcon);
lastButton = new JButton("Last Tree");
lastButton.putClientProperty("JButton.buttonType", "toolbar");
lastButton.addActionListener(new ActionListener() {
public void actionPerformed(ActionEvent e) {
lastTree();
}
});
lastButton.setOpaque(false);
lastButton.setEnabled(true);
statusField = new JLabel();
//		statusField.setBorder(BorderFactory.createLineBorder(Color.gray));
toolBar.add(firstButton);
toolBar.add(prevButton);
toolBar.add(nextButton);
toolBar.add(lastButton);
toolBar.add(new JToolBar.Separator());
toolBar.add(statusField);
toolBar.add(new JToolBar.Separator());
//		toolBar.add(new JSpinner());
//		add(toolBar, BorderLayout.NORTH);
}
public void setTrees(ArrayList trees) {
boolean isLast = (currentTree == this.trees.size() - 1);
this.trees = trees;
if (this.trees == null) {
// create an empty tree list.
this.trees = new ArrayList();
}
if (isLast) {
currentTree = trees.size() - 1;
} else {
currentTree = 0;
}
showTree(currentTree);
}
public void firstTree() {
currentTree = 0;
showTree(currentTree);
}
public void nextTree() {
if (currentTree < trees.size() - 1) {
currentTree++;
showTree(currentTree);
}
}
public void prevTree() {
if (currentTree > 0) {
currentTree--;
showTree(currentTree);
}
}
public void lastTree() {
currentTree = trees.size() - 1;
showTree(currentTree);
}
public void showTree(int index) {
if (index < 0 || index >= trees.size()) {
treeDisplay.setTree(null);
statusField.setText("0/0");
} else {
treeDisplay.setTree((Tree)trees.get(index));
statusField.setText(Integer.toString(index+1) + "/" + Integer.toString(trees.size()));
}
repaint();
}
private JTreeDisplay treeDisplay = null;
private int currentTree = 0;
private ArrayList trees = new ArrayList();
private JButton firstButton = null;
private JButton prevButton = null;
private JButton nextButton = null;
private JButton lastButton = null;
private JLabel statusField = null;
}
