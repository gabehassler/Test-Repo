
package dr.geo;

public class Reject {

    private int depth;
    private double[] space;
    private double time;

    public Reject(int depth, double time, double[] space) {
        this.depth = depth;
        this.time = time;
        this.space = space;
    }

    public int getDepth() {
        return depth;
    }

    public double getTime() {
        return time;
    }

    public double[] getSpace() {
        return space;
    }

}
