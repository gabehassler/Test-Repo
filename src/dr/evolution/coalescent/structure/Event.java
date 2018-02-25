package dr.evolution.coalescent.structure;
import dr.evolution.coalescent.IntervalType;
public class Event implements Comparable {
double time;
int[] lineageChanges;
private IntervalType type;
private int aboveColour;
private int belowColour;
public static Event createCoalescentEvent(double time, int colour, int colourCount) {
int[] lineageChanges = new int[colourCount];
lineageChanges[colour] = -1;
return new Event(time, IntervalType.COALESCENT, lineageChanges, colour, colour);
}
public static Event createAddSampleEvent(double time, int colour, int colourCount) {
int[] lineageChanges = new int[colourCount];
lineageChanges[colour] = +1;
return new Event(time, IntervalType.SAMPLE, lineageChanges, colour, colour);
}
public static Event createMigrationEvent(double time, int belowColour, int aboveColour, int colourCount) {
int[] lineageChanges = new int[colourCount];
lineageChanges[belowColour] = -1;
lineageChanges[aboveColour] = +1;
return new Event(time, IntervalType.MIGRATION, lineageChanges, aboveColour, belowColour);
}
private Event(double time, IntervalType type, int[] lineageChanges, int aboveColour, int belowColour) {
this.time = time;
this.type = type;
this.lineageChanges = lineageChanges;
this.aboveColour = aboveColour;
this.belowColour = belowColour;
}
public int getAboveColour() {
return aboveColour;
}
public int getBelowColour() {
return belowColour;
}
public IntervalType getType() {
return type;
}
public String toString() {
return type + " event at time " + time + " (above=" + aboveColour + ", below=" + belowColour + ")";
}
public int compareTo(Object o) {
Event e2 = (Event) o;
return new Double(time).compareTo(e2.time);
}
}
