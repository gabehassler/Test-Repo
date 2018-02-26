package dr.app.beagle.evomodel.substmodel;
import dr.inference.model.Model;
import dr.inference.model.Parameter;
import java.util.List;
public class ProductChainFrequencyModel extends FrequencyModel {
    public ProductChainFrequencyModel(String name, List<FrequencyModel> freqModels) {
        super(name);
        this.freqModels = freqModels;
        int freqCount = 1;
        numBaseModel = freqModels.size();
        stateSizes = new int[numBaseModel];
        for (int i = 0; i < numBaseModel; i++) {
            int size = freqModels.get(i).getFrequencyCount();
            stateSizes[i] = size;
            freqCount *= size;
            addModel(freqModels.get(i));
        }
        tmp = new int[numBaseModel];
        totalFreqCount = freqCount;
    }
    protected void handleModelChangedEvent(Model model, Object object, int index) {
        fireModelChanged(model);
    }
    public void setFrequency(int i, double value) {
        throw new RuntimeException("Not implemented");
    }
    public double getFrequency(int index) {
        double freq = 1.0;
        decomposeEntry(index, tmp);       
        for (int i = 0; i < numBaseModel; i++) {
            freq *= freqModels.get(i).getFrequency(tmp[i]);
        }
        return freq;
    }
    public int[] decomposeEntry(int index) {
        int[] tmp = new int[numBaseModel];
        decomposeEntry(index, tmp);
        return tmp;
    }
    private void decomposeEntry(int index, int[] decomposition) {
        int current = index;
        for (int i = numBaseModel - 1; i >= 0; --i) {           
            decomposition[i] = current % stateSizes[i];
            current /= stateSizes[i];
        }
    }
    public int getFrequencyCount() {
        return totalFreqCount;
    }
    public Parameter getFrequencyParameter() {
        throw new RuntimeException("Not implemented");
    }
    private List<FrequencyModel> freqModels;
    private final int numBaseModel;
    private final int totalFreqCount;
    private final int[] stateSizes;
    private final int[] tmp;
}
