package GUI;

import Models.Department; // Changed from models to Models
import Models.Device; // Changed from models to Models
import Models.User; // Changed from models to Models
import database.Queries;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import utils.EnergyCalculator;

public class ReportsPanel extends JPanel {
    private User currentUser;
    private JComboBox<String> reportTypeCombo;
    private JComboBox<Department> departmentCombo;
    private JButton generateButton;
    private JButton exportButton;
    private JTextArea reportArea;
    private JTable summaryTable;
    private DefaultTableModel tableModel;
    private JLabel generatedDateLabel;

    public ReportsPanel(User user) {
        this.currentUser = user;
        initializeUI();
        loadDepartments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Report Controls
        add(createControlPanel(), BorderLayout.NORTH);

        // Center panel - Report Display
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.4);

        // Summary Table
        splitPane.setTopComponent(createTablePanel());

        // Detailed Report
        splitPane.setBottomComponent(createReportPanel());

        add(splitPane, BorderLayout.CENTER);

        // Bottom panel - Export
        add(createExportPanel(), BorderLayout.SOUTH);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Report Controls"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Report Type
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Report Type:"), gbc);

        gbc.gridx = 1;
        String[] reportTypes = {
                "Department Summary",
                "Energy Consumption Details",
                "Carbon Footprint Report",
                "Cost Analysis",
                "Efficiency Ranking"
        };
        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setPreferredSize(new Dimension(200, 30));
        panel.add(reportTypeCombo, gbc);

        // Department (for non-summary reports)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Department:"), gbc);

        gbc.gridx = 1;
        departmentCombo = new JComboBox<>();
        departmentCombo.setPreferredSize(new Dimension(200, 30));
        panel.add(departmentCombo, gbc);

        // Generate Button
        gbc.gridx = 2;
        generateButton = new JButton("Generate Report");
        generateButton.setBackground(new Color(0, 102, 204));
        generateButton.setForeground(Color.BLACK);
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateReport());
        panel.add(generateButton, gbc);

        // Generated Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        generatedDateLabel = new JLabel("Last generated: Not yet");
        generatedDateLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        panel.add(generatedDateLabel, gbc);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Summary View"));

        String[] columns = { "Department", "Monthly kWh", "Monthly Cost", "Carbon (kg)", "Efficiency Score" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        summaryTable = new JTable(tableModel);
        summaryTable.setRowHeight(25);
        summaryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(summaryTable);
        scrollPane.setPreferredSize(new Dimension(800, 150));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Detailed Report"));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createExportPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        exportButton = new JButton("Export Report");
        exportButton.setBackground(new Color(0, 153, 76));
        exportButton.setForeground(Color.BLACK);
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportReport());

        panel.add(exportButton);

        return panel;
    }

    private void loadDepartments() {
        List<Department> departments = Queries.getAllDepartments();
        departmentCombo.removeAllItems();
        departmentCombo.addItem(new Department(0, "All Departments", 0, ""));

        for (Department dept : departments) {
            departmentCombo.addItem(dept);
        }

        // For non-admin, only show their department
        if (!currentUser.isAdmin() && currentUser.getDepartmentId() != null) {
            departmentCombo.removeAllItems();
            for (Department dept : departments) {
                if (dept.getDeptId() == currentUser.getDepartmentId()) {
                    departmentCombo.addItem(dept);
                    break;
                }
            }
            departmentCombo.setEnabled(false);
        }
    }

    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        Department selectedDept = (Department) departmentCombo.getSelectedItem();

        // Load summary table first
        loadSummaryTable();

        // Generate detailed report based on type
        String report = "";
        switch (reportType) {
            case "Department Summary":
                report = generateDepartmentSummary(selectedDept);
                break;
            case "Energy Consumption Details":
                report = generateEnergyDetails(selectedDept);
                break;
            case "Carbon Footprint Report":
                report = generateCarbonReport(selectedDept);
                break;
            case "Cost Analysis":
                report = generateCostAnalysis(selectedDept);
                break;
            case "Efficiency Ranking":
                report = generateEfficiencyRanking();  // ✅ CORRECT
                break;
        }

        reportArea.setText(report);
        reportArea.setCaretPosition(0);

        // Update timestamp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        generatedDateLabel.setText("Last generated: " + dtf.format(LocalDateTime.now()));
    }

    private void loadSummaryTable() {
        tableModel.setRowCount(0);
        List<Department> departments = Queries.getAllDepartments();

        for (Department dept : departments) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            double monthlyCost = EnergyCalculator.calculateMonthlyCost(monthlyKWh);
            double carbon = EnergyCalculator.calculateCarbonFootprint(monthlyKWh);
            int efficiencyScore = calculateEfficiencyScore(monthlyKWh, dept);

            Object[] row = {
                    dept.getDeptName(),
                    String.format("%.2f", monthlyKWh),
                    String.format("Rs. %.2f", monthlyCost),
                    String.format("%.2f", carbon),
                    efficiencyScore + "/100"
            };
            tableModel.addRow(row);
        }
    }

    private int calculateEfficiencyScore(double monthlyKWh, Department dept) {
        // Simple efficiency calculation based on consumption
        // Lower consumption = higher score
        if (monthlyKWh < 1000)
            return 90 + (int) (Math.random() * 10);
        if (monthlyKWh < 2000)
            return 75 + (int) (Math.random() * 15);
        if (monthlyKWh < 3000)
            return 60 + (int) (Math.random() * 15);
        if (monthlyKWh < 4000)
            return 45 + (int) (Math.random() * 15);
        return 30 + (int) (Math.random() * 15);
    }

    private String generateDepartmentSummary(Department dept) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("DEPARTMENT ENERGY SUMMARY REPORT\n");
        sb.append("=".repeat(80)).append("\n\n");

        if (dept != null && dept.getDeptId() != 0) {
            appendDepartmentDetails(sb, dept);
        } else {
            List<Department> departments = Queries.getAllDepartments();
            for (Department d : departments) {
                appendDepartmentDetails(sb, d);
                sb.append("-".repeat(60)).append("\n");
            }
        }

        return sb.toString();
    }

    private void appendDepartmentDetails(StringBuilder sb, Department dept) {
        double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
        double cost = EnergyCalculator.calculateMonthlyCost(monthlyKWh);
        double carbon = EnergyCalculator.calculateCarbonFootprint(monthlyKWh);
        int trees = EnergyCalculator.calculateTreesNeeded(carbon);

        sb.append(String.format("Department: %s\n", dept.getDeptName()));
        sb.append(String.format("Floor: %d\n", dept.getFloorNumber()));
        sb.append(String.format("Contact: %s\n",
                dept.getContactNumber() != null ? dept.getContactNumber() : "N/A"));
        sb.append(String.format("Monthly Consumption: %.2f kWh\n", monthlyKWh));
        sb.append(String.format("Monthly Cost: Rs. %.2f\n", cost));
        sb.append(String.format("Carbon Footprint: %.2f kg CO2\n", carbon));
        sb.append(String.format("Trees needed for offset: %d\n", trees));
    }

    private String generateEnergyDetails(Department dept) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("ENERGY CONSUMPTION DETAILS\n");
        sb.append("=".repeat(80)).append("\n\n");

        List<Department> depts;
        if (dept != null && dept.getDeptId() != 0) {
            depts = List.of(dept);
        } else {
            depts = Queries.getAllDepartments();
        }

        for (Department d : depts) {
            sb.append(String.format("\n%s Department:\n", d.getDeptName()));
            sb.append("-".repeat(40)).append("\n");

            List<Device> devices = Queries.getDevicesByDepartment(d.getDeptId());
            if (devices.isEmpty()) {
                sb.append("No devices registered.\n");
            } else {
                sb.append(String.format("%-20s %8s %8s %10s %12s\n",
                        "Device", "Wattage", "Qty", "Hours/Day", "Monthly kWh"));
                for (Device device : devices) {
                    sb.append(String.format("%-20s %8d %8d %10.1f %12.2f\n",
                            device.getDeviceName(),
                            device.getWattage(),
                            device.getQuantity(),
                            device.getHoursPerDay(),
                            device.calculateMonthlyKWh()));
                }
            }
        }

        return sb.toString();
    }

    private String generateCarbonReport(Department dept) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("CARBON FOOTPRINT REPORT\n");
        sb.append("=".repeat(80)).append("\n\n");

        sb.append("CO2 Emission Factor: 0.5 kg CO2 per kWh\n");
        sb.append("Tree Offset: 1 tree absorbs ~25 kg CO2 per year\n\n");

        List<Department> depts;
        if (dept != null && dept.getDeptId() != 0) {
            depts = List.of(dept);
        } else {
            depts = Queries.getAllDepartments();
        }

        double totalCarbon = 0;
        int totalTrees = 0;

        for (Department d : depts) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(d.getDeptId());
            double carbon = EnergyCalculator.calculateCarbonFootprint(monthlyKWh);
            int trees = EnergyCalculator.calculateTreesNeeded(carbon);

            sb.append(String.format("%s Department:\n", d.getDeptName()));
            sb.append(String.format("  Monthly CO2: %.2f kg\n", carbon));
            sb.append(String.format("  Annual CO2: %.2f kg\n", carbon * 12));
            sb.append(String.format("  Trees needed: %d\n\n", trees));

            totalCarbon += carbon;
            totalTrees += trees;
        }

        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("TOTAL MONTHLY CO2: %.2f kg\n", totalCarbon));
        sb.append(String.format("TOTAL ANNUAL CO2: %.2f kg\n", totalCarbon * 12));
        sb.append(String.format("TOTAL TREES NEEDED: %d\n", totalTrees));

        return sb.toString();
    }

    private String generateCostAnalysis(Department dept) {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("COST ANALYSIS REPORT\n");
        sb.append("=".repeat(80)).append("\n\n");

        sb.append("Cost per unit: Rs. 25.00/kWh\n\n");

        List<Department> depts;
        if (dept != null && dept.getDeptId() != 0) {
            depts = List.of(dept);
        } else {
            depts = Queries.getAllDepartments();
        }

        double totalCost = 0;

        for (Department d : depts) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(d.getDeptId());
            double cost = EnergyCalculator.calculateMonthlyCost(monthlyKWh);

            sb.append(String.format("%s Department:\n", d.getDeptName()));
            sb.append(String.format("  Monthly Cost: Rs. %.2f\n", cost));
            sb.append(String.format("  Annual Cost: Rs. %.2f\n\n", cost * 12));

            totalCost += cost;
        }

        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("TOTAL MONTHLY COST: Rs. %.2f\n", totalCost));
        sb.append(String.format("TOTAL ANNUAL COST: Rs. %.2f\n", totalCost * 12));

        return sb.toString();
    }

    private String generateEfficiencyRanking() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(80)).append("\n");
        sb.append("DEPARTMENT EFFICIENCY RANKING\n");
        sb.append("=".repeat(80)).append("\n\n");

        List<Department> departments = Queries.getAllDepartments();

        // Create array of departments with their scores
        Object[][] deptScores = new Object[departments.size()][2];
        for (int i = 0; i < departments.size(); i++) {
            Department d = departments.get(i);
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(d.getDeptId());
            int score = calculateEfficiencyScore(monthlyKWh, d);
            deptScores[i][0] = d;
            deptScores[i][1] = score;
        }

        // Sort by score (descending)
        java.util.Arrays.sort(deptScores, (a, b) -> (int) b[1] - (int) a[1]);

        sb.append(String.format("%-4s %-25s %15s %12s\n", "Rank", "Department", "Score", "Rating"));
        sb.append("-".repeat(60)).append("\n");

        for (int i = 0; i < deptScores.length; i++) {
            Department d = (Department) deptScores[i][0];
            int score = (int) deptScores[i][1];
            String rating = getRating(score);

            sb.append(String.format("%-4d %-25s %15d %12s\n",
                    i + 1, d.getDeptName(), score, rating));
        }

        return sb.toString();
    }

    private String getRating(int score) {
        if (score >= 90)
            return "Excellent";
        if (score >= 75)
            return "Good";
        if (score >= 60)
            return "Average";
        if (score >= 45)
            return "Below Average";
        return "Poor";
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new java.io.File("Energy_Report.txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.FileWriter writer = new java.io.FileWriter(file);
                writer.write(reportArea.getText());
                writer.close();

                JOptionPane.showMessageDialog(this,
                        "Report exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error exporting report: " + e.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}