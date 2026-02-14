package database;

import Models.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Queries {

    // ==================== USER QUERIES ====================

    public static User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setDepartmentId(rs.getInt("department_id"));
                if (rs.wasNull()) {
                    user.setDepartmentId(null);
                }
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== DEPARTMENT QUERIES ====================

    public static List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();
        // Saare departments, name ke hisaab se sorted
        String query = "SELECT * FROM departments ORDER BY dept_name";

        try (Statement stmt = DBConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {    // Har row ke liye
                Department dept = new Department();

                // Result se values lo aur set karo
                dept.setDeptId(rs.getInt("dept_id"));
                dept.setDeptName(rs.getString("dept_name"));
                dept.setFloorNumber(rs.getInt("floor_number"));
                dept.setContactNumber(rs.getString("contact_number"));

                // List mein add karo
                departments.add(dept);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public static Department getDepartmentById(int deptId) {
        String query = "SELECT * FROM departments WHERE dept_id = ?";
        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Department dept = new Department();
                dept.setDeptId(rs.getInt("dept_id"));
                dept.setDeptName(rs.getString("dept_name"));
                dept.setFloorNumber(rs.getInt("floor_number"));
                dept.setContactNumber(rs.getString("contact_number"));
                return dept;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addDepartment(Department department) {
        String query = "INSERT INTO departments (dept_name, floor_number, contact_number) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, department.getDeptName());
            pstmt.setInt(2, department.getFloorNumber());
            pstmt.setString(3, department.getContactNumber());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDepartment(Department department) {
        String query = "UPDATE departments SET dept_name = ?, floor_number = ?, contact_number = ? WHERE dept_id = ?";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, department.getDeptName());
            pstmt.setInt(2, department.getFloorNumber());
            pstmt.setString(3, department.getContactNumber());
            pstmt.setInt(4, department.getDeptId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteDepartment(int deptId) {
        String query = "DELETE FROM departments WHERE dept_id = ?";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, deptId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== DEVICE QUERIES ====================

    public static boolean addDevice(Device device) {
        String query = "INSERT INTO devices (dept_id, device_name, wattage, quantity, hours_per_day) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, device.getDeptId());
            pstmt.setString(2, device.getDeviceName());
            pstmt.setInt(3, device.getWattage());
            pstmt.setInt(4, device.getQuantity());
            pstmt.setDouble(5, device.getHoursPerDay());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Device> getDevicesByDepartment(int deptId) {
        List<Device> devices = new ArrayList<>();
        String query = "SELECT * FROM devices WHERE dept_id = ? ORDER BY device_name";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, deptId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Device device = new Device();
                device.setDeviceId(rs.getInt("device_id"));
                device.setDeptId(rs.getInt("dept_id"));
                device.setDeviceName(rs.getString("device_name"));
                device.setWattage(rs.getInt("wattage"));
                device.setQuantity(rs.getInt("quantity"));
                device.setHoursPerDay(rs.getDouble("hours_per_day"));
                devices.add(device);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return devices;
    }

    public static boolean updateDevice(Device device) {
        String query = "UPDATE devices SET device_name = ?, wattage = ?, quantity = ?, hours_per_day = ? WHERE device_id = ?";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setString(1, device.getDeviceName());
            pstmt.setInt(2, device.getWattage());
            pstmt.setInt(3, device.getQuantity());
            pstmt.setDouble(4, device.getHoursPerDay());
            pstmt.setInt(5, device.getDeviceId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteDevice(int deviceId) {
        String query = "DELETE FROM devices WHERE device_id = ?";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, deviceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static double getTotalMonthlyKWhByDepartment(int deptId) {
        List<Device> devices = getDevicesByDepartment(deptId);
        double total = 0;
        for (Device device : devices) {
            total += device.getMonthlyConsumption();
        }
        return total;
    }

    // ==================== ENERGY USAGE QUERIES ====================

    public static List<EnergyUsage> getEnergyUsageHistory(int deptId, int days) {
        List<EnergyUsage> history = new ArrayList<>();
        String query = "SELECT * FROM energy_usage WHERE dept_id = ? AND timestamp >= DATE_SUB(NOW(), INTERVAL ? DAY) ORDER BY timestamp";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, deptId);
            pstmt.setInt(2, days);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                EnergyUsage usage = new EnergyUsage();
                usage.setUsageId(rs.getInt("usage_id"));
                usage.setDeptId(rs.getInt("dept_id"));
                usage.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                usage.setKWh(rs.getDouble("kwh"));
                usage.setCost(rs.getDouble("cost"));
                usage.setCarbonFootprint(rs.getDouble("carbon_footprint"));
                history.add(usage);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public static boolean addEnergyUsage(EnergyUsage usage) {
        String query = "INSERT INTO energy_usage (dept_id, timestamp, kwh, cost, carbon_footprint) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DBConnection.getConnection().prepareStatement(query)) {
            pstmt.setInt(1, usage.getDeptId());
            pstmt.setTimestamp(2, Timestamp.valueOf(usage.getTimestamp()));
            pstmt.setDouble(3, usage.getKWh());
            pstmt.setDouble(4, usage.getCost());
            pstmt.setDouble(5, usage.getCarbonFootprint());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}