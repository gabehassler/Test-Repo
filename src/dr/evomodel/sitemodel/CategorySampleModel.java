
package dr.evomodel.sitemodel; 



public interface CategorySampleModel extends SiteModel{
	
	void toggleRandomSite();
	
	void addSitesInCategoryCount(int category);
	
	void subtractSitesInCategoryCount(int category);
	
	int getSitesInCategoryCount(int category);


	void setCategoriesParameter(int siteCount);


}