package controllers;

import models.User;
import models.Department;
import database.Queries;
import utils.EnergyCalculator;

import java.util.List;

public class DashboardController {
    private User currentUser;
    
    public DashboardController(User user) {
        this.currentUser = user;
    }
    
    public String getWelcomeMessage() {
        String role = currentUser.isAdmin() ? "Administrator" : "Department User";
        return "Welcome, " + currentUser.getUsername() + " (" + role + ")";
    }
    
    public double getTotalEnergyConsumption() {
        if (currentUser.isAdmin()) {
            // For admin, sum all departments
            List<Department> departments = Queries.getAllDepartments();
            double total = 0;
            for (Department dept : departments) {
                total += Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            }
            return total;
        } else {
            // For department user, only their department
            return Queries.getTotalMonthlyKWhByDepartment(currentUser.getDepartmentId());
        }
    }
    
    public double getTotalCost() {
        double consumption = getTotalEnergyConsumption();
        return EnergyCalculator.calculateMonthlyCost(consumption);
    }
    
    public double getTotalCarbonFootprint() {
        double consumption = getTotalEnergyConsumption();
        return EnergyCalculator.calculateCarbonFootprint(consumption);
    }
    
    public int getTreesNeeded() {
        double carbon = getTotalCarbonFootprint();
        return EnergyCalculator.calculateTreesNeeded(carbon);
    }
    
    public String getSystemStatus() {
        // Check database connection
        try {
            java.sql.Connection conn = database.DBConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                return "Connected";
            }
        } catch (Exception e) {
            return "Disconnected";
        }
        return "Unknown";
    }
}