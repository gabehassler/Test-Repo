package dr.evolution.datatype;
public class Codons extends DataType {
public static final String DESCRIPTION = "codon";
public static final int TYPE = CODONS;
public static final Codons UNIVERSAL = new Codons(GeneticCode.UNIVERSAL);
public static final Codons VERTEBRATE_MT = new Codons(GeneticCode.VERTEBRATE_MT);
public static final Codons YEAST = new Codons(GeneticCode.YEAST);
public static final Codons MOLD_PROTOZOAN_MT = new Codons(GeneticCode.MOLD_PROTOZOAN_MT);
public static final Codons MYCOPLASMA = new Codons(GeneticCode.MYCOPLASMA);
public static final Codons INVERTEBRATE_MT = new Codons(GeneticCode.INVERTEBRATE_MT);
public static final Codons CILIATE = new Codons(GeneticCode.CILIATE);
public static final Codons ECHINODERM_MT = new Codons(GeneticCode.ECHINODERM_MT);
public static final Codons EUPLOTID_NUC = new Codons(GeneticCode.EUPLOTID_NUC);
public static final Codons BACTERIAL = new Codons(GeneticCode.BACTERIAL);
public static final Codons ALT_YEAST = new Codons(GeneticCode.ALT_YEAST);
public static final Codons ASCIDIAN_MT = new Codons(GeneticCode.ASCIDIAN_MT);
public static final Codons FLATWORM_MT = new Codons(GeneticCode.FLATWORM_MT);
public static final Codons BLEPHARISMA_NUC = new Codons(GeneticCode.BLEPHARISMA_NUC);
public static final Codons NO_STOPS = new Codons(GeneticCode.NO_STOPS);
public static final int UNKNOWN_STATE = 64;
public static final int GAP_STATE = 65;
public static final String[] CODON_TRIPLETS = {
"AAA", "AAC", "AAG", "AAT", "ACA", "ACC", "ACG", "ACT",
"AGA", "AGC", "AGG", "AGT", "ATA", "ATC", "ATG", "ATT",
"CAA", "CAC", "CAG", "CAT", "CCA", "CCC", "CCG", "CCT",
"CGA", "CGC", "CGG", "CGT", "CTA", "CTC", "CTG", "CTT",
"GAA", "GAC", "GAG", "GAT", "GCA", "GCC", "GCG", "GCT",
"GGA", "GGC", "GGG", "GGT", "GTA", "GTC", "GTG", "GTT",
"TAA", "TAC", "TAG", "TAT", "TCA", "TCC", "TCG", "TCT",
"TGA", "TGC", "TGG", "TGT", "TTA", "TTC", "TTG", "TTT",
"???", "---"
};
public Codons() {
}
protected Codons(GeneticCode geneticCode) {
this.geneticCode = geneticCode;
stateCount = 64 - geneticCode.getStopCodonCount();
ambiguousStateCount = 66;
stateMap = new int[ambiguousStateCount];
reverseMap = new int[ambiguousStateCount];
int j = 0;
int k = stateCount;
for (int i = 0; i < 64; i++) {
if (!geneticCode.isStopCodon(i)) {
stateMap[j] = i;
reverseMap[i] = j;
j++;
} else {
stateMap[k] = i;
reverseMap[i] = k;
k++;
}
}
for (int i = 64; i < ambiguousStateCount; i++) {
stateMap[i] = i;
reverseMap[i] = i;
}
}
@Override
public char[] getValidChars() {
return null;
}
public final int getState(char c) {
throw new IllegalArgumentException("Codons datatype cannot be expressed as char");
}
public int getUnknownState() {
return UNKNOWN_STATE;
}
public int getGapState() {
return GAP_STATE;
}
public final int getState(char nuc1, char nuc2, char nuc3) {
return getState(Nucleotides.INSTANCE.getState(nuc1),
Nucleotides.INSTANCE.getState(nuc2),
Nucleotides.INSTANCE.getState(nuc3));
}
public final int getCanonicalState(int funnyState) {
return stateMap[funnyState];
}
public final int getState(int nuc1, int nuc2, int nuc3) {
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
int canonicalState = (nuc1 * 16) + (nuc2 * 4) + nuc3;
return reverseMap[canonicalState];
}
public final char getChar(int state) {
throw new IllegalArgumentException("Codons datatype cannot be expressed as char");
}
public final String getTriplet(int state) {
return CODON_TRIPLETS[stateMap[state]];
}
public final int[] getTripletStates(int state) {
int[] triplet = new int[3];
triplet[0] = Nucleotides.INSTANCE.getState(CODON_TRIPLETS[stateMap[state]].charAt(0));
triplet[1] = Nucleotides.INSTANCE.getState(CODON_TRIPLETS[stateMap[state]].charAt(1));
triplet[2] = Nucleotides.INSTANCE.getState(CODON_TRIPLETS[stateMap[state]].charAt(2));
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
return geneticCode.isStopCodon(stateMap[state]);
}
public static byte[] constructRateMap(Codons codonDataType) {
final int stateCount = codonDataType.getStateCount();
final int rateCount = (stateCount * (stateCount - 1)) / 2;
return constructRateMap(rateCount, stateCount, codonDataType, codonDataType.getGeneticCode());
}
public static Codons findByName(String codeStr) {
Codons codons = null;
if (codeStr.equals(GeneticCode.UNIVERSAL.getName())) {
codons = Codons.UNIVERSAL;
} else if (codeStr.equals(GeneticCode.VERTEBRATE_MT.getName())) {
codons = Codons.VERTEBRATE_MT;
} else if (codeStr.equals(GeneticCode.YEAST.getName())) {
codons = Codons.YEAST;
} else if (codeStr.equals(GeneticCode.MOLD_PROTOZOAN_MT.getName())) {
codons = Codons.MOLD_PROTOZOAN_MT;
} else if (codeStr.equals(GeneticCode.INVERTEBRATE_MT.getName())) {
codons = Codons.INVERTEBRATE_MT;
} else if (codeStr.equals(GeneticCode.CILIATE.getName())) {
codons = Codons.CILIATE;
} else if (codeStr.equals(GeneticCode.ECHINODERM_MT.getName())) {
codons = Codons.ECHINODERM_MT;
} else if (codeStr.equals(GeneticCode.EUPLOTID_NUC.getName())) {
codons = Codons.EUPLOTID_NUC;
} else if (codeStr.equals(GeneticCode.BACTERIAL.getName())) {
codons = Codons.BACTERIAL;
} else if (codeStr.equals(GeneticCode.ALT_YEAST.getName())) {
codons = Codons.ALT_YEAST;
} else if (codeStr.equals(GeneticCode.ASCIDIAN_MT.getName())) {
codons = Codons.ASCIDIAN_MT;
} else if (codeStr.equals(GeneticCode.FLATWORM_MT.getName())) {
codons = Codons.FLATWORM_MT;
} else if (codeStr.equals(GeneticCode.BLEPHARISMA_NUC.getName())) {
codons = Codons.BLEPHARISMA_NUC;
} else if (codeStr.equals(GeneticCode.NO_STOPS.getName())) {
codons = Codons.NO_STOPS;
} else {
throw new RuntimeException("Unknown genetics code");
}
return codons;
}
public static byte[] constructRateMap(int rateCount,
int stateCount,
Codons codonDataType,
GeneticCode geneticCode)
{
int u, v, i1, j1, k1, i2, j2, k2;
byte rateClass;
int[] codon;
int cs1, cs2, aa1, aa2;
int i = 0;
byte[] rateMap = new byte[rateCount];
for (u = 0; u < stateCount; u++) {
codon = codonDataType.getTripletStates(u);
i1 = codon[0];
j1 = codon[1];
k1 = codon[2];
cs1 = codonDataType.getState(i1, j1, k1);
aa1 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs1));
for (v = u + 1; v < stateCount; v++) {
codon = codonDataType.getTripletStates(v);
i2 = codon[0];
j2 = codon[1];
k2 = codon[2];
cs2 = codonDataType.getState(i2, j2, k2);
aa2 = geneticCode.getAminoAcidState(codonDataType.getCanonicalState(cs2));
rateClass = -1;
if (i1 != i2) {
if ( (i1 == 0 && i2 == 2) || (i1 == 2 && i2 == 0) || // A <-> G
(i1 == 1 && i2 == 3) || (i1 == 3 && i2 == 1) ) { // C <-> T
rateClass = 1; // Transition at position 1
} else {
rateClass = 2; // Transversion at position 1
}
}
if (j1 != j2) {
if (rateClass == -1) {
if ( (j1 == 0 && j2 == 2) || (j1 == 2 && j2 == 0) || // A <-> G
(j1 == 1 && j2 == 3) || (j1 == 3 && j2 == 1) ) { // C <-> T
rateClass = 1; // Transition
} else {
rateClass = 2; // Transversion
}
} else
rateClass = 0; // Codon changes at more than one position
}
if (k1 != k2) {
if (rateClass == -1) {
if ( (k1 == 0 && k2 == 2) || (k1 == 2 && k2 == 0) || // A <-> G
(k1 == 1 && k2 == 3) || (k1 == 3 && k2 == 1) ) { // C <-> T
rateClass = 1; // Transition
} else {
rateClass = 2; // Transversion
}
} else
rateClass = 0; // Codon changes at more than one position
}
if (rateClass != 0) {
if (aa1 != aa2) {
rateClass += 2; // Is a non-synonymous change
}
}
rateMap[i] = rateClass;
i++;
}
}
return rateMap;
}
// Private members
protected GeneticCode geneticCode;
protected int[] stateMap;
protected int[] reverseMap;
}
