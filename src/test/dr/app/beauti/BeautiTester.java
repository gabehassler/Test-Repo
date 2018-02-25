package test.dr.app.beauti;
import dr.app.beauti.options.BeautiOptions;
public class BeautiTester {     
public BeautiTester() {   
BeautiTesterConfig btc = new BeautiTesterConfig();
btc.createScriptWriter("tests/run_script.sh");
BeautiOptions beautiOptions = btc.createOptions();
btc.importFromFile("examples/Primates.nex", beautiOptions, false); 
btc.buildNucModels("tests/pri_", beautiOptions);
beautiOptions = btc.createOptions();
btc.importFromFile("examples/Primates.nex", beautiOptions, true);
btc.buildAAModels("tests/pri_", beautiOptions);
beautiOptions = btc.createOptions();
btc.importFromFile("examples/Dengue4.env.nex", beautiOptions, false);
//        beautiOptions.fixedSubstitutionRate = false;
btc.buildNucModels("tests/den_", beautiOptions);
beautiOptions = btc.createOptions();
btc.importFromFile("examples/Dengue4.env.nex", beautiOptions, true);
//        beautiOptions.fixedSubstitutionRate = false;
btc.buildAAModels("tests/den_", beautiOptions);
btc.closeScriptWriter();
}
//Main method
public static void main(String[] args) {
new BeautiTester();
}
}
