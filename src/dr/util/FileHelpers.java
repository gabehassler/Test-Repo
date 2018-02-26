package dr.util;
import java.io.*;
public class FileHelpers {
    public static final String FILE_NAME = "fileName";
    public static int numberOfLines(File file) throws IOException {
        RandomAccessFile randFile = new RandomAccessFile(file, "r");
        long lastRec = randFile.length();
        randFile.close();
        FileReader fileRead = new FileReader(file);
        LineNumberReader lineRead = new LineNumberReader(fileRead);
        lineRead.skip(lastRec);
        int count = lineRead.getLineNumber() - 1;
        fileRead.close();
        lineRead.close();
        return count;
    }
    public static File getFile(String fileName, String prefix) {
        final boolean localFile = fileName.startsWith("./");
        final boolean relative = masterDirectory != null && localFile;
        if (localFile) {
            fileName = fileName.substring(2);
        }
        if (prefix != null) {
            fileName = prefix + fileName;
        }
        final File file = new File(fileName);
        final String name = file.getName();
        String parent = file.getParent();
        if (!file.isAbsolute()) {
            String p;
            if (relative) {
                p = masterDirectory.getAbsolutePath();
            } else {
                p = System.getProperty("user.dir");
            }
            if (parent != null && parent.length() > 0) {
                parent = p + '/' + parent;
            } else {
                parent = p;
            }
        }
        return new File(parent, name);
    }
    public static File getFile(String fileName) {
        return getFile(fileName, null);
    }
    // directory where beast xml file resides
    private static File masterDirectory = null;
    public static void setMasterDir(File fileName) {
        masterDirectory = fileName;
    }
}
