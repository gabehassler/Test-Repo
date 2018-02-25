package dr.app.tools;
import dr.evolution.io.Importer;
import dr.evolution.io.MigrateTreeImporter;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import dr.evolution.tree.TreeTrait;
import dr.evolution.tree.TreeTraitProvider;
import java.io.*;
public class ConvertMigrateTrees {
static boolean forceDiscrete = false;
public static final String LABEL = "pop";
public static void main(String[] args) throws IOException, Importer.ImportException {
String migrateFile = null;
if (args.length == 1) {
migrateFile = args[0];
} else if (args.length == 2 && args[0].equals("-d")) {
forceDiscrete = true;
migrateFile = args[1];
} else {
System.out.println("USAGE: ConvertMigrateTrees [-d] <migrateTreeFileName>");
System.exit(0);
}
File file = makeInputNexus(migrateFile);
MigrateTreeImporter importer = new MigrateTreeImporter(new FileReader(file));
System.out.println("Starting conversion...");
final Tree[] trees = importer.importTrees(null);
writeNexus(migrateFile + ".figtree.trees", trees);
}
private static void writeNexus(String filename, Tree[] trees) throws IOException {
PrintWriter out = new PrintWriter(new FileWriter(filename));
TreeTraitProvider.Helper popAttributes = new TreeTraitProvider.Helper();
popAttributes.addTrait( new TreeTrait.S() {
public String getTraitName() {
return LABEL;
}
public Intent getIntent() {
return Intent.NODE;
}
public int getDimension() {
return 1;
}
public String getTrait(Tree tree, NodeRef node) {
Object attribute = tree.getNodeAttribute(node, MigrateTreeImporter.POP);
if (attribute == null) {
throw new RuntimeException(MigrateTreeImporter.POP + " is null for node " + node.getNumber());
}
return (forceDiscrete ? "d" : "") + attribute.toString();
}
});
writeNexusHeader(out);
for (int i = 0; i < trees.length; i++) {
out.println("tree tree_" + i + " = " +
Tree.Utils.newick(trees[i], new TreeTraitProvider[] { popAttributes}));
}
out.println("end;");
}
private static void writeNexusHeader(PrintWriter out) {
out.println("#NEXUS");
out.println();
out.println("begin trees;");
out.println();
}
private static File makeInputNexus(String migrateFile) throws IOException {
File temp = File.createTempFile(migrateFile, ".nexus");
System.out.println("Making temporary file: " + temp);
PrintWriter writer = new PrintWriter(new FileWriter(temp));
writeNexusHeader(writer);
BufferedReader reader = new BufferedReader(new FileReader(migrateFile));
String line = reader.readLine();
int treeCount = 0;
while (line != null) {
if (line.startsWith("[& Locus")) {
writer.write("tree tree" + treeCount + " = " + line);
treeCount += 1;
if (treeCount % 100 == 0) {
System.out.print(".");
System.out.flush();
}
} else {
writer.println(line);
}
line = reader.readLine();
}
writer.println("end;");
writer.close();
reader.close();
return temp;
}
}