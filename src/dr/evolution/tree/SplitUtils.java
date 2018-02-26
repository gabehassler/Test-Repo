
package dr.evolution.tree;

import dr.evolution.util.TaxonList;

public class SplitUtils
{
	//
	// Public stuff
	//

	public static SplitSystem getSplits(TaxonList taxonList, Tree tree)
	{
		int size = tree.getInternalNodeCount()-1;
		SplitSystem splitSystem = new SplitSystem(taxonList, size);

		boolean[][] splits = splitSystem.getSplitVector();
	
		int j = 0;
		for (int i = 0; i < tree.getInternalNodeCount(); i++) {
			NodeRef node = tree.getInternalNode(i);
			if (node != tree.getRoot()) {
				getSplit(taxonList, tree, node, splits[j]);
				j++;
			}
		}

		return splitSystem;
	}



	public static SplitSystem getSplits(Tree tree)
	{
        return getSplits(tree, tree);
	}



	public static void getSplit(TaxonList taxonList, Tree tree, NodeRef internalNode, boolean[] split)
	{
		if (tree.isExternal(internalNode) || tree.isRoot(internalNode))
		{
			throw new IllegalArgumentException("Only internal nodes (and no root) nodes allowed");
		}

		// make sure split is reset
		for (int i = 0; i < split.length; i++)
		{
			split[i] = false;
		}

		// mark all leafs downstream of the node

		for (int i = 0; i < tree.getChildCount(internalNode); i++)
		{
			markNode(taxonList, tree, internalNode, split);
		}

		// standardize split (i.e. first index is alway true)
		if ( !split[0] )
		{
			for (int i = 0; i < split.length; i++)
			{
                split[i] = !split[i];
			}
		}
	}

	public static boolean isSame(boolean[] s1, boolean[] s2)
	{
		boolean reverse;
        reverse = s1[0] != s2[0];

		if (s1.length != s2.length)
			throw new IllegalArgumentException("Splits must be of the same length!");

		for (int i = 0; i < s1.length; i++)
		{
			if (reverse)
			{
				// splits not identical
				if (s1[i] == s2[i]) return false;
			}
			else
			{
				// splits not identical
				if (s1[i] != s2[i]) return false;
			}
		}

		return true;
	}

	//
	// Package stuff
	//

	static void markNode(TaxonList taxonList, Tree tree, NodeRef node, boolean[] split)
	{
		if (tree.isExternal(node))
		{
			String name = tree.getTaxonId(node.getNumber());
			int index = taxonList.getTaxonIndex(name);

			if (index < 0)
			{
				throw new IllegalArgumentException("INCOMPATIBLE IDENTIFIER (" + name + ")");
			}

			split[index] = true;
		}
		else
		{
			for (int i = 0; i < tree.getChildCount(node); i++)
			{
				markNode(taxonList, tree, tree.getChild(node, i), split);
			}
		}
	}

}
