
package dr.app.beauti.treespanel;

import dr.app.beauti.BeautiFrame;
import dr.app.pathogen.TemporalRooting;
import dr.evolution.tree.Tree;
import dr.app.gui.tree.JTreeDisplay;
import dr.app.gui.tree.SquareTreePainter;

import javax.swing.*;
import java.awt.*;

public class TreeDisplayPanel extends JPanel {

    private Tree tree = null;

    BeautiFrame frame = null;
    JTabbedPane tabbedPane = new JTabbedPane();

    JTreeDisplay treePanel;
    JTreeDisplay scaledTreePanel;
//    JChartPanel rootToTipPanel;
//    JChart rootToTipChart;

    public TreeDisplayPanel(BeautiFrame parent) {
        super(new BorderLayout());

        this.frame = parent;

        treePanel = new JTreeDisplay(new SquareTreePainter());

        tabbedPane.add("Starting Tree", treePanel);

//      AR - have removed root-to-tip chart for now.
//        rootToTipChart = new JChart(new LinearAxis(), new LinearAxis(Axis.AT_ZERO, Axis.AT_MINOR_TICK));
//        rootToTipPanel = new JChartPanel(rootToTipChart, "", "time", "divergence");
//        rootToTipPanel.setOpaque(false);
//
//        tabbedPane.add("Root-to-tip", rootToTipPanel);

        scaledTreePanel = new JTreeDisplay(new SquareTreePainter());
        tabbedPane.add("Re-scaled tree", scaledTreePanel);

        setOpaque(false);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void setTree(Tree tree) {
        this.tree = tree;
        setupPanel();
    }

    private void setupPanel() {
        if (tree != null) {
            treePanel.setTree(tree);
            TemporalRooting temporalRooting = new TemporalRooting(tree);

//            Regression r = temporalRooting.getRootToTipRegression(tree);
//
//            rootToTipChart.removeAllPlots();
//            rootToTipChart.addPlot(new ScatterPlot(r.getXData(), r.getYData()));
//            rootToTipChart.addPlot(new RegressionPlot(r));
//            rootToTipChart.getXAxis().addRange(r.getXIntercept(), r.getXData().getMax());

            scaledTreePanel.setTree(temporalRooting.adjustTreeToConstraints(tree, null));
        } else {
            treePanel.setTree(null);
//            rootToTipChart.removeAllPlots();
            scaledTreePanel.setTree(null);
        }

        repaint();
    }
}