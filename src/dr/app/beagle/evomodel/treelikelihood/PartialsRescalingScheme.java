package dr.app.beagle.evomodel.treelikelihood;
public enum PartialsRescalingScheme {
DEFAULT("default"), // what ever our current favourite default is
NONE("none"),       // no scaling
DYNAMIC("dynamic"), // rescale when needed and reuse scaling factors
ALWAYS("always"),   // rescale every node, every site, every time - slow but safe
DELAYED("delayed"), // postpone until first underflow then switch to 'always'
AUTO("auto");       // BEAGLE automatic scaling - currently playing it safe with 'always'
//    KICK_ASS("kickAss"),// should be good, probably still to be discovered
PartialsRescalingScheme(String text) {
this.text = text;
}
public String getText() {
return text;
}
private final String text;
public static PartialsRescalingScheme parseFromString(String text) {
for (PartialsRescalingScheme scheme : PartialsRescalingScheme.values()) {
if (scheme.getText().compareToIgnoreCase(text) == 0)
return scheme;
}
return DEFAULT;
}
@Override
public String toString() {
return text;
}
}
