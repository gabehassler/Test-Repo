
package dr.inference.model;

import java.util.ArrayList;


public class IntersectionBounds implements Bounds<Double> {

    IntersectionBounds(int dimension) {
        this.dimension = dimension;
    }

    public void addBounds(Bounds<Double> boundary) {
        if (boundary.getBoundsDimension() != dimension) {
            throw new IllegalArgumentException("Incorrect dimension of bounds, expected " +
                    dimension + " but received " + boundary.getBoundsDimension());
        }
        if (bounds == null) {
            bounds = new ArrayList<Bounds<Double>>();
        }
        bounds.add(boundary);
    }

    public Double getLowerLimit(int index) {

        double lower = Double.NEGATIVE_INFINITY;
        if (bounds != null) {
            for (Bounds<Double> boundary : bounds) {
                if (boundary.getLowerLimit(index) > lower) {
                    lower = boundary.getLowerLimit(index);
                }
            }
        }
        return lower;
    }

    public Double getUpperLimit(int index) {

        double upper = Double.POSITIVE_INFINITY;
        if (bounds != null) {
            for (Bounds<Double> boundary : bounds) {
                if (boundary.getUpperLimit(index) < upper) {
                    upper = boundary.getUpperLimit(index);
                }
            }
        }
        return upper;
    }

    public int getBoundsDimension() {
        return dimension;
    }

    public String toString() {
        String str = "upper=[" + getUpperLimit(0);
        for (int i = 1; i < getBoundsDimension(); i++) {
            str += ", " + getUpperLimit(i);
        }
        str += "] lower=[" + getLowerLimit(0);
        for (int i = 1; i < getBoundsDimension(); i++) {
            str += ", " + getLowerLimit(i);
        }

        str += "]";
        return str;
    }

    private ArrayList<Bounds<Double>> bounds = null;
    private final int dimension;
}
