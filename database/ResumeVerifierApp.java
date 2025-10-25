import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResumeVerifierApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser; // Store logged-in user
    private int currentResumeId = -1; // Store current resume ID

    public ResumeVerifierApp() {
        setTitle("Resume Verifier");
        setSize(900, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(), "login");
        mainPanel.add(new DashboardPanel(), "dashboard");
        mainPanel.add(new ResumeHistoryPanel(), "history");
        add(mainPanel);

        cardLayout.show(mainPanel, "login");
    }

    // ----- LOGIN PANEL WITH DATABASE AUTHENTICATION -----
    class LoginPanel extends JPanel {
        public LoginPanel() {
            setLayout(new GridBagLayout());
            setBackground(new Color(250, 250, 250));

            JPanel box = new JPanel();
            box.setLayout(null);
            box.setPreferredSize(new Dimension(360, 280));
            box.setBackground(Color.WHITE);
            box.setBorder(new EmptyBorder(10, 35, 10, 35));

            JLabel title = new JLabel("RESUME VERIFIER", SwingConstants.CENTER);
            title.setFont(new Font("Arial Black", Font.BOLD, 22));
            title.setForeground(new Color(52, 73, 94));
            title.setBounds(32, 16, 295, 32);

            JLabel login = new JLabel("LOGIN", SwingConstants.CENTER);
            login.setFont(new Font("Arial", Font.BOLD, 16));
            login.setBounds(115, 52, 90, 20);

            JLabel emailLbl = new JLabel("Email");
            emailLbl.setBounds(22, 85, 65, 16);
            JTextField emailField = new JTextField();
            emailField.setBounds(80, 80, 250, 28);

            JLabel passLbl = new JLabel("Password");
            passLbl.setBounds(5, 123, 70, 16);
            JPasswordField passField = new JPasswordField();
            passField.setBounds(80, 118, 250, 28);

            JButton loginBtn = new JButton("Login");
            loginBtn.setBackground(new Color(34, 167, 240));
            loginBtn.setForeground(Color.WHITE);
            loginBtn.setBounds(120, 163, 120, 38);

            JLabel forgot = new JLabel("Forgot password?", SwingConstants.CENTER);
            forgot.setFont(new Font("Arial", Font.PLAIN, 12));
            forgot.setForeground(new Color(52, 73, 94));
            forgot.setBounds(117, 210, 130, 16);

            JLabel errorMsg = new JLabel("", SwingConstants.CENTER);
            errorMsg.setForeground(Color.RED);
            errorMsg.setBounds(65, 195, 230, 14);

            // DATABASE LOGIN AUTHENTICATION
            loginBtn.addActionListener(e -> {
                String email = emailField.getText().trim();
                String password = new String(passField.getPassword()).trim();

                if (email.isEmpty() || password.isEmpty()) {
                    errorMsg.setText("Enter both Email and Password!");
                } else {
                    errorMsg.setText("");

                    // Authenticate with database
                    User user = UserDAO.authenticateUser(email, password);

                    if (user != null) {
                        currentUser = user; // Save logged-in user
                        JOptionPane.showMessageDialog(LoginPanel.this,
                                "Welcome, " + user.getUsername() + "!",
                                "Login Successful",
                                JOptionPane.INFORMATION_MESSAGE);
                        cardLayout.show(mainPanel, "dashboard");
                    } else {
                        errorMsg.setText("Invalid email or password!");
                    }
                }
            });

            box.add(title); box.add(login); box.add(emailLbl); box.add(emailField);
            box.add(passLbl); box.add(passField);
            box.add(loginBtn); box.add(forgot); box.add(errorMsg);

            add(box);
        }
    }

    // ----- DASHBOARD PANEL WITH DATABASE INTEGRATION -----
    class DashboardPanel extends JPanel {
        private JTextArea resumeTextArea;
        private JButton verifyButton;
        private JLabel statusLabel, scoreValueLabel, wordCountValueLabel, keywordHitsValueLabel;
        private JTextField filePathField;
        private File selectedFile = null;

        private final Color PRIMARY_COLOR = new Color(34, 167, 240);
        private final Color ACCENT_COLOR = new Color(52, 73, 94);
        private final Color SECONDARY_COLOR = new Color(229, 237, 244);
        private final Color BACKGROUND_COLOR = new Color(250, 250, 250);
        private final Color SUCCESS_COLOR = new Color(39, 174, 96);

        public DashboardPanel() {
            setLayout(new BorderLayout(15, 15));
            setBackground(BACKGROUND_COLOR);
            setBorder(new EmptyBorder(0, 15, 15, 15));

            // HEADER PANEL
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(ACCENT_COLOR);
            headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

            JLabel titleLabel = new JLabel("RESUME ANALYTICS DASHBOARD");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.CENTER);

            // BUTTON PANEL (RIGHT SIDE)
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            buttonPanel.setBackground(ACCENT_COLOR);

            JButton historyBtn = new JButton("📋 View History");
            historyBtn.setBackground(new Color(39, 174, 96));
            historyBtn.setForeground(Color.WHITE);
            historyBtn.setFont(new Font("Arial", Font.BOLD, 12));
            historyBtn.addActionListener(e -> cardLayout.show(mainPanel, "history"));

            JButton logoutBtn = new JButton("Logout");
            logoutBtn.setBackground(BACKGROUND_COLOR);
            logoutBtn.setForeground(ACCENT_COLOR);
            logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
            logoutBtn.addActionListener(e -> {
                currentUser = null;
                cardLayout.show(mainPanel, "login");
            });

            buttonPanel.add(historyBtn);
            buttonPanel.add(logoutBtn);
            headerPanel.add(buttonPanel, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);

            // MAIN INPUT PANEL
            JPanel mainInputPanel = new JPanel(new BorderLayout(10, 10));
            mainInputPanel.setBackground(BACKGROUND_COLOR);

            JPanel fileSelectPanel = new JPanel(new BorderLayout(10, 5));
            fileSelectPanel.setBackground(SECONDARY_COLOR);
            fileSelectPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            JLabel label = new JLabel("File Path:");
            label.setFont(new Font("Arial", Font.BOLD, 14));
            label.setForeground(ACCENT_COLOR);
            filePathField = new JTextField("No file selected. Please use Browse or Paste content below.");
            filePathField.setEditable(false);
            filePathField.setBackground(Color.WHITE);
            filePathField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

            JButton browseButton = createStyledButton("Browse...");
            browseButton.setPreferredSize(new Dimension(120, 35));
            browseButton.addActionListener(e -> openFileChooser());

            fileSelectPanel.add(label, BorderLayout.WEST);
            fileSelectPanel.add(filePathField, BorderLayout.CENTER);
            fileSelectPanel.add(browseButton, BorderLayout.EAST);
            mainInputPanel.add(fileSelectPanel, BorderLayout.NORTH);

            resumeTextArea = new JTextArea();
            resumeTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            resumeTextArea.setLineWrap(true);
            resumeTextArea.setWrapStyleWord(true);
            resumeTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            resumeTextArea.setText("Paste your resume content here or use the 'Browse' button above...");

            JScrollPane scrollPane = new JScrollPane(resumeTextArea);
            scrollPane.setPreferredSize(new Dimension(800, 380));
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                    "Resume Content Editor", 0, 0, new Font("Arial", Font.BOLD, 14), PRIMARY_COLOR));
            mainInputPanel.add(scrollPane, BorderLayout.CENTER);

            add(mainInputPanel, BorderLayout.CENTER);

            // SOUTH PANEL (CONTROLS + RESULTS)
            JPanel southPanel = new JPanel(new BorderLayout(10, 10));
            southPanel.setBackground(BACKGROUND_COLOR);

            JPanel controlPanel = new JPanel(new BorderLayout(10, 10));
            controlPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
            verifyButton = createStyledButton("Analyze Resume");
            verifyButton.setPreferredSize(new Dimension(250, 45));
            statusLabel = new JLabel("Status: Ready to load or paste content.", SwingConstants.CENTER);
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            statusLabel.setForeground(Color.GRAY);

            JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonWrapper.setBackground(BACKGROUND_COLOR);
            buttonWrapper.add(verifyButton);
            controlPanel.add(buttonWrapper, BorderLayout.CENTER);
            controlPanel.add(statusLabel, BorderLayout.SOUTH);

            JPanel resultsPanel = createResultsPanel();

            southPanel.add(controlPanel, BorderLayout.NORTH);
            southPanel.add(resultsPanel, BorderLayout.CENTER);

            add(southPanel, BorderLayout.SOUTH);

            verifyButton.addActionListener(new VerifyActionListener());
        }

        private JPanel createResultsPanel() {
            JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
            panel.setBackground(BACKGROUND_COLOR);
            panel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(SUCCESS_COLOR, 1),
                    "Verification Summary", 0, 0, new Font("Arial", Font.BOLD, 14), ACCENT_COLOR));
            scoreValueLabel = createMetricPanel("Overall Score", "---", PRIMARY_COLOR);
            wordCountValueLabel = createMetricPanel("Total Words", "---", ACCENT_COLOR);
            keywordHitsValueLabel = createMetricPanel("Keywords Found", "---", SUCCESS_COLOR);
            panel.add(scoreValueLabel);
            panel.add(wordCountValueLabel);
            panel.add(keywordHitsValueLabel);
            return panel;
        }

        private JLabel createMetricPanel(String title, String initialValue, Color indicatorColor) {
            JLabel label = new JLabel("<html><center><div style='color:" + toHtmlColor(indicatorColor) +
                    "; font-size: 20px;'>&#9679;</div><font size='+2'><b>"+ initialValue +
                    "</b></font><br>" + title + "</center></html>", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.PLAIN, 14));
            label.setOpaque(true);
            label.setBackground(Color.WHITE);
            return label;
        }

        private String toHtmlColor(Color color) {
            return String.format("#%06x", color.getRGB() & 0xFFFFFF);
        }

        private JButton createStyledButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 16));
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)
            ));
            return button;
        }

        private void openFileChooser() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select Resume File (TXT recommended)");
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                filePathField.setText(selectedFile.getAbsolutePath());
                statusLabel.setText("File loaded: " + selectedFile.getName() + ". Ready to analyze.");

                // Read file content and display in text area
                try {
                    String content = new String(Files.readAllBytes(selectedFile.toPath()));
                    resumeTextArea.setText(content);
                } catch (IOException ex) {
                    resumeTextArea.setText("Error reading file: " + ex.getMessage());
                }
            }
        }

        // VERIFICATION ACTION LISTENER WITH DATABASE INTEGRATION
        private class VerifyActionListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resumeText = resumeTextArea.getText();

                if (resumeText.trim().isEmpty() || resumeText.contains("Paste your resume content here")) {
                    statusLabel.setText("Error: Please paste actual resume content or load a file.");
                    return;
                }

                statusLabel.setText("Verification in progress... Analyzing data. Please wait...");
                verifyButton.setEnabled(false);
                clearResults();

                Timer timer = new Timer(1500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        // Perform verification
                        VerificationResult result = performVerification(resumeText);

                        // Save to database
                        saveToDatabase(resumeText, result);

                        // Display results
                        displayResults(result);

                        statusLabel.setText("Verification Complete! Results saved to database.");
                        verifyButton.setEnabled(true);
                        ((Timer)evt.getSource()).stop();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }

        // PERFORM VERIFICATION LOGIC
        private VerificationResult performVerification(String resumeText) {
            VerificationResult result = new VerificationResult();

            // Word count
            int wordCount = resumeText.split("\\s+").length;

            // Check for email
            boolean hasEmail = checkEmail(resumeText);
            result.setHasEmail(hasEmail);

            // Check for phone
            boolean hasPhone = checkPhone(resumeText);
            result.setHasPhone(hasPhone);

            // Education keywords
            String[] educationKeywords = {"B.S.", "B.Tech", "M.S.", "M.Tech", "Ph.D.", "PhD",
                    "University", "College", "Bachelor", "Master", "Degree", "Engineering",
                    "Computer Science", "B.E.", "M.E.", "MBA", "BCA", "MCA"};
            int educationCount = countKeywords(resumeText, educationKeywords);
            result.setEducationKeywords(educationCount);
            result.setHasEducation(educationCount > 0);

            // Skill keywords
            String[] skillKeywords = {"Java", "Python", "C++", "JavaScript", "SQL", "MySQL",
                    "HTML", "CSS", "React", "Node.js", "Spring", "Hibernate", "Git", "Agile",
                    "REST", "API", "Cloud", "AWS", "Docker", "Kubernetes", "Machine Learning",
                    "Data Structure", "Algorithm", "OOP", "Database"};
            int skillCount = countKeywords(resumeText, skillKeywords);
            result.setSkillKeywords(skillCount);

            // Experience keywords
            String[] experienceKeywords = {"experience", "worked", "developed", "project",
                    "internship", "job", "position", "role", "responsibility", "achievement"};
            int experienceCount = countKeywords(resumeText, experienceKeywords);
            result.setHasExperience(experienceCount > 0);

            // Total keyword hits
            int totalKeywords = educationCount + skillCount;
            result.setKeywordHits(totalKeywords);

            // Calculate overall score (out of 100)
            int score = 0;

            // Base points
            score += 20; // Base score

            // Word count (max 15 points)
            if (wordCount > 500) score += 15;
            else if (wordCount > 300) score += 10;
            else if (wordCount > 150) score += 5;

            // Email and phone (10 points each)
            if (hasEmail) score += 10;
            if (hasPhone) score += 10;

            // Education (15 points)
            if (educationCount > 0) score += 15;

            // Skills (20 points)
            if (skillCount >= 10) score += 20;
            else if (skillCount >= 5) score += 15;
            else if (skillCount >= 3) score += 10;
            else if (skillCount > 0) score += 5;

            // Experience (10 points)
            if (experienceCount > 0) score += 10;

            // Cap at 100
            score = Math.min(score, 100);
            result.setOverallScore(score);

            // Set status based on score
            if (score >= 80) {
                result.setStatus("Excellent");
                result.setComments("Strong resume with good content coverage.");
            } else if (score >= 60) {
                result.setStatus("Good");
                result.setComments("Decent resume, could add more details.");
            } else if (score >= 40) {
                result.setStatus("Fair");
                result.setComments("Resume needs improvement in multiple areas.");
            } else {
                result.setStatus("Poor");
                result.setComments("Resume lacks essential information.");
            }

            return result;
        }

        // SAVE RESUME AND VERIFICATION RESULT TO DATABASE
        private void saveToDatabase(String resumeText, VerificationResult result) {
            if (currentUser == null) {
                System.err.println("❌ No user logged in!");
                return;
            }

            try {
                // Create Resume object
                Resume resume = new Resume();
                resume.setUserId(currentUser.getUserId());

                if (selectedFile != null) {
                    resume.setFileName(selectedFile.getName());

                    // Save file to uploads directory
                    String uploadDir = "uploads/";
                    File directory = new File(uploadDir);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                    Path destinationPath = Paths.get(uploadDir + newFileName);
                    Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                    resume.setFilePath(destinationPath.toString());
                } else {
                    resume.setFileName("pasted_content.txt");
                    resume.setFilePath("N/A");
                }

                resume.setResumeText(resumeText);
                resume.setWordCount(resumeText.split("\\s+").length);

                // Save resume to database
                int resumeId = ResumeDAO.saveResume(resume);

                if (resumeId > 0) {
                    currentResumeId = resumeId;

                    // Save verification result
                    result.setResumeId(resumeId);
                    VerificationResultDAO.saveVerificationResult(result);

                    System.out.println("✅ Resume and verification saved! Resume ID: " + resumeId);
                } else {
                    System.err.println("❌ Failed to save resume!");
                }

            } catch (IOException ex) {
                System.err.println("❌ Error saving file: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        // CHECK FOR EMAIL IN RESUME
        private boolean checkEmail(String text) {
            Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
            Matcher matcher = emailPattern.matcher(text);
            return matcher.find();
        }

        // CHECK FOR PHONE NUMBER IN RESUME
        private boolean checkPhone(String text) {
            Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
            Matcher matcher = phonePattern.matcher(text);
            return matcher.find();
        }

        // COUNT KEYWORDS
        private int countKeywords(String text, String[] keywords) {
            text = text.toLowerCase();
            int count = 0;
            for (String keyword : keywords) {
                if (text.contains(keyword.toLowerCase())) {
                    count++;
                }
            }
            return count;
        }

        // CLEAR RESULTS DISPLAY
        private void clearResults() {
            scoreValueLabel.setText(createMetricHtml("---", "Overall Score", PRIMARY_COLOR));
            wordCountValueLabel.setText(createMetricHtml("---", "Total Words", ACCENT_COLOR));
            keywordHitsValueLabel.setText(createMetricHtml("---", "Keywords Found", SUCCESS_COLOR));
        }

        // DISPLAY RESULTS
        private void displayResults(VerificationResult result) {
            scoreValueLabel.setText(createMetricHtml(
                    result.getOverallScore() + "/100", "Overall Score", PRIMARY_COLOR));

            // Get word count from resume text
            int wordCount = resumeTextArea.getText().split("\\s+").length;
            wordCountValueLabel.setText(createMetricHtml(
                    String.valueOf(wordCount), "Total Words", ACCENT_COLOR));

            keywordHitsValueLabel.setText(createMetricHtml(
                    String.valueOf(result.getKeywordHits()), "Key Hits", SUCCESS_COLOR));

            // Show detailed results dialog
            String details = String.format(
                    "Verification Results:\n\n" +
                            "Overall Score: %d/100\n" +
                            "Status: %s\n" +
                            "Word Count: %d\n" +
                            "Education Keywords: %d\n" +
                            "Skill Keywords: %d\n" +
                            "Has Email: %s\n" +
                            "Has Phone: %s\n" +
                            "Has Education: %s\n" +
                            "Has Experience: %s\n\n" +
                            "Comments: %s",
                    result.getOverallScore(),
                    result.getStatus(),
                    wordCount,
                    result.getEducationKeywords(),
                    result.getSkillKeywords(),
                    result.isHasEmail() ? "Yes" : "No",
                    result.isHasPhone() ? "Yes" : "No",
                    result.isHasEducation() ? "Yes" : "No",
                    result.isHasExperience() ? "Yes" : "No",
                    result.getComments()
            );

            JOptionPane.showMessageDialog(this, details,
                    "Detailed Verification Results", JOptionPane.INFORMATION_MESSAGE);
        }

        // CREATE METRIC HTML
        private String createMetricHtml(String value, String title, Color indicatorColor) {
            return "<html><center><div style='color:" + toHtmlColor(indicatorColor) +
                    "; font-size: 20px;'>&#9679;</div><font size='+2'><b>"+ value +
                    "</b></font><br>" + title + "</center></html>";
        }
    }

    // ----- RESUME HISTORY PANEL -----
    class ResumeHistoryPanel extends JPanel {
        private JTable resumeTable;
        private DefaultTableModel tableModel;
        private final Color PRIMARY_COLOR = new Color(34, 167, 240);
        private final Color ACCENT_COLOR = new Color(52, 73, 94);
        private final Color BACKGROUND_COLOR = new Color(250, 250, 250);

        public ResumeHistoryPanel() {
            setLayout(new BorderLayout(15, 15));
            setBackground(BACKGROUND_COLOR);
            setBorder(new EmptyBorder(15, 15, 15, 15));

            // HEADER
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(ACCENT_COLOR);
            headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

            JLabel titleLabel = new JLabel("RESUME HISTORY");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
            titleLabel.setForeground(Color.WHITE);
            headerPanel.add(titleLabel, BorderLayout.CENTER);

            JButton backBtn = new JButton("← Back to Dashboard");
            backBtn.setBackground(BACKGROUND_COLOR);
            backBtn.setForeground(ACCENT_COLOR);
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "dashboard"));
            headerPanel.add(backBtn, BorderLayout.EAST);

            add(headerPanel, BorderLayout.NORTH);

            // TABLE
            String[] columnNames = {"ID", "File Name", "Word Count", "Score", "Status", "Upload Date"};
            tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false; // Make table read-only
                }
            };

            resumeTable = new JTable(tableModel);
            resumeTable.setFont(new Font("Arial", Font.PLAIN, 14));
            resumeTable.setRowHeight(30);
            resumeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            resumeTable.getTableHeader().setBackground(PRIMARY_COLOR);
            resumeTable.getTableHeader().setForeground(Color.WHITE);

            JScrollPane scrollPane = new JScrollPane(resumeTable);
            scrollPane.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                    "All Uploaded Resumes", 0, 0,
                    new Font("Arial", Font.BOLD, 16), ACCENT_COLOR));

            add(scrollPane, BorderLayout.CENTER);

            // BOTTOM PANEL
            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            bottomPanel.setBackground(BACKGROUND_COLOR);

            JButton refreshBtn = new JButton("🔄 Refresh");
            refreshBtn.setFont(new Font("Arial", Font.BOLD, 14));
            refreshBtn.setBackground(PRIMARY_COLOR);
            refreshBtn.setForeground(Color.WHITE);
            refreshBtn.addActionListener(e -> loadResumeHistory());

            JButton viewDetailsBtn = new JButton("📄 View Details");
            viewDetailsBtn.setFont(new Font("Arial", Font.BOLD, 14));
            viewDetailsBtn.setBackground(new Color(39, 174, 96));
            viewDetailsBtn.setForeground(Color.WHITE);
            viewDetailsBtn.addActionListener(e -> viewSelectedResume());

            bottomPanel.add(refreshBtn);
            bottomPanel.add(viewDetailsBtn);
            add(bottomPanel, BorderLayout.SOUTH);

            // Load data initially
            loadResumeHistory();
        }

        private void loadResumeHistory() {
            if (currentUser == null) {
                JOptionPane.showMessageDialog(this, "No user logged in!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Clear existing data
            tableModel.setRowCount(0);

            // Fetch resumes from database
            List<Resume> resumes = ResumeDAO.getResumesByUser(currentUser.getUserId());

            for (Resume resume : resumes) {
                // Get verification result for this resume
                VerificationResult result = VerificationResultDAO.getResultByResumeId(resume.getResumeId());

                Object[] row = {
                        resume.getResumeId(),
                        resume.getFileName(),
                        resume.getWordCount(),
                        result != null ? result.getOverallScore() + "/100" : "N/A",
                        result != null ? result.getStatus() : "Pending",
                        resume.getUploadDate()
                };
                tableModel.addRow(row);
            }

            if (resumes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No resumes found. Upload some resumes first!",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void viewSelectedResume() {
            int selectedRow = resumeTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Please select a resume from the table!",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int resumeId = (int) tableModel.getValueAt(selectedRow, 0);

            // Fetch resume details
            Resume resume = ResumeDAO.getResumeById(resumeId);
            VerificationResult result = VerificationResultDAO.getResultByResumeId(resumeId);

            if (resume != null && result != null) {
                String details = String.format(
                        "Resume Details:\n\n" +
                                "File Name: %s\n" +
                                "Upload Date: %s\n" +
                                "Word Count: %d\n\n" +
                                "Verification Results:\n" +
                                "Overall Score: %d/100\n" +
                                "Status: %s\n" +
                                "Education Keywords: %d\n" +
                                "Skill Keywords: %d\n" +
                                "Has Email: %s\n" +
                                "Has Phone: %s\n" +
                                "Has Education: %s\n" +
                                "Has Experience: %s\n\n" +
                                "Comments: %s",
                        resume.getFileName(),
                        resume.getUploadDate(),
                        resume.getWordCount(),
                        result.getOverallScore(),
                        result.getStatus(),
                        result.getEducationKeywords(),
                        result.getSkillKeywords(),
                        result.isHasEmail() ? "Yes" : "No",
                        result.isHasPhone() ? "Yes" : "No",
                        result.isHasEducation() ? "Yes" : "No",
                        result.isHasExperience() ? "Yes" : "No",
                        result.getComments()
                );

                JOptionPane.showMessageDialog(this, details,
                        "Resume Details - ID: " + resumeId,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // MAIN METHOD
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResumeVerifierApp().setVisible(true));
    }
}
