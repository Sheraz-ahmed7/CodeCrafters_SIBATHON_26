package controllers;

import Models.User;
import Models.Department;
import database.Queries;
import utils.EnergyCalculator;
import utils.CarbonCalculator;

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
            List<Department> departments = Queries.getAllDepartments();
            double total = 0;
            for (Department dept : departments) {
                total += Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            }
            return total;
        } else {
            return Queries.getTotalMonthlyKWhByDepartment(currentUser.getDepartmentId());
        }
    }

    public double getTotalCost() {
        double consumption = getTotalEnergyConsumption();
        return EnergyCalculator.calculateMonthlyCost(consumption);
    }

    public double getTotalCarbonFootprint() {
        double consumption = getTotalEnergyConsumption();
        return CarbonCalculator.calculateCarbonFootprint(consumption);
    }

    public int getTreesNeeded() {
        double carbon = getTotalCarbonFootprint();
        return CarbonCalculator.calculateTreesNeeded(carbon);
    }

    public String getSystemStatus() {
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

    public int getDepartmentCount() {
        return Queries.getAllDepartments().size();
    }

    public int getTotalDevices() {
        List<Department> departments = Queries.getAllDepartments();
        int total = 0;
        for (Department dept : departments) {
            total += Queries.getDevicesByDepartment(dept.getDeptId()).size();
        }
        return total;
    }
}