
package dr.app.beagle.evomodel.treelikelihood;

public interface MarkovJumpsTraitProvider extends AncestralStateTraitProvider {

    public int getPatternCount();

    public double getLogLikelihood();

}
