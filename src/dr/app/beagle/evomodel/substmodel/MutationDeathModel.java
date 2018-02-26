package dr.app.beagle.evomodel.substmodel;
import dr.evolution.datatype.DataType;
import dr.evomodelxml.substmodel.MutationDeathModelParser;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import java.util.Arrays;
public class MutationDeathModel extends ComplexSubstitutionModel { //BaseSubstitutionModel {
    private SubstitutionModel CTMCModel;
    private Parameter delParameter = null;
    protected double[] trMatrix;
    private Parameter baseSubModelFreq;
    private Parameter thisSubModelFreq;
    private Parameter mutationRate;
    private double[] baseModelMatrix;
    public MutationDeathModel(Parameter delParameter, DataType dT, SubstitutionModel evoModel,
                              FrequencyModel freqModel, Parameter mutationRate) {
        super(MutationDeathModelParser.MD_MODEL, dT, freqModel, null);
        CTMCModel = evoModel;
        stateCount = freqModel.getFrequencyCount();
        this.delParameter = delParameter;
        this.dataType = dT;
        this.mutationRate = mutationRate;
        addVariable(delParameter);
        delParameter.addBounds(new Parameter.DefaultBounds(Double.POSITIVE_INFINITY, 0.0, 1));
        addVariable(mutationRate);
//        addModel(freqModel);
        if (evoModel != null) {
            addModel(evoModel.getFrequencyModel());
            addModel(evoModel);
        }
        trMatrix = new double[(stateCount - 1) * (stateCount - 1)];
        if (CTMCModel != null) {
            baseSubModelFreq = CTMCModel.getFrequencyModel().getFrequencyParameter();
        } else {
            baseSubModelFreq = new Parameter.Default(new double[]{1.0});
        }
        thisSubModelFreq = getFrequencyModel().getFrequencyParameter();
        double total = 0;
        for (int i = 0; i < baseSubModelFreq.getDimension(); i++) {
            double value = thisSubModelFreq.getParameterValue(i);
            total += value;
            baseSubModelFreq.setParameterValue(i, value);
        }
        for (int i = 0; i < baseSubModelFreq.getDimension(); i++) {
            baseSubModelFreq.setParameterValue(i, baseSubModelFreq.getParameterValue(i) / total);
        }
        thisSubModelFreq.setParameterValue(thisSubModelFreq.getDimension() - 1, 0.0);
        copyFrequencies();
        frequenciesChanged();
        ratesChanged();
        if (CTMCModel != null) {
            final int baseStateCount = CTMCModel.getDataType().getStateCount();
            baseModelMatrix = new double[baseStateCount * baseStateCount];
        } else {
            baseModelMatrix = new double[1];
        }
        setDoNormalization(false);
//        setupRelativeRates();
    }
    public Parameter getDeathParameter() {
        return this.delParameter;
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        if (object == baseSubModelFreq) {
            copyFrequencies();
            fireModelChanged(object, index);
        } else if (model == CTMCModel) {
            fireModelChanged(object, index);
        }
    }
    private void copyFrequencies() {
        for (int i = 0; i < baseSubModelFreq.getDimension(); i++)
            thisSubModelFreq.setParameterValueQuietly(i, baseSubModelFreq.getParameterValue(i));
    }
    @Override
    protected void frequenciesChanged() {
    }
    @Override
    protected void ratesChanged() {
    }
    @Override
    protected void setupQMatrix(double[] rates, double[] pi, double[][] matrix) {
        // Zero matrix
        for (int i = 0; i < matrix.length; ++i) {
            Arrays.fill(matrix[i], 0.0);
        }
        int baseStateCount = 1;
        if (CTMCModel != null) {
            CTMCModel.getInfinitesimalMatrix(baseModelMatrix);
            baseStateCount = CTMCModel.getDataType().getStateCount();
        }
        double deathR = delParameter.getParameterValue(0);
        double mutationR = 2 * mutationRate.getParameterValue(0); // TODO Double-check
        int k = 0;
        for (int i = 0; i < baseStateCount; ++i) {
            for (int j = 0; j < baseStateCount; ++j) {
                matrix[i][j] = baseModelMatrix[k] - deathR;
                k++;
            }
            matrix[i][baseStateCount] = deathR;
        }
    }
    @Override
    protected void setupRelativeRates(double[] rates) {
    }
}