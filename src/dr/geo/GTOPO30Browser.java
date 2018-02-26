package dr.geo;
import dr.app.gui.ColorFunction;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.IOException;
public class GTOPO30Browser extends JFrame implements ChangeListener {
    GTOPO30Panel gtopo30panel;
    JScrollPane scrollPane;
    public GTOPO30Browser(String[] tilefiles) throws IOException {
        super("GTOPO30 Browser");
        ColorFunction function = new ColorFunction(
                new Color[]{Color.blue, Color.yellow, Color.green.darker(), Color.orange.darker(), Color.white, Color.pink},
                new float[]{-410, 0, 100, 1500, 4000, 8800});
        gtopo30panel = new GTOPO30Panel(tilefiles, function);
        scrollPane = new JScrollPane(gtopo30panel);
        getContentPane().add(BorderLayout.CENTER, scrollPane);
        JSlider scale = new JSlider(JSlider.HORIZONTAL, 1, 100, 10);
        scale.addChangeListener(this);
        getContentPane().add(BorderLayout.SOUTH, scale);
    }
    public static void main(String[] args) throws IOException {
        GTOPO30Browser browser = new GTOPO30Browser(args);
        browser.setSize(800, 800);
        browser.setVisible(true);
    }
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int scale = source.getValue();
            gtopo30panel.setScale((double) scale / 100.0f);
            scrollPane.repaint();
        }
    }
}
