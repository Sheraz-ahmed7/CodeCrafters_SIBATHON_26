package src.utils;

import src.models.Device;
import java.util.List;

public class EnergyCalculator {
    
    // Constants
    public static final double COST_PER_KWH = 25.0; // Rs. per kWh
    public static final double EMISSION_FACTOR = 0.5; // kg CO2 per kWh
    
    // Calculate total monthly consumption from list of devices
    public static double calculateMonthlyConsumption(List<Device> devices) {
        double total = 0;
        for (Device device : devices) {
            total += device.getMonthlyConsumption();
        }
        return total;
    }
    
    // Calculate monthly cost
    public static double calculateMonthlyCost(double totalKWh) {
        return totalKWh * COST_PER_KWH;
    }
    
    // Calculate carbon footprint
    public static double calculateCarbonFootprint(double totalKWh) {
        return totalKWh * EMISSION_FACTOR;
    }
    
    // Calculate trees needed to offset carbon (1 tree absorbs ~22 kg CO2 per year)
    public static int calculateTreesNeeded(double carbonKg) {
        return (int) Math.ceil(carbonKg / 22);
    }
    
    // Calculate sustainability score (0-100)
    public static int calculateSustainabilityScore(double totalKWh, double areaSqFt) {
        // Assume benchmark of 1.5 kWh per sq ft per month is average
        double benchmark = areaSqFt * 1.5;
        
        if (totalKWh <= benchmark * 0.5) {
            return 90 + (int)(Math.random() * 10); // Excellent (90-100)
        } else if (totalKWh <= benchmark * 0.8) {
            return 70 + (int)(Math.random() * 15); // Good (70-85)
        } else if (totalKWh <= benchmark * 1.2) {
            return 50 + (int)(Math.random() * 15); // Average (50-65)
        } else {
            return (int)(Math.max(0, 50 - (totalKWh - benchmark) / benchmark * 30)); // Below average
        }
    }
}