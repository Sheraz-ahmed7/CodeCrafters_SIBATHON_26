package GUI;

import Models.User;
import Models.Device;
import Models.Department;
import database.Queries;
import utils.EnergyCalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DeviceEntryPanel extends JPanel {
    private User currentUser;
    private JTable deviceTable;
    private DefaultTableModel tableModel;
    private JComboBox<Department> departmentCombo;
    private JTextField deviceNameField, wattageField, quantityField, hoursField;
    private JButton addButton, updateButton, deleteButton, calculateButton, refreshButton;
    private JLabel totalKWhLabel, totalCostLabel, carbonLabel, treesLabel;

    public DeviceEntryPanel(User user) {
        this.currentUser = user;
        initializeUI();
        loadDepartments();
        loadDevices();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createFormPanel(), BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        centerPanel.add(createSummaryPanel(), BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add New Device"));
        panel.setPreferredSize(new Dimension(300, 400));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Department:"), gbc);

        gbc.gridy = 1;
        departmentCombo = new JComboBox<>();
        if (!currentUser.isAdmin()) {
            departmentCombo.setEnabled(false);
        }
        panel.add(departmentCombo, gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Device Name:"), gbc);

        gbc.gridy = 3;
        deviceNameField = new JTextField(20);
        panel.add(deviceNameField, gbc);

        gbc.gridy = 4;
        panel.add(new JLabel("Wattage (Watts):"), gbc);

        gbc.gridy = 5;
        wattageField = new JTextField(20);
        panel.add(wattageField, gbc);

        gbc.gridy = 6;
        panel.add(new JLabel("Quantity:"), gbc);

        gbc.gridy = 7;
        quantityField = new JTextField(20);
        panel.add(quantityField, gbc);

        gbc.gridy = 8;
        panel.add(new JLabel("Hours per Day:"), gbc);

        gbc.gridy = 9;
        hoursField = new JTextField(20);
        panel.add(hoursField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        // ✅ PROFESSIONAL BUTTONS
        addButton = new JButton("Add Device");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        calculateButton = new JButton("Calculate");
        refreshButton = new JButton("Refresh");

        // Add - Professional Blue (#2196F3)
        styleButton(addButton, new Color(33, 150, 243));

        // Update - Professional Orange (#FF9800)
        styleButton(updateButton, new Color(255, 152, 0));

        // Delete - Professional Red (#F44336)
        styleButton(deleteButton, new Color(244, 67, 54));

        // Calculate - Professional Green (#4CAF50)
        styleButton(calculateButton, new Color(76, 175, 80));

        // Refresh - Professional Gray (#9E9E9E)
        styleButton(refreshButton, new Color(158, 158, 158));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(refreshButton);

        gbc.gridy = 10;
        panel.add(buttonPanel, gbc);

        addButton.addActionListener(e -> addDevice());
        updateButton.addActionListener(e -> updateDevice());
        deleteButton.addActionListener(e -> deleteDevice());
        calculateButton.addActionListener(e -> calculateEnergy());
        refreshButton.addActionListener(e -> {
            clearForm();
            loadDevices();
        });

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Device List"));

        String[] columns = { "ID", "Device Name", "Wattage", "Qty", "Hours/Day", "Daily kWh", "Monthly kWh" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        deviceTable = new JTable(tableModel);
        deviceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedDevice();
            }
        });

        JScrollPane scrollPane = new JScrollPane(deviceTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Energy Summary"));
        panel.setPreferredSize(new Dimension(800, 80));

        panel.add(new JLabel("Total Monthly kWh:", SwingConstants.RIGHT));
        totalKWhLabel = new JLabel("0.00 kWh", SwingConstants.LEFT);
        totalKWhLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalKWhLabel.setForeground(new Color(33, 150, 243)); // Professional Blue

        panel.add(new JLabel("Monthly Cost:", SwingConstants.RIGHT));
        totalCostLabel = new JLabel("Rs. 0.00", SwingConstants.LEFT);
        totalCostLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalCostLabel.setForeground(new Color(76, 175, 80)); // Professional Green

        panel.add(totalKWhLabel);
        panel.add(totalCostLabel);

        panel.add(new JLabel("Carbon Footprint:", SwingConstants.RIGHT));
        carbonLabel = new JLabel("0.00 kg CO2", SwingConstants.LEFT);
        carbonLabel.setFont(new Font("Arial", Font.BOLD, 14));
        carbonLabel.setForeground(new Color(244, 67, 54)); // Professional Red

        panel.add(new JLabel("Trees Needed:", SwingConstants.RIGHT));
        treesLabel = new JLabel("0 trees", SwingConstants.LEFT);
        treesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        treesLabel.setForeground(new Color(255, 152, 0)); // Professional Orange

        panel.add(carbonLabel);
        panel.add(treesLabel);

        return panel;
    }

    // ✅ Helper method for button styling
    private void styleButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadDepartments() {
        List<Department> departments = Queries.getAllDepartments();
        departmentCombo.removeAllItems();

        for (Department dept : departments) {
            departmentCombo.addItem(dept);
        }

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

    private void loadDevices() {
        Department selectedDept = (Department) departmentCombo.getSelectedItem();
        if (selectedDept == null)
            return;

        List<Device> devices = Queries.getDevicesByDepartment(selectedDept.getDeptId());

        tableModel.setRowCount(0);
        double totalMonthlyKWh = 0;

        for (Device device : devices) {
            double dailyKWh = device.calculateDailyKWh();
            double monthlyKWh = device.calculateMonthlyKWh();
            totalMonthlyKWh += monthlyKWh;

            Object[] row = {
                    device.getDeviceId(),
                    device.getDeviceName(),
                    device.getWattage(),
                    device.getQuantity(),
                    device.getHoursPerDay(),
                    String.format("%.2f", dailyKWh),
                    String.format("%.2f", monthlyKWh)
            };
            tableModel.addRow(row);
        }

        updateSummary(totalMonthlyKWh);
    }

    private void updateSummary(double totalMonthlyKWh) {
        double cost = EnergyCalculator.calculateMonthlyCost(totalMonthlyKWh);
        double carbon = EnergyCalculator.calculateCarbonFootprint(totalMonthlyKWh);
        int trees = EnergyCalculator.calculateTreesNeeded(carbon);

        totalKWhLabel.setText(String.format("%.2f kWh", totalMonthlyKWh));
        totalCostLabel.setText(String.format("Rs. %.2f", cost));
        carbonLabel.setText(String.format("%.2f kg CO2", carbon));
        treesLabel.setText(trees + " trees");
    }

    private void loadSelectedDevice() {
        int selectedRow = deviceTable.getSelectedRow();
        if (selectedRow >= 0) {
            deviceNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            wattageField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            quantityField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            hoursField.setText(tableModel.getValueAt(selectedRow, 4).toString());

            addButton.setEnabled(false);
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }
    }

    private void clearForm() {
        deviceNameField.setText("");
        wattageField.setText("");
        quantityField.setText("");
        hoursField.setText("");

        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void addDevice() {
        if (!validateForm())
            return;

        try {
            Department selectedDept = (Department) departmentCombo.getSelectedItem();
            if (selectedDept == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a department",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Device device = new Device();
            device.setDeptId(selectedDept.getDeptId());
            device.setDeviceName(deviceNameField.getText().trim());
            device.setWattage(Integer.parseInt(wattageField.getText().trim()));
            device.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            device.setHoursPerDay(Double.parseDouble(hoursField.getText().trim()));

            boolean success = Queries.addDevice(device);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Device added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                clearForm();
                loadDevices();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to add device",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDevice() {
        int selectedRow = deviceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a device to update",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm())
            return;

        try {
            int deviceId = (int) tableModel.getValueAt(selectedRow, 0);
            Department selectedDept = (Department) departmentCombo.getSelectedItem();

            Device device = new Device();
            device.setDeviceId(deviceId);
            device.setDeptId(selectedDept.getDeptId());
            device.setDeviceName(deviceNameField.getText().trim());
            device.setWattage(Integer.parseInt(wattageField.getText().trim()));
            device.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            device.setHoursPerDay(Double.parseDouble(hoursField.getText().trim()));

            boolean success = Queries.updateDevice(device);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Device updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDevices();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating device: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDevice() {
        int selectedRow = deviceTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a device to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this device?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int deviceId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean success = Queries.deleteDevice(deviceId);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Device deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDevices();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete device",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void calculateEnergy() {
        Department selectedDept = (Department) departmentCombo.getSelectedItem();
        if (selectedDept == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a department",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        loadDevices();
    }

    private boolean validateForm() {
        if (deviceNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Device name is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            deviceNameField.requestFocus();
            return false;
        }

        try {
            int wattage = Integer.parseInt(wattageField.getText().trim());
            if (wattage <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid wattage",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            wattageField.requestFocus();
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (quantity <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid quantity",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            quantityField.requestFocus();
            return false;
        }

        try {
            double hours = Double.parseDouble(hoursField.getText().trim());
            if (hours < 0 || hours > 24)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid hours (0-24)",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            hoursField.requestFocus();
            return false;
        }

        return true;
    }
}