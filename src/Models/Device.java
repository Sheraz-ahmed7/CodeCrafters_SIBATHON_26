package src.models;

public class Device {
    private int deviceId;
    private int deptId;
    private String deviceName;
    private int wattage;
    private int quantity;
    private double hoursPerDay;
    
    // Constructors
    public Device() {}
    
    public Device(int deptId, String deviceName, int wattage, int quantity, double hoursPerDay) {
        this.deptId = deptId;
        this.deviceName = deviceName;
        this.wattage = wattage;
        this.quantity = quantity;
        this.hoursPerDay = hoursPerDay;
    }
    
    // Getters and Setters
    public int getDeviceId() { return deviceId; }
    public void setDeviceId(int deviceId) { this.deviceId = deviceId; }
    
    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    
    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
    
    public int getWattage() { return wattage; }
    public void setWattage(int wattage) { this.wattage = wattage; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getHoursPerDay() { return hoursPerDay; }
    public void setHoursPerDay(double hoursPerDay) { this.hoursPerDay = hoursPerDay; }
    
    // Calculate daily consumption in kWh
    public double getDailyConsumption() {
        return (wattage * quantity * hoursPerDay) / 1000.0;
    }
    
    // Calculate monthly consumption in kWh
    public double getMonthlyConsumption() {
        return getDailyConsumption() * 30;
    }
}