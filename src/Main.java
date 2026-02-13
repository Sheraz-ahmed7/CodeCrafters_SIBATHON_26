import GUI.loginScreen;
import javax.swing.*;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set default font for better UI
<<<<<<< HEAD
            javax.swing.plaf.FontUIResource fontUIResource = 
                new javax.swing.plaf.FontUIResource("Arial", java.awt.Font.PLAIN, 12);
            Enumeration keys = UIManager.getDefaults().keys();
=======
            javax.swing.plaf.FontUIResource fontUIResource = new javax.swing.plaf.FontUIResource("Arial",
                    java.awt.Font.PLAIN, 12);
            java.util.Enumeration keys = UIManager.getDefaults().keys();
>>>>>>> b23a74205933b8b3110fdf867a6d20be7135d99d
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

        // Launch login screen on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new loginScreen().setVisible(true);
        });
    }
}