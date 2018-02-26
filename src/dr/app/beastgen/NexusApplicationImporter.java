package dr.app.beastgen;

import dr.app.beauti.options.BeautiOptions;
import dr.app.beauti.options.PartitionSubstitutionModel;
import dr.app.beauti.options.PartitionTreeModel;
import dr.app.beauti.types.StartingTreeType;
import dr.evolution.alignment.Alignment;
import dr.evolution.alignment.CharSetAlignment;
import dr.evolution.io.NexusImporter;
import dr.evomodel.substmodel.NucModelType;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class NexusApplicationImporter extends NexusImporter {

    public static final NexusBlock ASSUMPTIONS_BLOCK = new NexusBlock("ASSUMPTIONS");
    public static final NexusBlock SETS_BLOCK = new NexusBlock("SETS");
    public static final NexusBlock PAUP_BLOCK = new NexusBlock("PAUP");
    public static final NexusBlock MRBAYES_BLOCK = new NexusBlock("MRBAYES");

    public NexusApplicationImporter(Reader reader) {
        super(reader);
        setCommentDelimiters('[', ']', '\0');
    }

    public NexusApplicationImporter(Reader reader, Writer commentWriter) {
        super(reader, commentWriter);
        setCommentDelimiters('[', ']', '\0');
    }

    public NexusBlock findBlockName(String blockName) {
        if (blockName.equalsIgnoreCase(ASSUMPTIONS_BLOCK.toString())) {
            return ASSUMPTIONS_BLOCK;
        } else if (blockName.equalsIgnoreCase(SETS_BLOCK.toString())) {
            return SETS_BLOCK;
        } else if (blockName.equalsIgnoreCase(PAUP_BLOCK.toString())) {
            return PAUP_BLOCK;
        } else if (blockName.equalsIgnoreCase(MRBAYES_BLOCK.toString())) {
            return MRBAYES_BLOCK;
        } else {
            return super.findBlockName(blockName);
        }
    }

    public void parseAssumptionsBlock(List<CharSet> charSets) throws ImportException, IOException {
        boolean done = false;
        while (!done) {
            String command = readToken(";");
            if (command.equalsIgnoreCase("ENDBLOCK") || command.equalsIgnoreCase("END")) {
                done = true;
            } else if (match("CHARSET", command, 5)) {
                if (getLastDelimiter() != ';') {
                    charSets.add(readCharSetCommand());
                }
            } else {
                System.err.println("The command, '" + command + "', is not used by BEAST and has been ignored");
            }
        }
    }

    public PartitionSubstitutionModel parsePAUPBlock(BeautiOptions options, List<CharSet> charSets) throws ImportException, IOException {
        PartitionSubstitutionModel model = new PartitionSubstitutionModel(options, "nucs");
        readTopLevelBlock(options, model, charSets);
        return model;
    }

    public PartitionSubstitutionModel parseMrBayesBlock(BeautiOptions options, List<CharSet> charSets) throws ImportException, IOException {
        PartitionSubstitutionModel model = new PartitionSubstitutionModel(options, "nucs");
        readTopLevelBlock(options, model, charSets);
        return model;
    }

    private CharSet readCharSetCommand() throws ImportException, IOException {

        String name = readToken("=;");

        CharSet charSet = new CharSet(name);

//        System.out.print("Char set " + name);

        int from;
        int to;
        int every = 1;

        while (getLastDelimiter() != ';') {

            String token = readToken(";,");
            while (getLastDelimiter() != ';' && getLastDelimiter() != ',') {
                token += readToken(";,");
            }

            String[] parts = token.split("-");
            System.out.print(token + " ");

            try {

                if (parts.length == 2) {
                    from = Integer.parseInt(parts[0].trim());

                    String[] toParts = parts[1].split("\\\\");

                    to = Integer.parseInt(toParts[0].trim());

                    every = 1;
                    if (toParts.length > 1) every = Integer.parseInt(toParts[1].trim());

                } else if (parts.length == 1) {
                    from = Integer.parseInt(parts[0].trim());
                    to = from;
                } else {
                    throw new ImportException("CharSet, " + name + ", unable to be parsed");
                }
            } catch (NumberFormatException nfe) {
                throw new ImportException("CharSet, " + name + ", unable to be parsed");
            }
            charSet.addCharSetBlock(new CharSetBlock(from, to, every));

        }
        System.out.println();

        return charSet;
    }

    private void readTopLevelBlock(BeautiOptions options, PartitionSubstitutionModel model, List<CharSet> charSets)
            throws ImportException, IOException {
        boolean done = false;

        while (!done) {
            String command = readToken(";");
            if (command.equalsIgnoreCase("ENDBLOCK") || command.equalsIgnoreCase("END")) {
                done = true;
            } else if (match("HSEARCH", command, 2)) {
                // Once we reach a search in PAUP then stop
                done = true;
            } else if (match("MCMC", command, 4)) {
                if (getLastDelimiter() != ';') {
                    readMCMCCommand(options);
                }
                done = true;
            } else if (match("MCMCP", command, 5)) {
                if (getLastDelimiter() != ';') {
                    readMCMCCommand(options);
                }
            } else if (match("LSET", command, 2)) {
                if (getLastDelimiter() != ';') {
                    readLSETCommand(model);
                }
            } else if (match("CHARSET", command, 5)) {
                if (getLastDelimiter() != ';') {
                    charSets.add(readCharSetCommand());
                }
            } else {
                System.err.println("The command, '" + command + "', is not used by BEAST and has been ignored");
            }
        }
    }

    private void readLSETCommand(PartitionSubstitutionModel model) throws ImportException, IOException {
        boolean done = false;

        while (!done) {
            String subcommand = readToken("=;");
            if (match("NST", subcommand, 2)) {
                int nst = readInteger(";");
                if (nst == 1) {
                    model.setNucSubstitutionModel(NucModelType.JC);
                } else if (nst == 2) {
                    model.setNucSubstitutionModel(NucModelType.HKY);
                } else if (nst == 6) {
                    model.setNucSubstitutionModel(NucModelType.GTR);
                } else {
                    throw new BadFormatException("Bad value for NST subcommand of LSET command");
                }
            } else if (match("RATES", subcommand, 2)) {
                String token = readToken(";");

                if (match("EQUAL", token, 1)) {
                    model.setGammaHetero(false);
                    model.setInvarHetero(false);
                } else if (match("GAMMA", token, 1)) {
                    model.setGammaHetero(true);
                    model.setInvarHetero(false);
                } else if (match("PROPINV", token, 1)) {
                    model.setGammaHetero(false);
                    model.setInvarHetero(true);
                } else if (match("INVGAMMA", token, 1)) {
                    model.setGammaHetero(true);
                    model.setInvarHetero(true);
                } else if (match("ADGAMMA", token, 1)) {
                    System.err.println("The option, 'RATES=ADGAMMA', in the LSET command is not used by BEAST and has been ignored");
                } else if (match("SITESPEC", token, 1)) {
                    System.err.println("The option, 'RATES=SITESPEC', in the LSET command is not used by BEAST and has been ignored");
                } else {
                    throw new BadFormatException("Unknown value, '" + token + "'");
                }
            } else if (match("NGAMMACAT", subcommand, 2)) {

                model.setGammaCategories(readInteger(";"));
            } else {

                System.err.println("The option, '" + subcommand + "', in the LSET command is not used by BEAST and has been ignored");
            }

            if (getLastDelimiter() == ';') {
                done = true;
            }
        }
    }

    private void readMCMCCommand(BeautiOptions options) throws ImportException, IOException {
        boolean done = false;

        while (!done) {
            String subcommand = readToken("=;");
            if (match("NGEN", subcommand, 2)) {
                options.chainLength = readInteger(";");
            } else if (match("SAMPLEFREQ", subcommand, 2)) {
                options.logEvery = readInteger(";");
            } else if (match("PRINTFREQ", subcommand, 1)) {
                options.echoEvery = readInteger(";");
            } else if (match("FILENAME", subcommand, 1)) {
                options.fileName = readToken(";");
            } else if (match("BURNIN", subcommand, 1)) {
                options.burnIn = readInteger(";");
            } else if (match("STARTINGTREE", subcommand, 2)) {
                String token = readToken(";");
                if (match("USER", token, 1)) {
                    // How do we know what tree to use?
//                    options.startingTreeType = StartingTreeType.USER;
                    for (PartitionTreeModel model : options.getPartitionTreeModels()) {
                        model.setStartingTreeType(StartingTreeType.USER);
                    }
                } else if (match("RANDOM", token, 1)) {
//                    options.startingTreeType = StartingTreeType.RANDOM;
                    for (PartitionTreeModel model : options.getPartitionTreeModels()) {
                        model.setStartingTreeType(StartingTreeType.RANDOM);
                    }
                } else {
                    throw new BadFormatException("Unknown value, '" + token + "'");
                }
            } else {

                System.err.println("The option, '" + subcommand + "', in the MCMC command is not used by BEAST and has been ignored");
            }

            if (getLastDelimiter() == ';') {
                done = true;
            }
        }
    }

    private boolean match(String reference, String target, int min) throws ImportException {
        if (target.length() < min) {

            //throw new BadFormatException("Ambiguous command or subcommand, '" + target + "'");
        }

        return reference.startsWith(target.toUpperCase());
    }

    public class CharSet {

        String name;
        List<CharSetBlock> blocks;

        public CharSet(String name) {
            this.name = name;
            blocks = new ArrayList<CharSetBlock>();
        }

        public List<CharSetBlock> getBlocks() {
            return blocks;
        }

        public String getName() {
            return name;
        }

        public void addCharSetBlock(CharSetBlock b) {
            blocks.add(b);
        }
    }

    public class CharSetBlock {

        public CharSetBlock(int fromSite, int toSite, int every) {

            this.fromSite = fromSite;
            this.toSite = toSite;
            this.every = every;
        }

        public int getFromSite() {
            return fromSite;
        }

        public int getToSite() {
            return toSite;
        }

        public int getEvery() {
            return every;
        }

        private final int fromSite;
        private final int toSite;
        private final int every;
    }
}
