package dr.evolution.tree;
import dr.evolution.util.TaxonList;
import java.io.PrintWriter;
import java.io.StringWriter;
public class SplitSystem
{
//
// Public stuff
//
public SplitSystem(TaxonList taxonList, int size)
{
this.taxonList = taxonList;
labelCount = taxonList.getTaxonCount();
splitCount = size;
splits = new boolean[splitCount][labelCount];
}
public int getSplitCount()
{		
return splitCount;
}
public int getLabelCount()
{		
return labelCount;
}
public boolean[][] getSplitVector()
{		
return splits;
}
public boolean[] getSplit(int i)
{		
return splits[i];
}
public TaxonList getTaxonList() { return taxonList; }
+ test whether a split is contained in this split system
public boolean hasSplit(boolean[] split)
{
for (int i = 0; i < splitCount; i++)
{
if (SplitUtils.isSame(split, splits[i])) return true;
}
return false;
}
public String toString()
{
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
for (int i = 0; i < labelCount; i++)
{
pw.println(taxonList.getTaxon(i));
}
pw.println();
for (int i = 0; i < splitCount; i++)
{
for (int j = 0; j < labelCount; j++)
{
if (splits[i][j] == true)
pw.print('*');
else
pw.print('.');
}
pw.println();
}
return sw.toString();
}
//
// Private stuff
//
private int labelCount, splitCount;
private TaxonList taxonList;
private boolean[][] splits;
}
