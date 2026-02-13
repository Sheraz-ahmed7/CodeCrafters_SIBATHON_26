package controllers;

import models.Department;
import database.Queries;

import java.util.List;

public class DepartmentController {
    
    public List<Department> getAllDepartments() {
        return Queries.getAllDepartments();
    }
    
    public Department getDepartmentById(int deptId) {
        return Queries.getDepartmentById(deptId);
    }
    
    public boolean addDepartment(Department department) {
        // Validate department data
        if (!validateDepartment(department)) {
            return false;
        }
        
        // TODO: Implement addDepartment in Queries
        // return Queries.addDepartment(department);
        return true; // Placeholder
    }
    
    public boolean updateDepartment(Department department) {
        if (!validateDepartment(department)) {
            return false;
        }
        
        // TODO: Implement updateDepartment in Queries
        return true; // Placeholder
    }
    
    public boolean deleteDepartment(int deptId) {
        // TODO: Implement deleteDepartment in Queries
        return true; // Placeholder
    }
    
    private boolean validateDepartment(Department department) {
        if (department == null) return false;
        if (department.getDeptName() == null || department.getDeptName().trim().isEmpty()) {
            return false;
        }
        return true;
    }
    
    public double getDepartmentConsumption(int deptId) {
        return Queries.getTotalMonthlyKWhByDepartment(deptId);
    }
    
    public int getDepartmentCount() {
        return Queries.getAllDepartments().size();
    }
}