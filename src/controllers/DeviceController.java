package controllers;

import models.Device;
import database.Queries;

import java.util.List;

public class DeviceController {
    
    public List<Device> getDevicesByDepartment(int deptId) {
        return Queries.getDevicesByDepartment(deptId);
    }
    
    public boolean addDevice(Device device) {
        if (!validateDevice(device)) {
            return false;
        }
        return Queries.addDevice(device);
    }
    
    public boolean updateDevice(Device device) {
        if (!validateDevice(device)) {
            return false;
        }
        // TODO: Implement updateDevice in Queries
        return true; // Placeholder
    }
    
    public boolean deleteDevice(int deviceId) {
        return Queries.deleteDevice(deviceId);
    }
    
    private boolean validateDevice(Device device) {
        if (device == null) return false;
        if (device.getDeviceName() == null || device.getDeviceName().trim().isEmpty()) {
            return false;
        }
        if (device.getWattage() <= 0) return false;
        if (device.getQuantity() <= 0) return false;
        if (device.getHoursPerDay() < 0 || device.getHoursPerDay() > 24) return false;
        return true;
    }
    
    public double calculateTotalConsumption(List<Device> devices) {
        return devices.stream()
                .mapToDouble(Device::calculateMonthlyKWh)
                .sum();
    }
    
    public int getDeviceCount(int deptId) {
        return Queries.getDevicesByDepartment(deptId).size();
    }
}