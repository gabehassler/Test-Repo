
package dr.evomodel.speciation;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import jebl.util.FixedBitSet;

import dr.evolution.tree.NodeRef;
import dr.evolution.util.Taxon;
import dr.evomodel.tree.TreeModel;
import dr.evomodelxml.speciation.AlloppSpeciesBindingsParser;
import dr.inference.loggers.LogColumn;
import dr.inference.loggers.Loggable;
import dr.inference.model.AbstractModel;
import dr.inference.model.Model;
import dr.inference.model.ModelListener;
import dr.inference.model.Variable;
import dr.inference.model.Variable.ChangeType;
import dr.math.MathUtils;
import dr.util.AlloppMisc;




public class AlloppSpeciesBindings extends AbstractModel implements Loggable {

    private final GeneTreeInfo[] geneTreeInfos;

    private final ApSpInfo[] apspecies;
    private final Taxon[] taxa;
    private final Map<Taxon, Integer> taxon2index = new HashMap<Taxon, Integer>();
    private final int spsq[][];
    private final int numberOfSpSeqs;
    private final double initialmingenenodeheight; // for starting network




    public static class Individual extends Taxon {
        final public String id; // individual ID, such as "02_Alpha" in AlloppSpeciesInfoParser XML example
        public final Taxon[] taxa;

        public Individual(String id, Taxon[] taxa) {
            super(id);
            this.id = id;
            this.taxa = taxa;
        }
    }


    private class SpeciesIndivPair {
        public int spIndex;
        public int ivIndex;

        public SpeciesIndivPair(int spIndex, int ivIndex) {
            this.spIndex = spIndex;
            this.ivIndex = ivIndex;
        }
    }


    public static class ApSpInfo extends Taxon {

        final public String name;
        final public int ploidylevel; // 2 means diploid, 4 means allotetraploid, etc
        final Individual[] individuals;

        public ApSpInfo(String name, int ploidylevel, Individual[] individuals) {
            super(name);
            this.name = name;
            this.individuals = individuals;
            this.ploidylevel = ploidylevel;

            // check
            if (individuals != null) {
                int ntaxaperindiv = ploidylevel / 2;
                for (int ii = 0; ii < individuals.length; ++ii) {
                    assert(individuals[ii].taxa.length == ntaxaperindiv);
                    // may want to allow 3 as well as 2 for tetraploid for organelle DNA
                }
            }
        }


        public Taxon taxonFromIndSeq(int i, int sq) {
            return individuals[i].taxa[sq];
        }
    }


    private class GeneTreeInfo {
        private final TreeModel tree;
        private SequenceAssignment seqassigns[];
        private SequenceAssignment oldseqassigns[];
        private final int[] lineagesCount;
        private final double popFactor; // grjtodo-oneday will mul pops by this, eg for chloroplast data.


        private class SequenceAssignment {
            public int spIndex;
            public int seqIndex;

            public SequenceAssignment(int spIndex, int seqIndex) {
                this.spIndex = spIndex;
                this.seqIndex = seqIndex;
            }

            public String toString() {
                String s = "" + seqIndex;
                return s;
            }
        }




        private class GeneUnionNode {
            private GeneUnionNode child[];
            private double height;
            private FixedBitSet union;
            private String name; // for debugging

            // Constructor makes a half-formed tip node. Tips need unions
            // and internal nodes need all fields filling in.
            public GeneUnionNode() {
                child = new GeneUnionNode[0];
                height = 0.0;
                union = new FixedBitSet(numberOfSpSeqs());
                name = "";
            }



            public String asText(int indentlen) {
                StringBuilder s = new StringBuilder();
                Formatter formatter = new Formatter(s, Locale.US);
                if (child.length == 0) {
                    formatter.format("%s ", name);
                } else {
                    formatter.format("%s ", "+");
                }
                while (s.length() < 30-indentlen) {
                    formatter.format("%s", " ");
                }
                formatter.format("%s ", AlloppMisc.nonnegIn8Chars(height));
                formatter.format("%20s ", AlloppMisc.FixedBitSetasText(union));

                return s.toString();
            }

        }

        private class GeneUnionTree {
            private GeneUnionNode[] nodes;
            private int nextn;


            public GeneUnionTree() {
                nodes = new GeneUnionNode[tree.getNodeCount()];
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i] = new GeneUnionNode();
                }
                genetree2geneuniontree(tree.getRoot());
            }


            public GeneUnionNode getRoot() {
                return nodes[nodes.length-1];
            }



            private boolean subtreeFitsInNetwork(GeneUnionNode node,
                                                 final AlloppSpeciesNetworkModel asnm) {
                for (int i = 0; i < node.child.length; i++) {
                    if (!subtreeFitsInNetwork(node.child[i], asnm)) {
                        return false;
                    }
                }
                return asnm.coalescenceIsCompatible(node.height, node.union);
            }


            private void subtreeRecordCoalescences(GeneUnionNode node,
                                                   final AlloppSpeciesNetworkModel asnm) {
                for (int i = 0; i < node.child.length; i++) {
                    subtreeRecordCoalescences(node.child[i], asnm);
                }
                if (node.child.length > 0) {
                    asnm.recordCoalescence(node.height, node.union);
                }
            }


            private void genetree2geneuniontree(NodeRef gnode) {
                if (tree.isExternal(gnode)) {
                    nodes[nextn].child = new GeneUnionNode[0];
                    int ti = taxon2index.get(tree.getNodeTaxon(gnode));
                    int spseq = spsq[seqassigns[ti].spIndex][seqassigns[ti].seqIndex];
                    nodes[nextn].union.set(spseq);
                    nodes[nextn].name = tree.getNodeTaxon(gnode).getId();
                } else {
                    genetree2geneuniontree(tree.getChild(gnode,0));
                    int c0 = nextn - 1;
                    genetree2geneuniontree(tree.getChild(gnode,1));
                    int c1 = nextn - 1;
                    nodes[nextn].child = new GeneUnionNode[2];
                    nodes[nextn].child[0] = nodes[c0];
                    nodes[nextn].child[1] = nodes[c1];
                    nodes[nextn].union.union(nodes[c0].union);
                    nodes[nextn].union.union(nodes[c1].union);
                }
                nodes[nextn].height = tree.getNodeHeight(gnode);
                nextn++;
            }





            public String asText() {
                String s = "";
                Stack<Integer> x = new Stack<Integer>();
                return subtreeAsText(getRoot(), s, x, 0, "");
            }


            private String subtreeAsText(GeneUnionNode node, String s, Stack<Integer> x, int depth, String b) {
                Integer[] y = x.toArray(new Integer[x.size()]);
                StringBuffer indent = new StringBuffer();
                for (int i = 0; i < depth; i++) {
                    indent.append("  ");
                }
                for (int i = 0; i < y.length; i++) {
                    indent.replace(2*y[i], 2*y[i]+1, "|");
                }
                if (b.length() > 0) {
                    indent.replace(indent.length()-b.length(), indent.length(), b);
                }
                s += indent;
                s += node.asText(indent.length());
                s += System.getProperty("line.separator");
                String subs = "";
                if (node.child.length > 0) {
                    x.push(depth);
                    subs += subtreeAsText(node.child[0], "", x, depth+1, "-");
                    x.pop();
                    subs += subtreeAsText(node.child[1], "", x, depth+1, "`-");
                }
                return s + subs;
            }

        } // end GeneTreeInfo.GeneUnionTree


        GeneTreeInfo(TreeModel tree, double popFactor, boolean permuteSequenceAssignments) {
            this.tree = tree;
            this.popFactor = popFactor;
            seqassigns = new SequenceAssignment[taxa.length];
            oldseqassigns = new SequenceAssignment[taxa.length];

            // This uses taxa list for *all* gene trees, not this gene tree.
            for (int s = 0; s < apspecies.length; s++) {
                for (int i = 0; i < apspecies[s].individuals.length; i++) {
                    int nseqs = apspecies[s].individuals[i].taxa.length;
                    int asgns[] = new int [nseqs];
                    for (int x = 0; x < nseqs; x++) {
                        asgns[x] = x;
                    }
                    if (permuteSequenceAssignments) { MathUtils.permute(asgns); }
                    for (int x = 0; x < nseqs; x++) {
                        int t = taxon2index.get(apspecies[s].individuals[i].taxa[x]);
                        seqassigns[t] = new SequenceAssignment(s, asgns[x]);
                        oldseqassigns[t] = new SequenceAssignment(s, asgns[x]);
                    }
                }
            }

            lineagesCount = new int[apspecies.length];
            Arrays.fill(lineagesCount, 0);

            for (int nl = 0; nl < lineagesCount.length; ++nl) {
                for (Individual indiv : apspecies[nl].individuals) {
                    boolean got = false;
                    for (Taxon t : indiv.taxa) {
                        if (tree.getTaxonIndex(t) >= 0) {
                            got = true;
                        }
                    }
                    for (Taxon t : indiv.taxa) {
                        assert (tree.getTaxonIndex(t) >= 0) == got;
                    }
                    assert got;
                    if (got) {
                        ++lineagesCount[nl];
                    }
                }
            }
        }


        public String seqassignsAsText() {
            String s = "Sequence assignments" + System.getProperty("line.separator");
            for (int tx = 0; tx < seqassigns.length; tx++) {
                s += taxa[tx];
                s += ":";
                s += seqassigns[tx].seqIndex;
                if (tx+1 < seqassigns.length  &&  seqassigns[tx].spIndex != seqassigns[tx+1].spIndex) {
                    s += System.getProperty("line.separator");
                } else {
                    s += "  ";
                }
            }
            return s;
        }



        public String genetreeAsText() {
            GeneUnionTree gutree = new GeneUnionTree();
            return gutree.asText();
        }


        public boolean fitsInNetwork(final AlloppSpeciesNetworkModel asnm) {
            GeneUnionTree gutree = new GeneUnionTree();
            boolean fits = gutree.subtreeFitsInNetwork(gutree.getRoot(), asnm);
            if (AlloppSpeciesNetworkModel.DBUGTUNE) {
                if (!fits) {
                    System.err.println("INCOMPATIBLE");
                    System.err.println(seqassignsAsText());
                    System.err.println(gutree.asText());
                    System.err.println(asnm.mullabTreeAsText());
                }
            }
            return fits;
        }


        // returns log(P(g_i|S)) = probability that gene tree fits into species network
        public double treeLogLikelihood(final AlloppSpeciesNetworkModel asnm) {
            GeneUnionTree gutree = new GeneUnionTree();
            asnm.clearCoalescences();
            gutree.subtreeRecordCoalescences(gutree.getRoot(), asnm);
            asnm.sortCoalescences();
            asnm.recordLineageCounts();
            double llhood = asnm.geneTreeInNetworkLogLikelihood();
            if (AlloppSpeciesNetworkModel.DBUGTUNE) {
                System.err.println("COMPATIBLE: log-likelihood = " + llhood);
                System.err.println(seqassignsAsText());
                System.err.println(gutree.asText());
                System.err.println(asnm.mullabTreeAsText());
            }
            return llhood;
        }


        public void storeSequenceAssignments() {
            for (int i = 0; i < seqassigns.length; i++) {
                oldseqassigns[i].seqIndex = seqassigns[i].seqIndex;
            }
        }

        public void restoreSequenceAssignments() {
            for (int i = 0; i < seqassigns.length; i++) {
                seqassigns[i].seqIndex = oldseqassigns[i].seqIndex;
            }
        }




        public double spseqUpperBound(FixedBitSet spsq0, FixedBitSet spsq1) {
            GeneUnionTree gutree = new GeneUnionTree();
            return subtreeSpseqUpperBound(gutree.getRoot(), spsq0, spsq1, Double.MAX_VALUE);
        }



        public void permuteOneSpeciesOneIndiv() {
            int sp = MathUtils.nextInt(apspecies.length);
            int iv = MathUtils.nextInt(apspecies[sp].individuals.length);
            flipOneAssignment(sp, iv);
        }


        public void permuteSetOfIndivs() {
            int num = tree.getInternalNodeCount();
            int i = MathUtils.nextInt(num);
            NodeRef node = tree.getInternalNode(i);
            Set<SpeciesIndivPair> spivs = new HashSet<SpeciesIndivPair>();
            collectIndivsOfNode(node, spivs);
            for (SpeciesIndivPair spiv : spivs) {
                flipOneAssignment(spiv.spIndex, spiv.ivIndex);
            }
        }




        public SequenceAssignment getSeqassigns(int tx) {
            return seqassigns[tx];
        }


        // called when a gene tree has changed, which affects likelihood.
        // 2011-08-12 I am not using dirty flags (yet). I return
        // false from getLikelihoodKnown() in AlloppMSCoalescent
        // and that seems to be sufficient.
        public void wasChanged() {
        }






        private void collectIndivsOfNode(NodeRef node, Set<SpeciesIndivPair> spivs) {
            if (tree.isExternal(node)) {
                SpeciesIndivPair x = apspeciesId2speciesindiv(tree.getNodeTaxon(node).getId());
                spivs.add(x);
            } else {
                collectIndivsOfNode(tree.getChild(node, 0), spivs);
                collectIndivsOfNode(tree.getChild(node, 1), spivs);
            }
        }





        // start at root of gutree and recurse.
        // A node which has one child which contains some of species spp0
        // and where the other contains some of species spp1, imposes a limit
        // on how early a speciation can occur.
        private double subtreeSpseqUpperBound(GeneUnionNode node,
                                              FixedBitSet spsq0, FixedBitSet spsq1, double bound) {
            if (node.child.length == 0) {
                return bound;
            }
            for (GeneUnionNode ch : node.child) {
                bound = Math.min(bound, subtreeSpseqUpperBound(ch, spsq0, spsq1, bound));
            }
            FixedBitSet genespp0 = node.child[0].union;
            int int00 = genespp0.intersectCardinality(spsq0);
            int int01 = genespp0.intersectCardinality(spsq1);
            FixedBitSet genespp1 = node.child[1].union;
            int int10 = genespp1.intersectCardinality(spsq0);
            int int11 = genespp1.intersectCardinality(spsq1);
            if ((int00 > 0 && int11 > 0)  ||  (int10 > 0 && int01 > 0)) {
                bound = Math.min(bound, node.height);
            }
            return bound;
        }



        private void flipOneAssignment(int sp, int iv) {
            // grjtodo-tetraonly
            int tx;
            if (apspecies[sp].individuals[iv].taxa.length == 2) {
                tx = taxon2index.get(apspecies[sp].individuals[iv].taxa[0]);
                seqassigns[tx].seqIndex = 1 - seqassigns[tx].seqIndex;
                tx = taxon2index.get(apspecies[sp].individuals[iv].taxa[1]);
                seqassigns[tx].seqIndex = 1 - seqassigns[tx].seqIndex;
            }
        }



        private void flipAssignmentsForSpecies(int sp) {
            for (int iv = 0; iv < apspecies[sp].individuals.length; iv++) {
                flipOneAssignment(sp, iv);
            }
        }



    }
    // end of GeneTreeInfo





    public AlloppSpeciesBindings(ApSpInfo[] apspecies, TreeModel[] geneTrees,
                                 double minheight, double[] popFactors, boolean permuteSequenceAssignments) {
        super(AlloppSpeciesBindingsParser.ALLOPPSPECIES);

        this.apspecies = apspecies;
        initialmingenenodeheight = minheight;
        // make the flattened arrays
        int n = 0;
        for (int s = 0; s < apspecies.length; s++) {
            n += apspecies[s].individuals.length;
        }
        Individual [] indivs = new Individual[n];
        n = 0;
        for (int s = 0; s < apspecies.length; s++) {
            for (int i = 0; i < apspecies[s].individuals.length; i++, n++) {
                indivs[n] =  apspecies[s].individuals[i];
            }
        }
        int t = 0;
        for (int i = 0; i < indivs.length; i++) {
            t += indivs[i].taxa.length;
        }
        taxa = new Taxon[t];
        t = 0;
        for (int i = 0; i < indivs.length; i++) {
            for (int j = 0; j < indivs[i].taxa.length; j++, t++) {
                taxa[t] =  indivs[i].taxa[j];
            }
        }
        // set up maps to indices
        for (int i = 0; i < taxa.length; i++) {
            taxon2index.put(taxa[i], i);
        }
        spsq = new int[apspecies.length][];
        int spsqindex = 0;
        for (int sp = 0; sp < apspecies.length; sp++) {
            spsq[sp] = new int[apspecies[sp].ploidylevel/2];
            for (int seq = 0; seq < spsq[sp].length; seq++, spsqindex++) {
                spsq[sp][seq] = spsqindex;
            }
        }
        numberOfSpSeqs = spsqindex;

        geneTreeInfos = new GeneTreeInfo[geneTrees.length];
        for (int i = 0; i < geneTrees.length; i++) {
            geneTreeInfos[i] = new GeneTreeInfo(geneTrees[i], popFactors[i], permuteSequenceAssignments);
        }

        for (GeneTreeInfo gti : geneTreeInfos) {
            NodeRef[] nodes = gti.tree.getNodes();
            for (NodeRef node : nodes) {
                if (!gti.tree.isExternal(node)) {
                    double height = gti.tree.getNodeHeight(node);
                    gti.tree.setNodeHeight(node, minheight+height);
                }
            }
        }
    }


    public AlloppSpeciesBindings(ApSpInfo[] apspecies, TreeModel[] geneTrees,
                                 double minheight, double[] popFactors) {
        this(apspecies, geneTrees,  minheight, popFactors, true);
    }

    public AlloppSpeciesBindings(ApSpInfo[] apspecies) {
        this(apspecies, new TreeModel[0], 0.0, new double[0]);
    }


    public double initialMinGeneNodeHeight() {
        return initialmingenenodeheight;
    }



    public FixedBitSet speciesseqEmptyUnion() {
        FixedBitSet union = new FixedBitSet(numberOfSpSeqs());
        return union;
    }


    // Taxons vs species.
    // Taxons may have a final "0", "1",... to distinguish sequences, while
    // species do not. AlloppLeggedTree uses a SimpleTree, which only has
    // Taxons, so same thing there. Multree needs distinguishable Taxons
    // so has suffices.
    public FixedBitSet taxonseqToTipUnion(Taxon tx, int seq) {
        FixedBitSet union = speciesseqEmptyUnion();
        int sp = apspeciesId2index(tx.getId());
        int spseq = spandseq2spseqindex(sp, seq);
        union.set(spseq);
        return union;
    }


    public FixedBitSet spsqunion2spunion(FixedBitSet spsqunion) {
        FixedBitSet spunion = new FixedBitSet(apspecies.length);
        for (int sp = 0; sp < apspecies.length; sp++) {
            boolean got = false;
            for (int seq = 0; seq < spsq[sp].length; seq++) {
                if (spsqunion.contains(spsq[sp][seq])) {
                    got = true;
                }
            }
            if (got) {
                spunion.set(sp);
            }
        }
        return spunion;
    }


    public int numberOfGeneTrees() {
        return geneTreeInfos.length;
    }


    public double maxGeneTreeHeight() {
        if (geneTreeInfos.length == 0) {
            return 999;   // for test code only
        }
        double maxheight = 0.0;
        for (GeneTreeInfo gti : geneTreeInfos) {
            double height = gti.tree.getNodeHeight(gti.tree.getRoot());
            if (height > maxheight) {
                maxheight = height;
            }
        }
        return maxheight;
    }




    public boolean geneTreeFitsInNetwork(int i, final AlloppSpeciesNetworkModel asnm) {
        return geneTreeInfos[i].fitsInNetwork(asnm);
    }

    public double geneTreeLogLikelihood(int i, final AlloppSpeciesNetworkModel asnm) {
        return geneTreeInfos[i].treeLogLikelihood(asnm);
    }


    public int numberOfSpecies() {
        return apspecies.length;
    }



    public String apspeciesName(int i) {
        return apspecies[i].name;
    }



    public Taxon[] SpeciesWithinPloidyLevel(int pl) {
        ArrayList<Taxon> names = new ArrayList<Taxon>();
        for (int i = 0; i < apspecies.length; i++) {
            if (apspecies[i].ploidylevel == pl) {
                names.add(new Taxon(apspecies[i].name));
            }
        }
        Taxon[] spp = new Taxon[names.size()];
        names.toArray(spp);
        return spp;
    }


    public int spandseq2spseqindex(int sp, int seq) {
        return spsq[sp][seq];
    }



    public int spseqindex2sp(int spsqindex) {
        return spseqindex2spandseq(spsqindex)[0];
    }

    public int spseqindex2seq(int spsqindex) {
        return spseqindex2spandseq(spsqindex)[1];
    }



    public int apspeciesId2index(String apspId) {
        int index = -1;
        for (int i = 0; i < apspecies.length; i++) {
            if (apspecies[i].name.compareTo(apspId) == 0) {
                assert index == -1;
                index = i;
            }
        }
        if (index == -1) {
            System.out.println("BUG in apspeciesId2index");
        }
        assert index != -1;
        return index;
    }


    public SpeciesIndivPair apspeciesId2speciesindiv(String apspId) {
        int sp = -1;
        int iv = -1;
        for (int s = 0; s < apspecies.length; s++) {
            for (int i = 0; i < apspecies[s].individuals.length; i++) {
                for (int t = 0; t < apspecies[s].individuals[i].taxa.length; t++) {
                    Taxon taxon = apspecies[s].individuals[i].taxa[t];
                    if (taxon.getId().compareTo(apspId) == 0) {
                        sp = s;
                        iv = i;
                    }
                }
            }
        }
        assert sp != -1;
        SpeciesIndivPair x = new SpeciesIndivPair(sp, iv);

        return x;
    }


    public int numberOfSpSeqs() {
        return numberOfSpSeqs;
    }


    int nLineages(int speciesIndex) {
        int n = geneTreeInfos[0].lineagesCount[speciesIndex];
        for (GeneTreeInfo gti : geneTreeInfos) {
            assert gti.lineagesCount[speciesIndex] == n;
        }
        return n;
    }



    public double spseqUpperBound(FixedBitSet left, FixedBitSet right) {
        double bound = Double.MAX_VALUE;
        for (GeneTreeInfo gti : geneTreeInfos) {
            bound = Math.min(bound, gti.spseqUpperBound(left, right));
        }
        return bound;
    }



    public void permuteOneSpeciesOneIndivForOneGene() {
        int i = MathUtils.nextInt(geneTreeInfos.length);
        geneTreeInfos[i].permuteOneSpeciesOneIndiv();
    }


    public void permuteSetOfIndivsForOneGene() {
        int i = MathUtils.nextInt(geneTreeInfos.length);
        geneTreeInfos[i].permuteSetOfIndivs();
    }


    public void flipAssignmentsForAllGenesOneSpecies(int sp) {
        for (GeneTreeInfo gti : geneTreeInfos) {
            gti.flipAssignmentsForSpecies(sp);
        }

    }



    public String seqassignsAsText(int g) {
        return geneTreeInfos[g].seqassignsAsText();
    }


    public String genetreeAsText(int g) {
        String s = "Gene tree " + g + "                     height             union" + System.getProperty("line.separator");
        s += geneTreeInfos[g].genetreeAsText();
        return s;
    }


    @Override
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        for (GeneTreeInfo g : geneTreeInfos) {
            if (g.tree == model) {
                g.wasChanged();
                break;
            }
        }
        fireModelChanged(object, index);
        if (AlloppSpeciesNetworkModel.DBUGTUNE)
            System.err.println("AlloppSpeciesBindings.handleModelChangedEvent() " + model.getId());
    }

    @Override
    protected void handleVariableChangedEvent(Variable variable, int index,
                                              ChangeType type) {
        assert false; // copies SpeciesBindings; not understood
        if (AlloppSpeciesNetworkModel.DBUGTUNE)
            System.err.println("AlloppSpeciesBindings.handleVariableChangedEvent() " + variable.getId());
    }

    @Override
    protected void storeState() {
        for (GeneTreeInfo gti : geneTreeInfos) {
            gti.storeSequenceAssignments();
        }
        if (AlloppSpeciesNetworkModel.DBUGTUNE)
            System.err.println("AlloppSpeciesBindings.storeState()");
    }

    @Override
    protected void restoreState() {
        for (GeneTreeInfo gti : geneTreeInfos) {
            gti.restoreSequenceAssignments();
            if (AlloppSpeciesNetworkModel.DBUGTUNE)
                System.err.println("AlloppSpeciesBindings.restoreState()");
        }

    }

    @Override
    protected void acceptState() {
    }


    public void addModelListeners(ModelListener listener) {
        for (GeneTreeInfo gti : geneTreeInfos) {
            gti.tree.addModelListener(listener);
        }
        addModelListener(listener); // for sequence assignments
    }



    public LogColumn[] getColumns() {
        int ncols = geneTreeInfos.length * taxa.length;
        LogColumn[] columns = new LogColumn[ncols];
        for (int g = 0, i = 0; g < geneTreeInfos.length; g++) {
            for (int tx = 0; tx < taxa.length; tx++, i++) {
                GeneTreeInfo.SequenceAssignment sqa = geneTreeInfos[g].getSeqassigns(tx);
                String header = "Gene" + g + "taxon" + tx;
                columns[i] = new LogColumn.Default(header, sqa);
            }

        }

        return columns;
    }



    private int[] spseqindex2spandseq(int spsqindex) {
        int indexp = -1;
        int indexq = -1;
        for (int p = 0; p < spsq.length; p++) {
            for (int q = 0; q < spsq[p].length; q++) {
                if (spsq[p][q] == spsqindex) {
                    assert indexp == -1;
                    assert indexq == -1;
                    indexp = p;
                    indexq = q;
                }
            }
        }
        assert indexp != -1;
        assert indexq != -1;
        int[] pq = new int[2];
        pq[0] = indexp;
        pq[1] = indexq;
        return pq;
    }

}



