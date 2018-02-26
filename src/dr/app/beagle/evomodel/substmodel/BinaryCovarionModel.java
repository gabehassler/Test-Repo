package dr.app.beagle.evomodel.substmodel;
import dr.app.beagle.evomodel.parsers.BinaryCovarionModelParser;
import dr.evolution.datatype.TwoStateCovarion;
import dr.evomodel.substmodel.SubstitutionModelUtils;
import dr.inference.model.Parameter;
public class BinaryCovarionModel extends AbstractCovarionModel {
    public BinaryCovarionModel(TwoStateCovarion dataType,
                               Parameter frequencies,
                               Parameter hiddenFrequencies,
                               Parameter alphaParameter,
                               Parameter switchingParameter,
                               dr.evomodel.substmodel.BinaryCovarionModel.Version version) {
        super(BinaryCovarionModelParser.COVARION_MODEL, dataType, frequencies, hiddenFrequencies);
        this.alpha = alphaParameter;
        this.switchRate = switchingParameter;
        this.frequencies = frequencies;
        this.hiddenFrequencies = hiddenFrequencies;
        this.version = version;
        addVariable(alpha);
        addVariable(switchRate);
        addVariable(frequencies);
        addVariable(hiddenFrequencies);
    }
    protected void setupQMatrix(double[] rates, double[] pi, double[][] unnormalizedQ) {
        double a = alpha.getParameterValue(0);
        double s = switchRate.getParameterValue(0);
        double f0 = hiddenFrequencies.getParameterValue(0);
        double f1 = hiddenFrequencies.getParameterValue(1);
        double p0 = frequencies.getParameterValue(0);   // TODO Use pi[0], ..., pi[3]
        double p1 = frequencies.getParameterValue(1);
        assert Math.abs(1.0 - f0 - f1) < 1e-8;
        assert Math.abs(1.0 - p0 - p1) < 1e-8;
        f0 = version.getF0(f0);
        f1 = version.getF1(f1);
        unnormalizedQ[0][1] = a * p1;
        unnormalizedQ[0][2] = s * f0;
        unnormalizedQ[0][3] = 0.0;
        unnormalizedQ[1][0] = a * p0;
        unnormalizedQ[1][2] = 0.0;
        unnormalizedQ[1][3] = s * f0;
        unnormalizedQ[2][0] = s * f1;
        unnormalizedQ[2][1] = 0.0;
        unnormalizedQ[2][3] = p1;
        unnormalizedQ[3][0] = 0.0;
        unnormalizedQ[3][1] = s * f1;
        unnormalizedQ[3][2] = p0;
    }
    protected void frequenciesChanged() {
    }
    protected void ratesChanged() {
    }
    public String toString() {
        final int stateCount = dataType.getStateCount();
        double[] tmp = new double[stateCount * stateCount];
        getInfinitesimalMatrix(tmp);
        double[][] q = new double[stateCount][stateCount];
        for (int i = 0; i < stateCount; ++i) {
            for (int j = 0; j < stateCount; ++j) {
                q[i][j] = tmp[i * stateCount + j];
            }
        }
        setupQMatrix(null, null, q);
        return SubstitutionModelUtils.toString(q, dataType, 2);
    }
    protected double getNormalizationValue(double[][] matrix, double[] pi) {
        double subst = 0.0;
        int dimension = pi.length;
        for (int i = 0; i < dimension; i++) {
            subst += -matrix[i][i] * pi[i];
        }
        // normalize, including switches
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                matrix[i][j] = matrix[i][j] / subst;
            }
        }
        double switchingProportion = 0.0;
        switchingProportion += matrix[0][2] * pi[2];
        switchingProportion += matrix[2][0] * pi[0];
        switchingProportion += matrix[1][3] * pi[3];
        switchingProportion += matrix[3][1] * pi[1];
        // normalize, removing switches
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                matrix[i][j] = matrix[i][j] / (1.0 - switchingProportion);
            }
        }
        return 1.0; // Already normalized
    }
    private Parameter alpha;
    private Parameter switchRate;
    private Parameter frequencies;
    private Parameter hiddenFrequencies;
    final private dr.evomodel.substmodel.BinaryCovarionModel.Version version;
}