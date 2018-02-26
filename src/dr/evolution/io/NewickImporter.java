
package dr.evolution.io;

import dr.evolution.tree.FlexibleNode;
import dr.evolution.tree.FlexibleTree;
import dr.evolution.tree.Tree;
import dr.evolution.util.Taxon;
import dr.evolution.util.TaxonList;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class NewickImporter extends Importer implements TreeImporter {
    public static final String COMMENT = "comment";

    public class BranchMissingException extends ImportException {
        private static final long serialVersionUID = 777435104809244693L;

        public BranchMissingException() {
            super();
        }

        public BranchMissingException(String msg) {
            super("Branch missing: " + msg);
            System.err.println(msg);
        }
    }

    public NewickImporter(Reader reader) {
        super(reader);
    }

    public NewickImporter(String treeString) {
        this(new StringReader(treeString));
    }

    public Tree importTree(TaxonList taxonList) throws IOException, ImportException {
        setCommentDelimiters('[', ']', '\0', '\0', '&');

        try {
            skipUntil("(");
            unreadCharacter('(');

            final FlexibleNode root = readInternalNode(taxonList);
            if (getLastMetaComment() != null) {
                root.setAttribute(COMMENT, getLastMetaComment());
            }
//			if (getLastDelimiter() != ';') {
//				throw new BadFormatException("Expecting ';' after tree");
//			}

            return new FlexibleTree(root, false, true);

        } catch (EOFException e) {
            throw new ImportException("incomplete tree");
        }
    }

    public Tree[] importTrees(TaxonList taxonList) throws IOException, ImportException {
        boolean done = false;
        ArrayList<FlexibleTree> array = new ArrayList<FlexibleTree>();

        do {

            try {

                skipUntil("(");
                unreadCharacter('(');

                FlexibleNode root = readInternalNode(taxonList);
                FlexibleTree tree = new FlexibleTree(root, false, true);
                array.add(tree);

                if (taxonList == null) {
                    taxonList = tree;
                }

                if (readCharacter() != ';') {
                    throw new BadFormatException("Expecting ';' after tree");
                }

            } catch (EOFException e) {
                done = true;
            }
        } while (!done);

        Tree[] trees = new Tree[array.size()];
        array.toArray(trees);

        return trees;
    }

    public boolean hasTree() throws IOException, ImportException {
        try {
            skipUntil("(");
            unreadCharacter('(');
        } catch (EOFException e) {
            lastTree = null;
            return false;
        }

        return true;
    }

    private Tree lastTree = null;

    public Tree importNextTree() throws IOException, ImportException {
        FlexibleTree tree = null;

        try {
            skipUntil("(");
            unreadCharacter('(');

            FlexibleNode root = readInternalNode(lastTree);

            tree = new FlexibleTree(root, false, true);

        } catch (EOFException e) {
            //
        }

        lastTree = tree;

        return tree;
    }

    private FlexibleNode readBranch(TaxonList taxonList) throws IOException, ImportException {
        double length = 0.0;
        FlexibleNode branch;

        if (nextCharacter() == '(') {
            // is an internal node
            branch = readInternalNode(taxonList);

        } else {
            // is an external node
            branch = readExternalNode(taxonList);
        }

        final String comment = getLastMetaComment();
        if (comment != null) {
            branch.setAttribute(COMMENT, comment);
            clearLastMetaComment();
        }

        if (getLastDelimiter() == ':') {
            length = readDouble(",():;");
        }

        branch.setLength(length);

        return branch;
    }

    private FlexibleNode readInternalNode(TaxonList taxonList) throws IOException, ImportException {
        FlexibleNode node = new FlexibleNode();

        // read the opening '('
        final char ch = readCharacter();
        assert ch == '(';

        // read the first child
        node.addChild(readBranch(taxonList));

        // an internal node must have at least 2 children
        if (getLastDelimiter() != ',') {
            throw new BadFormatException("Expecting ',' in tree, but got '" + (char) getLastDelimiter() + "'");
        }

        // read subsequent children
        do {
            node.addChild(readBranch(taxonList));

        } while (getLastDelimiter() == ',');

        // should have had a closing ')'
        if (getLastDelimiter() != ')') {
            throw new BadFormatException("Missing closing ')' in tree");
        }

        // If there is a label before the colon, store it:
        try {
            String label = readToken(",():;");
            if (label.length() > 0) {
                node.setAttribute("label", label);
            }
        } catch (IOException ioe) {
            // probably an end of file without a terminal ';'
            // we are going to allow this and return the nodes...
        }

        return node;
    }

    private FlexibleNode readExternalNode(TaxonList taxonList) throws IOException, ImportException {
        FlexibleNode node = new FlexibleNode();

        String label = readToken(":(),;");

        Taxon taxon;

        if (taxonList != null) {
            // if a taxon list is given then the taxon must be in it...
            int index = taxonList.getTaxonIndex(label);
            if (index != -1) {
                taxon = taxonList.getTaxon(index);
            } else {
                throw new UnknownTaxonException("Taxon in tree, '" + label + "' is unknown");
            }
        } else {
            // No taxon list given so create new taxa
            taxon = new Taxon(label);
        }

        node.setTaxon(taxon);
        return node;
    }

}
