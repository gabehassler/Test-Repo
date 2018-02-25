package dr.app.tools;
import dr.app.beast.BeastVersion;
import dr.app.util.Arguments;
import dr.app.util.Utils;
import dr.inference.trace.TraceAnalysis;
import dr.inference.trace.TraceException;
import dr.util.Version;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
public class LogAnalyser {
private final static Version version = new BeastVersion();
public LogAnalyser(int burnin, String inputFileName, String outputFileName, boolean verbose,
boolean hpds, boolean ess, boolean stdErr,
String marginalLikelihood) throws java.io.IOException, TraceException {
File parentFile = new File(inputFileName);
if (parentFile.isDirectory()) {
System.out.println("Analysing all log files below directory: " + inputFileName);
} else if (parentFile.isFile()) {
System.out.println("Analysing log file: " + inputFileName);
} else {
System.err.println(inputFileName + " does not exist!");
System.exit(0);
}
if (outputFileName != null) {
FileOutputStream outputStream = new FileOutputStream(outputFileName);
System.setOut(new PrintStream(outputStream));
}
analyze(parentFile, burnin, verbose, new boolean[]{true}, hpds, ess, stdErr, marginalLikelihood);
}
public LogAnalyser(int burnin, File[] files, String outputFileName, boolean verbose,
boolean hpds, boolean ess, boolean stdErr,
String marginalLikelihood) throws java.io.IOException, TraceException {
for (File f : files) {
if (f.isFile()) {
System.out.println("Analysing log file: " + f.getAbsoluteFile());
} else {
System.err.println(f.getAbsoluteFile() + " does not exist!");
System.exit(0);
}
if (outputFileName != null) {
FileOutputStream outputStream = new FileOutputStream(outputFileName);
System.setOut(new PrintStream(outputStream));
}
//            setDefaultDir(f);
analyze(f, burnin, verbose, new boolean[]{true}, hpds, ess, stdErr, marginalLikelihood);
}
}
//
private static File openDefaultDirectory = null;
//
//    private void setDefaultDir(File file) {
//        final String s = file.getAbsolutePath();
//        String p = s.substring(0, s.length() - file.getName().length());
//        openDefaultDirectory = new File(p);
//        if (!openDefaultDirectory.isDirectory()) {
//            openDefaultDirectory = null;
//        }
//    }
private void analyze(File file, int burnin, boolean verbose, boolean[] drawHeader,
boolean hpds, boolean ess, boolean stdErr,
String marginalLikelihood) throws TraceException {
if (file.isFile()) {
try {
String name = file.getCanonicalPath();
if (verbose) {
TraceAnalysis.report(name, burnin, marginalLikelihood);
} else {
TraceAnalysis.shortReport(name, burnin, drawHeader[0], hpds, ess, stdErr, marginalLikelihood);
drawHeader[0] = false;
}
} catch (IOException e) {
//e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
}
} else {
File[] files = file.listFiles();
for (File f : files) {
if (f.isDirectory()) {
analyze(f, burnin, verbose, drawHeader, hpds, ess, stdErr, marginalLikelihood);
} else if (f.getName().endsWith(".log") || f.getName().endsWith(".p")) {
analyze(f, burnin, verbose, drawHeader, hpds, ess, stdErr, marginalLikelihood);
} else {
if (verbose) System.out.println("Ignoring file: " + f);
}
}
}
}
public static void printTitle() {
System.out.println();
centreLine("LogAnalyser " + version.getVersionString() + ", " + version.getDateString(), 60);
centreLine("MCMC Output analysis", 60);
centreLine("by", 60);
centreLine("Andrew Rambaut and Alexei J. Drummond", 60);
System.out.println();
centreLine("Institute of Evolutionary Biology", 60);
centreLine("University of Edinburgh", 60);
centreLine("a.rambaut@ed.ac.uk", 60);
System.out.println();
centreLine("Department of Computer Science", 60);
centreLine("University of Auckland", 60);
centreLine("alexei@cs.auckland.ac.nz", 60);
System.out.println();
System.out.println();
}
public static void centreLine(String line, int pageWidth) {
int n = pageWidth - line.length();
int n1 = n / 2;
for (int i = 0; i < n1; i++) {
System.out.print(" ");
}
System.out.println(line);
}
public static void printUsage(Arguments arguments) {
arguments.printUsage("loganalyser", "[-burnin <burnin>] [-short][-hpd] [-std] [<input-file-name> [<output-file-name>]]");
System.out.println();
System.out.println("  Example: loganalyser test.log");
System.out.println("  Example: loganalyser -burnin 10000 trees.log out.txt");
System.out.println();
}
//Main method
public static void main(String[] args) throws java.io.IOException, TraceException {
// There is a major issue with languages that use the comma as a decimal separator.
// To ensure compatibility between programs in the package, enforce the US locale.
Locale.setDefault(Locale.US);
printTitle();
Arguments arguments = new Arguments(
new Arguments.Option[]{
new Arguments.IntegerOption("burnin", "the number of states to be considered as 'burn-in'"),
new Arguments.Option("short", "use this option to produce a short report"),
new Arguments.Option("hpd", "use this option to produce hpds for each trace"),
new Arguments.Option("ess", "use this option to produce ESSs for each trace"),
new Arguments.Option("stdErr", "use this option to produce standard Error"),
new Arguments.StringOption("marginal", "trace_name", "specify the trace to use to calculate the marginal likelihood"),
//				new Arguments.Option("html", "format output as html"),
//				new Arguments.Option("svg", "generate svg graphics"),
new Arguments.Option("help", "option to print this message")
});
try {
arguments.parseArguments(args);
} catch (Arguments.ArgumentException ae) {
System.out.println(ae);
printUsage(arguments);
System.exit(1);
}
if (arguments.hasOption("help")) {
printUsage(arguments);
System.exit(0);
}
int burnin = -1;
if (arguments.hasOption("burnin")) {
burnin = arguments.getIntegerOption("burnin");
}
boolean hpds = arguments.hasOption("hpd");
boolean ess = arguments.hasOption("ess");
boolean stdErr = arguments.hasOption("stdErr");
boolean shortReport = arguments.hasOption("short");
String marginalLikelihood = null;
if (arguments.hasOption("marginal")) {
marginalLikelihood = arguments.getStringOption("marginal");
}
String inputFileName = null;
String outputFileName = null;
String[] args2 = arguments.getLeftoverArguments();
if (args2.length > 2) {
System.err.println("Unknown option: " + args2[2]);
System.err.println();
printUsage(arguments);
System.exit(1);
}
if (args2.length > 0) {
inputFileName = args2[0];
}
if (args2.length > 1) {
outputFileName = args2[1];
}
if (inputFileName == null) {
// No input file name was given so throw up a dialog box...
//            inputFileName = Utils.getLoadFileName("LogAnalyser " + version.getVersionString() + " - Select log file to analyse");
File[] files = Utils.getLoadFiles("LogAnalyser " + version.getVersionString() + " - Select log file to analyse",
openDefaultDirectory, "BEAST log (*.log) Files", "log", "txt");
new LogAnalyser(burnin, files, outputFileName, !shortReport, hpds, ess, stdErr, marginalLikelihood);
} else {
new LogAnalyser(burnin, inputFileName, outputFileName, !shortReport, hpds, ess, stdErr, marginalLikelihood);
}
System.exit(0);
}
}
