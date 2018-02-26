package dr.evolution.colouring;

import dr.app.beagle.evomodel.substmodel.SubstitutionModel;

public class SubstitutionModelToColourChangeMatrix extends ColourChangeMatrix {

    public SubstitutionModelToColourChangeMatrix(SubstitutionModel substModel) {
        this.substModel = substModel;                        
    }

    private SubstitutionModel substModel;

}
