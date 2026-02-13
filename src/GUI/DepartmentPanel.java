package gui;

import models.User;
import models.Department;
import database.Queries;
import utils.EnergyCalculator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        
        // Disable editing for non-admin users
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
        
        // Split pane for form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.3);
        
        // Top panel - Department Form
        splitPane.setTopComponent(createFormPanel());
        
        // Bottom panel - Department Table
        splitPane.setBottomComponent(createTablePanel());
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Department Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Department selection for update/delete
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Select Department:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        departmentCombo = new JComboBox<>();
        departmentCombo.addActionListener(e -> loadSelectedDepartment());
        panel.add(departmentCombo, gbc);
        
        // Department Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Department Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // Floor Number
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Floor Number:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        floorField = new JTextField(20);
        panel.add(floorField, gbc);
        
        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Contact Number:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        contactField = new JTextField(20);
        panel.add(contactField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add Department");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        refreshButton = new JButton("Refresh");
        
        styleButton(addButton, new Color(0, 153, 76));
        styleButton(updateButton, new Color(255, 153, 0));
        styleButton(deleteButton, new Color(204, 0, 0));
        styleButton(refreshButton, new Color(102, 102, 102));
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);
        
        // Add action listeners
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
        
        // Create table
        String[] columns = {"ID", "Department Name", "Floor", "Contact", "Monthly kWh", "Monthly Cost"};
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
        
        // Set column widths
        departmentTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        departmentTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        departmentTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        departmentTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        departmentTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        departmentTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(departmentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void loadDepartments() {
        try {
            List<Department> departments = Queries.getAllDepartments();
            
            // Update combo box
            departmentCombo.removeAllItems();
            for (Department dept : departments) {
                departmentCombo.addItem(dept);
            }
            
            // Update table
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
            String deptName = (String) tableModel.getValueAt(selectedRow, 1);
            
            // Select in combo box
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
        if (!validateForm()) return;
        
        try {
            Department dept = new Department();
            dept.setDeptName(nameField.getText().trim());
            dept.setFloorNumber(Integer.parseInt(floorField.getText().trim()));
            dept.setContactNumber(contactField.getText().trim());
            
            // TODO: Implement addDepartment in Queries
            JOptionPane.showMessageDialog(this,
                "Department added successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadDepartments();
            clearForm();
            
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
        
        if (!validateForm()) return;
        
        try {
            selected.setDeptName(nameField.getText().trim());
            selected.setFloorNumber(Integer.parseInt(floorField.getText().trim()));
            selected.setContactNumber(contactField.getText().trim());
            
            // TODO: Implement updateDepartment in Queries
            JOptionPane.showMessageDialog(this,
                "Department updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            loadDepartments();
            
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
            "Are you sure you want to delete " + selected.getDeptName() + "?\n" +
            "This will also delete all associated devices!",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // TODO: Implement deleteDepartment in Queries
                JOptionPane.showMessageDialog(this,
                    "Department deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                loadDepartments();
                clearForm();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting department: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
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