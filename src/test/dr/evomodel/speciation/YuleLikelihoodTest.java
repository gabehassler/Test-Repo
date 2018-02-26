package test.dr.evomodel.speciation;
import dr.evolution.io.NewickImporter;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.evomodel.speciation.BirthDeathGernhard08Model;
import dr.evomodel.speciation.SpeciationLikelihood;
import dr.evomodel.speciation.SpeciationModel;
import dr.evomodelxml.tree.TreeModelParser;
import dr.inference.model.Likelihood;
import dr.inference.model.Parameter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
public class YuleLikelihoodTest extends TestCase {
    static final String TL = "TL";
    static final String TREE_HEIGHT = TreeModelParser.ROOT_HEIGHT;
    private FlexibleTree tree;
    public YuleLikelihoodTest(String name) {
        super(name);
    }
    public void setUp() throws Exception {
        super.setUp();
        NewickImporter importer = new NewickImporter("((1:1.0,2:1.0):1.0,(3:1.0,4:1.0):1.0);");
        tree = (FlexibleTree) importer.importTree(null);
    }
    public void testYuleLikelihood() {
        yuleLikelihoodTester(tree, 1.0, -2.8219461696520542);
    }
    private void yuleLikelihoodTester(Tree tree, double birthRate, double logL) {
        Parameter b = new Parameter.Default("b", birthRate, 0.0, Double.MAX_VALUE);
        Parameter d = new Parameter.Default("d", 0.0, 0.0, Double.MAX_VALUE);
        SpeciationModel speciationModel = new BirthDeathGernhard08Model(b, d, null, BirthDeathGernhard08Model.TreeType.TIMESONLY,
                Units.Type.YEARS);
        Likelihood likelihood = new SpeciationLikelihood(tree, speciationModel, "yule.like");
        assertEquals(likelihood.getLogLikelihood(), logL);
    }
    public static Test suite() {
        return new TestSuite(YuleLikelihoodTest.class);
    }
}