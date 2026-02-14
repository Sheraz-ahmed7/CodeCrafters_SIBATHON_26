package GUI;

import controllers.LoginController;
import Models.User;
import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private LoginController loginController;

    public LoginScreen() {
        loginController = new LoginController();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Smart Energy Optimization System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("University Energy Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        // ✅ FORCED BUTTON COLORS - VISIBLE
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");

        // Login button - Dark Blue
        loginButton.setBackground(new Color(0, 120, 215));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 100), 1));
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Cancel button - Dark Red
        cancelButton.setBackground(new Color(200, 0, 0));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(100, 0, 0), 1));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        loginButton.addActionListener(e -> performLogin());
        cancelButton.addActionListener(e -> System.exit(0));
        passwordField.addActionListener(e -> performLogin());

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both username and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginButton.setEnabled(false);

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return loginController.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        dispose();
                        new Dashboard(user).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(LoginScreen.this,
                                "Invalid username or password",
                                "Login Failed",
                                JOptionPane.ERROR_MESSAGE);
                        passwordField.setText("");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(LoginScreen.this,
                            "Error during login: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    loginButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }
}