package dr.inference.trace;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;
public class LogFileTraces extends AbstractTraceList {
    public LogFileTraces(String name, File file) {
        this.name = name;
        this.file = file;
    }
    public String getName() {
        return name;
    }
    public File getFile() {
        return file;
    }
    public long getMaxState() {
        return lastState;
    }
    public boolean isIncomplete() {
        return false;
    }
    public int getStateCount() {
        // This is done as two integer divisions to ensure the same rounding for
        // the burnin...
        return (int) (((lastState - firstState) / stepSize) - (getBurnIn() / stepSize) + 1);
    }
    public int getBurninStateCount() {
        return (getBurnIn() / stepSize);
    }
    public int getStepSize() {
        return stepSize;
    }
    public int getBurnIn() {
        return burnIn;
    }
    public int getTraceCount() {
        return traces.size();
    }
    public int getTraceIndex(String name) {
        for (int i = 0; i < traces.size(); i++) {
            Trace trace = getTrace(i);
            if (name.equals(trace.getName())) {
                return i;
            }
        }
        return -1;
    }
    public String getTraceName(int index) {
        return getTrace(index).getName();
    }
    public Trace getTrace(int index) {
        return traces.get(index);
    }
    public void setBurnIn(long burnin2) {
        this.burnIn = (int) burnin2;
        for (Trace trace : traces) {
            trace.setTraceStatistics(null);
        }
    }
    public double getStateValue(int trace, int index) {
        return (Double) getTrace(trace).getValue(index + (burnIn / stepSize));
    }
    public void getStateValues(int nState, double[] destination, int offset) {
        final int index1 = nState + (burnIn / stepSize);
        for (int k = 0; k < destination.length; ++k) {
            destination[k] = (Double) getTrace(k + offset).getValue(index1);
        }
    }
    public List getValues(int index, int fromIndex, int toIndex) {
        List newList = null;
        try {
            newList = getTrace(index).getValues(fromIndex, toIndex);
        } catch (Exception e) {
            System.err.println("getValues error: trace index = " + index);
        }
        return newList;
    }
    public List getValues(int index) {
        return this.getValues(index, getBurninStateCount(), getTrace(index).getValuesSize());
    }
    public List getBurninValues(int index) {
        return this.getValues(index, 0, getBurninStateCount());
    }
    public void loadTraces() throws TraceException, IOException {
        FileReader reader = new FileReader(file);
        loadTraces(reader);
        reader.close();
    }
    public void loadTraces(Reader r) throws TraceException, java.io.IOException {
        TrimLineReader reader = new LogFileTraces.TrimLineReader(r);
        // Read through to first token
        StringTokenizer tokens = reader.tokenizeLine();
        if (tokens == null) {
            throw new TraceException("Trace file is empty.");
        }
        // read over empty lines
        while (!tokens.hasMoreTokens()) {
            tokens = reader.tokenizeLine();
        }
        // skip the first column which should be the state number
        String token = tokens.nextToken();
        // lines starting with [ are ignored, assuming comments in MrBayes file
        // lines starting with # are ignored, assuming comments in Migrate or BEAST file
        while (token.startsWith("[") || token.startsWith("#")) {
            readTraceType(token, tokens); // using # to define type
            tokens = reader.tokenizeLine();
            // read over empty lines
            while (!tokens.hasMoreTokens()) {
                tokens = reader.tokenizeLine();
            }
            // read state token and ignore
            token = tokens.nextToken();
        }
        // read label tokens
        String[] labels = new String[tokens.countTokens()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = tokens.nextToken();
            addTraceAndType(labels[i]);
        }
        int traceCount = getTraceCount();
        boolean firstState = true;
        tokens = reader.tokenizeLine();
        while (tokens != null && tokens.hasMoreTokens()) {
            String stateString = tokens.nextToken();
            long state = 0;
            try {
                try {
                    // Changed this to parseDouble because LAMARC uses scientific notation for the state number
                    state = (long) Double.parseDouble(stateString);
                } catch (NumberFormatException nfe) {
                    throw new TraceException("Unable to parse state number in column 1 (Line " + reader.getLineNumber() + ")");
                }
                if (firstState) {
                    // MrBayes puts 1 as the first state, BEAST puts 0
                    // In order to get the same gap between subsequent samples,
                    // we force this to 0.
                    if (state == 1) state = 0;
                    firstState = false;
                }
                if (!addState(state)) {
                    throw new TraceException("State " + state + " is not consistent with previous spacing (Line " + reader.getLineNumber() + ")");
                }
            } catch (NumberFormatException nfe) {
                throw new TraceException("State " + state + ":Expected real value in column " + reader.getLineNumber());
            }
            for (int i = 0; i < traceCount; i++) {
                if (tokens.hasMoreTokens()) {
                    String value = tokens.nextToken();
                    if (state == 0) assignTraceTypeAccordingValue(value);
                    try {
//                        values[i] = Double.parseDouble(tokens.nextToken());
                        addParsedValue(i, value);
                    } catch (NumberFormatException nfe) {
                        throw new TraceException("State " + state + ": Expected correct number type (Double, Integer or String) in column "
                                + (i + 1) + " (Line " + reader.getLineNumber() + ")");
                    }
                } else {
                    throw new TraceException("State " + state + ": missing values at line " + reader.getLineNumber());
                }
            }
            tokens = reader.tokenizeLine();
        }
        burnIn = (int) (0.1 * lastState);
    }
    private void addParsedValue(int nTrace, String value) {
        String name = getTraceName(nTrace);
//        System.out.println(thisTrace.getTraceType() + "   " + value);
        if (tracesType.get(name) == TraceFactory.TraceType.DOUBLE
                || tracesType.get(name) == TraceFactory.TraceType.INTEGER) {
            Double v = Double.parseDouble(value);
            getTrace(nTrace).add(v);
        } else if (tracesType.get(name) == TraceFactory.TraceType.STRING) {
            getTrace(nTrace).add(value);
        } else {
            throw new RuntimeException("Trace type is not recognized: " + tracesType.get(name));
        }
    }
    private void assignTraceTypeAccordingValue(String value) {
        //todo
    }
    private void readTraceType(String firstToken, StringTokenizer tokens) {
        if (tokens.hasMoreTokens()) {
            String token; //= tokens.nextToken();
            if (firstToken.contains(TraceFactory.TraceType.INTEGER.toString())
                    || firstToken.contains(TraceFactory.TraceType.INTEGER.toString().toUpperCase())) {
                while (tokens.hasMoreTokens()) {
                    token = tokens.nextToken();
                    tracesType.put(token, TraceFactory.TraceType.INTEGER);
                }
            } else if (firstToken.contains(TraceFactory.TraceType.STRING.toString())
                    || firstToken.contains(TraceFactory.TraceType.STRING.toString().toUpperCase())) {
                while (tokens.hasMoreTokens()) {
                    token = tokens.nextToken();
                    tracesType.put(token, TraceFactory.TraceType.STRING);
                }
            }
        }
    }
//    public Trace<?> assignTraceType(String name, int numberOfLines) {
//        Trace<?> trace = null;
//        if (tracesType != null) {
//            if (tracesType.get(name) == TraceFactory.TraceType.INTEGER) {
//                trace = new DiscreteTrace(name, numberOfLines);
////                trace.setTraceType(TraceType.INTEGER);
//            } else if (tracesType.get(name) == TraceFactory.TraceType.STRING) {
//                trace = new CategoryTrace(name, numberOfLines);
////                trace.setTraceType(TraceType.STRING);
//            }
//        } else {
//            trace = new ContinuousTrace(name, numberOfLines); // default DOUBLE
//        }
//        return trace;
//    }
    //************************************************************************
    // private methods
    //************************************************************************
    // These methods are used by the load function, above
    private void addTraceAndType(String name) {
        if (tracesType == null || tracesType.get(name) == null || tracesType.get(name) == TraceFactory.TraceType.DOUBLE) {
            traces.add(createTrace(name, TraceFactory.TraceType.DOUBLE));
            tracesType.put(name, TraceFactory.TraceType.DOUBLE);
        } else {
            traces.add(createTrace(name, tracesType.get(name)));
        }
    }
    private Trace createTrace(String name, TraceFactory.TraceType traceType) {
        // System.out.println("create trace (" + name + ") with type " + traceType);
        switch (traceType) {
            case DOUBLE:
                return new Trace<Double>(name, TraceFactory.TraceType.DOUBLE);
            case INTEGER:
                return new Trace<Double>(name, TraceFactory.TraceType.INTEGER); // use Double for legacy issue
            case STRING:
                return new Trace<String>(name, TraceFactory.TraceType.STRING);
            default:
                throw new IllegalArgumentException("The trace type " + traceType + " is not recognized.");
        }
    }
    // TODO get rid of generic to make things easy
    // TODO change to String only, and parse to double, int or string in getValues according to trace type
    public void changeTraceType(int id, TraceFactory.TraceType newType) throws TraceException {
        if (id >= getTraceCount() || id < 0) throw new TraceException("trace id is invaild " + id);
        Trace trace = traces.get(id);
        if (trace.getTraceType() != newType) {
            Trace newTrace = null;
            try {
                if (trace.getTraceType() == TraceFactory.TraceType.STRING) {
                    if (newType == TraceFactory.TraceType.DOUBLE) {
                        newTrace = createTrace(trace.getName(), TraceFactory.TraceType.DOUBLE);
                    } else {
                        newTrace = createTrace(trace.getName(), TraceFactory.TraceType.INTEGER);
                    }
                    for (int i = 0; i < trace.getValuesSize(); i++) { // String => Double
                        newTrace.add(Double.parseDouble(trace.getValue(i).toString()));
                    }
                } else if (newType == TraceFactory.TraceType.STRING) {
                    newTrace = createTrace(trace.getName(), TraceFactory.TraceType.STRING);
                    for (int i = 0; i < trace.getValuesSize(); i++) { // Double => String
                        newTrace.add(trace.getValue(i).toString());
                    }
                } else {
                    newTrace = createTrace(trace.getName(), newType); // not need to copy values, because they are both Double
                }
            } catch (Exception e) {
                throw new TraceException("Type change is failed, when parsing " + trace.getTraceType()
                        + " to " + newType + " in trace " + trace.getName());
            }
            if (trace.getTraceType() == TraceFactory.TraceType.STRING || newType == TraceFactory.TraceType.STRING) {
                if (newTrace.getValuesSize() != trace.getValuesSize())
                    throw new TraceException("Type change is failed, because values size is different after copy !");
                traces.set(id, newTrace);
            } else {
                trace.setTraceType(newType);
            }
        }
    }
    private boolean addState(long stateNumber) {
        if (firstState < 0) {
            firstState = stateNumber;
        } else if (stepSize < 0) {
            stepSize = (int) (stateNumber - firstState);
        } else {
            int step = (int) (stateNumber - lastState);
            if (step != stepSize) {
            	//System.out.println("stateNumber: " + stateNumber + " lastState: " + lastState);
            	//System.out.println("step: " + step + " != " + stepSize);
                return false;
            }
        }
        lastState = stateNumber;
        return true;
    }
    protected final File file;
    protected final String name;
    private final List<Trace> traces = new ArrayList<Trace>();
    public void addTrace(String newTName, int i) {
        TraceCustomized tc = new TraceCustomized(newTName);
        tc.addValues(traces.get(i)); // only Double
        traces.add(tc);
        tracesType.put(newTName, TraceFactory.TraceType.DOUBLE);
    }
    // tracesType only save INTEGER and STRING, and only use during loading files
    private TreeMap<String, TraceFactory.TraceType> tracesType = new TreeMap<String, TraceFactory.TraceType>();
    private int burnIn = -1;
    private long firstState = -1;
    private long lastState = -1;
    private int stepSize = -1;
    public static class TrimLineReader extends BufferedReader {
        public TrimLineReader(Reader reader) {
            super(reader);
        }
        public String readLine() throws java.io.IOException {
            lineNumber += 1;
            String line = super.readLine();
            if (line != null) return line.trim();
            return null;
        }
        public StringTokenizer tokenizeLine() throws java.io.IOException {
            String line = readLine();
            if (line == null) return null;
            return new StringTokenizer(line, "\t");
        }
        public int getLineNumber() {
            return lineNumber;
        }
        private int lineNumber = 0;
    }
//    public class D extends LogFileTraces implements TraceList.D {
//
//        public D(String name, File file) {
//            super(name, file);
//        }
//
//        public Double[] getValues(int index, int length) {
//            return this.getValues(index, length, 0);
//        }
//
//        public Double[] getValues(int index, int length, int offset) {
//            Double[] destination = null;
//            try {
//                destination = ((Trace.D) getTrace(index)).getValues(length, getBurninStateCount(), offset, selected);
//            } catch (Exception e) {
//                System.err.println("getValues error: trace index = " + index);
//            }
//            return destination;
//        }
//
//        public Double[] getBurninValues(int index, int length) {
//            Double[] destination = null;
//            try {
//                destination = (Double[]) getTrace(index).getValues(length, 0, 0, getBurninStateCount(), selected);
//            } catch (Exception e) {
//                System.err.println("getValues error: trace index = " + index);
//            }
//            return destination;
//        }
//    }
}
