
package dr.app.util;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;

public class Utils {
    public static final String TRAITS = "traits";

    public static String absName(File file) {
        return (file != null) ? file.getAbsolutePath() : null;
    }

    public static String getLoadFileName(String message) {
        return absName(getLoadFile(message));
    }

    public static String getSaveFileName(String message) {
        return absName(getSaveFile(message));
    }

    public static File getLoadFile(String message) {
        // No file name in the arguments so throw up a dialog box...
        java.awt.Frame frame = new java.awt.Frame();
        java.awt.FileDialog chooser = new java.awt.FileDialog(frame, message,
                java.awt.FileDialog.LOAD);
//        chooser.show();
        chooser.setVisible(true);
        if (chooser.getFile() == null) return null;
        java.io.File file = new java.io.File(chooser.getDirectory(), chooser.getFile());
        chooser.dispose();
        frame.dispose();

        return file;
    }

    public static File[] getLoadFiles(String message, File openDefaultDirectory, String description, String... extensions) {
        // No file name in the arguments so throw up a dialog box...
        java.awt.Frame frame = new java.awt.Frame();
        frame.setTitle(message);
        final JFileChooser chooser = new JFileChooser(openDefaultDirectory);
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
        chooser.setFileFilter(filter);

        final int returnVal = chooser.showOpenDialog(frame);
        File[] files = null;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            files = chooser.getSelectedFiles();

        }
        frame.dispose();
        return files;
    }

    public static File getSaveFile(String message) {
        // No file name in the arguments so throw up a dialog box...
        java.awt.Frame frame = new java.awt.Frame();
        java.awt.FileDialog chooser = new java.awt.FileDialog(frame, message,
                java.awt.FileDialog.SAVE);
//        chooser.show();
        chooser.setVisible(true);
        java.io.File file = new java.io.File(chooser.getDirectory(), chooser.getFile());
        chooser.dispose();
        frame.dispose();

        return file;
    }

    // detect type of text value - return class of type

    public static Class detectType(final String valueString) {
        if (valueString.equalsIgnoreCase("TRUE") || valueString.equalsIgnoreCase("FALSE")) {
            return Boolean.class;
        }

        try {
            final double number = Double.parseDouble(valueString);
            if (Math.round(number) == number) {
                return Integer.class;
            }
            return Double.class;
        } catch (NumberFormatException pe) {
            return String.class;
        }
    }

    // New object of type cl from text.
    // return null if can't be done of value can't be converted.

    public static Object constructFromString(Class cl, String value) {
        for (Constructor c : cl.getConstructors()) {
            final Class[] classes = c.getParameterTypes();
            if (classes.length == 1 && classes[0].equals(String.class)) {
                try {
                    return c.newInstance(value);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }

    // skip over comment and empty lines

    public static String nextNonCommentLine(BufferedReader reader) throws IOException {
        String line;
        do {
            line = reader.readLine();
            // ignore empty or comment lines
        } while (line != null && (line.trim().length() == 0 || line.trim().charAt(0) == '#'));
        return line;
    }

    public static Map<String, List<String[]>> importTraitsFromFile(File file, final String delimiter)
            throws IOException, Arguments.ArgumentException {
        final BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = nextNonCommentLine(reader);

        // define where is the trait keyword in the 1st row of file
        final int startAt = 1;

        final String[] traitNames = line.split(delimiter);
        for (int k = 0; k < traitNames.length; ++k) {
            traitNames[k] = traitNames[k].trim();
        }

        if (!(traitNames[0].equalsIgnoreCase(TRAITS) || traitNames[0].length() < 1))
            throw new Arguments.ArgumentException("Wrong file format:\ntrait key word should be declared in the 1st row");

        Map<String, List<String[]>> traits = new HashMap<String, List<String[]>>();
        for (int i = startAt; i < traitNames.length; i++) {
            traits.put(traitNames[i], new ArrayList<String[]>());
        }

        line = nextNonCommentLine(reader);
        while (line != null) {
            String[] values = line.split(delimiter);

            assert (values.length > 0);
            if (values.length != traitNames.length)
                throw new Arguments.ArgumentException("Wrong file format:\neach trait should have its corresponding value");

            try {
                if (traitNames[0].equalsIgnoreCase(TRAITS)) {
                    importStatesMoreThanTaxon(traits, values, traitNames, startAt);
                } else {
                    importSpecies(traits, values, traitNames, startAt);
                }
            } catch (Arguments.ArgumentException e) {
                e.printStackTrace();
            }

            line = nextNonCommentLine(reader);
        }
        return traits;
    }

    private static void importSpecies(Map<String, List<String[]>> traits, String[] values, String[] traitNames, int startAt)
            throws Arguments.ArgumentException {
        // first column is label for the redundant "taxa" name
        final String first = values[0].trim();
        int k = Arrays.asList(traitNames).indexOf(first);
        if (k >= 0) {
            List<String[]> trait = traits.get(first);
            if (trait == null) {
                throw new Arguments.ArgumentException("undefined trait " + first);
            }
            final String traitVal = values[1].trim();
            for (int i = 2; i < values.length; i++) {
                trait.add(new String[]{values[i], traitVal}); // {taxon_name, trait}
            }
        } else {
            for (int i = startAt; i < values.length; i++) {
                if (i < traitNames.length) {
                    List<String[]> column = traits.get(traitNames[i]);
                    column.add(new String[]{first, values[i].trim()});
                }
            }
        }
    }

    private static void importStatesMoreThanTaxon(Map<String, List<String[]>> traits, String[] values, String[] traitNames, int startAt)
            throws Arguments.ArgumentException {
        // first column is label taxon name

        if (traitNames.length < 2) {
            throw new Arguments.ArgumentException("Wrong file format:\ntrait key words in the 1st row are loaded improperly");
        } else if (traitNames.length - startAt < 1) {
            throw new Arguments.ArgumentException("startAt set improperly");
        }

        for (int i = 0; i < (traitNames.length - startAt); i++) {
            List<String[]> trait = traits.get(traitNames[i + startAt]);
            if (trait == null) throw new Arguments.ArgumentException("undefined trait " + traitNames[i + startAt]);

            trait.add(new String[]{values[0].trim(), values[i + startAt].trim()}); // {taxon_name, trait}
        }
    }

    public static String trimExtensions(String fileName, String[] extensions) {

        String newName = null;

        for (String extension : extensions) {
            final String ext = "." + extension;
            if (fileName.toUpperCase().endsWith(ext.toUpperCase())) {
                newName = fileName.substring(0, fileName.length() - ext.length());
            }
        }

        return (newName != null) ? newName : fileName;
    }

    public static Image getImage(Object caller, String name) {

        java.net.URL url = caller.getClass().getResource(name);
        if (url != null) {
            return Toolkit.getDefaultToolkit().createImage(url);
        } else {
            if (caller instanceof Component) {
                Component c = (Component) caller;
                Image i = c.createImage(100, 20);
                Graphics g = c.getGraphics();
                g.drawString("Not found!", 1, 15);
                return i;
            } else return null;
        }
    }

    public static File getCWD() {
        final String f = System.getProperty("user.dir");
        return new File(f);
    }

//    enum Platform {
//        WINDOWS,
//        MACOSX,
//        LINUX;
//
//        Platform detect() {
//
//            final String os = System.getProperty("os.name");
//
//            if( os.equals("Linux") ) {
//                return LINUX;
//            }
//            // todo probably wrong, please check on windows
//            if( os.equals("Windows") ) {
//                return WINDOWS;
//            }
//
//            if( System.getProperty("os.name").toLowerCase().startsWith("mac os x") ) {
//                return MACOSX;
//            }
//
//            return null;
//        }
//    }
}
