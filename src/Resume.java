import java.sql.Timestamp;

public class Resume {
    private int resumeId;
    private int userId;
    private String fileName;
    private String filePath;
    private String resumeText;
    private int wordCount;
    private Timestamp uploadDate;

    // Constructors
    public Resume() {}

    public Resume(int userId, String fileName, String filePath, String resumeText, int wordCount) {
        this.userId = userId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.resumeText = resumeText;
        this.wordCount = wordCount;
    }

    // Getters and Setters
    public int getResumeId() { return resumeId; }
    public void setResumeId(int resumeId) { this.resumeId = resumeId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }

    public Timestamp getUploadDate() { return uploadDate; }
    public void setUploadDate(Timestamp uploadDate) { this.uploadDate = uploadDate; }
}
