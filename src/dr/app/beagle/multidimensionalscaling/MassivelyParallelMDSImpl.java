
package dr.app.beagle.multidimensionalscaling;

public class MassivelyParallelMDSImpl implements MultiDimensionalScalingCore {

    private NativeMDSSingleton singleton = null;
    private int instance = -1; // Get instance # via initialization
    private final long flags = 0;

    private static final long LEFT_TRUNCATION = 1 << 5;

    public MassivelyParallelMDSImpl() {
        singleton = NativeMDSSingleton.loadLibrary();
    }

    @Override
    public void initialize(int embeddingDimension, int locationCount, boolean isLeftTruncated) {
        long flags = this.flags;
        if (isLeftTruncated) {
            flags |= LEFT_TRUNCATION;
        }

        instance = singleton.initialize(embeddingDimension, locationCount, flags);
        this.observationCount = (locationCount * (locationCount - 1)) / 2;
        this.isLeftTruncated = isLeftTruncated;
    }

    @Override
    public void setPairwiseData(double[] observations) {
        singleton.setPairwiseData(instance, observations);
    }

    @Override
    public void setParameters(double[] parameters) {
        precision = parameters[0];
        singleton.setParameters(instance, parameters); // Necessary for truncation
    }

    @Override
    public void updateLocation(int locationIndex, double[] location) {
        singleton.updateLocations(instance, locationIndex, location);
    }

    @Override
    public double calculateLogLikelihood() {
        double sumOfSquaredResiduals = singleton.getSumOfSquaredResiduals(instance);

        double logLikelihood = (0.5 * Math.log(precision) * observationCount) -
                        (0.5 * precision * sumOfSquaredResiduals);

        if (isLeftTruncated) {
            logLikelihood -= singleton.getSumOfLogTruncations(instance);
        }

        return logLikelihood;
    }

    @Override
    public void storeState() {
        singleton.storeState(instance);
        storedPrecision = precision;
    }

    @Override
    public void restoreState() {
        singleton.restoreState(instance);
        precision = storedPrecision;
    }

    @Override
    public void acceptState() {
        singleton.acceptState(instance);
    }

    @Override
    public void makeDirty() {
        singleton.makeDirty(instance);
    }

    private int observationCount;
    private double precision;
    private double storedPrecision;
    private boolean isLeftTruncated;

}
