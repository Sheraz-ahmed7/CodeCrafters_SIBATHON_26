package gui;

import models.User;
import models.Department;
import models.Device;
import database.Queries;
import utils.EnergyCalculator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class ChartsPanel extends JPanel {
    private User currentUser;
    private JTabbedPane chartTabbedPane;
    private JComboBox<String> timeRangeCombo;
    private JComboBox<Department> departmentCombo;
    private JButton refreshButton;
    
    public ChartsPanel(User user) {
        this.currentUser = user;
        initializeUI();
        loadCharts();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Controls
        add(createControlPanel(), BorderLayout.NORTH);
        
        // Center panel - Chart tabs
        chartTabbedPane = new JTabbedPane();
        chartTabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));
        
        add(chartTabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Chart Controls"));
        
        panel.add(new JLabel("Department:"));
        departmentCombo = new JComboBox<>();
        departmentCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(departmentCombo);
        
        panel.add(new JLabel("Time Range:"));
        String[] ranges = {"Last 7 Days", "Last 30 Days", "Last 3 Months", "Last Year"};
        timeRangeCombo = new JComboBox<>(ranges);
        timeRangeCombo.setPreferredSize(new Dimension(120, 25));
        panel.add(timeRangeCombo);
        
        refreshButton = new JButton("Refresh Charts");
        refreshButton.setBackground(new Color(0, 102, 204));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadCharts());
        panel.add(refreshButton);
        
        loadDepartments();
        
        return panel;
    }
    
    private void loadDepartments() {
        List<Department> departments = Queries.getAllDepartments();
        departmentCombo.removeAllItems();
        departmentCombo.addItem(new Department(0, "All Departments", 0, ""));
        
        for (Department dept : departments) {
            departmentCombo.addItem(dept);
        }
        
        if (!currentUser.isAdmin() && currentUser.getDepartmentId() != null) {
            for (Department dept : departments) {
                if (dept.getDeptId() == currentUser.getDepartmentId()) {
                    departmentCombo.setSelectedItem(dept);
                    break;
                }
            }
            departmentCombo.setEnabled(false);
        }
    }
    
    private void loadCharts() {
        chartTabbedPane.removeAll();
        
        // Add different chart types
        chartTabbedPane.addTab("Consumption by Department", createConsumptionChart());
        chartTabbedPane.addTab("Device Breakdown", createDeviceChart());
        chartTabbedPane.addTab("Cost Analysis", createCostChart());
        chartTabbedPane.addTab("Trend Analysis", createTrendChart());
        chartTabbedPane.addTab("Carbon Footprint", createCarbonChart());
        chartTabbedPane.addTab("Efficiency Score", createEfficiencyChart());
    }
    
    private JPanel createConsumptionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Department> departments = Queries.getAllDepartments();
        
        for (Department dept : departments) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            dataset.addValue(monthlyKWh, "Consumption", dept.getDeptName());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Energy Consumption by Department",
            "Department",
            "Consumption (kWh)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        // Customize chart
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 102, 204));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createDeviceChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        
        Department selected = (Department) departmentCombo.getSelectedItem();
        List<Department> departments;
        
        if (selected != null && selected.getDeptId() != 0) {
            departments = List.of(selected);
        } else {
            departments = Queries.getAllDepartments();
        }
        
        // Aggregate device types
        int acCount = 0, lightCount = 0, computerCount = 0, otherCount = 0;
        
        for (Department dept : departments) {
            List<Device> devices = Queries.getDevicesByDepartment(dept.getDeptId());
            for (Device device : devices) {
                String name = device.getDeviceName().toLowerCase();
                if (name.contains("ac") || name.contains("air conditioner")) {
                    acCount += device.getQuantity();
                } else if (name.contains("light") || name.contains("lamp")) {
                    lightCount += device.getQuantity();
                } else if (name.contains("computer") || name.contains("pc") || name.contains("laptop")) {
                    computerCount += device.getQuantity();
                } else {
                    otherCount += device.getQuantity();
                }
            }
        }
        
        dataset.setValue("ACs", acCount);
        dataset.setValue("Lights", lightCount);
        dataset.setValue("Computers", computerCount);
        dataset.setValue("Other", otherCount);
        
        JFreeChart chart = ChartFactory.createPieChart(
            "Device Distribution",
            dataset,
            true,
            true,
            false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCostChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Department> departments = Queries.getAllDepartments();
        
        for (Department dept : departments) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            double cost = EnergyCalculator.calculateMonthlyCost(monthlyKWh);
            dataset.addValue(cost, "Cost (Rs.)", dept.getDeptName());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Monthly Electricity Cost by Department",
            "Department",
            "Cost (Rs.)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(204, 0, 0));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTrendChart() {
        TimeSeries series = new TimeSeries("Energy Consumption Trend");
        
        // Generate sample data for last 30 days
        Random random = new Random();
        double baseConsumption = 1000;
        
        for (int i = 30; i >= 0; i--) {
            Day day = new Day(15, 2, 2024); // Using sample date
            double variation = random.nextDouble() * 200 - 100;
            series.add(day, baseConsumption + variation);
        }
        
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Energy Consumption Trend",
            "Date",
            "Consumption (kWh)",
            dataset,
            true,
            true,
            false
        );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCarbonChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Department> departments = Queries.getAllDepartments();
        
        for (Department dept : departments) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            double carbon = EnergyCalculator.calculateCarbonFootprint(monthlyKWh);
            dataset.addValue(carbon, "CO2 (kg)", dept.getDeptName());
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "Carbon Footprint by Department",
            "Department",
            "CO2 Emissions (kg)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0, 153, 76));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEfficiencyChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Department> departments = Queries.getAllDepartments();
        
        for (Department dept : departments) {
            double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
            int score = calculateEfficiencyScore(monthlyKWh);
            dataset.addValue(score, "Score", dept.getDeptName());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
            "Department Efficiency Scores",
            "Department",
            "Efficiency Score",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangePannable(true);
        
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 153, 0));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesLinesVisible(0, true);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private int calculateEfficiencyScore(double monthlyKWh) {
        if (monthlyKWh < 1000) return 90 + (int)(Math.random() * 10);
        if (monthlyKWh < 2000) return 75 + (int)(Math.random() * 15);
        if (monthlyKWh < 3000) return 60 + (int)(Math.random() * 15);
        if (monthlyKWh < 4000) return 45 + (int)(Math.random() * 15);
        return 30 + (int)(Math.random() * 15);
    }
}