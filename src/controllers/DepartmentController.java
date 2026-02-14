package controllers;

import Models.Department;
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
        if (!validateDepartment(department)) {
            return false;
        }
        return Queries.addDepartment(department);
    }

    public boolean updateDepartment(Department department) {
        if (!validateDepartment(department)) {
            return false;
        }
        return Queries.updateDepartment(department);
    }

    public boolean deleteDepartment(int deptId) {
        return Queries.deleteDepartment(deptId);
    }

    private boolean validateDepartment(Department department) {
        if (department == null) {
            return false;
        }
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
