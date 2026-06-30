package my;
import java.sql.*;

public class DBConnection {
    public static Connection getConnection() {
        try {
        // Driver ko force load karne ke liye ye line likhein
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        
        // integratedSecurity=true ko hata kar user aur password dalein
String dbURL = "jdbc:sqlserver://localhost:1433;databaseName=collage;user=sa;password=12345;encrypt=true;trustServerCertificate=true;";
        return DriverManager.getConnection(dbURL);
    } catch (Exception e) {
        System.out.println("Connection Error: " + e.getMessage());
        return null;
        }
    }
}