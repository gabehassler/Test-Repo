package dr.app.treestat.statistics;
public interface SummaryStatisticDescription {
String getSummaryStatisticName();
String getSummaryStatisticDescription();
String getSummaryStatisticReference();
boolean allowsPolytomies();
boolean allowsNonultrametricTrees();
boolean allowsUnrootedTrees();
Category getCategory();
public class Category {
public static final Category TREE_SHAPE = new Category("Tree shape");
public static final Category PHYLOGENETIC = new Category("Phylogenetic");
public static final Category POPULATION_GENETIC = new Category("Population genetic");
public static final Category GENERAL = new Category("General");
public static final Category SPECIATION = new Category("Speciation/Birth-death");
private Category(String name) {
this.name = name;
}
public String toString() { return name; }
private String name;
}
}
