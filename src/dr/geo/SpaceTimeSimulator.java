package dr.geo;
import dr.math.distributions.MultivariateNormalDistribution;
import java.util.ArrayList;
import java.util.List;
public class SpaceTimeSimulator {
    // the diffusion kernel
    MultivariateNormalDistribution D;
    public SpaceTimeSimulator(MultivariateNormalDistribution D) {
        this.D = D;
    }
    public List<SpaceTime> simulatePath(SpaceTime start, SpaceTimeRejector rejector, double dt, int steps) {
        ArrayList<SpaceTime> list = new ArrayList<SpaceTime>();
        list.add(start);
        for (int i = 0; i < steps; i++) {
            SpaceTime lastSpaceTime = list.get(list.size() - 1);
            SpaceTime newST;
            do {
                double[] newPoint = new double[start.getX().length];
                D.nextScaledMultivariateNormal(lastSpaceTime.getX(), dt, newPoint);
                newST = new SpaceTime(lastSpaceTime.getTime() + dt, newPoint);
            } while (rejector.reject(newST.time, newST.space));
            list.add(newST);
        }
        return list;
    }
    public SpaceTime simulate(SpaceTime spaceTime, SpaceTimeRejector rejector, double dt, int steps) {
        SpaceTime newST = new SpaceTime(spaceTime);
        SpaceTime nextST = new SpaceTime(spaceTime);
        for (int i = 0; i < steps; i++) {
            do {
                D.nextScaledMultivariateNormal(nextST.getX(), dt, newST.space);
                newST.time = nextST.getTime() + dt;
            } while (rejector.reject(newST.time, newST.space));
            nextST.time = newST.time;
            nextST.space = newST.space;
        }
        return nextST;
    }
    public SpaceTime simulateAbsorbing(SpaceTime spaceTime, SpaceTimeRejector rejector, double dt, int steps) {
        int i = 0;
        boolean found = false;
        boolean reject = false;
        SpaceTime nextST = null;
        while (!found) {
            SpaceTime newST = new SpaceTime(spaceTime);
            nextST = new SpaceTime(spaceTime);
            while (i < steps && !reject) {
                D.nextScaledMultivariateNormal(nextST.getX(), dt, newST.space);
                newST.time = nextST.getTime() + dt;
                reject = rejector.reject(newST.time, newST.space);
                nextST.time = newST.time;
                nextST.space = newST.space;
                i += 1;
            }
            if (!reject) found = true;
        }
        return nextST;
    }
}
