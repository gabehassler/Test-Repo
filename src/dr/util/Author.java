
package dr.util;


public class Author {

    String surname;
    String firstnames;

    public Author(String firstnames, String surname) {
        this.surname = surname;
        this.firstnames = firstnames;
    }

    public String getInitials() { // TODO Determine initials from first names
        return firstnames;
    }

    public String toString() {
        return surname + " " + getInitials();
    }
}
