package dr.evomodel.simulator;
import dr.evolution.tree.Tree;
import dr.evolution.util.TaxonList;
import dr.evomodel.coalescent.CoalescentSimulator;
import dr.evomodel.coalescent.DemographicModel;
import dr.evomodel.tree.TreeStatistic;
import dr.inference.loggers.Logger;
import dr.xml.*;
import java.util.ArrayList;
public class Simulator implements Runnable {
	public static final String SIMULATOR = "simulator";
	public static final String REPEATS = "repeats";
	private final DemographicModel demographicModel;
	private final TaxonList taxa;
	private final int simulationCount;
	private final TreeStatistic[] statistics;
	private final Logger[] loggers;
	public Simulator(
		int simulationCount,
		DemographicModel demographicModel,
		TaxonList taxa,
		TreeStatistic[] statistics,
		Logger[] loggers) {
		this.simulationCount = simulationCount;
		this.demographicModel = demographicModel;
		this.taxa = taxa;
		this.loggers = loggers;
		this.statistics = statistics;
	}
	public void run() {
		simulate();
	}
	public void simulate() {
		if (loggers != null) {
            for(Logger logger : loggers) {
                logger.startLogging();
            }
		}
		CoalescentSimulator coalSim = new CoalescentSimulator();
		timer.start();
		for (int i = 0; i < simulationCount; i++) {
			Tree tree = coalSim.simulateTree(taxa, demographicModel);
			if (loggers != null) {
                for(TreeStatistic statistic : statistics) {
                    statistic.setTree(tree);
                }
                for(Logger logger : loggers) {
                    logger.log(i);
                }
			}
		}
		if (loggers != null) {
            for(Logger logger : loggers) {
                logger.stopLogging();
            }
		}
		timer.stop();
	}	
	public dr.util.Timer getTimer() { return timer; }
	public final int getSimulationCount() { return simulationCount; }
	public static XMLObjectParser getXMLObjectParser() {
		return new AbstractXMLObjectParser() {
			public String getParserName() { return SIMULATOR; }
			public Object parseXMLObject(XMLObject xo) throws XMLParseException {
				DemographicModel demoModel = (DemographicModel)xo.getChild(DemographicModel.class);
				TaxonList taxa = (TaxonList)xo.getChild(TaxonList.class);
				ArrayList<TreeStatistic> statistics = new ArrayList<TreeStatistic>();
				ArrayList<Logger> loggers = new ArrayList<Logger>();
				int repeats = xo.getIntegerAttribute(REPEATS);
				for (int i = 0; i < xo.getChildCount(); i++) {	
					final Object child = xo.getChild(i);
					if (child instanceof TreeStatistic) {
						statistics.add((TreeStatistic) child);
					} else if (child instanceof Logger) {
						loggers.add((Logger) child);
					}
				}
				TreeStatistic[] statArray = new TreeStatistic[statistics.size()];
				statistics.toArray(statArray);
				Logger[] logArray = new Logger[loggers.size()];
				loggers.toArray(logArray);
				Simulator simulator = new Simulator(repeats, demoModel, taxa, statArray, logArray);
				return simulator;
			}
			//************************************************************************
			// AbstractXMLObjectParser implementation
			//************************************************************************
			public String getParserDescription() {
				return "A simulator.";
			}
			public Class getReturnType() { return Simulator.class; }
			public XMLSyntaxRule[] getSyntaxRules() { return rules; }
			private final XMLSyntaxRule[] rules = {
				AttributeRule.newIntegerRule(REPEATS),
				new ElementRule(DemographicModel.class),
				new ElementRule(TaxonList.class),
				new ElementRule(TreeStatistic.class, 1, Integer.MAX_VALUE),
				new ElementRule(Logger.class, 1, Integer.MAX_VALUE)
			};
		};
	}
	// PRIVATE TRANSIENTS 
	private final dr.util.Timer timer = new dr.util.Timer();
}
