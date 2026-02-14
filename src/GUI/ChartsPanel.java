package GUI;

import Models.User;
import Models.Department;
import Models.Device;
import database.Queries;
import utils.EnergyCalculator;
import utils.CarbonCalculator;

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
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
// import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ChartsPanel extends JPanel {
    private User currentUser;
    private JTabbedPane chartTabbedPane;
    private JComboBox<String> timeRangeCombo;
    private JComboBox<Department> departmentCombo;
    private JButton refreshButton;

    // ✅ CONSTRUCTOR - YE SAHI HONA CHAHIYE
    public ChartsPanel(User user) {
        this.currentUser = user;
        initializeUI();
        loadCharts();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(createControlPanel(), BorderLayout.NORTH);

        chartTabbedPane = new JTabbedPane();
        chartTabbedPane.setFont(new Font("Arial", Font.PLAIN, 12));

        add(chartTabbedPane, BorderLayout.CENTER);
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Chart Controls",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)));

        panel.add(new JLabel("Department:"));
        departmentCombo = new JComboBox<>();
        departmentCombo.setPreferredSize(new Dimension(200, 25));
        panel.add(departmentCombo);

        panel.add(new JLabel("Time Range:"));
        String[] ranges = { "Last 7 Days", "Last 30 Days", "Last 3 Months", "Last Year" };
        timeRangeCombo = new JComboBox<>(ranges);
        timeRangeCombo.setPreferredSize(new Dimension(120, 25));
        panel.add(timeRangeCombo);

        refreshButton = new JButton("Refresh Charts");
        refreshButton.setBackground(new Color(0, 102, 204));
        refreshButton.setForeground(Color.BLACK);
        refreshButton.setFocusPainted(false);
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

        chartTabbedPane.addTab("Consumption", createConsumptionChart());
        chartTabbedPane.addTab("Devices", createDeviceChart());
        chartTabbedPane.addTab("Cost", createCostChart());
        chartTabbedPane.addTab("Trend", createTrendChart());
        chartTabbedPane.addTab("Carbon", createCarbonChart());
        chartTabbedPane.addTab("Efficiency", createEfficiencyChart());
    }

    private JPanel createConsumptionChart() {
        try {
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
                    false);

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

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading consumption chart");
        }
    }

    private JPanel createDeviceChart() {
        try {
            DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

            Department selected = (Department) departmentCombo.getSelectedItem();
            List<Department> departments;

            if (selected != null && selected.getDeptId() != 0) {
                departments = Arrays.asList(selected);
            } else {
                departments = Queries.getAllDepartments();
            }

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

            if (acCount > 0)
                dataset.setValue("ACs", acCount);
            if (lightCount > 0)
                dataset.setValue("Lights", lightCount);
            if (computerCount > 0)
                dataset.setValue("Computers", computerCount);
            if (otherCount > 0)
                dataset.setValue("Other", otherCount);

            JFreeChart chart = ChartFactory.createPieChart(
                    "Device Distribution",
                    dataset,
                    true,
                    true,
                    false);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 500));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            return panel;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading device chart");
        }
    }

    private JPanel createCostChart() {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            List<Department> departments = Queries.getAllDepartments();

            for (Department dept : departments) {
                double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
                double cost = EnergyCalculator.calculateMonthlyCost(monthlyKWh);
                dataset.addValue(cost, "Cost", dept.getDeptName());
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Monthly Electricity Cost by Department",
                    "Department",
                    "Cost (Rs.)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.BLACK);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(204, 0, 0));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 500));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            return panel;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading cost chart");
        }
    }

    private JPanel createTrendChart() {
        try {
            TimeSeries series = new TimeSeries("Energy Consumption");

            Random random = new Random();
            double baseConsumption = 1000;

            for (int i = 30; i >= 0; i--) {
                Day day = new Day(15, 2, 2024);
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
                    false);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 500));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            return panel;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading trend chart");
        }
    }

    private JPanel createCarbonChart() {
        try {
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            List<Department> departments = Queries.getAllDepartments();

            for (Department dept : departments) {
                double monthlyKWh = Queries.getTotalMonthlyKWhByDepartment(dept.getDeptId());
                double carbon = CarbonCalculator.calculateCarbonFootprint(monthlyKWh);
                dataset.addValue(carbon, "CO2", dept.getDeptName());
            }

            JFreeChart chart = ChartFactory.createBarChart(
                    "Carbon Footprint by Department",
                    "Department",
                    "CO2 Emissions (kg)",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.BLACK);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

            BarRenderer renderer = (BarRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(0, 153, 76));

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 500));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            return panel;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading carbon chart");
        }
    }

    private JPanel createEfficiencyChart() {
        try {
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
                    false);

            CategoryPlot plot = chart.getCategoryPlot();
            plot.setBackgroundPaint(Color.BLACK);
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setSeriesPaint(0, new Color(255, 153, 0));
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesLinesVisible(0, true);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 500));

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(chartPanel, BorderLayout.CENTER);
            return panel;

        } catch (Exception e) {
            e.printStackTrace();
            return createErrorPanel("Error loading efficiency chart");
        }
    }

    private int calculateEfficiencyScore(double monthlyKWh) {
        if (monthlyKWh < 1000)
            return 90 + (int) (Math.random() * 10);
        if (monthlyKWh < 2000)
            return 75 + (int) (Math.random() * 15);
        if (monthlyKWh < 3000)
            return 60 + (int) (Math.random() * 15);
        if (monthlyKWh < 4000)
            return 45 + (int) (Math.random() * 15);
        return 30 + (int) (Math.random() * 15);
    }

    private JPanel createErrorPanel(String message) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel errorLabel = new JLabel("⚠️ " + message);
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(errorLabel, BorderLayout.CENTER);
        return panel;
    }
}