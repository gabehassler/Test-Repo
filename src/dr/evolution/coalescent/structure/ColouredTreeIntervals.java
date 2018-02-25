package dr.evolution.coalescent.structure;
import dr.evolution.coalescent.IntervalType;
import dr.evolution.colouring.BranchColouring;
import dr.evolution.colouring.TreeColouring;
import dr.evolution.tree.ColourChange;
import dr.evolution.tree.NodeRef;
import dr.evolution.tree.Tree;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ColouredTreeIntervals implements StructuredIntervalList {
int intervalCount;
int sampleCount;
List<Event> eventList = new ArrayList<Event>();
int colourStateCount;
int[][] lineageCount;
Type units;
public ColouredTreeIntervals(Tree tree, TreeColouring colouring) {
colourStateCount = colouring.getColourCount();
units = tree.getUnits();
extractCoalescentEvents(tree, colouring, eventList);
sampleCount = extractSampleEvents(tree, colouring, eventList);
extractMigrationEvents(tree, colouring, eventList);
Collections.sort(eventList);
lineageCount = new int[eventList.size() + 1][colourStateCount];
int externalNodeCount = tree.getExternalNodeCount();
for (int i = 0; i < externalNodeCount; i++) {
NodeRef node = tree.getExternalNode(i);
double time = tree.getNodeHeight(node);
int colour = colouring.getNodeColour(node);
if (time == 0.0) {
lineageCount[0][colour] += 1;
}
}
for (int i = 0; i < eventList.size(); i++) {
Event event = eventList.get(i);
//System.out.println(event);
for (int j = 0; j < colourStateCount; j++) {
lineageCount[i + 1][j] = lineageCount[i][j] + event.lineageChanges[j];
if (lineageCount[i + 1][j] < 0) {
throw new RuntimeException("lineageCount[" + (i + 1) + "][" + j + "] = " + lineageCount[i + 1][j] + ". This is wrong!");
}
}
}
intervalCount = eventList.size();
}
public int getPopulationCount() {
return lineageCount[0].length;
}
public int getIntervalCount() {
return intervalCount;
}
public int getSampleCount() {
return sampleCount;
}
public double getInterval(int i) {
if (i == 0) return getEvent(i).time;
return getEvent(i).time - getEvent(i - 1).time;
}
public int getLineageCount(int interval, int population) {
return lineageCount[interval][population];
}
public int getLineageCount(int interval) {
int totalLineages = 0;
for (int i = 0; i < colourStateCount; i++) {
totalLineages += lineageCount[interval][i];
}
return totalLineages;
}
public int getCoalescentEvents(int i) {
if (getEvent(i).getType() == IntervalType.COALESCENT) return 1;
return 0;
}
public IntervalType getIntervalType(int i) {
return getEvent(i).getType();
}
public double getTotalDuration() {
return getEvent(getIntervalCount()).time;
}
public boolean isBinaryCoalescent() {
return true;
}
public boolean isCoalescentOnly() {
return false;
}
public Type getUnits() {
return units;
}
public void setUnits(Type units) {
this.units = units;
}
private void extractCoalescentEvents(Tree tree, TreeColouring colouring, List<Event> eventList) {
int internalNodeCount = tree.getInternalNodeCount();
for (int i = 0; i < internalNodeCount; i++) {
NodeRef node = tree.getInternalNode(i);
double time = tree.getNodeHeight(node);
int colour = colouring.getNodeColour(node);
eventList.add(Event.createCoalescentEvent(time, colour, colouring.getColourCount()));
}
}
private int extractSampleEvents(Tree tree, TreeColouring colouring, List<Event> eventList) {
int externalNodeCount = tree.getExternalNodeCount();
int sampleEventCount = 0;
for (int i = 0; i < externalNodeCount; i++) {
NodeRef node = tree.getExternalNode(i);
double time = tree.getNodeHeight(node);
int colour = colouring.getNodeColour(node);
if (time != 0.0) {
eventList.add(Event.createAddSampleEvent(time, colour, colouring.getColourCount()));
sampleEventCount += 1;
}
}
return sampleEventCount;
}
private void extractMigrationEvents(Tree tree, TreeColouring colouring, List<Event> eventList) {
int nodeCount = tree.getNodeCount();
for (int i = 0; i < nodeCount; i++) {
NodeRef node = tree.getNode(i);
if (!tree.isRoot(node)) {
BranchColouring branchColouring = colouring.getBranchColouring(node);
List<ColourChange> changes = branchColouring.getColourChanges();
int belowColour = colouring.getNodeColour(node);
for (ColourChange change : changes) {
double time = change.getTime();
int aboveColour = change.getColourAbove();
eventList.add(Event.createMigrationEvent(time, belowColour, aboveColour, colouring.getColourCount()));
belowColour = aboveColour;
}
}
}
}
public final Event getEvent(int i) {
return eventList.get(i);
}
}
