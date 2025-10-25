import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResumeDAO {

    // Save resume to database
    public static int saveResume(Resume resume) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO resumes (user_id, file_name, file_path, resume_text, word_count) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, resume.getUserId());
            pst.setString(2, resume.getFileName());
            pst.setString(3, resume.getFilePath());
            pst.setString(4, resume.getResumeText());
            pst.setInt(5, resume.getWordCount());

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pst.getGeneratedKeys();
                if (rs.next()) {
                    int resumeId = rs.getInt(1);
                    System.out.println("✅ Resume saved with ID: " + resumeId);
                    return resumeId;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to save resume!");
            e.printStackTrace();
        }

        return -1;
    }

    // Get all resumes for a user
    public static List<Resume> getResumesByUser(int userId) {
        List<Resume> resumes = new ArrayList<>();
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM resumes WHERE user_id = ? ORDER BY upload_date DESC";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Resume resume = new Resume();
                resume.setResumeId(rs.getInt("resume_id"));
                resume.setUserId(rs.getInt("user_id"));
                resume.setFileName(rs.getString("file_name"));
                resume.setFilePath(rs.getString("file_path"));
                resume.setResumeText(rs.getString("resume_text"));
                resume.setWordCount(rs.getInt("word_count"));
                resume.setUploadDate(rs.getTimestamp("upload_date"));
                resumes.add(resume);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resumes;
    }

    // Get resume by ID
    public static Resume getResumeById(int resumeId) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM resumes WHERE resume_id = ?";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, resumeId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Resume resume = new Resume();
                resume.setResumeId(rs.getInt("resume_id"));
                resume.setUserId(rs.getInt("user_id"));
                resume.setFileName(rs.getString("file_name"));
                resume.setFilePath(rs.getString("file_path"));
                resume.setResumeText(rs.getString("resume_text"));
                resume.setWordCount(rs.getInt("word_count"));
                resume.setUploadDate(rs.getTimestamp("upload_date"));
                return resume;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

