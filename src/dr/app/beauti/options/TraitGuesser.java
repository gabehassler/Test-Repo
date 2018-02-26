package dr.app.beauti.options;
import dr.app.beauti.traitspanel.GuessTraitException;
import dr.evolution.util.TaxonList;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class TraitGuesser implements Serializable {
    private static final long serialVersionUID = 1786080930502001515L;
    private final TraitData traitData;
    public TraitGuesser(TraitData traitData) {
        this.traitData = traitData;
    }
    public static enum GuessType {
        DELIMITER,
        REGEX
    }
    private GuessType guessType = GuessType.DELIMITER;
    private int order = 0;
    private String delimiter;
    private String regex;
    private static final String REGEX_CHARACTERS = "|[].*()-^$";
    ////////////////////////////////////////////////////////////////
    public TraitData getTraitData() {
        return traitData;
    }
    public GuessType getGuessType() {
        return guessType;
    }
    public void setGuessType(GuessType guessType) {
        this.guessType = guessType;
    }
    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public String getDelimiter() {
        return delimiter;
    }
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    public String getRegex() {
        return regex;
    }
    public void setRegex(String regex) {
        this.regex = regex;
    }
    ////////////////////////////////////////////////////////////////
    public void guessTrait(TaxonList taxa) {
        for (int i = 0; i < taxa.getTaxonCount(); i++) {
            String value = null;
            try {
                switch (guessType) {
                    case DELIMITER:
                        value = guessTraitByDelimiter(taxa.getTaxonId(i), delimiter);
                        break;
                    case REGEX:
                        value = guessTraitFromRegex(taxa.getTaxonId(i), regex);
                        break;
                    default:
                        throw new IllegalArgumentException("unknown GuessType");
                }
            } catch (GuessTraitException gfe) {
                //
            }
            taxa.getTaxon(i).setAttribute(traitData.getName(), value);
        }
    }
    private String guessTraitByDelimiter(String label, String delimiter) throws GuessTraitException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < delimiter.length(); i++) {
            if (REGEX_CHARACTERS.contains("" + delimiter.charAt(i))) {
                sb.append("\\").append(delimiter.charAt(i));
            } else {
                sb.append(delimiter.charAt(i));
            }
        }
        if (sb.toString().length() < 1) {
            throw new IllegalArgumentException("No delimiter");
        }
        String[] tokens = label.split(sb.toString());
        if (tokens.length < 2) {
            throw new IllegalArgumentException("Can not find delimiter in taxon label (" + label + ")\n or invalid delimiter (" + delimiter + ").");
        }
        if (order >= 0) {
            if (order >= tokens.length) {
                throw new IllegalArgumentException("Insufficent delimiters in taxon lable (" + label + ")\n to find requested field.");
            }
            return tokens[order];
        } else {
            if (tokens.length + order < 0) {
                throw new IllegalArgumentException("Insufficent delimiters in taxon lable (" + label + ")\n to find requested field.");
            }
            return tokens[tokens.length + order];
        }
    }
    private String guessTraitFromRegex(String label, String regex) throws GuessTraitException {
        String t;
        if (!regex.contains("(")) {
            // if user hasn't specified a replace element, assume the whole regex should match
            regex = "(" + regex + ")";
        }
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(label);
            if (!matcher.find()) {
                throw new GuessTraitException("Regular expression doesn't find a match in taxon label, " + label);
            }
            if (matcher.groupCount() < 1) {
                throw new GuessTraitException("Trait value group not defined in regular expression");
            }
            t = matcher.group(1); // TODO: not working?
        } catch (NumberFormatException nfe) {
            throw new GuessTraitException("Badly formatted trait value in taxon label, " + label);
        }
        return t;
    }
}
