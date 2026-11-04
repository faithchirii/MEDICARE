package org.example.medicare;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:medicare.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Users Table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "role TEXT)");

            // Patients Table & Safe Columns
            stmt.execute("CREATE TABLE IF NOT EXISTS patients (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT)");
            addColumnSafely(stmt, "patients", "dob", "TEXT");
            addColumnSafely(stmt, "patients", "gender", "TEXT");
            addColumnSafely(stmt, "patients", "disease", "TEXT");
            addColumnSafely(stmt, "patients", "contact", "TEXT");
            addColumnSafely(stmt, "patients", "address", "TEXT");

            // Doctors Table
            stmt.execute("CREATE TABLE IF NOT EXISTS doctors (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "specialization TEXT, " +
                    "contact TEXT)");

            // Appointments Table & Safe Columns
            stmt.execute("CREATE TABLE IF NOT EXISTS appointments (id INTEGER PRIMARY KEY AUTOINCREMENT, patient_name TEXT)");
            addColumnSafely(stmt, "appointments", "doctor_name", "TEXT");
            addColumnSafely(stmt, "appointments", "date", "TEXT");
            addColumnSafely(stmt, "appointments", "time", "TEXT");
            addColumnSafely(stmt, "appointments", "reason", "TEXT");
            addColumnSafely(stmt, "appointments", "status", "TEXT");

            // Medical History Table
            stmt.execute("CREATE TABLE IF NOT EXISTS medical_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "patient_id INTEGER, " +
                    "notes TEXT, " +
                    "date TEXT)");

            // Bills Table
            stmt.execute("CREATE TABLE IF NOT EXISTS bills (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "patient_name TEXT, " +
                    "total_amount REAL, " +
                    "amount_paid REAL, " +
                    "balance REAL, " +
                    "status TEXT)");

            // Medicines Table
            stmt.execute("CREATE TABLE IF NOT EXISTS medicines (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "quantity INTEGER, " +
                    "price REAL, " +
                    "expiry_date TEXT)");

            // Default Admin Account (Password: 1234 hashed)
            stmt.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES ('admin', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'Admin')");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addColumnSafely(Statement stmt, String tableName, String columnName, String columnType) {
        try {
            stmt.execute("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + columnType);
        } catch (Exception e) {
            // Column already exists, safe to ignore
        }
    }
}