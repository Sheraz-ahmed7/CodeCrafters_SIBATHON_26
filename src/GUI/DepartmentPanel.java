package GUI;

import Models.User;
import Models.Department;
import database.Queries;
import utils.EnergyCalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DepartmentPanel extends JPanel {
    private User currentUser;
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, floorField, contactField;
    private JButton addButton, updateButton, deleteButton, refreshButton;
    private JComboBox<Department> departmentCombo;

    public DepartmentPanel(User user) {
        this.currentUser = user;
        initializeUI();
        loadDepartments();

        if (!currentUser.isAdmin()) {
            setEnabled(false);
            nameField.setEnabled(false);
            floorField.setEnabled(false);
            contactField.setEnabled(false);
            addButton.setEnabled(false);
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);

        splitPane.setTopComponent(createFormPanel());
        splitPane.setBottomComponent(createTablePanel());

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Department Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Department:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        departmentCombo = new JComboBox<>();
        departmentCombo.addActionListener(e -> loadSelectedDepartment());
        panel.add(departmentCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Department Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Floor Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        floorField = new JTextField(20);
        panel.add(floorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Contact Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        contactField = new JTextField(20);
        panel.add(contactField, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        // ✅ PROFESSIONAL BUTTONS
        addButton = new JButton("Add Department");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");

        // Add - Professional Blue (#2196F3)
        styleButton(addButton, new Color(33, 150, 243));

        // Update - Professional Orange (#FF9800)
        styleButton(updateButton, new Color(255, 152, 0));

        // Delete - Professional Red (#F44336)
        styleButton(deleteButton, new Color(244, 67, 54));

        // Refresh - Professional Gray (#9E9E9E)
        styleButton(refreshButton, new Color(158, 158, 158));

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        refreshButton.addActionListener(e -> {
            loadDepartments();
            clearForm();
        });

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Department List"));

        String[] columns = { "ID", "Department Name", "Floor", "Contact", "Monthly kWh", "Monthly Cost" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        departmentTable = new JTable(tableModel);
        departmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        departmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedDepartmentFromTable();
            }
        });

        JScrollPane scrollPane = new JScrollPane(departmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        try {
            List<Department> departments = Queries.getAllDepartments();

            departmentCombo.removeAllItems();
            for (Department dept : departments) {
                departmentCombo.addItem(dept);
            }

            tableModel.setRowCount(0);
            for (Department dept : departments) {
                double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
                double monthlyCost = EnergyCalculator.calculateMonthlyCost(monthlyKWh);

                Object[] row = {
                        dept.getDeptId(),
                        dept.getDeptName(),
                        dept.getFloorNumber(),
                        dept.getContactNumber() != null ? dept.getContactNumber() : "N/A",
                        String.format("%.2f", monthlyKWh),
                        String.format("Rs. %.2f", monthlyCost)
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading departments: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedDepartment() {
        Department selected = (Department) departmentCombo.getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getDeptName());
            floorField.setText(String.valueOf(selected.getFloorNumber()));
            contactField.setText(selected.getContactNumber());
        }
    }

    private void loadSelectedDepartmentFromTable() {
        int selectedRow = departmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int deptId = (int) tableModel.getValueAt(selectedRow, 0);
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                Department dept = departmentCombo.getItemAt(i);
                if (dept.getDeptId() == deptId) {
                    departmentCombo.setSelectedItem(dept);
                    break;
                }
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        floorField.setText("");
        contactField.setText("");
        departmentCombo.setSelectedIndex(-1);
    }

    private void addDepartment() {
        if (!validateForm())
            return;

        try {
            Department dept = new Department();
            dept.setDeptName(nameField.getText().trim());
            dept.setFloorNumber(Integer.parseInt(floorField.getText().trim()));
            dept.setContactNumber(contactField.getText().trim());

            boolean success = Queries.addDepartment(dept);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Department added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDepartments();
                clearForm();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error adding department: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDepartment() {
        Department selected = (Department) departmentCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a department to update",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateForm())
            return;

        try {
            selected.setDeptName(nameField.getText().trim());
            selected.setFloorNumber(Integer.parseInt(floorField.getText().trim()));
            selected.setContactNumber(contactField.getText().trim());

            boolean success = Queries.updateDepartment(selected);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Department updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDepartments();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating department: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartment() {
        Department selected = (Department) departmentCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a department to delete",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selected.getDeptName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = Queries.deleteDepartment(selected.getDeptId());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Department deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDepartments();
                clearForm();
            }
        }
    }

    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Department name is required",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        try {
            if (!floorField.getText().trim().isEmpty()) {
                Integer.parseInt(floorField.getText().trim());
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Floor number must be a valid number",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            floorField.requestFocus();
            return false;
        }

        return true;
    }
}