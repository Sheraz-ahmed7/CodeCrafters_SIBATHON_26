package controllers;

import Models.Device;
import Models.Department;
import database.Queries;

import java.util.List;

public class DeviceController {

    public List<Device> getDevicesByDepartment(int deptId) {
        return Queries.getDevicesByDepartment(deptId);
    }

    public List<Device> getDevicesByDepartment(Department dept) {
        if (dept == null) return List.of();
        return Queries.getDevicesByDepartment(dept.getDeptId());
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
        return Queries.updateDevice(device);
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
        double total = 0;
        for (Device device : devices) {
            total += device.getMonthlyConsumption();
        }
        return total;
    }

    public int getDeviceCount(int deptId) {
        return Queries.getDevicesByDepartment(deptId).size();
    }

    public double getAverageWattage(int deptId) {
        List<Device> devices = Queries.getDevicesByDepartment(deptId);
        if (devices.isEmpty()) return 0;

        double total = 0;
        for (Device device : devices) {
            total += device.getWattage();
        }
        return total / devices.size();
    }
}