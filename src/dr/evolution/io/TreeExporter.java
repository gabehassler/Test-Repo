
package dr.evolution.io;

import dr.evolution.tree.Tree;

public interface TreeExporter {

	void exportTree(Tree tree);

	void exportTrees(Tree[] trees);
}
