import GUI.LoginScreen;

import javax.swing.*;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            javax.swing.plaf.FontUIResource fontUIResource = new javax.swing.plaf.FontUIResource("Arial",
                    java.awt.Font.PLAIN, 12);
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, fontUIResource);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
}