
package dr.evolution.tree;

import dr.evolution.io.NewickImporter;
import dr.evolution.util.TaxonList;

import java.io.FileReader;

public class TreeMetrics {

	public static double getRobinsonFouldsDistance(Tree t1, Tree t2)
	{
		SplitSystem s1 = SplitUtils.getSplits(t1);

		return getRobinsonFouldsDistance(s1, t2);
	}


	public static double getRobinsonFouldsDistance(SplitSystem s1, Tree t2)
	{
		TaxonList taxonList = s1.getTaxonList();
		SplitSystem s2 = SplitUtils.getSplits(taxonList, t2);

		if (s1.getLabelCount() != s2.getLabelCount())
			throw new IllegalArgumentException("Number of labels must be the same!");

		int ns1 = s1.getSplitCount();
		int ns2 = s2.getSplitCount();

		// number of splits in t1 missing in t2
		int fn = 0;
		for (int i = 0; i < ns1; i++)
		{
			if (!s2.hasSplit(s1.getSplit(i))) fn++;
		}

		// number of splits in t2 missing in t1
		int fp = 0;
		for (int i = 0; i < ns2; i++)
		{
			if (!s1.hasSplit(s2.getSplit(i))) fp++;
		}


		return 0.5*((double) fp + (double) fn);
	}

	public static double getRobinsonFouldsRescaledDistance(Tree t1, Tree t2)
	{
		SplitSystem s1 = SplitUtils.getSplits(t1);

		return getRobinsonFouldsRescaledDistance(s1, t2);
	}


	public static double getRobinsonFouldsRescaledDistance(SplitSystem s1, Tree t2)
	{
		return getRobinsonFouldsDistance(s1, t2)/(double) s1.getSplitCount();
	}
	
	private static void analyze(Tree[] trees, int update) {
	
		TaxonList masterList = trees[0];
	
		System.out.println("Calculating splits...");
		SplitSystem[] splits = new SplitSystem[trees.length];
		for (int i =0; i < splits.length; i++) {
			splits[i] = SplitUtils.getSplits(masterList, trees[i]);
		}
		
		int maxOffset = MAX_OFFSET;
		int samples = trees.length;
		if ((samples/3) < maxOffset) {
			maxOffset = (samples/3);
		}	
			
		double[] meanDistances = new double[maxOffset];
			
		System.out.println("Calculating mean distance per lag...");
		for (int i=0; i < maxOffset; i++) {
			meanDistances[i] = 0;
			for (int j = 0; j < samples-i; j++) {
    			double distance = getRobinsonFouldsRescaledDistance(splits[i], trees[j]);
    			meanDistances[i] += distance;
			}
			meanDistances[i] /= ((double) samples-i);
			System.out.println(meanDistances[i]);
		}
			
	}
	
	public static final void main(String[] args) throws Exception {
		FileReader reader = new FileReader(args[0]);
		NewickImporter importer = new NewickImporter(reader);
		
		Tree[] trees = importer.importTrees(null);
		System.out.println("Imported " + trees.length + " trees...");
		analyze(trees, 1000);
	}
	
	private static int MAX_OFFSET = 1000;
}
