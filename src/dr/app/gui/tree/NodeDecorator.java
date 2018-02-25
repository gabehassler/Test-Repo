package dr.app.gui.tree;
import dr.evolution.tree.Tree;
import dr.evolution.tree.NodeRef;
import java.awt.*;
public interface NodeDecorator {
boolean isDecoratable(Tree tree, NodeRef node);
void decorateNode(Tree tree, NodeRef node, Graphics2D g2, CoordinateTransform transform);
}
