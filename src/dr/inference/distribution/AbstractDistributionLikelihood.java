package dr.inference.distribution;
import dr.inference.model.Likelihood;
import dr.inference.model.Model;
import dr.util.Attribute;
import java.util.ArrayList;
import java.util.List;
public abstract class AbstractDistributionLikelihood extends Likelihood.Abstract {
    public AbstractDistributionLikelihood(Model model) {
        super(model);
    }
    public void addData(Attribute<double[]> data) {
        dataList.add(data);
    }
    protected ArrayList<Attribute<double[]>> dataList = new ArrayList<Attribute<double[]>>();
    public abstract double calculateLogLikelihood();
    protected boolean getLikelihoodKnown() {
        return false;
	}
    public List<Attribute<double[]>> getDataList() {
        return dataList;
    }
}
