package dr.evolution.phylogeny;
public abstract class Phylogeny {
public static final int INCREMENT=10000;
Lineage[] lineages = new Lineage[INCREMENT];
int lineageCount = 0;
int availableSize = 0;
int[] extantLineages;
Phylogeny() {
}
public void lineageBirth(Lineage lineage, double birthTime) {
int descendantIndex1 = createLineage(lineage, birthTime);
int descendantIndex2 = createLineage(lineage, birthTime);
int lineageIndex = extantLineages[lineage.getExtantIndex()];
extantLineages[lineage.getExtantIndex()] = descendantIndex1;
}
public void lineageDeath(Lineage lineage, double deathTime) {
lineage.setDeathTime(deathTime);
}
private int createLineage(Lineage parent, double birthTime) {
int index = lineageCount;
if (availableSize > 0) {
// there are some unused lineage available so return one..
availableSize--;
} else {
if (lineageCount == lineages.length) {
// run out of space in the array so reallocate it...
Lineage[] newLineages = new Lineage[lineages.length + INCREMENT];
for (int i = 0; i < lineages.length; i++) {
newLineages[i] = lineages[i];
}
lineages = newLineages;
}
lineages[index] = newLineage(parent, birthTime);
lineages[index].setIndex(index);
}
lineageCount++;
return index;
}
protected abstract Lineage newLineage(Lineage parent, double birthTime);
}