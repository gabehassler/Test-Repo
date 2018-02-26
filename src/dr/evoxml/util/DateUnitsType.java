
package dr.evoxml.util;

public enum DateUnitsType {
    YEARS("units", "Years"), // 
    MONTHS("units", "Months"),
    DAYS("days", "Days"), // 
    FORWARDS("forwards", "Since some time in the past"), // 
    BACKWARDS("backwards", "Before the present"); // 
    
	DateUnitsType(String attr, String name) {
        this.attr = attr;
        this.name = name;        
    }

    public String toString() {
        return name;
    }
    
    public String getAttribute() {
        return attr;
    }

    private final String attr;
    private final String name;
}
