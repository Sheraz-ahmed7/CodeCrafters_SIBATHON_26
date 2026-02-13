import gui.LoginScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default font for better UI
            javax.swing.plaf.FontUIResource fontUIResource = 
                new javax.swing.plaf.FontUIResource("Arial", java.awt.Font.PLAIN, 12);
            java.util.Enumeration keys = UIManager.getDefaults().keys();
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
            new LoginScreen().setVisible(true);
        });
    }
}