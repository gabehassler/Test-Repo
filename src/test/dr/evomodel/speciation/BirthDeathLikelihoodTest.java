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
public class BirthDeathLikelihoodTest extends TestCase {
static final String TL = "TL";
static final String TREE_HEIGHT = TreeModelParser.ROOT_HEIGHT;
private FlexibleTree tree;
public BirthDeathLikelihoodTest(String name) {
super(name);
}
public void setUp() throws Exception {
super.setUp();
NewickImporter importer = new NewickImporter("((1:1.0,2:1.0):1.0,3:2.0);");
tree = (FlexibleTree) importer.importTree(null);
}
public void testBirthDeathLikelihood() {
//birth rate
double b = 1.0;
//death rate
double d = 0.5;
// correct value for oriented trees
double correct = -3.534621219768513;
birthDeathLikelihoodTester(tree, b - d, d / b, correct);
}
private void birthDeathLikelihoodTester(
Tree tree, double birthRate, double deathRate, double logL) {
Parameter b = new Parameter.Default("b", birthRate, 0.0, Double.MAX_VALUE);
Parameter d = new Parameter.Default("d", deathRate, 0.0, Double.MAX_VALUE);
SpeciationModel speciationModel = new BirthDeathGernhard08Model(b, d, null, BirthDeathGernhard08Model.TreeType.ORIENTED,
Units.Type.YEARS);
Likelihood likelihood = new SpeciationLikelihood(tree, speciationModel, "bd.like");
assertEquals(logL, likelihood.getLogLikelihood(), 1e-14);
}
public static Test suite() {
return new TestSuite(BirthDeathLikelihoodTest.class);
}
}