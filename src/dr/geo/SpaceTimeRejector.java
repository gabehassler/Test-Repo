package dr.geo;
import java.awt.geom.Rectangle2D;
import java.util.List;
public interface SpaceTimeRejector {
    boolean reject(double time, double[] space);
    // removes all rejects
    void reset();
    List<Reject> getRejects();
    class Utils {
        public static SpaceTimeRejector createSimpleBounds2D(final Rectangle2D bounds) {
            return new SpaceTimeRejector() {
                public boolean reject(double time, double[] space) {
                    return !bounds.contains(space[0], space[1]);
                }
                public void reset() {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
                public List<Reject> getRejects() {
                    return null;  //To change body of implemented methods use File | Settings | File Templates.
                }
            };
        }
    }
}
