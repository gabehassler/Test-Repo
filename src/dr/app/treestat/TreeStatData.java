
package dr.app.treestat;

import dr.app.treestat.statistics.SummaryStatisticDescription;
import org.jdom.Document;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeStatData {
	public static final String version = "1.0";

	public TreeStatData() {
	}

	// Data options
	public Set<String> allTaxa = new HashSet<String>();
	public List<TaxonSet> taxonSets = new ArrayList<TaxonSet>();
	public List<Character> characters = new ArrayList<Character>();
	public List<SummaryStatisticDescription> statistics = new ArrayList<SummaryStatisticDescription>();

	public static class TaxonSet {
		String name;
		List taxa;
		public String toString() { return name; }
	}

	public static class Character {
		String name;
		List<TreeStatData.State> states;
		public String toString() { return name; }
	}

	public static class State {
		String name;
		String description;
		List<String> taxa;
		public String toString() { return name; }
	}

	public Document create() {

		Element root = new Element("treeTracer");
		root.setAttribute("version", version);

		Element taxonSetsElement = new Element("taxonSets");
		Element charactersElement = new Element("characters");
		Element statisticsElement = new Element("statistics");

		root.addContent(taxonSetsElement);
		root.addContent(charactersElement);
		root.addContent(statisticsElement);

        return new Document(root);
	}

	public void parse(Document document) throws dr.xml.XMLParseException {

		Element root = document.getRootElement();
		if (!root.getName().equals("treeTracer")) {
			throw new dr.xml.XMLParseException("This document does not appear to be a TreeTracer file");
		}
	}
}

