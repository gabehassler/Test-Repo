package dr.evolution.tree;
import java.util.*;
public interface RankedForest {
public List<RankedNode> getNodes();
public int getSize();
public int rank();
public boolean compatibleRank(List<BitSet> constraints);
public Set<BitSet> clades();
public int constraintsSatisfied();
public boolean isClear();
public class Default implements RankedForest {
List<RankedNode> nodes;
boolean clear;
public Default(int size, boolean clear) {
nodes = new ArrayList<RankedNode>(size);
for (int i = size - 1; i >= 0; i--) {
nodes.add(new RankedNode(i, size));
}
this.clear = clear;
}
public int getSize() {
return nodes.get(0).n;
}
public int rank() {
return nodes.get(0).rank;
}
public boolean compatibleRank(List<BitSet> constraints) {
return true;
}
public Set<BitSet> clades() {
return Collections.emptySet();
}
public int constraintsSatisfied() {
return 0;
}
public boolean isClear() {
return clear;
}
public List<RankedNode> getNodes() {
return nodes;
}
}
public class Parent implements RankedForest {
RankedForest child;
RankedNode parent;
List<RankedNode> nodes;
HashSet<BitSet> clades;
int constraintsSatisfied = 0;
boolean clear;
public Parent(RankedForest child, RankedNode parent, List<BitSet> constraints) {
this.child = child;
this.parent = parent;
nodes = new ArrayList<RankedNode>();
nodes.add(parent);
nodes.addAll(child.getNodes());
nodes.remove(parent.child1);
nodes.remove(parent.child2);
clades = new HashSet<BitSet>();
clades.addAll(child.clades());
clades.add(parent.cladeBits);
if (!clear) {
constraintsSatisfied = child.constraintsSatisfied();
//System.out.println(parent.cladeBits);
if (constraints.contains(parent.cladeBits)) {
//System.out.println("Found " + parent.cladeBits + " in constraints!");
constraintsSatisfied += 1;
}
clear = (constraintsSatisfied == constraints.size());
}
//System.out.println("making parent wih rank " + parent.rank);
}
public List<RankedNode> getNodes() {
return nodes;
}
public int getSize() {
return nodes.get(0).n;
}
public int rank() {
return nodes.get(0).rank;
}
public boolean compatibleRank(List<BitSet> constraints) {
if (constraints.size() == 1) return true;
int rank = 0;
for (BitSet constraint : constraints) {
int newRank = getRank(constraint);
if (newRank < rank) return false;
rank = newRank;
}
if (rank != Integer.MAX_VALUE) clear = true;
return true;
}
public Set<BitSet> clades() {
return clades;
}
public int constraintsSatisfied() {
return constraintsSatisfied;
}
public boolean isClear() {
return clear;
}
private int getRank(BitSet constraint) {
//if (constraint.cardinality() > parent.rank) return Integer.MAX_VALUE;
if (parent.cladeBits.equals(constraint)) {
return parent.rank;
} else {
if (child instanceof Parent) {
return ((Parent) child).getRank(constraint);
}
return Integer.MAX_VALUE;
}
}
//public String toString() {}
}
}
