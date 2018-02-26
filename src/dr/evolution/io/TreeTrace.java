package dr.evolution.io;
import dr.evolution.tree.Tree;
import dr.util.Identifiable;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
public class TreeTrace implements Identifiable {
    public TreeTrace() {
    }
    public int getTreeCount(int burnin) {
        int startIndex = (burnin - minState) / stepSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        return trees.size() - startIndex;
    }
    public Tree getTree(int index, int burnin) {
        int startIndex = (burnin - minState) / stepSize;
        if (startIndex < 0) {
            startIndex = 0;
        }
        return trees.get(index + startIndex);
    }
    public void add(Tree tree) {
        trees.add(tree);
    }
    public void setMinimumState(int minState) {
        this.minState = minState;
    }
    public int getMinimumState() {
        return minState;
    }
    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }
    public int getStepSize() {
        return stepSize;
    }
    public int getMaximumState() {
        return (trees.size() - 1) * stepSize + minState;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    private ArrayList<Tree> trees = new ArrayList<Tree>();
    private int minState;
    private int stepSize;
    private String id;
    public static TreeTrace loadTreeTrace(Reader r) throws IOException, Importer.ImportException {
        BufferedReader reader = new BufferedReader(r);
        TreeTrace trace = new TreeTrace();
        dr.evolution.util.TaxonList taxonList = null;
        int minState = -1;
        int stepSize = 0;
        String line = reader.readLine();
        if (line.toUpperCase().startsWith("#NEXUS")) {
            NexusImporter importer = new NexusImporter(reader);
            Tree[] trees = importer.importTrees(null);
            if (trees.length < 2) {
                throw new Importer.ImportException("Less than two trees in the trace file");
            }
            String id1 = trees[0].getId();
            String id2 = trees[1].getId();
            minState = getStateNumber(id1);
            stepSize = getStateNumber(id2) - minState;
            for (Tree tree : trees) {
                trace.add(tree);
            }
        } else {
            NewickImporter importer = new NewickImporter(reader);
            while (true) {
                int state = 0;
                Tree tree;
                try {
                    state = importer.readInteger();
                    tree = importer.importTree(taxonList);
                    if (taxonList == null) {
                        // The first tree becomes the taxon list. This means
                        // that all subsequent trees will look up their taxa
                        // in that taxon list rather than creating their own
                        // duplicitous ones.
                        taxonList = tree;
                    }
                } catch (Importer.ImportException ie) {
                    System.out.println("Error reading tree for state " + state);
                    throw ie;
                } catch (EOFException e) {
                    break;
                }
                if (minState == -1) {
                    minState = state;
                } else if (stepSize == 0) {
                    stepSize = state - minState;
                }
                trace.add(tree);
            }
        }
        trace.setMinimumState(minState);
        trace.setStepSize(stepSize);
        return trace;
    }
    private static int getStateNumber(String id) throws Importer.ImportException {
        try {
            if (id.indexOf('_') != -1) { // probably BEAST tree file
                return Integer.parseInt(id.substring(id.indexOf('_') + 1));
            } else if (id.indexOf('.') != -1) { // probably MrBayes tree file
                int rep = Integer.parseInt(id.substring(id.indexOf('.') + 1));
                if (rep == 1) rep = 0;
                return rep;
            }
            throw new NumberFormatException();
        } catch (NumberFormatException nfe) {
            throw new Importer.ImportException("Bad state number in tree label '" + id + "', the state must be preceded by an underscore(_).");
        }
    }
}