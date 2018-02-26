package dr.evomodel.sitemodel;
import dr.evomodel.substmodel.FrequencyModel;
import dr.evomodel.substmodel.SubstitutionModel;
public interface SiteModel extends SiteRateModel {
    public static final String SITE_MODEL = "siteModel";
    SubstitutionModel getSubstitutionModel();
    boolean integrateAcrossCategories();  // TODO Consider moving into SiteRateModel
    int getCategoryOfSite(int site);    // TODO Consider moving into SiteRateModel
    FrequencyModel getFrequencyModel();
}