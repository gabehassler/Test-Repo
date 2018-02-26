
package dr.evomodel.arg;

import dr.evoxml.util.GraphMLUtils;
import dr.inference.loggers.LogFormatter;
import dr.inference.loggers.MCLogger;
import dr.inference.loggers.MLLogger;
import dr.inference.loggers.TabDelimitedFormatter;
import dr.xml.*;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.logging.Logger;

public class ARGLogger extends MCLogger {

	public static final String LOG_ARG = "logArg";
	public static final String DOT_FORMAT = "dotFormat";
	public static final String COMPRESSED_STRING = "compressedString";
	public static final String EXTENDED_NEWICK = "extendedNewick";
	public static final String STRIPPED_NEWICK = "strippedNewick";
	public static final String FULL_STRING = "fullString";
	public static final String DISTINCT_TREES = "distinctTrees";
	public static final String FORMAT = "format";

	// The following were in MCLogger; where did they go?
	public static final String LOG = "log";
	public static final String ECHO = "echo";
	public static final String ECHO_EVERY = "echoEvery";
	public static final String TITLE = "title";
	public static final String FILE_NAME = "fileName";
	//	public static final String FORMAT = "format";
	public static final String TAB = "tab";
	public static final String HTML = "html";
	public static final String PRETTY = "pretty";
	public static final String LOG_EVERY = "logEvery";

	public static final String COLUMNS = "columns";
	public static final String COLUMN = "column";
	public static final String LABEL = "label";
	public static final String SIGNIFICANT_FIGURES = "sf";
	public static final String DECIMAL_PLACES = "dp";
	public static final String WIDTH = "width";


	private ARGModel argModel;
	private String formatType;
	
	private ARGDistinctTreeCountStatistic treeStat;

	public ARGLogger(ARGModel argModel, LogFormatter formatter, int logEvery, String formatType) {
		super(formatter, logEvery, false);
		this.argModel = argModel;
		this.formatType = formatType;
		
		if(formatType.equals(DISTINCT_TREES)){
			treeStat = new ARGDistinctTreeCountStatistic(argModel);
		}
	}


	public static final String GRAPHML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">";
	public static final String GRAPHML_FOOTER = "</graphml>";

	public void startLogging() {
		if (formatType.equals(EXTENDED_NEWICK)) {
			logLine("stateARG.string");
		} else if (formatType.equals(STRIPPED_NEWICK)) {
			logLine("StrippedARGString");
		} else {
			logLine(GRAPHML_HEADER);
		}
//		if (!dotFormat && !newickFormat)
//			logLine(GRAPHML_HEADER);
//		if (newickFormat)
//			logLine("state ARG.string");
	}

	public void stopLogging() {
		if (!formatType.equals(EXTENDED_NEWICK) && !formatType.equals(STRIPPED_NEWICK)) {
			logLine(GRAPHML_FOOTER);
		}
//		if (!dotFormat && !newickFormat)
//			logLine(GRAPHML_FOOTER);
	}

	static XMLOutputter outputter = new XMLOutputter(org.jdom.output.Format.getPrettyFormat());

	public void log(long state) {
		if (logEvery <= 0 || ((state % logEvery) == 0)) {
			Element graphElement = argModel.toXML();
			graphElement.setAttribute(ARGModel.ID_ATTRIBUTE, "STATE_" + state);
			if (formatType.equals(DOT_FORMAT))
				logLine(GraphMLUtils.dotFormat(graphElement));
			else if (formatType.equals(COMPRESSED_STRING))
				logLine("ARG STATE_" + state + " = " + argModel.toGraphStringCompressed(false));
			else if (formatType.equals(FULL_STRING))
				logLine(outputter.outputString(graphElement));
			else if (formatType.equals(EXTENDED_NEWICK))
				logLine(argModel.toExtendedNewick());
			else if (formatType.equals(DISTINCT_TREES))
				logLine(treeStat.getStatisticValue(0) + "\t" + treeStat.getFullOutput());
			else
				logLine(argModel.toStrippedNewick());
		}
//		logLine("ARG STATE_" + state + " = " + argModel.toExtendedNewick());
	}


	public static XMLObjectParser PARSER = new AbstractXMLObjectParser() {

		public String getParserName() {
			return LOG_ARG;
		}

		public Object parseXMLObject(XMLObject xo) throws XMLParseException {

			ARGModel argModel = (ARGModel) xo.getChild(ARGModel.class);

			String fileName = null;
			String title = null;
			String argModelLoggerFormat = null;
//			boolean dotFormat = false;
//			boolean newickFormat = false;
//			boolean nexusFormat = false;
//
//			String colouringLabel = "demes";
//			String rateLabel = "rate";
//			String likelihoodLabel = "lnP";

			if (xo.hasAttribute(TITLE)) {
				title = xo.getStringAttribute(TITLE);
			}

			if (xo.hasAttribute(FILE_NAME)) {
				fileName = xo.getStringAttribute(FILE_NAME);
			}

			if (xo.hasAttribute(FORMAT)) {
				argModelLoggerFormat = xo.getStringAttribute(FORMAT);
			}

//			boolean substitutions = false;
//			if (xo.hasAttribute(BRANCH_LENGTHS)) {
//				substitutions = xo.getStringAttribute(BRANCH_LENGTHS).equals(SUBSTITUTIONS);
//			}

//			BranchRateModel branchRateModel = (BranchRateModel) xo.getChild(BranchRateModel.class);

//			ColourSamplerModel colourSamplerModel = (ColourSamplerModel) xo.getChild(ColourSamplerModel.class);

//			Likelihood likelihood = (Likelihood) xo.getChild(Likelihood.class);

			// logEvery of zero only displays at the end
			int logEvery = 1;

			if (xo.hasAttribute(LOG_EVERY)) {
				logEvery = xo.getIntegerAttribute(LOG_EVERY);
			}

			PrintWriter pw;

			if (fileName != null) {

				try {
					File file = new File(fileName);
					String name = file.getName();
					String parent = file.getParent();

					if (!file.isAbsolute()) {
						parent = System.getProperty("user.dir");
					}

//					System.out.println("Writing log file to "+parent+System.getProperty("path.separator")+name);
					pw = new PrintWriter(new FileOutputStream(new File(parent, name)));
				} catch (FileNotFoundException fnfe) {
					throw new XMLParseException("File '" + fileName + "' can not be opened for " + getParserName() + " element.");
				}
			} else {
				pw = new PrintWriter(System.out);
			}

			LogFormatter formatter = new TabDelimitedFormatter(pw);

			Logger.getLogger("dr.evomodel").info("Creating " + LOG_ARG + " of type " + argModelLoggerFormat);
			
			ARGLogger logger = new ARGLogger(argModel, formatter, logEvery, argModelLoggerFormat);

//			TreeLogger logger = new TreeLogger(tree, branchRateModel, rateLabel,
//					colourSamplerModel, colouringLabel, likelihood, likelihoodLabel,
//					formatter, logEvery, nexusFormat, substitutions);

			if (title != null) {
				logger.setTitle(title);
			}

			return logger;
		}

		//************************************************************************
		// AbstractXMLObjectParser implementation
		//************************************************************************
		public XMLSyntaxRule[] getSyntaxRules() {
			return rules;
		}

		String[] validFormats = {DOT_FORMAT, EXTENDED_NEWICK,
				COMPRESSED_STRING, FULL_STRING, STRIPPED_NEWICK, DISTINCT_TREES};

		private XMLSyntaxRule[] rules = new XMLSyntaxRule[]{

				AttributeRule.newIntegerRule(LOG_EVERY),
				new StringAttributeRule(FORMAT, "The type logger's output type",
						validFormats, false),
				new StringAttributeRule(FILE_NAME,
						"The name of the file to send log output to. " +
								"If no file name is specified then log is sent to standard output", true),
				new StringAttributeRule(TITLE, "The title of the log", true),
//				AttributeRule.newBooleanRule(NEXUS_FORMAT, true,
//						"Whether to use the NEXUS format for the tree log"),
//				new StringAttributeRule(BRANCH_LENGTHS, "What units should the branch lengths be in", new String[]{TIME, SUBSTITUTIONS}, true),
				new ElementRule(ARGModel.class, "The ARG which is to be logged"),
//				new ElementRule(BranchRateModel.class, true),
//				new ElementRule(ColourSamplerModel.class, true),
//				new ElementRule(Likelihood.class, true)
		};

		public String getParserDescription() {
			return "Logs an ARG to a file";
		}

		public String getExample() {
			return
					"<!-- The " + getParserName() + " element takes an argModel to be logged -->\n" +
							"<" + getParserName() + " " + LOG_EVERY + "=\"100\" " + FILE_NAME + "=\"log.args\">\n" +
							"	<argModel idref=\"treeModel1\"/>\n" +
							"</" + getParserName() + ">\n";
		}

		public Class getReturnType() {
			return MLLogger.class;
		}
	};


}
