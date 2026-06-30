package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*; 
import java.sql.*;
import java.util.ArrayList; 

public class StudentView extends JPanel {

    // --- COLORS (Wahi purana theme) ---
    private final Color BG_DARK = new Color(18, 18, 18);
    private final Color SIDEBAR_BG = new Color(25, 25, 25);
    private final Color HEADER_RED = new Color(180, 40, 40);
    private final Color CARD_BG = new Color(30, 30, 30);
    private final Color TEXT_WHITE = new Color(245, 245, 245);
    private final Color TEXT_GRAY = new Color(160, 160, 160);

    private String studentId; 
    private JLabel lblWelcomeName = new JLabel("Welcome Student | Logout"); 
    private JLabel lblEnrollNo = new JLabel("Enroll: --");
    private JLabel lblBranchBox = new JLabel("N/A");
    private JLabel lblSemester = new JLabel("N/A");
    private JLabel lblPhoto = new JLabel("PHOTO", SwingConstants.CENTER);
    private JLabel lblAttendance = new JLabel("0%"); 
    private DefaultTableModel subModel;

    public StudentView(String sId) {
        this.studentId = sId;
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // Sidebar
        JPanel sidebar = createSidebar();
        sidebar.setPreferredSize(new Dimension(240, 0));
        add(sidebar, BorderLayout.WEST);

        // Main Content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.add(createHeader(), BorderLayout.NORTH);

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(20, 25, 20, 25));

        body.add(createStatsRow()); 
        body.add(Box.createRigidArea(new Dimension(0, 20)));
        body.add(createActionButtons()); 
        body.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setOpaque(false);
        tablesPanel.add(createSubjectTable()); 
        tablesPanel.add(createTableBox("Recent Notices (No News)")); 
        
        body.add(tablesPanel);
        mainContent.add(body, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Fetch Data logic
        fetchDBData();
    }

    private void fetchDBData() {
        subModel.setRowCount(0); 

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) return;

            // 1. Basic Student Info
            String q1 = "SELECT Name, LTRIM(RTRIM(Branch)) as Branch, LTRIM(RTRIM(Semester)) as Semester, PhotoPath FROM dbo.Students WHERE RollNo = ?";
            PreparedStatement pst1 = con.prepareStatement(q1);
            pst1.setString(1, studentId);
            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                String b = rs1.getString("Branch"); 
                String s = rs1.getString("Semester");
                String photoPathFromDB = rs1.getString("PhotoPath");
                
                lblWelcomeName.setText("Welcome " + rs1.getString("Name") + " | Logout");
                lblBranchBox.setText(b);
                lblSemester.setText(s);
                lblEnrollNo.setText("Enroll: " + studentId);

                // --- REAL ATTENDANCE CALCULATION ---
                String qAtt = "SELECT " +
                              "(SELECT COUNT(DISTINCT AttendanceDate) FROM StudentAttendance A2 " +
                              " JOIN Students S2 ON A2.RollNo = S2.RollNo " +
                              " WHERE S2.Branch = ? AND S2.Semester = ? AND A2.AttendanceDate >= (SELECT StartDate FROM SemesterSettings WHERE Branch=? AND Semester=?)) as TotalDays, " +
                              "(SELECT COUNT(*) FROM StudentAttendance WHERE RollNo = ? AND Status='P' AND AttendanceDate >= (SELECT StartDate FROM SemesterSettings WHERE Branch=? AND Semester=?)) as MyPresent";
                
                PreparedStatement pstAtt = con.prepareStatement(qAtt);
                pstAtt.setString(1, b); pstAtt.setString(2, s);
                pstAtt.setString(3, b); pstAtt.setString(4, s);
                pstAtt.setString(5, studentId);
                pstAtt.setString(6, b); pstAtt.setString(7, s);
                
                ResultSet rsAtt = pstAtt.executeQuery();
                if (rsAtt.next()) {
                    int total = rsAtt.getInt("TotalDays");
                    int present = rsAtt.getInt("MyPresent");
                    double perc = (total > 0) ? (present * 100.0 / total) : 0.0;
                    lblAttendance.setText(String.format("%.2f", perc) + "%");
                    
                    if (perc < 75) lblAttendance.setForeground(new Color(231, 76, 60)); // Red
                    else lblAttendance.setForeground(new Color(46, 204, 113)); // Green
                }

                // Student Photo Loading
                if (photoPathFromDB != null && !photoPathFromDB.trim().isEmpty()) {
                    try {
                        ImageIcon icon = new ImageIcon(photoPathFromDB.trim());
                        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                            lblPhoto.setIcon(new ImageIcon(img));
                            lblPhoto.setText(""); 
                        }
                    } catch (Exception e) {
                        System.err.println("Photo load error: " + e.getMessage());
                    }
                }

                // Subjects Loading
                String q2 = "SELECT DISTINCT SubjectName FROM dbo.StudentSubjects " +
                            "WHERE LTRIM(RTRIM(Branch)) = ? AND LTRIM(RTRIM(Semester)) = ?";
                
                PreparedStatement pst2 = con.prepareStatement(q2);
                pst2.setString(1, b); 
                pst2.setString(2, s); 
                
                ResultSet rs2 = pst2.executeQuery();

                int count = 1;
                while (rs2.next()) {
                    subModel.addRow(new Object[]{count++, rs2.getString("SubjectName")});
                }

                if (count == 1 && b.equalsIgnoreCase("Computer Science")) {
                    pst2.setString(1, "CS"); 
                    rs2 = pst2.executeQuery();
                    while (rs2.next()) {
                        subModel.addRow(new Object[]{count++, rs2.getString("SubjectName")});
                    }
                }

                if (count == 1) {
                    subModel.addRow(new Object[]{"-", "No Subjects found for " + b});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private JPanel createSidebar() {
        JPanel s = new JPanel();
        s.setLayout(new BoxLayout(s, BoxLayout.Y_AXIS));
        s.setBackground(SIDEBAR_BG);
        s.setBorder(new EmptyBorder(20, 10, 20, 10));

        lblPhoto.setPreferredSize(new Dimension(100, 100));
        lblPhoto.setMaximumSize(new Dimension(100, 100));
        lblPhoto.setForeground(Color.GRAY);
        lblPhoto.setBorder(new LineBorder(HEADER_RED));
        lblPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        s.add(lblPhoto);
        
        s.add(Box.createRigidArea(new Dimension(0, 10)));
        lblEnrollNo.setForeground(Color.WHITE);
        lblEnrollNo.setAlignmentX(Component.CENTER_ALIGNMENT);
        s.add(lblEnrollNo);
        s.add(Box.createRigidArea(new Dimension(0, 25)));

        String[] menu = {"Dashboard", "Video Lectures","Notice Board", "Profile", "Logout"};
        for(String m : menu) {
            JButton b = new JButton(m);
            b.setMaximumSize(new Dimension(220, 38));
            b.setBackground(m.equals("Dashboard") ? HEADER_RED : SIDEBAR_BG);
            b.setForeground(Color.WHITE);
            b.setBorderPainted(false); b.setFocusPainted(false);
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // --- ACTION LISTENERS TO MAKE BUTTONS ACTIVE ---
            b.addActionListener(e -> {
                if(m.equals("Logout")) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                    if(confirm == JOptionPane.YES_OPTION) {
                        Window win = SwingUtilities.getWindowAncestor(this);
                        if (win != null) {
                            win.dispose(); // Close current window
                            // Yahan apni main login class ka naam likho, e.g., new LoginPage().setVisible(true);
                            // Agar tumhare pas koi specific piche jane wali frame hai toh yahan call karo
                        }
                    }
                } else if(m.equals("Video Lectures")) {
        // YE LINE ADD KARNI HAI VIDEO WALE BUTTON KE LIYE
        new VideoLecturesPage().setVisible(true);
    }else if(m.equals("Notice Board")) {
    new NoticeBoardPage().setVisible(true);
}else if(m.equals("Profile")) {
    // YE LINE ADD KARO: studentId wahi hai jo class ke constructor mein aayi thi
    new StudentProfilePage(studentId).setVisible(true);
}
    else {
        JOptionPane.showMessageDialog(this, m + " Section Active!");
    }
            });

            s.add(b);
            s.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        return s;
    }

    private JPanel createHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(HEADER_RED);
        h.setPreferredSize(new Dimension(0, 55));
        h.setBorder(new EmptyBorder(0, 20, 0, 20));
        JLabel title = new JLabel("GOVERNMENT POLYTECHNIC STUDENT PORTAL");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcomeName.setForeground(Color.WHITE);
        h.add(title, BorderLayout.WEST);
        h.add(lblWelcomeName, BorderLayout.EAST);
        return h;
    }

    private JPanel createStatsRow() {
        JPanel r = new JPanel(new GridLayout(1, 4, 15, 0));
        r.setOpaque(false);
        r.add(createStatBox("ATTENDANCE", lblAttendance, Color.GREEN));
        r.add(createStatBox("YOUR BRANCH", lblBranchBox, Color.CYAN));
        r.add(createStatBox("SEMESTER", lblSemester, Color.ORANGE));
        r.add(createStatBox("LIBRARY BOOKS", new JLabel("0"), Color.MAGENTA));
        return r;
    }

    private JPanel createStatBox(String title, JLabel val, Color lineC) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD_BG);
        p.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel t = new JLabel(title); t.setForeground(TEXT_GRAY); t.setFont(new Font("Segoe UI", Font.BOLD, 10));
        val.setForeground(Color.WHITE); val.setFont(new Font("Segoe UI", Font.BOLD, 18));
        p.add(t, BorderLayout.NORTH); p.add(val, BorderLayout.CENTER);
        JPanel line = new JPanel(); line.setBackground(lineC); line.setPreferredSize(new Dimension(0, 3));
        p.add(line, BorderLayout.SOUTH);
        return p;
    }

    private JPanel createActionButtons() {
        JPanel r = new JPanel(new GridLayout(1, 3, 15, 0));
        r.setOpaque(false);
        
        JButton btnQuiz = createActionBtn("START ONLINE TEST", new Color(231, 76, 60));
        btnQuiz.addActionListener(e -> {
            String pass = JOptionPane.showInputDialog(this, "Enter Test Password:");
            if (pass != null && !pass.trim().isEmpty()) {
                new QuizPage(studentId, "Online Test", pass.trim()).setVisible(true);
            }
        });

        JButton btnTimeTable = createActionBtn("VIEW TIME TABLE", new Color(52, 152, 219));
        btnTimeTable.addActionListener(e -> {
            String branch = lblBranchBox.getText().trim();
            String sem = lblSemester.getText().trim();
            try (Connection con = DBConnection.getConnection()) {
                String sql = "SELECT ImagePath FROM dbo.TimeTable WHERE Branch = ? AND Semester = ?";
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, branch);
                pst.setString(2, sem);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    showImagePopup(rs.getString("ImagePath"), "Time Table - " + branch);
                } else {
                    JOptionPane.showMessageDialog(this, "No Time Table image found for " + branch + " " + sem);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
            }
        });

        JButton btnSyllabus = createActionBtn("DOWNLOAD SYLLABUS", new Color(46, 204, 113));
        btnSyllabus.addActionListener(e -> {
            String[] options = {"BTEUP Syllabus", "URISE Portal", "BTEUP Official Site", "Cancel"};
            int choice = JOptionPane.showOptionDialog(this, 
                "Select the source for Syllabus/Updates:", 
                "Official Websites", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.INFORMATION_MESSAGE, 
                null, options, options[0]);

            String url = "";
            switch(choice) {
                case 0: url = "https://bteup.ac.in/webapp/SYLLABUS.aspx?type=18"; break;
                case 1: url = "https://urise.up.gov.in/syllabus"; break;
                case 2: url = "https://bteup.ac.in"; break;
                default: return;
            }

            try {
                Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Could not open browser: " + ex.getMessage());
            }
        });

        r.add(btnQuiz);
        r.add(btnTimeTable);
        r.add(btnSyllabus);
        return r;
    }

    private void showImagePopup(String fileName, String title) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setSize(1000, 750);
        dialog.setLocationRelativeTo(this);
        try {
            String resourcePath = "/my/New folder (2)/" + fileName; 
            java.net.URL imgURL = getClass().getResource(resourcePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(950, 680, Image.SCALE_SMOOTH);
                JLabel lbl = new JLabel(new ImageIcon(img));
                dialog.add(new JScrollPane(lbl));
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Image file not found in folder: " + fileName);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage());
        }
    }

    private JButton createActionBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel createSubjectTable() {
        JPanel p = createTableBox("Registered Subjects (Current Semester)");
        subModel = new DefaultTableModel(new String[]{"S.No", "SUBJECT NAME"}, 0);
        JTable table = new JTable(subModel);
        table.setBackground(CARD_BG); table.setForeground(Color.WHITE); table.setRowHeight(35);
        table.getTableHeader().setBackground(new Color(45, 45, 45)); table.getTableHeader().setForeground(Color.WHITE);
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(CARD_BG); sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createTableBox(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD_BG);
        p.setBorder(new LineBorder(new Color(45, 45, 45)));
        JLabel t = new JLabel("  " + title); t.setForeground(TEXT_WHITE); t.setPreferredSize(new Dimension(0, 40));
        p.add(t, BorderLayout.NORTH);
        return p;
    }
}