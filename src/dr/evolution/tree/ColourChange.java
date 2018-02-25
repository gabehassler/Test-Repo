package dr.evolution.tree;
public class ColourChange {
// time of the change in colour
private double time;
// the colour above this time
private int aboveColour;
public ColourChange(ColourChange change) {
this(change.time, change.aboveColour);
}
public ColourChange(double time, int aboveColour) {
this.time = time;
this.aboveColour = aboveColour;
}
public final double getTime() { return time; }
public final int getColourAbove() { return aboveColour; }
public final void setColourAbove(int aboveColour) { this.aboveColour = aboveColour; }
}
