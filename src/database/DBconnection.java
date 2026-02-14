package database;

import java.sql.*;
import javax.swing.*;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/university_energy_mgt";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "abc123";

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                // 1. MySQL Driver load karo
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                // Driver nahi mila - MySQL Connector JAR missing
                JOptionPane.showMessageDialog(null,
                        "MySQL JDBC Driver not found!\nPlease add MySQL Connector JAR to classpath.",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (SQLException e) {
                // Connection failed - wrong password, DB not exist, etc.
                JOptionPane.showMessageDialog(null,
                        "Database Connection Failed: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}