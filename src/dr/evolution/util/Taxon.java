package dr.evolution.util;
import dr.util.Attributable;
import dr.util.Identifiable;
import java.util.ArrayList;
import java.util.Iterator;
public class Taxon implements Attributable, Identifiable, Comparable<Taxon> {
public Taxon(String id) {
setId(id);
}
public void setDate(Date date) {
setAttribute("date", date);
addDateToTimeScale(date);
}
public Date getDate() {
Object date = getAttribute("date");
if (date != null && date instanceof Date) {
return (Date)date;
}
return null;
}
public double getHeight() {
Object date = getAttribute("date");
if (date != null && date instanceof Date) {
return getHeightFromDate((Date)date);
}
return 0.0;
}
public void setLocation(Location location) {
setAttribute("location", location);
}
public Location getLocation() {
Object location = getAttribute("location");
if (location != null && location instanceof Location) {
return (Location)location;
}
return null;
}
// **************************************************************
// Attributable IMPLEMENTATION
// **************************************************************
private Attributable.AttributeHelper attributes = null;
public void setAttribute(String name, Object value) {
if (attributes == null)
attributes = new Attributable.AttributeHelper();
attributes.setAttribute(name, value);
}
public Object getAttribute(String name) {
if (attributes == null)
return null;
else
return attributes.getAttribute(name);
}
public boolean containsAttribute(String name) {
return attributes != null && attributes.containsAttribute(name);
}
public Iterator<String> getAttributeNames() {
if (attributes == null)
return new ArrayList<String>().iterator();
else
return attributes.getAttributeNames();
}
// **************************************************************
// Identifiable IMPLEMENTATION
// **************************************************************
protected String id = null;
public String getId() {
return id;
}
public void setId(String id) {
this.id = id;
}
public String toString() { return getId(); }
@Override
public boolean equals(final Object o) {
return getId().equals(((Taxon)o).getId());
}
@Override
public int hashCode() {
return getId().hashCode();
}
// **************************************************************
// Comparable IMPLEMENTATION
// **************************************************************
public int compareTo(Taxon o) {
return getId().compareTo(o.getId());
}
private static void addDateToTimeScale(Date date) {
if (date != null && (mostRecentDate == null || date.after(mostRecentDate))) {
mostRecentDate = date;
timeScale = null;
}
}
public static double getHeightFromDate(Date date) {
if (timeScale == null) {
Date mostRecent = mostRecentDate;
if (mostRecent == null) {
mostRecent = dr.evolution.util.Date.createRelativeAge(0.0, date.getUnits());
}
timeScale = new TimeScale(mostRecent.getUnits(), true, mostRecent.getAbsoluteTimeValue());
}
return timeScale.convertTime(date.getTimeValue(), date);
}
public static Date getMostRecentDate() {
return mostRecentDate;
}
private static dr.evolution.util.Date mostRecentDate = null;
private static TimeScale timeScale = null;
}
