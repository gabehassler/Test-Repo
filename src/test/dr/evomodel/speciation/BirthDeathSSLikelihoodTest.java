
package test.dr.evomodel.speciation;

import dr.evolution.io.NewickImporter;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.Tree;
import dr.evolution.util.Units;
import dr.evomodel.speciation.BirthDeathSerialSamplingModel;
import dr.evomodel.speciation.SpeciationLikelihood;
import dr.evomodel.speciation.SpeciationModel;
import dr.inference.model.Likelihood;
import dr.inference.model.Variable;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BirthDeathSSLikelihoodTest extends TestCase {

    private FlexibleTree tree;
    private FlexibleTree tree2;

    public BirthDeathSSLikelihoodTest(String name) {
        super(name);
    }

    public void setUp() throws Exception {
        super.setUp();

        NewickImporter importer = new NewickImporter("((1:1.0,2:1.0):1.0,3:2.0);");
        tree = (FlexibleTree) importer.importTree(null);

        importer = new NewickImporter("((3 : 1.5, 4 : 0.5) : 1 , (1 : 2, 2 : 1) : 3);");
        tree2 = (FlexibleTree) importer.importTree(null);
    }

    public void testBirthDeathLikelihoodBEAST2() {
        System.out.println("RootHeight = " + tree2.getRootHeight());
        Variable<Double> origin = new Variable.D("origin", 6.0);

        final double birthRate = 2.0;
        final double deathRate = 1.0;
        final double psiRate = 0.5; // rate of sampling taxa through time
        final double sampleProbability = 0.0; // the proportion of taxa sampled, default to fix to 0
        final boolean hasFinalSample = false;
        Variable<Double> b = new Variable.D("b", birthRate);
        Variable<Double> d = new Variable.D("d", deathRate);
        Variable<Double> psi = new Variable.D("psi", psiRate);
        Variable<Double> p = new Variable.D("p", sampleProbability);
        Variable<Double> r = new Variable.D("r", 0.0); // sampleBecomesNonInfectiousProb

        SpeciationModel speciationModel = new BirthDeathSerialSamplingModel(b, d, psi, p, false, r, hasFinalSample, origin, Units.Type.YEARS);
        Likelihood likelihood = new SpeciationLikelihood(tree2, speciationModel, "bdss.like");

        assertEquals(-19.0198, likelihood.getLogLikelihood(), 1e-5);
    }

    final double birthRate = 1.0;
    final double deathRate = 0.5;
    final double p = 1.0;
    final double psi = 0.0;

    public void testBirthDeathLikelihoodP0() {
        assertEquals(BirthDeathSerialSamplingModel.p0(birthRate, deathRate, p, psi, 1.0), 0.28236670080320814);
    }

//    public void testP1() {
//        assertEquals(BirthDeathSerialSamplingModel.p1(birthRate, deathRate, p, psi, 1.0), 0.31236180503535266);
//    }

//    public void testPureBirthLikelihood() {
//        likelihoodTester(tree, 1, 0, -2.8219461696520542);
//    }

//    public void testBirthDeathLikelihood() {
//        likelihoodTester(tree, birthRate, deathRate, null, -4.633233508436623);
//    }

    public void testBirthDeathLikelihoodOrigin() {
        System.out.println("RootHeight = " + tree.getRootHeight());
        Variable<Double> origin = new Variable.D("origin", 50);
        likelihoodTester(tree, birthRate, deathRate, origin, -29.529647743897872);
    }

    private void likelihoodTester(Tree tree, double birthRate, double deathRate, Variable<Double> origin, double logL) {

        Variable<Double> b = new Variable.D("b", birthRate);
        Variable<Double> d = new Variable.D("d", deathRate);
        Variable<Double> psi = new Variable.D("psi", this.psi);
        Variable<Double> p = new Variable.D("p", this.p);
        Variable<Double> r = new Variable.D("r", 0.5);
        Variable<Double> fTime = new Variable.D("time", 0.0);

        SpeciationModel speciationModel = new BirthDeathSerialSamplingModel(b, d, psi, p, false, r, true, origin, Units.Type.YEARS);
        Likelihood likelihood = new SpeciationLikelihood(tree, speciationModel, "bdss.like");

        assertEquals(logL, likelihood.getLogLikelihood());
    }

    public static Test suite() {
        return new TestSuite(BirthDeathSSLikelihoodTest.class);
    }
}