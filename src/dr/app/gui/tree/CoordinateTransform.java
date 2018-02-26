package dr.app.gui.tree;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
public interface CoordinateTransform {
    double xCoordinate(double height);
    double yCoordinate(Tree tree, NodeRef node);
}
