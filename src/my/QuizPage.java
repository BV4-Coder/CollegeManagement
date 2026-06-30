package my;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class QuizPage extends JFrame {
    private JLabel lblHeader, lblTimer, lblQuestion;
    private JRadioButton[] options = new JRadioButton[4];
    private ButtonGroup bg;
    private JButton btnNext, btnPrev, btnSubmit;
    
    private String rollNo, testPassword;
    private ArrayList<QuestionData> qList = new ArrayList<>();
    private int currentIdx = 0, score = 0, timeLeft = 600; 
    private Timer timer;

    public QuizPage(String rollNo, String subjectName, String testPassword) {
        this.rollNo = rollNo;
        this.testPassword = testPassword;

        setTitle("Online Test Panel");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(18, 18, 18));

        initUI();
        fetchQuestions();
        startTimer();
        displayQuestion();
    }

    private void initUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(180, 40, 40));
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        lblHeader = new JLabel("Online Examination System");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));

        lblTimer = new JLabel("Time Left: 10:00");
        lblTimer.setForeground(Color.WHITE);
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 16));

        header.add(lblHeader, BorderLayout.WEST);
        header.add(lblTimer, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(null);
        center.setOpaque(false);

        lblQuestion = new JLabel("Loading...");
        lblQuestion.setForeground(Color.WHITE);
        lblQuestion.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblQuestion.setBounds(50, 40, 800, 30);
        center.add(lblQuestion);

        bg = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            options[i].setForeground(Color.LIGHT_GRAY);
            options[i].setBackground(new Color(30, 30, 30));
            options[i].setFont(new Font("Segoe UI", Font.PLAIN, 15));
            options[i].setBounds(70, 100 + (i * 60), 700, 40);
            bg.add(options[i]);
            center.add(options[i]);
        }
        add(center, BorderLayout.CENTER);

        JPanel footer = new JPanel();
        footer.setBackground(new Color(25, 25, 25));
        footer.setPreferredSize(new Dimension(0, 80));

        btnPrev = new JButton("PREVIOUS");
        btnNext = new JButton("NEXT");
        btnSubmit = new JButton("FINISH TEST");

        footer.add(btnPrev); footer.add(btnNext); footer.add(btnSubmit);
        add(footer, BorderLayout.SOUTH);

        btnNext.addActionListener(e -> { saveAnswer(); currentIdx++; displayQuestion(); });
        btnPrev.addActionListener(e -> { saveAnswer(); currentIdx--; displayQuestion(); });
        btnSubmit.addActionListener(e -> finalizeTest());
    }

    private void fetchQuestions() {
        try (Connection con = DBConnection.getConnection()) {
            // Aapke screenshot ke hisab se column names: QuestionText, CorrectAnswer
            String q = "SELECT QuestionText, Option1, Option2, Option3, Option4, CorrectAnswer FROM dbo.MCQ_Questions WHERE TestPassword = ?";
            PreparedStatement pst = con.prepareStatement(q);
            pst.setString(1, testPassword);
            ResultSet rs = pst.executeQuery();
            
            qList.clear();
            while (rs.next()) {
                qList.add(new QuestionData(
                    rs.getString("QuestionText"), 
                    new String[]{rs.getString("Option1"), rs.getString("Option2"), rs.getString("Option3"), rs.getString("Option4")}, 
                    rs.getString("CorrectAnswer")
                ));
            }
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Fetch Error: " + e.getMessage());
        }
    }

    private void displayQuestion() {
        if (qList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid Password or No Questions Found!");
            this.dispose();
            return;
        }
        bg.clearSelection();
        QuestionData q = qList.get(currentIdx);
        lblQuestion.setText("Q" + (currentIdx + 1) + ": " + q.question);
        for (int i = 0; i < 4; i++) {
            options[i].setText(q.options[i]);
            if(q.userAnswer.equals(q.options[i])) options[i].setSelected(true);
        }
        btnPrev.setEnabled(currentIdx > 0);
        btnNext.setVisible(currentIdx < qList.size() - 1);
        btnSubmit.setVisible(currentIdx == qList.size() - 1);
    }

    private void saveAnswer() {
        for (JRadioButton rb : options) {
            if (rb.isSelected()) qList.get(currentIdx).userAnswer = rb.getText();
        }
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            timeLeft--;
            lblTimer.setText(String.format("Time Left: %02d:%02d", timeLeft/60, timeLeft%60));
            if (timeLeft <= 0) finalizeTest();
        });
        timer.start();
    }

    private void finalizeTest() {
        saveAnswer();
        timer.stop();
        score = 0;
        for (QuestionData q : qList) {
            if (q.correctAnswer != null && q.userAnswer != null && 
                q.correctAnswer.trim().equalsIgnoreCase(q.userAnswer.trim())) {
                score++;
            }
        }

        try (Connection con = DBConnection.getConnection()) {
            // StudentResults table: RollNo, Score, TotalMarks, SubmitDate
            String sql = "INSERT INTO dbo.StudentResults (RollNo, Score, TotalMarks, SubmitDate) VALUES (?, ?, ?, GETDATE())";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, rollNo);
            pst.setInt(2, score);
            pst.setInt(3, qList.size());
            pst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Test Submitted Successfully!\nYour Score: " + score + "/" + qList.size());
            this.dispose();
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(this, "Result Save Error: " + e.getMessage());
        }
    }

    class QuestionData {
        String question, correctAnswer, userAnswer = "";
        String[] options;
        QuestionData(String q, String[] opts, String ans) {
            this.question = q; this.options = opts; this.correctAnswer = ans;
        }
    }
}