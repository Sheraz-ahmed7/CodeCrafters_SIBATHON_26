// Add these methods to your existing Queries.java file

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