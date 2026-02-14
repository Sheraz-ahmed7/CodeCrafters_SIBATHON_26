package GUI;

import Models.User;
import Models.Department;
import database.Queries;
import utils.EnergyCalculator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Dashboard extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private JLabel welcomeLabel;
    private JLabel energySummaryLabel;

    private DepartmentPanel departmentPanel;
    private DeviceEntryPanel deviceEntryPanel;
    private SolarSimulationPanel solarPanel;
    private ReportsPanel reportsPanel;
    private ChartsPanel chartsPanel;

    public Dashboard(User user) {
        this.currentUser = user;
        initializeUI();
        loadDashboardData();
    }

    private void initializeUI() {
        setTitle("Smart Energy Optimization System - Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());
        setLayout(new BorderLayout());

        add(createTopPanel(), BorderLayout.NORTH);

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        departmentPanel = new DepartmentPanel(currentUser);
        deviceEntryPanel = new DeviceEntryPanel(currentUser);
        solarPanel = new SolarSimulationPanel(currentUser);
        reportsPanel = new ReportsPanel(currentUser);
        chartsPanel = new ChartsPanel(currentUser);

        tabbedPane.addTab("Dashboard Home", createHomePanel());
        tabbedPane.addTab("Department Management", departmentPanel);
        tabbedPane.addTab("Device Entry", deviceEntryPanel);
        tabbedPane.addTab("Solar Simulation", solarPanel);
        tabbedPane.addTab("Reports", reportsPanel);
        tabbedPane.addTab("Charts & Analytics", chartsPanel);

        add(tabbedPane, BorderLayout.CENTER);
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem exitItem = new JMenuItem("Exit");

        logoutItem.addActionListener(e -> logout());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu toolsMenu = new JMenu("Tools");
        JMenuItem calculateItem = new JMenuItem("Energy Calculator");
        JMenuItem exportItem = new JMenuItem("Export Report");

        calculateItem.addActionListener(e -> showEnergyCalculator());
        exportItem.addActionListener(e -> exportReport());

        toolsMenu.add(calculateItem);
        toolsMenu.add(exportItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(51, 51, 51));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String role = currentUser.isAdmin() ? "Administrator" : "Department User";
        welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (" + role + ")");
        welcomeLabel.setForeground(Color.BLACK);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutBtn = new JButton("Logout");
        // Logout button - Dark Red
        logoutBtn.setBackground(new Color(200, 0, 0));
        logoutBtn.setForeground(Color.BLACK);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 0, 0), 1));
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createHomePanel() {
        JPanel homePanel = new JPanel(new GridBagLayout());
        homePanel.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        JPanel welcomeCard = createInfoCard(
                "Welcome to Smart Energy System",
                "Monitor, analyze and optimize energy consumption",
                new Color(0, 102, 204));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        homePanel.add(welcomeCard, gbc);

        JPanel statsCard = createStatsCard();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.35;
        homePanel.add(statsCard, gbc);

        JPanel actionsCard = createActionsCard();
        gbc.gridx = 1;
        gbc.gridy = 1;
        homePanel.add(actionsCard, gbc);

        JPanel tipsCard = createTipsCard();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.35;
        homePanel.add(tipsCard, gbc);

        return homePanel;
    }

    private JPanel createInfoCard(String title, String message, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatsCard() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Quick Statistics"));

        energySummaryLabel = new JLabel("Loading...");
        energySummaryLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(new JLabel("Current Month:"));
        panel.add(energySummaryLabel);
        panel.add(new JLabel("Last Updated: Just now"));

        return panel;
    }

    private JPanel createActionsCard() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Quick Actions"));

        // ✅ FORCED BUTTON COLORS - VISIBLE
        JButton addDeviceBtn = new JButton("+ Add New Device");
        JButton viewReportBtn = new JButton("📊 View Reports");
        JButton calculateSolarBtn = new JButton("☀️ Solar Calculator");

        // Add Device - Dark Blue
        styleForcedButton(addDeviceBtn, new Color(0, 120, 215));

        // View Reports - Dark Green
        styleForcedButton(viewReportBtn, new Color(0, 100, 0));

        // Solar Calculator - Dark Orange
        styleForcedButton(calculateSolarBtn, new Color(200, 100, 0));

        addDeviceBtn.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        viewReportBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        calculateSolarBtn.addActionListener(e -> tabbedPane.setSelectedIndex(3));

        panel.add(addDeviceBtn);
        panel.add(viewReportBtn);
        panel.add(calculateSolarBtn);

        return panel;
    }

    // Helper method for forced button styling
    private void styleForcedButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(bgColor.darker(), 1));
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private JPanel createTipsCard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Energy Saving Tips"));

        String[] tips = {
                "• Turn off lights when leaving the room",
                "• Use energy-efficient LED bulbs",
                "• Set AC temperature to 24°C for optimal efficiency",
                "• Unplug chargers when not in use",
                "• Use natural light during daytime"
        };

        JTextArea tipsArea = new JTextArea();
        tipsArea.setEditable(false);
        tipsArea.setBackground(panel.getBackground());
        for (String tip : tips) {
            tipsArea.append(tip + "\n");
        }

        panel.add(new JScrollPane(tipsArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel("Connected to database | System ready");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        statusBar.add(statusLabel);

        return statusBar;
    }

    private void loadDashboardData() {
        try {
            int deptId = currentUser.isAdmin() ? 1 : currentUser.getDepartmentId();
            double totalKWh = Queries.getTotalMonthlyKWhByDepartment(deptId);
            double cost = EnergyCalculator.calculateMonthlyCost(totalKWh);

            energySummaryLabel.setText(String.format("%.2f kWh (Rs. %.2f)", totalKWh, cost));
        } catch (Exception e) {
            energySummaryLabel.setText("Error loading data");
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginScreen().setVisible(true);
        }
    }

    private void showEnergyCalculator() {
        JOptionPane.showMessageDialog(this,
                "Energy Calculator Tool - Coming Soon!",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        JOptionPane.showMessageDialog(this,
                "Export functionality will be available in next version",
                "Info",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String message = "Smart Energy Optimization System for Universities\n" +
                "Version 1.0\n\n" +
                "A desktop application for monitoring and optimizing\n" +
                "energy consumption in university departments.\n\n" +
                "© 2024 Hackathon Project";

        JOptionPane.showMessageDialog(this,
                message,
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }
}