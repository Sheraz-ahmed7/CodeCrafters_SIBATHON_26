package GUI;

import Models.User; // Changed from models to Models
import Models.Department; // Changed from models to Models
import database.Queries;
import utils.EnergyCalculator;
import utils.SolarSimulator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SolarSimulationPanel extends JPanel {
    private User currentUser;
    private JComboBox<Department> departmentCombo;
    private JTextField monthlyConsumptionField;
    private JButton calculateButton;
    private JLabel panelsLabel, costLabel, generationLabel, savingsLabel, paybackLabel;

    public SolarSimulationPanel(User user) {
        this.currentUser = user;
        initializeUI();
        loadDepartments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel - Input Section
        add(createInputPanel(), BorderLayout.NORTH);

        // Center panel - Results Section
        add(createResultsPanel(), BorderLayout.CENTER);

        // Bottom panel - Info Section
        add(createInfoPanel(), BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
                "Solar Panel Simulation"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Department Selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Department:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        departmentCombo = new JComboBox<>();
        departmentCombo.setPreferredSize(new Dimension(250, 30));
        departmentCombo.addActionListener(e -> loadDepartmentConsumption());
        panel.add(departmentCombo, gbc);

        // Monthly Consumption
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Monthly Consumption (kWh):"), gbc);

        gbc.gridx = 1;
        monthlyConsumptionField = new JTextField(15);
        monthlyConsumptionField.setFont(new Font("Arial", Font.PLAIN, 14));
        monthlyConsumptionField.setEditable(false);
        panel.add(monthlyConsumptionField, gbc);

        // Calculate Button
        gbc.gridx = 2;
        calculateButton = new JButton("Calculate Solar ROI");
        calculateButton.setBackground(new Color(255, 153, 0));
        calculateButton.setForeground(Color.BLACK);
        calculateButton.setFont(new Font("Arial", Font.BOLD, 14));
        calculateButton.setFocusPainted(false);
        calculateButton.addActionListener(e -> calculateSolar());
        panel.add(calculateButton, gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JLabel noteLabel = new JLabel("Note: Calculations based on 400W panels, 5 peak sun hours, Rs. 25/unit");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        noteLabel.setForeground(Color.GRAY);
        panel.add(noteLabel, gbc);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Results"));

        // Results grid
        JPanel resultsGrid = new JPanel(new GridLayout(5, 2, 10, 10));
        resultsGrid.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Labels
        resultsGrid.add(new JLabel("Solar Panels Needed:", SwingConstants.RIGHT));
        panelsLabel = new JLabel("0", SwingConstants.LEFT);
        panelsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panelsLabel.setForeground(new Color(0, 102, 204));

        resultsGrid.add(new JLabel("Installation Cost:", SwingConstants.RIGHT));
        costLabel = new JLabel("Rs. 0", SwingConstants.LEFT);
        costLabel.setFont(new Font("Arial", Font.BOLD, 16));
        costLabel.setForeground(new Color(204, 0, 0));

        resultsGrid.add(new JLabel("Monthly Generation:", SwingConstants.RIGHT));
        generationLabel = new JLabel("0 kWh", SwingConstants.LEFT);
        generationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        generationLabel.setForeground(new Color(0, 153, 76));

        resultsGrid.add(new JLabel("Annual Savings:", SwingConstants.RIGHT));
        savingsLabel = new JLabel("Rs. 0", SwingConstants.LEFT);
        savingsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        savingsLabel.setForeground(new Color(0, 153, 76));

        resultsGrid.add(new JLabel("Payback Period:", SwingConstants.RIGHT));
        paybackLabel = new JLabel("0 years", SwingConstants.LEFT);
        paybackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        paybackLabel.setForeground(new Color(255, 153, 0));

        resultsGrid.add(panelsLabel);
        resultsGrid.add(costLabel);
        resultsGrid.add(generationLabel);
        resultsGrid.add(savingsLabel);
        resultsGrid.add(paybackLabel);

        panel.add(resultsGrid, BorderLayout.CENTER);

        // Recommendation panel
        JPanel recommendationPanel = new JPanel();
        recommendationPanel.setBackground(new Color(240, 248, 255));
        recommendationPanel.setBorder(BorderFactory.createEtchedBorder());

        JLabel recommendationLabel = new JLabel("Enter consumption data to see solar recommendations");
        recommendationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        recommendationPanel.add(recommendationLabel);

        panel.add(recommendationPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Solar Energy Facts"));

        String[] facts = {
                "<html><b>Peak Sun Hours:</b> Pakistan receives 5-7 hours of peak sunlight daily</html>",
                "<html><b>ROI:</b> Solar panels typically pay for themselves in 3-7 years</html>",
                "<html><b>Lifespan:</b> Modern solar panels last 25-30 years with minimal maintenance</html>"
        };

        for (String fact : facts) {
            JLabel label = new JLabel(fact);
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            panel.add(label);
        }

        return panel;
    }

    private void loadDepartments() {
        List<Department> departments = Queries.getAllDepartments();
        departmentCombo.removeAllItems();

        for (Department dept : departments) {
            departmentCombo.addItem(dept);
        }

        // Auto-select department for non-admin users
        if (!currentUser.isAdmin() && currentUser.getDepartmentId() != null) {
            for (Department dept : departments) {
                if (dept.getDeptId() == currentUser.getDepartmentId()) {
                    departmentCombo.setSelectedItem(dept);
                    break;
                }
            }
            departmentCombo.setEnabled(false);
        }
    }

    private void loadDepartmentConsumption() {
        Department selected = (Department) departmentCombo.getSelectedItem();
        if (selected != null) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(selected.getDeptId());
            monthlyConsumptionField.setText(String.format("%.2f", monthlyKWh));
        }
    }

    private void calculateSolar() {
        try {
            double monthlyKWh;

            if (monthlyConsumptionField.getText().trim().isEmpty()) {
                monthlyKWh = 0;
            } else {
                monthlyKWh = Double.parseDouble(monthlyConsumptionField.getText().trim());
            }

            if (monthlyKWh <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid monthly consumption",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            SolarSimulator.SolarResult result = SolarSimulator.simulate(monthlyKWh);

            // Update labels
            panelsLabel.setText(String.valueOf(result.panelsNeeded));
            costLabel.setText(String.format("Rs. %.2f", result.installationCost));
            generationLabel.setText(String.format("%.2f kWh", result.monthlyGeneration));
            savingsLabel.setText(String.format("Rs. %.2f", result.annualSavings));
            paybackLabel.setText(String.format("%.1f years", result.paybackYears));

            // Update recommendation
            String recommendation = getRecommendation(result);
            JPanel recommendationPanel = (JPanel) ((JPanel) getComponent(1)).getComponent(1);
            recommendationPanel.removeAll();
            JLabel recLabel = new JLabel(recommendation);
            recLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            recommendationPanel.add(recLabel);
            recommendationPanel.revalidate();
            recommendationPanel.repaint();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Invalid consumption value",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getRecommendation(SolarSimulator.SolarResult result) {
        if (result.paybackYears < 3) {
            return "✅ Excellent ROI! Strongly recommended to install solar panels.";
        } else if (result.paybackYears < 5) {
            return "👍 Good ROI. Consider installing solar panels.";
        } else if (result.paybackYears < 7) {
            return "🤔 Moderate ROI. Evaluate based on long-term goals.";
        } else {
            return "⚠️ Long payback period. Consider energy efficiency first, then solar.";
        }
    }
}