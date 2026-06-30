package my;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CreateTest extends JFrame {
    private final Color PRIMARY_COLOR = new Color(52, 152, 219);
    private final Color SIDEBAR_COLOR = new Color(33, 37, 43);
    private final Color BG_COLOR = new Color(45, 49, 58);
    private final Color EXIT_COLOR = new Color(231, 76, 60);
    private final Color TEXT_WHITE = new Color(240, 240, 240);
    private final Color FIELD_BG = new Color(60, 65, 75);

    private JTextField tfQue, tfO1, tfO2, tfO3, tfO4, tfAns, tfTime, tfSearch;
    private JComboBox<String> cbBranch, cbSem, cbLang;
    private JLabel lblQueNo;
    
    private String currentTestPassword = "";
    private int nextQueNo = 1;
    private boolean isViewMode = false;

    public CreateTest(String password, boolean viewMode) {
        this.currentTestPassword = password;
        this.isViewMode = viewMode;
        if (isViewMode) loadFirstQuestion();
        setupFrame();
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.add(createSidebar(), BorderLayout.WEST);
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.add(createHeader(), BorderLayout.NORTH);
        contentArea.add(createFormPanel(), BorderLayout.CENTER);
        mainContainer.add(contentArea, BorderLayout.CENTER);
        add(mainContainer);
        setVisible(true);
    }

    private void loadFirstQuestion() { nextQueNo = 1; }

    private String translate(String text, String langCode) {
        if(text.trim().isEmpty()) return "";
        try {
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=" + langCode + "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
            String result = response.toString();
            return result.substring(4, result.indexOf("\"", 4));
        } catch (Exception e) { return text; }
    }

    private void performTranslation() {
        String selected = cbLang.getSelectedItem().toString();
        String targetLang = selected.equals("Hindi") ? "hi" : "en";
        Font f = selected.equals("Hindi") ? new Font("Nirmala UI", Font.PLAIN, 15) : new Font("Segoe UI", Font.PLAIN, 14);
        tfQue.setText(translate(tfQue.getText(), targetLang));
        tfO1.setText(translate(tfO1.getText(), targetLang));
        tfO2.setText(translate(tfO2.getText(), targetLang));
        tfO3.setText(translate(tfO3.getText(), targetLang));
        tfO4.setText(translate(tfO4.getText(), targetLang));
        tfQue.setFont(f); tfO1.setFont(f); tfO2.setFont(f); tfO3.setFont(f); tfO4.setFont(f);
    }

    private void setupFrame() {
        setTitle("MCQ ENGINE | Session: " + currentTestPassword);
        setSize(1150, 750);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SIDEBAR_COLOR);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
        JLabel title = new JLabel(isViewMode ? "VIEW MODE" : "CREATOR MODE");
        title.setForeground(PRIMARY_COLOR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(0, 25, 0, 0));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        searchPanel.setOpaque(false);
        tfSearch = new JTextField(5);
        styleField(tfSearch);
        JButton btnFind = new JButton("FETCH");
        styleButton(btnFind, PRIMARY_COLOR);
        btnFind.addActionListener(e -> fetchByQuestionNo());
        lblQueNo = new JLabel(isViewMode ? "Viewing" : "Q.No: " + nextQueNo);
        lblQueNo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblQueNo.setForeground(new Color(46, 204, 113));
        searchPanel.add(lblQueNo); searchPanel.add(tfSearch); searchPanel.add(btnFind);
        header.add(title, BorderLayout.WEST); header.add(searchPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel formWrapper = new JPanel(new GridBagLayout());
        formWrapper.setBackground(BG_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20); gbc.fill = GridBagConstraints.HORIZONTAL;
        addComp(formWrapper, "BRANCH:", cbBranch = new JComboBox<>(new String[]{"Computer Science", "Electrical", "Mining","Electronice"}), gbc, 0);
        addComp(formWrapper, "SEMESTER:", cbSem = new JComboBox<>(new String[]{"FIRST SEM", "SECOND SEM", "THIRD SEM", "FOURTH SEM", "FIFTH SEM", "SIXTH SEM"}), gbc, 1);
        cbLang = new JComboBox<>(new String[]{"English", "Hindi"});
        cbLang.addActionListener(e -> performTranslation());
        addComp(formWrapper, "LANGUAGE:", cbLang, gbc, 2);
        addComp(formWrapper, "TIMER (sec):", tfTime = new JTextField("30"), gbc, 3);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formWrapper.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        addComp(formWrapper, "QUESTION:", tfQue = new JTextField(40), gbc, 5);
        addComp(formWrapper, "OPTION 1:", tfO1 = new JTextField(), gbc, 6);
        addComp(formWrapper, "OPTION 2:", tfO2 = new JTextField(), gbc, 7);
        addComp(formWrapper, "OPTION 3:", tfO3 = new JTextField(), gbc, 8);
        addComp(formWrapper, "OPTION 4:", tfO4 = new JTextField(), gbc, 9);
        addComp(formWrapper, "CORRECT ANS:", tfAns = new JTextField(), gbc, 10);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setOpaque(false);
        
        JButton btnSave = new JButton(isViewMode ? "REFRESH" : "SAVE DATA");
        styleButton(btnSave, new Color(46, 204, 113));
        btnSave.addActionListener(e -> { if (!isViewMode) saveToDatabase(); else fetchByQuestionNo(); });
        
        // --- UPDATED END SESSION BUTTON ---
        JButton btnEnd = new JButton("END SESSION");
        styleButton(btnEnd, EXIT_COLOR);
        btnEnd.addActionListener(e -> {
            // Last question save logic
            if (!isViewMode && !tfQue.getText().trim().isEmpty()) {
                saveToDatabase();
            }
            // Go back to Teacherview (v small) dashboard
            new Teacherview("T101").setVisible(true);
            dispose();
        });

        btnPanel.add(btnSave); btnPanel.add(btnEnd);
        gbc.gridx = 1; gbc.gridy = 11; gbc.insets = new Insets(30, 20, 10, 20);
        formWrapper.add(btnPanel, gbc);
        return formWrapper;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setPreferredSize(new Dimension(150, 45)); btn.setBackground(bg);
        btn.setForeground(Color.WHITE); btn.setFocusPainted(false);
        btn.setBorderPainted(false); btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleField(JTextField f) {
        f.setBackground(FIELD_BG); f.setForeground(TEXT_WHITE);
        f.setCaretColor(TEXT_WHITE); f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(80, 80, 80), 1), new EmptyBorder(5, 10, 5, 10)));
    }

    private void addComp(JPanel p, String l, JComponent c, GridBagConstraints g, int r) {
        JLabel label = new JLabel(l); label.setForeground(new Color(200, 200, 200));
        g.gridx = 0; g.gridy = r; p.add(label, g);
        if(c instanceof JTextField) styleField((JTextField)c);
        g.gridx = 1; p.add(c, g);
    }

    private JPanel createSidebar() {
        JPanel s = new JPanel(); s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(SIDEBAR_COLOR); s.setPreferredSize(new Dimension(220, 0));
        s.add(Box.createRigidArea(new Dimension(0, 40)));
        JLabel l = new JLabel("TEST ADMIN"); l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setForeground(TEXT_WHITE); l.setFont(new Font("Segoe UI", Font.BOLD, 22));
        s.add(l); return s;
    }

    private void saveToDatabase() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "INSERT INTO dbo.MCQ_Questions (TestPassword, QuestionNo, Branch, Semester, Language, QuestionText, Option1, Option2, Option3, Option4, CorrectAnswer, TimeDuration) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, currentTestPassword); pst.setInt(2, nextQueNo);
            pst.setString(3, cbBranch.getSelectedItem().toString()); pst.setString(4, cbSem.getSelectedItem().toString());
            pst.setString(5, cbLang.getSelectedItem().toString()); pst.setString(6, tfQue.getText());
            pst.setString(7, tfO1.getText()); pst.setString(8, tfO2.getText());
            pst.setString(9, tfO3.getText()); pst.setString(10, tfO4.getText());
            pst.setString(11, tfAns.getText()); pst.setInt(12, Integer.parseInt(tfTime.getText()));
            if (pst.executeUpdate() > 0) {
                if(!isViewMode) JOptionPane.showMessageDialog(this, "Saved!"); 
                nextQueNo++;
                lblQueNo.setText("Q.No: " + nextQueNo); clearFields();
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void fetchByQuestionNo() {
        String s = tfSearch.getText().trim();
        if(s.isEmpty()) return;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT * FROM dbo.MCQ_Questions WHERE TestPassword = ? AND QuestionNo = ?")) {
            pst.setString(1, currentTestPassword); pst.setInt(2, Integer.parseInt(s));
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    tfQue.setText(rs.getString("QuestionText")); tfO1.setText(rs.getString("Option1"));
                    tfO2.setText(rs.getString("Option2")); tfO3.setText(rs.getString("Option3"));
                    tfO4.setText(rs.getString("Option4")); tfAns.setText(rs.getString("CorrectAnswer"));
                } else JOptionPane.showMessageDialog(this, "Not Found!");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void clearFields() {
        tfQue.setText(""); tfO1.setText(""); tfO2.setText("");
        tfO3.setText(""); tfO4.setText(""); tfAns.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] options = {"Create New", "View Test"};
            int choice = JOptionPane.showOptionDialog(null, "Select Mode", "MCQ Engine", 0, 3, null, options, options[0]);
            if (choice == 0) {
                String pass = JOptionPane.showInputDialog("New Password:");
                if (pass != null && !pass.isEmpty()) {
                    if (isUsed(pass)) JOptionPane.showMessageDialog(null, "Exists!");
                    else if (reg(pass)) new CreateTest(pass, false);
                }
            } else if (choice == 1) {
                String pass = JOptionPane.showInputDialog("Enter Password:");
                if (pass != null && isUsed(pass)) new CreateTest(pass, true);
            }
        });
    }

    private static boolean isUsed(String p) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("SELECT TestPassword FROM dbo.Test_Config WHERE TestPassword = ?")) {
            pst.setString(1, p);
            try (ResultSet rs = pst.executeQuery()) { return rs.next(); }
        } catch (Exception e) { return false; }
    }

    private static boolean reg(String p) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement("INSERT INTO dbo.Test_Config (TestPassword) VALUES (?)")) {
            pst.setString(1, p); return pst.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }
}