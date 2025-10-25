import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // ⚠️ IMPORTANT: Update PASSWORD if your MySQL has a password!
    private static final String URL = "jdbc:mysql://localhost:3306/resume_verifier";
    private static final String USER = "root";
    private static final String PASSWORD = "shremathi2006";  // Leave empty if no password, or add your password

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Establish connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("✅ Database connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL JDBC Driver not found!");
            System.err.println("Make sure you added mysql-connector-j JAR to your project!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed!");
            System.err.println("Check your URL, username, and password!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Database connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Test the connection
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        Connection conn = DatabaseConnection.getConnection();

        if (conn != null) {
            System.out.println("✅✅✅ Connection test SUCCESSFUL! ✅✅✅");
            closeConnection();
        } else {
            System.out.println("❌❌❌ Connection test FAILED! ❌❌❌");
        }
    }
}
