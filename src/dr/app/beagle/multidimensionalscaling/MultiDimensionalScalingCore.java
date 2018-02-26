
package dr.app.beagle.multidimensionalscaling;


public interface MultiDimensionalScalingCore {

    void initialize(int embeddingDimension, int locationCount, boolean isLeftTruncated);

    void setPairwiseData(double[] observations);

    void setParameters(double[] parameters);

    void updateLocation(int locationIndex, double[] location);

    double calculateLogLikelihood();

    void storeState();

    void restoreState();

    void makeDirty();

}
