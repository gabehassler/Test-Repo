package dr.evolution.util;
import dr.util.Attribute;
import dr.util.NumberFormatter;
import java.util.Calendar;
import java.util.TimeZone;
public class Date extends TimeScale implements Attribute { 
public static final String DATE = "date";
private double precision = 0.0;
public Date(double time, Type units, boolean backwards, java.util.Date origin) {
super(units, backwards, origin);
this.time = time;
}
public Date(java.util.Date date) {
super(Units.Type.YEARS, false);
origin = -1970.0;
initUsingDate(date);
}
public Date(java.util.Date date, Type units) {
super(units, false);
initUsingDate(date);
}
public Date(java.util.Date date, Type units, java.util.Date origin) {
super(units, false, origin);
initUsingDate(date);
}
public Date(double time, Type units, boolean backwards) {
super(units, backwards);
this.time = time;
}
private Date(double time, Type units, boolean backwards, double origin) {
super(units, backwards, origin);
this.time = time;
}
//************************************************************************
// Factory methods
//************************************************************************
public static Date createRelativeAge(double age, Type units) {
return new Date(age, units, true);
}
public static Date createTimeAgoFromOrigin(double age, Type units, java.util.Date origin) {
return new Date(age, units, true, origin);
}
public static Date createTimeAgoFromOrigin(double age, Type units, double origin) {
return new Date(age, units, true, origin);
}
public static Date createTimeSinceOrigin(double age, Type units, java.util.Date origin) {
return new Date(age, units, false, origin);
}
public static Date createTimeSinceOrigin(double age, Type units, double origin) {
return new Date(age, units, false, origin);
}
public static Date createDate(java.util.Date date) {
return new Date(date, Units.Type.YEARS);
}
//************************************************************************
// Private methods
//************************************************************************
private void initUsingDate(java.util.Date date) {
// get the number of milliseconds this date is after the 1st January 1970
long millisAhead = date.getTime();
double daysAhead = ((double)millisAhead)/MILLIS_PER_DAY;
switch (units) {
case DAYS: time = daysAhead;
break;
case MONTHS: time = daysAhead / DAYS_PER_MONTH;
break;
case YEARS:
//time = daysAhead / DAYS_PER_YEAR;
// more precise (so 1st Jan 2013 is 2013.0)
// to avoid timezone specific differences in date calculations, all dates and calendars are
// set to GMT.
Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
cal.setTime(date);
int year = cal.get(Calendar.YEAR);
long millis1 = cal.getTimeInMillis();
cal.set(year, Calendar.JANUARY, 1, 0, 0);
long millis2 = cal.getTimeInMillis();
cal.set(year + 1, Calendar.JANUARY, 1, 0, 0);
long millis3 = cal.getTimeInMillis();
double fractionalYear = ((double)(millis1 - millis2)) / (millis3 - millis2);
time = fractionalYear + year - 1970;
break;
default: throw new IllegalArgumentException();
}
if (time < getOrigin()) {
time = getOrigin() - time;
backwards = true;
} else {
time = time - getOrigin();
backwards = false;
}
}
public double getTimeValue() { 
return time; 
}
public double getAbsoluteTimeValue() { 
if (isBackwards()) {
return getOrigin() - getTimeValue();
}
return getOrigin() + getTimeValue();
}
public boolean before(Date date) {
double newTime = convertTime(date.getTimeValue(), date);
if (isBackwards()) {
return getTimeValue() > newTime;
}
return getTimeValue() < newTime; 
}
public boolean after(Date date) {
double newTime = convertTime(date.getTimeValue(), date);
if (isBackwards()) {
return getTimeValue() < newTime;
}
return getTimeValue() > newTime; 
}
public boolean equals(Date date) {
double newTime = convertTime(date.getTimeValue(), date);
return getTimeValue() == newTime; 
}
public String getAttributeName() { return DATE; }
public Object getAttributeValue() { return this; }
public String toString() {
if (isBackwards()) {
return formatter.format(time).trim() + " " + unitString(time) + " ago";
} else {
return formatter.format(time).trim() + " " + unitString(time);
}	
}
private double time;
private NumberFormatter formatter = new NumberFormatter(5);
public void setPrecision(double precision) {
this.precision = precision;
}
public double getPrecision() {
return precision;
}
}
