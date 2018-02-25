package dr.app.beauti.generator;
import dr.app.beauti.util.XMLWriter;
public interface ComponentGenerator {
enum InsertionPoint {
BEFORE_TAXA,                // at the beginning of the document
IN_TAXON,                   // in each individual taxon
AFTER_TAXA,                 // after all taxon sets have been defined
AFTER_SEQUENCES,            // after all alignments have been defined
AFTER_PATTERNS,             // after all patterns
IN_TREE_MODEL,              // in the tree model(s)
AFTER_TREE_MODEL,           // after the tree model
AFTER_TREE_PRIOR,           // after the tree prior
AFTER_SUBSTITUTION_MODEL,   // after all substitution models
AFTER_SITE_MODEL,           // after all site models
IN_TREE_LIKELIHOOD,         // in the tree likelihood(s)
AFTER_TREE_LIKELIHOOD,      // after all tree likelihoods
AFTER_TRAITS,				// after each traits mapping 
BEFORE_OPERATORS,           // before the operator schedule
IN_OPERATORS,               // in the operator schedule
AFTER_OPERATORS,            // after the operator schedule
IN_MCMC_PRIOR,              // in the prior section of the MCMC
IN_MCMC_LIKELIHOOD,         // in the likelihood section of the MCMC
IN_SCREEN_LOG,              // in the screen log
AFTER_SCREEN_LOG,           // after the screen log
IN_FILE_LOG_PARAMETERS,     // in the file log after the parameters have been logged
IN_FILE_LOG_LIKELIHOODS,    // in the file log after the likelihoods have been logged
AFTER_FILE_LOG,             // after the file log
IN_TREES_LOG,               // in the trees log
AFTER_TREES_LOG,            // after the trees log
AFTER_MCMC                 // after the mcmc element
}
boolean usesInsertionPoint(InsertionPoint point);
void generateAtInsertionPoint(Generator generator, InsertionPoint point, Object item, XMLWriter writer);
void generateAtInsertionPoint(Generator generator, InsertionPoint point, Object item, String prefix, XMLWriter writer);
}
