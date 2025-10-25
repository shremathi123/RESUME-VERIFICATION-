import java.sql.*;

public class UserDAO {

    // Authenticate user login
    public static User authenticateUser(String email, String password) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM users WHERE email = ? AND password = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, email);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                System.out.println("✅ User authenticated: " + user.getUsername());
                return user;
            } else {
                System.out.println("❌ Invalid email or password!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Authentication failed!");
            e.printStackTrace();
        }

        return null;
    }

    // Register new user (optional - for future use)
    public static boolean registerUser(String username, String email, String password) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, 'user')";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, email);
            pst.setString(3, password);

            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ User registration failed!");
            e.printStackTrace();
            return false;
        }
    }
}
