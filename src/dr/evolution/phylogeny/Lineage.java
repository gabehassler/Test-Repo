package dr.evolution.phylogeny;
public interface Lineage {
    Lineage getParent();
    int getDescendantCount();
    Lineage getDescendant(int descendant);
    int getExtantIndex();
    void setExtantIndex(int index);
    int getIndex();
    void setIndex(int index);
    double getDeathTime();
    void setDeathTime(double deathTime);
}
