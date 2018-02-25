package dr.evolution.datatype;
public class SimpleCodons extends DataType {
public static final String DESCRIPTION = "simpleCodon";
public static final int TYPE = CODONS;
public static final SimpleCodons UNIVERSAL = new SimpleCodons(GeneticCode.UNIVERSAL);
public static final SimpleCodons VERTEBRATE_MT = new SimpleCodons(GeneticCode.VERTEBRATE_MT);
public static final SimpleCodons YEAST = new SimpleCodons(GeneticCode.YEAST);
public static final SimpleCodons MOLD_PROTOZOAN_MT = new SimpleCodons(GeneticCode.MOLD_PROTOZOAN_MT);
public static final SimpleCodons MYCOPLASMA = new SimpleCodons(GeneticCode.MYCOPLASMA);
public static final SimpleCodons INVERTEBRATE_MT = new SimpleCodons(GeneticCode.INVERTEBRATE_MT);
public static final SimpleCodons CILIATE = new SimpleCodons(GeneticCode.CILIATE);
public static final SimpleCodons ECHINODERM_MT = new SimpleCodons(GeneticCode.ECHINODERM_MT);
public static final SimpleCodons EUPLOTID_NUC = new SimpleCodons(GeneticCode.EUPLOTID_NUC);
public static final SimpleCodons BACTERIAL = new SimpleCodons(GeneticCode.BACTERIAL);
public static final SimpleCodons ALT_YEAST = new SimpleCodons(GeneticCode.ALT_YEAST);
public static final SimpleCodons ASCIDIAN_MT = new SimpleCodons(GeneticCode.ASCIDIAN_MT);
public static final SimpleCodons FLATWORM_MT = new SimpleCodons(GeneticCode.FLATWORM_MT);
public static final SimpleCodons BLEPHARISMA_NUC = new SimpleCodons(GeneticCode.BLEPHARISMA_NUC);
public static final int UNKNOWN_STATE = 64;
public static final int GAP_STATE = 65;
public static final String[] CODON_TRIPLETS = {
"AAA", "AAC", "AAG", "AAT", "ACA", "ACC", "ACG","ACT",
"AGA", "AGC", "AGG", "AGT", "ATA", "ATC", "ATG","ATT",
"CAA", "CAC", "CAG", "CAT", "CCA", "CCC", "CCG","CCT",
"CGA", "CGC", "CGG", "CGT", "CTA", "CTC", "CTG","CTT",
"GAA", "GAC", "GAG", "GAT", "GCA", "GCC", "GCG","GCT",
"GGA", "GGC", "GGG", "GGT", "GTA", "GTC", "GTG","GTT",
"TAA", "TAC", "TAG", "TAT", "TCA", "TCC", "TCG","TCT",
"TGA", "TGC", "TGG", "TGT", "TTA", "TTC", "TTG","TTT",
"???", "---"
};
private SimpleCodons(GeneticCode geneticCode) {
this.geneticCode = geneticCode;
stateCount = 64;
ambiguousStateCount = 66;
}
@Override
public char[] getValidChars() {
return null;
}
public final int getState(char c)
{
throw new IllegalArgumentException("Codons datatype cannot be expressed as char");
}
public int getUnknownState() {
return UNKNOWN_STATE;
}
public int getGapState() {
return GAP_STATE;
}
public final int getState(char nuc1, char nuc2, char nuc3)
{
return getState(Nucleotides.INSTANCE.getState(nuc1), 
Nucleotides.INSTANCE.getState(nuc2), 
Nucleotides.INSTANCE.getState(nuc3));
}
public final int getState(int nuc1, int nuc2, int nuc3)
{
if (Nucleotides.INSTANCE.isGapState(nuc1) ||
Nucleotides.INSTANCE.isGapState(nuc2) ||
Nucleotides.INSTANCE.isGapState(nuc3)) {
return GAP_STATE;
}
if (Nucleotides.INSTANCE.isAmbiguousState(nuc1) ||
Nucleotides.INSTANCE.isAmbiguousState(nuc2) ||
Nucleotides.INSTANCE.isAmbiguousState(nuc3)) {
return UNKNOWN_STATE;
}
return (nuc1 * 16) + (nuc2 * 4) + nuc3;
}
public final char getChar(int state)
{
throw new IllegalArgumentException("Codons datatype cannot be expressed as char");
}
public final String getTriplet(int state)
{
return CODON_TRIPLETS[state];
}
public final int[] getTripletStates(int state)
{
int[] triplet = new int[3];
triplet[0] = Nucleotides.INSTANCE.getState(CODON_TRIPLETS[state].charAt(0));
triplet[1] = Nucleotides.INSTANCE.getState(CODON_TRIPLETS[state].charAt(1));
triplet[2] = Nucleotides.INSTANCE.getState(CODON_TRIPLETS[state].charAt(2));
return triplet;
}
public String getDescription() {
return DESCRIPTION;
}
public int getType() {
return TYPE;
}
public GeneticCode getGeneticCode() {
return geneticCode;
}
public final boolean isStopCodon(int state) {
return geneticCode.isStopCodon(state);
}
// Private members
private GeneticCode geneticCode;
}
