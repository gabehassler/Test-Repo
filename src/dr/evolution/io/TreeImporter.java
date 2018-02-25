package dr.evolution.io;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import java.io.IOException;
public interface TreeImporter { 
boolean hasTree() throws IOException, Importer.ImportException;
Tree importNextTree() throws IOException, Importer.ImportException;
Tree importTree(TaxonList taxonList) throws IOException, Importer.ImportException;
Tree[] importTrees(TaxonList taxonList) throws IOException, Importer.ImportException;
}
