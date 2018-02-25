package dr.evolution.tree;
import dr.evolution.datatype.DataType;
import java.util.List;
public interface ColouredTree extends Tree {
int getColour(NodeRef node);
int getColour(NodeRef node, double time);
DataType getColourDataType();
List getColourChanges(NodeRef node);
public class Utils {
public static int getChangeCount(ColouredTree tree) {
int changeCount = 0;
int nodeCount = tree.getNodeCount();
for (int i = 0; i < nodeCount; i++) {
NodeRef node = tree.getNode(i);
if (!tree.isRoot(node)) {
changeCount += tree.getColourChanges(tree.getNode(i)).size();
}
}
return changeCount;
}
}
}
