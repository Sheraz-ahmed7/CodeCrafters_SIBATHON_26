package gui;

import controllers.LoginController;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        setLocationRelativeTo(null); // Center on screen
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("University Energy Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        
        // Style buttons
        loginButton.setBackground(new Color(0, 153, 76));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        cancelButton.setBackground(new Color(204, 0, 0));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Enter key press for password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Add all panels to main panel
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
        
        // Disable login button during validation
        loginButton.setEnabled(false);
        
        // Perform login in background thread
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
                        // Login successful
                        dispose(); // Close login screen
                        new Dashboard(user).setVisible(true);
                    } else {
                        // Login failed
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