package src.utils;

public class SolarSimulator {
    
    // Constants for solar calculation
    private static final double PEAK_SUN_HOURS = 5.0; // Average peak sun hours per day in Pakistan
    private static final double PANEL_WATTAGE = 400.0; // Wattage per solar panel
    private static final double PANEL_COST = 50000.0; // Rs. per panel (installed)
    private static final double SQUARE_FEET_PER_PANEL = 20.0; // Approx area needed per panel
    
    public static class SolarResult {
        public int panelsNeeded;
        public double installationCost;
        public double annualSavings;
        public double paybackYears;
        public double areaRequired;
        public double monthlyGeneration;
        
        public SolarResult(int panelsNeeded, double installationCost, double annualSavings, 
                          double paybackYears, double areaRequired, double monthlyGeneration) {
            this.panelsNeeded = panelsNeeded;
            this.installationCost = installationCost;
            this.annualSavings = annualSavings;
            this.paybackYears = paybackYears;
            this.areaRequired = areaRequired;
            this.monthlyGeneration = monthlyGeneration;
        }
    }
    
    public static SolarResult simulateSolar(double monthlyConsumptionKWh) {
        // Daily consumption
        double dailyConsumption = monthlyConsumptionKWh / 30.0;
        
        // Panels needed: Daily consumption / (peak sun hours * panel output per hour in kW)
        double panelOutputKW = PANEL_WATTAGE / 1000.0;
        double dailyGenerationPerPanel = panelOutputKW * PEAK_SUN_HOURS;
        int panelsNeeded = (int) Math.ceil(dailyConsumption / dailyGenerationPerPanel);
        
        // Installation cost
        double installationCost = panelsNeeded * PANEL_COST;
        
        // Annual savings
        double annualSavings = monthlyConsumptionKWh * 12 * EnergyCalculator.COST_PER_KWH;
        
        // Payback period
        double paybackYears = installationCost / annualSavings;
        
        // Area required
        double areaRequired = panelsNeeded * SQUARE_FEET_PER_PANEL;
        
        // Monthly generation
        double monthlyGeneration = dailyGenerationPerPanel * panelsNeeded * 30;
        
        return new SolarResult(panelsNeeded, installationCost, annualSavings, 
                              paybackYears, areaRequired, monthlyGeneration);
    }
}