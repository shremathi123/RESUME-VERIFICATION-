import java.sql.*;

public class VerificationResultDAO {

    // Save verification result
    public static boolean saveVerificationResult(VerificationResult result) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO verification_results (resume_id, overall_score, keyword_hits, " +
                "education_keywords, skill_keywords, has_email, has_phone, has_education, " +
                "has_experience, status, comments) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, result.getResumeId());
            pst.setInt(2, result.getOverallScore());
            pst.setInt(3, result.getKeywordHits());
            pst.setInt(4, result.getEducationKeywords());
            pst.setInt(5, result.getSkillKeywords());
            pst.setBoolean(6, result.isHasEmail());
            pst.setBoolean(7, result.isHasPhone());
            pst.setBoolean(8, result.isHasEducation());
            pst.setBoolean(9, result.isHasExperience());
            pst.setString(10, result.getStatus());
            pst.setString(11, result.getComments());

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Verification result saved!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to save verification result!");
            e.printStackTrace();
        }

        return false;
    }

    // Get verification result by resume ID
    public static VerificationResult getResultByResumeId(int resumeId) {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM verification_results WHERE resume_id = ? ORDER BY verification_date DESC LIMIT 1";

        try {
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, resumeId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                VerificationResult result = new VerificationResult();
                result.setResultId(rs.getInt("result_id"));
                result.setResumeId(rs.getInt("resume_id"));
                result.setOverallScore(rs.getInt("overall_score"));
                result.setKeywordHits(rs.getInt("keyword_hits"));
                result.setEducationKeywords(rs.getInt("education_keywords"));
                result.setSkillKeywords(rs.getInt("skill_keywords"));
                result.setHasEmail(rs.getBoolean("has_email"));
                result.setHasPhone(rs.getBoolean("has_phone"));
                result.setHasEducation(rs.getBoolean("has_education"));
                result.setHasExperience(rs.getBoolean("has_experience"));
                result.setStatus(rs.getString("status"));
                result.setComments(rs.getString("comments"));
                result.setVerificationDate(rs.getTimestamp("verification_date"));
                return result;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}

