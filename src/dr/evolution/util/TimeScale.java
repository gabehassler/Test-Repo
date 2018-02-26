
package dr.evolution.util;

public class TimeScale implements Units { 

	public TimeScale(Type units, boolean backwards) {
		this(units, backwards, 0.0);
	}
	
	public TimeScale(Type units, boolean backwards, double origin) {
		this.units = units;
		this.backwards = backwards;
		this.origin = origin;
	}
	
	public TimeScale(Type units, boolean backwards, java.util.Date origin) {
		this.units = units;
		this.backwards = backwards;
		
		long millisAhead = origin.getTime();
		
		double daysAhead = ((double)millisAhead)/MILLIS_PER_DAY;
		
		switch (units) {	
			case DAYS: this.origin = daysAhead; break;
			case MONTHS: this.origin = daysAhead / DAYS_PER_MONTH; break;
			case YEARS: this.origin = daysAhead / DAYS_PER_YEAR; break;
			default: throw new IllegalArgumentException();
		}
		
	}
	
	public Type getUnits() { return units; }
	
	public void setUnits(Type units) { this.units = units; }
	
	public boolean isBackwards() { return backwards; }
	
	public double getOrigin() { return origin; }
	
	public double convertTime(double time, TimeScale timeScale) {
		
		// make it forwards
		if (timeScale.isBackwards()) time = -time;
		
		// make it absolute
		time += timeScale.getOrigin();
		
		// convert to the new timescale units
		double newTime = convertTimeUnits(time, getUnits(), timeScale.getUnits());
		
		// make it relative
		newTime -= origin;
		
		// make it backwards if required
		if (backwards) newTime = -newTime;

		return newTime;
	}
	
	public String toString() {
	
		StringBuffer buffer = new StringBuffer("timescale(");
		buffer.append(unitString(0.0));
		if (backwards) {
			buffer.append(", backwards");
		} else {
			buffer.append(", forewards");
		}
		buffer.append(" from " + origin + ")");
		
		return buffer.toString();
	}
	
	public String unitString(double time) {
		String unitString = null;
		switch (units) {	
			case DAYS: unitString = "day"; break;
			case MONTHS: unitString = "month"; break;
			case YEARS: unitString = "year"; break;
			default: throw new IllegalArgumentException();
		}
		if (time == 1.0) {
			return unitString;
		} else return unitString + "s";
	}
	
	public static void main(String[] args) {
	
		TimeScale timeScale1 = new TimeScale(Units.Type.DAYS, true);
		TimeScale timeScale2 = new TimeScale(Units.Type.YEARS, true);
		
		System.out.println(timeScale1);
		System.out.println(timeScale2);
		
		double testTime = 100.0;
		System.out.println("Test time = " + testTime);
		
		System.out.println("timeScale1.convertTime(" + testTime + ", timeScale2)=" + timeScale1.convertTime(testTime, timeScale2));
		System.out.println("timeScale2.convertTime(" + testTime + ", timeScale1)=" + timeScale2.convertTime(testTime, timeScale1));
	}
	
	
	//*************************************************************************
	// STATIC STUFF
	//*************************************************************************
	
	public static double convertTimeUnits(double time, Type currentUnits, Type newUnits) {
		
		return time * getScale(currentUnits, newUnits);
	}
	
	public static double getScale(Type currentUnits, Type newUnits) {
		if (currentUnits == newUnits) return 1.0;
		
		switch (currentUnits) {
			case DAYS:
				switch (newUnits) {
					case MONTHS: return 1.0/DAYS_PER_MONTH;
					case YEARS: return 1.0/DAYS_PER_YEAR;
					default: throw new IllegalArgumentException();
				}
			case MONTHS:
				switch (newUnits) {
					case DAYS: return DAYS_PER_MONTH;
					case YEARS: return 1.0/MONTHS_PER_YEAR;
					default: throw new IllegalArgumentException();
				}
			case YEARS:
				switch (newUnits) {
					case DAYS: return DAYS_PER_YEAR;
					case MONTHS: return MONTHS_PER_YEAR;
					default: throw new IllegalArgumentException();
				}
			default: throw new IllegalArgumentException();
		}
	}
	
	//*************************************************************************
	// PRIVATE STUFF
	//*************************************************************************

	// The origin is specified in days relative to 1st January 1970
	protected double origin = 720035.0;
	protected Type units;
	protected boolean backwards;
	
	protected static double MILLIS_PER_DAY = 86400000.0;
	protected static double DAYS_PER_YEAR = 365.25;
	protected static double MONTHS_PER_YEAR = 12.0;
	protected static double DAYS_PER_MONTH = DAYS_PER_YEAR / MONTHS_PER_YEAR;
}
