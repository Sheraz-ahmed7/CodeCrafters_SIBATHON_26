package models;

import java.time.LocalDateTime;

public class EnergyUsage {
    private int usageId;
    private int deptId;
    private LocalDateTime timestamp;
    private double kWh;
    private double cost;
    private double carbonFootprint;
    
    public EnergyUsage() {}
    
    public EnergyUsage(int usageId, int deptId, LocalDateTime timestamp, 
                      double kWh, double cost, double carbonFootprint) {
        this.usageId = usageId;
        this.deptId = deptId;
        this.timestamp = timestamp;
        this.kWh = kWh;
        this.cost = cost;
        this.carbonFootprint = carbonFootprint;
    }
    
    // Getters and Setters
    public int getUsageId() { return usageId; }
    public void setUsageId(int usageId) { this.usageId = usageId; }
    
    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public double getKWh() { return kWh; }
    public void setKWh(double kWh) { this.kWh = kWh; }
    
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    public double getCarbonFootprint() { return carbonFootprint; }
    public void setCarbonFootprint(double carbonFootprint) { this.carbonFootprint = carbonFootprint; }
}