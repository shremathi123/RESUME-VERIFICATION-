import java.sql.Timestamp;

public class VerificationResult {
    private int resultId;
    private int resumeId;
    private int overallScore;
    private int keywordHits;
    private int educationKeywords;
    private int skillKeywords;
    private boolean hasEmail;
    private boolean hasPhone;
    private boolean hasEducation;
    private boolean hasExperience;
    private String status;
    private String comments;
    private Timestamp verificationDate;

    // Constructors
    public VerificationResult() {}

    public VerificationResult(int resumeId, int overallScore, int keywordHits,
                              int educationKeywords, int skillKeywords) {
        this.resumeId = resumeId;
        this.overallScore = overallScore;
        this.keywordHits = keywordHits;
        this.educationKeywords = educationKeywords;
        this.skillKeywords = skillKeywords;
    }

    // Getters and Setters
    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getResumeId() { return resumeId; }
    public void setResumeId(int resumeId) { this.resumeId = resumeId; }

    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }

    public int getKeywordHits() { return keywordHits; }
    public void setKeywordHits(int keywordHits) { this.keywordHits = keywordHits; }

    public int getEducationKeywords() { return educationKeywords; }
    public void setEducationKeywords(int educationKeywords) { this.educationKeywords = educationKeywords; }

    public int getSkillKeywords() { return skillKeywords; }
    public void setSkillKeywords(int skillKeywords) { this.skillKeywords = skillKeywords; }

    public boolean isHasEmail() { return hasEmail; }
    public void setHasEmail(boolean hasEmail) { this.hasEmail = hasEmail; }

    public boolean isHasPhone() { return hasPhone; }
    public void setHasPhone(boolean hasPhone) { this.hasPhone = hasPhone; }

    public boolean isHasEducation() { return hasEducation; }
    public void setHasEducation(boolean hasEducation) { this.hasEducation = hasEducation; }

    public boolean isHasExperience() { return hasExperience; }
    public void setHasExperience(boolean hasExperience) { this.hasExperience = hasExperience; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public Timestamp getVerificationDate() { return verificationDate; }
    public void setVerificationDate(Timestamp verificationDate) { this.verificationDate = verificationDate; }
}
