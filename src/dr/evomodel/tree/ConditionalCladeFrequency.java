package dr.evomodel.tree;
import dr.evolution.io.Importer;
import dr.evolution.io.NewickImporter;
import dr.evolution.io.NexusImporter;
import dr.evolution.io.TreeTrace;
import dr.evolution.tree.Clade;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.SimpleTree;
import dr.evolution.tree.Tree;
import dr.inference.model.Likelihood;
import dr.inference.prior.Prior;
import dr.math.MathUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
public class ConditionalCladeFrequency extends
        AbstractCladeImportanceDistribution {
    private double EPSILON;
    private long samples = 0;
    private HashMap<BitSet, Clade> cladeProbabilities;
    private HashMap<BitSet, HashMap<BitSet, Clade>> cladeCoProbabilities;
    private TreeTrace[] traces;
    private int burnin;
    public ConditionalCladeFrequency(Tree tree, double epsilon) {
        // initializing global variables
        cladeProbabilities = new HashMap<BitSet, Clade>();
        cladeCoProbabilities = new HashMap<BitSet, HashMap<BitSet, Clade>>();
        // setting global variables
        EPSILON = epsilon;
    }
    public ConditionalCladeFrequency(TreeTrace[] traces, double epsilon,
                                     int burnIn, boolean verbose) {
        // initializing global variables
        cladeProbabilities = new HashMap<BitSet, Clade>();
        cladeCoProbabilities = new HashMap<BitSet, HashMap<BitSet, Clade>>();
        // setting global variables
        EPSILON = epsilon;
        this.traces = traces;
        // calculates the burn-in to 10% if it was set out of the boundaries
        int minMaxState = Integer.MAX_VALUE;
        for (TreeTrace trace : traces) {
            if (trace.getMaximumState() < minMaxState) {
                minMaxState = trace.getMaximumState();
            }
        }
        if (burnIn < 0 || burnIn >= minMaxState) {
            this.burnin = minMaxState / (10 * traces[0].getStepSize());
            if (verbose)
                System.out
                        .println("WARNING: Burn-in larger than total number of states - using 10% of smallest trace");
        } else {
            this.burnin = burnIn;
        }
        // analyzing the whole trace -> reading the trees
        analyzeTrace(verbose);
    }
    public void analyzeTrace(boolean verbose) {
        if (verbose) {
            if (traces.length > 1)
                System.out.println("Combining " + traces.length + " traces.");
        }
        // get first tree to extract the taxon
        Tree tree = getTree(0);
        // taxonMap = getTaxonMap(tree);
        // read every tree from the trace
        for (TreeTrace trace : traces) {
            // do some output stuff
            int treeCount = trace.getTreeCount(burnin * trace.getStepSize());
            double stepSize = treeCount / 60.0;
            int counter = 1;
            if (verbose) {
                System.out.println("Analyzing " + treeCount + " trees...");
                System.out
                        .println("0              25             50             75            100");
                System.out
                        .println("|--------------|--------------|--------------|--------------|");
                System.out.print("*");
            }
            for (int i = 1; i < treeCount; i++) {
                // get the next tree
                tree = trace.getTree(i, burnin * trace.getStepSize());
                // add the tree and its clades to the frequencies
                addTree(tree);
                // some more output stuff
                if (i >= (int) Math.round(counter * stepSize) && counter <= 60) {
                    if (verbose) {
                        System.out.print("*");
                        System.out.flush();
                    }
                    counter += 1;
                }
            }
            if (verbose) {
                System.out.println("*");
            }
        }
    }
    public void report(Reader r) throws IOException, Importer.ImportException {
        System.err.println("making report"); 
        ArrayList<Tree> referenceTrees = new ArrayList<Tree>();
        BufferedReader reader = new BufferedReader(r);
        String line = reader.readLine();
        if (line.toUpperCase().startsWith("#NEXUS")) {
        	NexusImporter importer = new NexusImporter(reader);
        	Tree[] trees = importer.importTrees(null);
        	for (Tree tree : trees) {
        		referenceTrees.add(tree);
        		SimpleTree sTree = new SimpleTree(tree);
        		System.out.println("Estimated marginal posterior by condiational clade frequencies:");
        		System.out.println(getTreeProbability(sTree) + "\t\t" + sTree);
        	}
        } else {
      	    throw new RuntimeException("Could not read reference tree. Only Nexus format is supported.");
        }
        System.out.flush();
    }
    public double getTreeProbability(Tree tree) {
        double prob = 0.0;
        List<Clade> clades = new ArrayList<Clade>();
        List<Clade> parentClades = new ArrayList<Clade>();
        // get clades contained in the tree
        getNonComplementaryClades(tree, tree.getRoot(), parentClades, clades);
        int size = clades.size();
        // for every clade multiply its conditional clade probability to the tree probability
        for (int i = 0; i < size; i++) {
            Clade c = clades.get(i);
            // get the bits of the clade
            Clade parent = parentClades.get(i);
            // set the occurrences to epsilon
            double tmp = EPSILON;
            double parentOccurrences = 0.0;
            BitSet parentBits = parent.getBits();
            if (cladeProbabilities.containsKey(parentBits)) {
                // if we observed this clade in the trace, add the occurrences to epsilon
                parentOccurrences += cladeProbabilities.get(parentBits)
                        .getSampleCount();
            }
            if (cladeCoProbabilities.containsKey(parentBits)) {
                // if we observed the parent clade
                HashMap<BitSet, Clade> conditionalProbs = cladeCoProbabilities
                        .get(parentBits);
                BitSet bits = c.getBits();
                if (conditionalProbs.containsKey(bits)) {
                    // if we observed this conditional clade in the trace, add the occurrences to epsilon
                    tmp += conditionalProbs.get(bits).getSampleCount();
                }
            }
            // add epsilon for each clade
            final double splits = Math.pow(2, parent.getSize() - 1) - 1;
            parentOccurrences += EPSILON * splits;
            // multiply the conditional clade probability to the tree
            // probability
            prob += Math.log(tmp / parentOccurrences);
        }
        return prob;
    }
    public double getTreeProbability(Tree tree,
                                     HashMap<String, Integer> taxonMap) {
        double prob = 0.0;
        List<Clade> clades = new ArrayList<Clade>();
        List<Clade> parentClades = new ArrayList<Clade>();
        // get clades contained in the tree
        getNonComplementaryClades(tree, tree.getRoot(), parentClades, clades,
                taxonMap);
        int size = clades.size();
        // for every clade multiply its conditional clade probability to the
        // tree probability
        for (int i = 0; i < size; i++) {
            Clade c = clades.get(i);
            // get the bits of the clade
            Clade parent = parentClades.get(i);
            // set the occurrences to epsilon
            double tmp = EPSILON;
            double parentOccurrences = 0.0;
            BitSet parentBits = parent.getBits();
            if (cladeProbabilities.containsKey(parentBits)) {
                // if we observed this clade in the trace, add the
                // occurrences
                // to epsilon
                parentOccurrences += cladeProbabilities.get(parentBits)
                        .getSampleCount();
            }
            if (cladeCoProbabilities.containsKey(parentBits)) {
                // if we observed the parent clade
                HashMap<BitSet, Clade> conditionalProbs = cladeCoProbabilities
                        .get(parentBits);
                BitSet bits = c.getBits();
                if (conditionalProbs.containsKey(bits)) {
                    // if we observed this conditional clade in the trace,
                    // add
                    // the occurrences to epsilon
                    tmp += conditionalProbs.get(bits).getSampleCount();
                }
            }
            // add epsilon for each clade
            final double splits = Math.pow(2, parent.getSize() - 1) - 1;
            parentOccurrences += EPSILON * splits;
            // multiply the conditional clade probability to the tree
            // probability
            prob += Math.log(tmp / parentOccurrences);
        }
        return prob;
    }
    public double splitClade(Clade parent, Clade[] children) {
        // the number of all possible clades is 2^n with n the number of tips
        // reduced by 2 because we wont consider the clades with all or no tips
        // contained
        // divide this number by 2 because every clade has a matching clade to
        // form the split
        // #splits = 2^(n-1) - 1
        final double splits = Math.pow(2, parent.getSize() - 1) - 1;
        double prob = 0;
        if (cladeCoProbabilities.containsKey(parent.getBits())) {
            HashMap<BitSet, Clade> childClades = cladeCoProbabilities
                    .get(parent.getBits());
            double noChildClades = 0.0;
            double sum = 0.0;
            Set<BitSet> keys = childClades.keySet();
            for (BitSet child : keys) {
                Clade tmp = childClades.get(child);
                if (parent.getSize() > tmp.getSize() + 1) {
                    sum += (tmp.getSampleCount() + EPSILON) / 2.0;
                    noChildClades += 0.5;
                } else {
                    sum += (tmp.getSampleCount() + EPSILON);
                    noChildClades += 1.0;
                }
            }
            // add epsilon for each not observed clade
            sum += EPSILON * (splits - noChildClades);
            // roulette wheel
            double randomNumber = Math.random() * sum;
            for (BitSet child : keys) {
                Clade tmp = childClades.get(child);
                if (parent.getSize() > tmp.getSize() + 1) {
                    randomNumber -= (tmp.getSampleCount() + EPSILON) / 2.0;
                } else {
                    randomNumber -= (tmp.getSampleCount() + EPSILON);
                }
                if (randomNumber < 0) {
                    children[0] = tmp;
                    prob = (tmp.getSampleCount() + EPSILON) / sum;
                    break;
                }
            }
            if (randomNumber >= 0) {
                // randomNumber /= EPSILON;
                prob = EPSILON / sum;
                BitSet newChild;
                BitSet inverseBits;
                do {
                    do {
                        newChild = (BitSet) parent.getBits().clone();
                        int index = -1;
                        do {
                            index = newChild.nextSetBit(index + 1);
                            if (index > -1 && MathUtils.nextBoolean()) {
                                newChild.clear(index);
                            }
                        } while (index > -1);
                    } while (newChild.cardinality() == 0
                            || newChild.cardinality() == parent.getSize());
                    inverseBits = (BitSet) newChild.clone();
                    inverseBits.xor(parent.getBits());
                } while (childClades.containsKey(newChild)
                        || childClades.containsKey(inverseBits));
                Clade randomClade = new Clade(newChild, 0.9999 * parent
                        .getHeight());
                children[0] = randomClade;
                BitSet secondChild = (BitSet) children[0].getBits().clone();
                secondChild.xor(parent.getBits());
                children[1] = new Clade(secondChild, 0.9999 * parent
                        .getHeight());
            } else {
                BitSet secondChild = (BitSet) children[0].getBits().clone();
                secondChild.xor(parent.getBits());
                children[1] = childClades.get(secondChild);
                if (children[1] == null) {
                    children[1] = new Clade(secondChild, 0.9999 * parent
                            .getHeight());
                }
            }
        } else {
            prob = 1.0 / splits;
            BitSet newChild;
            do {
                newChild = (BitSet) parent.getBits().clone();
                int index = -1;
                do {
                    index = newChild.nextSetBit(index + 1);
                    if (index > -1 && MathUtils.nextBoolean()) {
                        newChild.clear(index);
                    }
                } while (index > -1);
            } while (newChild.cardinality() == 0
                    || newChild.cardinality() == parent.getSize());
            Clade randomClade = new Clade(newChild, 0.9999 * parent.getHeight());
            // randomClade.addSample();
            randomClade.addHeight(0.9999 * parent.getHeight());
            children[0] = randomClade;
            BitSet secondChild = (BitSet) children[0].getBits().clone();
            secondChild.xor(parent.getBits());
            children[1] = new Clade(secondChild, 0.9999 * parent.getHeight());
            // children[1].addSample();
            randomClade.addHeight(0.9999 * parent.getHeight());
        }
        return Math.log(prob);
    }
    public double getChanceForNodeHeights(TreeModel tree,
                                          Likelihood likelihood, Prior prior) {
        double prob = 0.0;
        NodeRef node = tree.getRoot();
        Clade currentClade = getClade(tree, node);
        int childcount = tree.getChildCount(node);
        for (int i = 0; i < childcount; i++) {
            NodeRef child = tree.getChild(node, i);
            if (!tree.isExternal(child)) {
//				prob += getChanceForNodeheights(tree, child, currentClade,
//						likelihood, prior);
            }
        }
        return prob;
    }
    public double setNodeHeights(TreeModel tree, Likelihood likelihood,
                                 Prior prior) {
        double prob = 0.0;
        NodeRef node = tree.getRoot();
        Clade currentClade = getClade(tree, node);
        int childcount = tree.getChildCount(node);
        for (int i = 0; i < childcount; i++) {
            NodeRef child = tree.getChild(node, i);
            if (!tree.isExternal(child)) {
//				prob += setNodeHeights(tree, child, currentClade, likelihood,
//						prior);
            }
        }
        return prob;
    }
    public final Tree getTree(int index) {
        int oldTreeCount = 0;
        int newTreeCount = 0;
        for (TreeTrace trace : traces) {
            newTreeCount += trace.getTreeCount(burnin * trace.getStepSize());
            if (index < newTreeCount) {
                return trace.getTree(index - oldTreeCount, burnin
            }
            oldTreeCount = newTreeCount;
        }
        throw new RuntimeException("Couldn't find tree " + index);
    }
    public void addTree(Tree tree) {
        samples++;
        List<Clade> clades = new ArrayList<Clade>();
        List<Clade> parentClades = new ArrayList<Clade>();
        // get clades contained in the tree
        getClades(tree, tree.getRoot(), parentClades, clades);
        // add the clade containing all taxa as well so that it get counted
        clades.add(parentClades.get(parentClades.size() - 1));
        parentClades.add(clades.get(clades.size() - 1));
        int size = clades.size();
        // for every clade multiply its conditional clade probability to the
        // tree probability
        for (int i = 0; i < size; i++) {
            Clade c = clades.get(i);
            // get the bits of the clade
            Clade parent = parentClades.get(i);
            HashMap<BitSet, Clade> coFreqs;
            // increment the clade occurrences
            if (cladeProbabilities.containsKey(c.getBits())) {
                Clade tmp = cladeProbabilities.get(c.getBits());
                // tmp.addSample();
                tmp.addHeight(c.getHeight());
                // add the amount to the current occurences
                // frequency += cladeProbabilities.get(c);
            } else {
                // just to set the first value of the height value list
                // c.addSample();
                c.addHeight(c.getHeight());
                cladeProbabilities.put(c.getBits(), c);
            }
            // increment the conditional clade occurrences
            if (!parent.equals(c)) {
                if (cladeCoProbabilities.containsKey(parent.getBits())) {
                    coFreqs = cladeCoProbabilities.get(parent.getBits());
                } else {
                    // if it's the first time we observe the parent then we need
                    // a new list for its conditional clades
                    coFreqs = new HashMap<BitSet, Clade>();
                    cladeCoProbabilities.put(parent.getBits(), coFreqs);
                }
                // add the previous observed occurrences for this conditional
                // clade
                if (coFreqs.containsKey(c.getBits())) {
                    Clade tmp = coFreqs.get(c.getBits());
                    tmp.addHeight(c.getHeight());
                    // coFrequency += coFreqs.get(c.getBits());
                } else {
                    // TODO check this code, especially if the cloning is needed
                    // and not just the clade could be added
                    Clade tmp = new Clade((BitSet) c.getBits().clone(), c.getHeight());
                    tmp.addHeight(c.getHeight());
                    coFreqs.put(c.getBits(), tmp);
                }
            }
        }
    }
    public void addTree(Tree tree, HashMap<String, Integer> taxonMap) {
        samples++;
        List<Clade> clades = new ArrayList<Clade>();
        List<Clade> parentClades = new ArrayList<Clade>();
        // get clades contained in the tree
        getClades(tree, tree.getRoot(), parentClades, clades, taxonMap);
        // add the clade containing all taxa as well so that it get counted
        clades.add(parentClades.get(parentClades.size() - 1));
        parentClades.add(clades.get(clades.size() - 1));
        int size = clades.size();
        // for every clade multiply its conditional clade probability to the
        // tree probability
        for (int i = 0; i < size; i++) {
            Clade c = clades.get(i);
            // get the bits of the clade
            Clade parent = parentClades.get(i);
            HashMap<BitSet, Clade> coFreqs;
            // increment the clade occurrences
            if (cladeProbabilities.containsKey(c.getBits())) {
                Clade tmp = cladeProbabilities.get(c.getBits());
                // tmp.addSample();
                tmp.addHeight(c.getHeight());
                // add the amount to the current occurences
                // frequency += cladeProbabilities.get(c);
            } else {
                // just to set the first value of the height value list
                // c.addSample();
                c.addHeight(c.getHeight());
                cladeProbabilities.put(c.getBits(), c);
            }
            // increment the conditional clade occurrences
            if (!parent.equals(c)) {
                if (cladeCoProbabilities.containsKey(parent.getBits())) {
                    coFreqs = cladeCoProbabilities.get(parent.getBits());
                } else {
                    // if it's the first time we observe the parent then we need
                    // a new list for its conditional clades
                    coFreqs = new HashMap<BitSet, Clade>();
                    cladeCoProbabilities.put(parent.getBits(), coFreqs);
                }
                // add the previous observed occurrences for this conditional
                // clade
                if (coFreqs.containsKey(c.getBits())) {
                    Clade tmp = coFreqs.get(c.getBits());
                    tmp.addHeight(c.getHeight());
                    // coFrequency += coFreqs.get(c.getBits());
                } else {
                    // TODO check this code, especially if the cloning is needed
                    // and not just the clade could be added
                    Clade tmp = new Clade((BitSet) c.getBits().clone(), c
                            .getHeight());
                    tmp.addHeight(c.getHeight());
                    coFreqs.put(c.getBits(), tmp);
                }
            }
        }
    }
    public static ConditionalCladeFrequency analyzeLogFile(Reader[] reader,
                                                           double e, int burnin, boolean verbose) throws IOException {
        TreeTrace[] trace = new TreeTrace[reader.length];
        for (int i = 0; i < reader.length; i++) {
            try {
                trace[i] = TreeTrace.loadTreeTrace(reader[i]);
            } catch (Importer.ImportException ie) {
                throw new RuntimeException(ie.toString());
            }
            reader[i].close();
        }
        return new ConditionalCladeFrequency(trace, e, burnin, verbose);
    }
}
