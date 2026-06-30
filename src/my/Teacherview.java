package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.File;

public class Teacherview extends JFrame {

    // --- THEME COLORS ---
    Color sidebarColor = new Color(33, 37, 43);   
    Color buttonTextColor = new Color(102, 204, 255); 
    Color headerColor = new Color(40, 44, 52);
    Color bgLight = new Color(28, 30, 34);
    Color cardColor = new Color(40, 44, 52);
    
    JLabel teacherPhotoLabel, teacherNameLabel, teacherIdLabel;
    JPanel contentArea; 

    public Teacherview(String teacherId) {
        setTitle("Faculty Dashboard | GP Talbehat");
        setSize(1300, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(bgLight);
        setLayout(new BorderLayout());

        // --- 1. TOP BAR ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(headerColor);
        topBar.setPreferredSize(new Dimension(0, 100));
        topBar.setBorder(new MatteBorder(0, 0, 2, 0, new Color(60, 60, 60)));

        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        profilePanel.setOpaque(false);

        teacherPhotoLabel = new JLabel("PHOTO");
        teacherPhotoLabel.setPreferredSize(new Dimension(80, 80));
        teacherPhotoLabel.setBorder(new LineBorder(buttonTextColor, 2));
        teacherPhotoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        teacherPhotoLabel.setForeground(Color.WHITE);
        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        teacherNameLabel = new JLabel("Loading...");
        teacherNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        teacherNameLabel.setForeground(Color.WHITE);
        
        teacherIdLabel = new JLabel("ID: " + teacherId);
        teacherIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        teacherIdLabel.setForeground(buttonTextColor);
        
        textPanel.add(teacherNameLabel);
        textPanel.add(teacherIdLabel);
        profilePanel.add(teacherPhotoLabel);
        profilePanel.add(textPanel);
        topBar.add(profilePanel, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        setupSidebar(teacherId);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(bgLight);
        add(contentArea, BorderLayout.CENTER);

        fetchTeacherData(teacherId);
        showDashboardStats(teacherId); 
        
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupSidebar(String tId) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(280, 0));

        String[] menuItems = {"Dashboard", "My Subject","Attendance", "Upload Video", "Watch Videos", "Live Class", "Create Test","profile","Manage Password", "Logout"};

        for (String item : menuItems) {
            JButton btn = new JButton(item.toUpperCase());
            btn.setMaximumSize(new Dimension(280, 55));
            btn.setBackground(sidebarColor);
            btn.setForeground(buttonTextColor);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setFocusPainted(false);
            btn.setBorder(new MatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btn.addActionListener(e -> {
                if (item.equalsIgnoreCase("Dashboard")) {
                    showDashboardStats(tId);
                }
                else if (item.equalsIgnoreCase("My Subject")) showMySubjects(tId);
                else if (item.equalsIgnoreCase("Attendance")) showAttendancePanel(); 
                else if (item.equalsIgnoreCase("Upload Video")) showUploadVideo(tId);
                else if (item.equalsIgnoreCase("Watch Videos")) showWatchVideos(tId);
                else if (item.equalsIgnoreCase("Live Class")) {
                    new LiveClass(tId).setVisible(true); 
                }
                else if (item.equalsIgnoreCase("Create Test")) {
                    // CreateTest constructor calling instead of main
                    new CreateTest("", false).setVisible(true);
                }
                else if (item.equalsIgnoreCase("Profile")) {
                    new TeacherProfile(tId).setVisible(true);
                }
                else if (item.equalsIgnoreCase("Manage Password")) {
                    showManagePassword(tId);
                }
                else if (item.equalsIgnoreCase("Logout")) { 
                    dispose(); 
                    new DashBoard().setVisible(true); 
                }
                else JOptionPane.showMessageDialog(this, item + " feature is coming soon!");
            });
            sidebar.add(btn);
        }
        add(sidebar, BorderLayout.WEST);
    }

    private void showAttendancePanel() {
        contentArea.removeAll();
        contentArea.add(new TeacherAttendanceView(), BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showDashboardStats(String tId) {
        contentArea.removeAll();
        contentArea.setLayout(new BorderLayout());
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 50));
        statsPanel.setOpaque(false);

        String subjectName = "None";
        int videoCount = 0;

        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps1 = con.prepareStatement("SELECT Subject FROM Teachers WHERE TeacherID=?");
            ps1.setString(1, tId);
            ResultSet rs1 = ps1.executeQuery();
            if(rs1.next()) subjectName = rs1.getString("Subject");

            PreparedStatement ps2 = con.prepareStatement("SELECT COUNT(*) FROM dbo.Videos WHERE TeacherID=?");
            ps2.setString(1, tId);
            ResultSet rs2 = ps2.executeQuery();
            if(rs2.next()) videoCount = rs2.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }

        statsPanel.add(createStatCard("MY MAIN SUBJECT", subjectName, new Color(41, 128, 185)));
        statsPanel.add(createStatCard("VIDEOS UPLOADED", String.valueOf(videoCount), new Color(39, 174, 96)));

        contentArea.add(statsPanel, BorderLayout.NORTH);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showMySubjects(String tId) {
        contentArea.removeAll();
        contentArea.setLayout(new BorderLayout());
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        filterPanel.setBackground(cardColor);
        filterPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));

        String[] branches = {"Mining", "Computer Science", "Electonice", "Electrical"};
        String[] sems = {"FIRST SEM", "SECOND SEM", "THIRD SEM", "FOURTH SEM", "FIFTH SEM", "SIXTH SEM"};

        JComboBox<String> cbBranch = new JComboBox<>(branches);
        JComboBox<String> cbSem = new JComboBox<>(sems);
        JButton btnCheck = new JButton("CHECK SCHEDULE");
        btnCheck.setBackground(buttonTextColor);
        btnCheck.setForeground(Color.BLACK);

        filterPanel.add(new JLabel("Branch:") {{ setForeground(Color.WHITE); }});
        filterPanel.add(cbBranch);
        filterPanel.add(new JLabel("Sem:") {{ setForeground(Color.WHITE); }});
        filterPanel.add(cbSem);
        filterPanel.add(btnCheck);
        contentArea.add(filterPanel, BorderLayout.NORTH);

        String[] columns = {"DAY", "TIME SLOT", "SUBJECT", "ROOM NO", "DURATION"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setBackground(cardColor);
        table.setForeground(Color.WHITE);
        table.getTableHeader().setBackground(sidebarColor);
        table.getTableHeader().setForeground(buttonTextColor);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(bgLight);
        contentArea.add(scroll, BorderLayout.CENTER);

        btnCheck.addActionListener(e -> {
            model.setRowCount(0);
            try (Connection con = DBConnection.getConnection()) {
                String subQuery = "SELECT Subject FROM Teachers WHERE TeacherID = ?";
                PreparedStatement pst1 = con.prepareStatement(subQuery);
                pst1.setString(1, tId);
                ResultSet rs1 = pst1.executeQuery();
                if (rs1.next()) {
                    String teacherSub = rs1.getString("Subject");
                    String ttQuery = "SELECT Day, TimeSlot, SubjectName, RoomNo, Duration FROM dbo.Subjects WHERE SubjectName = ? AND Branch = ? AND Semester = ?";
                    PreparedStatement pst2 = con.prepareStatement(ttQuery);
                    pst2.setString(1, teacherSub);
                    pst2.setString(2, cbBranch.getSelectedItem().toString());
                    pst2.setString(3, cbSem.getSelectedItem().toString());
                    ResultSet rs2 = pst2.executeQuery();
                    while (rs2.next()) {
                        model.addRow(new Object[]{
                            rs2.getString(1), 
                            rs2.getString(2), 
                            rs2.getString(3), 
                            rs2.getString(4), 
                            rs2.getString(5) + " Period(s)" 
                        });
                    }
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        });
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showUploadVideo(String tId) {
        contentArea.removeAll();
        contentArea.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLbl = new JLabel("UPLOAD NEW LECTURE", JLabel.CENTER);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLbl.setForeground(buttonTextColor);

        JTextField txtTitle = new JTextField(20);
        JTextField txtURL = new JTextField(20);
        JButton btnBrowse = new JButton("Browse File");
        JButton btnSave = new JButton("UPLOAD TO DATABASE");

        txtTitle.setBackground(cardColor); txtTitle.setForeground(Color.WHITE); txtTitle.setCaretColor(Color.WHITE);
        txtURL.setBackground(cardColor); txtURL.setForeground(Color.WHITE); txtURL.setEditable(false);
        btnSave.setBackground(new Color(39, 174, 96)); btnSave.setForeground(Color.WHITE);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentArea.add(titleLbl, gbc);
        gbc.gridy = 1; gbc.gridwidth = 1;
        contentArea.add(new JLabel("Title/Topic:") {{ setForeground(Color.WHITE); }}, gbc);
        gbc.gridx = 1;
        contentArea.add(txtTitle, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        contentArea.add(btnBrowse, gbc);
        gbc.gridx = 1;
        contentArea.add(txtURL, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        contentArea.add(btnSave, gbc);

        btnBrowse.addActionListener(al -> {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtURL.setText(fc.getSelectedFile().getAbsolutePath());
            }
        });

        btnSave.addActionListener(al -> {
            try (Connection con = DBConnection.getConnection()) {
                String q = "INSERT INTO dbo.Videos (TeacherID, Title, VideoURL, UploadDate) VALUES (?, ?, ?, GETDATE())";
                PreparedStatement ps = con.prepareStatement(q);
                ps.setString(1, tId);
                ps.setString(2, txtTitle.getText());
                ps.setString(3, txtURL.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Video Details Saved!");
                showDashboardStats(tId);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showWatchVideos(String tId) {
        contentArea.removeAll();
        contentArea.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10,10,10,10));

        JLabel info = new JLabel("SELECT A VIDEO AND CLICK PLAY", JLabel.CENTER);
        info.setForeground(buttonTextColor);
        info.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JButton btnPlay = new JButton("PLAY SELECTED VIDEO");
        btnPlay.setBackground(new Color(39, 174, 96));
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFocusPainted(false);

        headerPanel.add(info, BorderLayout.WEST);
        headerPanel.add(btnPlay, BorderLayout.EAST);
        contentArea.add(headerPanel, BorderLayout.NORTH);

        String[] cols = {"ID", "TITLE", "VIDEO URL", "DATE"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setBackground(cardColor); 
        table.setForeground(Color.WHITE);
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT id, Title, VideoURL, UploadDate FROM dbo.Videos WHERE TeacherID=?");
            ps.setString(1, tId);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)});
        } catch (Exception e) { e.printStackTrace(); }

        btnPlay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                String path = table.getValueAt(row, 2).toString();
                try {
                    File videoFile = new File(path);
                    if (videoFile.exists()) Desktop.getDesktop().open(videoFile);
                    else JOptionPane.showMessageDialog(this, "File not found!");
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a video first!");
            }
        });

        contentArea.add(new JScrollPane(table), BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(300, 160));
        card.setBackground(cardColor);
        card.setBorder(new LineBorder(accent, 2));
        JLabel tLbl = new JLabel(title, JLabel.CENTER);
        tLbl.setForeground(Color.LIGHT_GRAY);
        JLabel vLbl = new JLabel(value, JLabel.CENTER);
        vLbl.setForeground(Color.WHITE);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 28));
        card.add(tLbl, BorderLayout.NORTH);
        card.add(vLbl, BorderLayout.CENTER);
        return card;
    }
    
   private void showManagePassword(String tId) {
    contentArea.removeAll();
    contentArea.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(15, 15, 15, 15);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    JLabel title = new JLabel("RESET ACCOUNT PASSWORD", JLabel.CENTER);
    title.setFont(new Font("Segoe UI", Font.BOLD, 22));
    title.setForeground(buttonTextColor);

    JTextField txtVerifyId = new JTextField(20);
    JPasswordField txtNew = new JPasswordField(20);
    JPasswordField txtConfirm = new JPasswordField(20);
    JButton btnUpdate = new JButton("UPDATE PASSWORD");

    Color fieldBg = new Color(45, 48, 54);
    txtVerifyId.setBackground(fieldBg); txtVerifyId.setForeground(Color.WHITE); txtVerifyId.setCaretColor(Color.WHITE);
    txtNew.setBackground(fieldBg); txtNew.setForeground(Color.WHITE); txtNew.setCaretColor(Color.WHITE);
    txtConfirm.setBackground(fieldBg); txtConfirm.setForeground(Color.WHITE); txtConfirm.setCaretColor(Color.WHITE);
    btnUpdate.setBackground(new Color(39, 174, 96)); btnUpdate.setForeground(Color.WHITE);

    gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
    contentArea.add(title, gbc);
    
    gbc.gridwidth = 1; gbc.gridy = 1;
    contentArea.add(new JLabel("Confirm Your ID:") {{ setForeground(Color.WHITE); }}, gbc);
    gbc.gridx = 1; contentArea.add(txtVerifyId, gbc);

    gbc.gridx = 0; gbc.gridy = 2;
    contentArea.add(new JLabel("New Password:") {{ setForeground(Color.WHITE); }}, gbc);
    gbc.gridx = 1; contentArea.add(txtNew, gbc);

    gbc.gridx = 0; gbc.gridy = 3;
    contentArea.add(new JLabel("Confirm New Password:") {{ setForeground(Color.WHITE); }}, gbc);
    gbc.gridx = 1; contentArea.add(txtConfirm, gbc);

    gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
    contentArea.add(btnUpdate, gbc);

    btnUpdate.addActionListener(al -> {
        String inputId = txtVerifyId.getText().trim();
        String newP = new String(txtNew.getPassword());
        String confP = new String(txtConfirm.getPassword());

        if(inputId.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        if(!inputId.equalsIgnoreCase(tId)) {
            JOptionPane.showMessageDialog(this, "ID mismatch! You can only change password for your own ID: " + tId);
            return;
        }

        if(!newP.equals(confP)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String upQ = "UPDATE Teachers SET Password = HASHBYTES('SHA2_256', ?) WHERE TeacherID=?";
            PreparedStatement psUp = con.prepareStatement(upQ);
            psUp.setString(1, newP);
            psUp.setString(2, tId);
            
            int result = psUp.executeUpdate();
            if(result > 0) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!");
                txtVerifyId.setText(""); txtNew.setText(""); txtConfirm.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. ID not found in database.");
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    });

    contentArea.revalidate();
    contentArea.repaint();
}

    private void fetchTeacherData(String id) {
        try (Connection con = DBConnection.getConnection()) {
            PreparedStatement pst = con.prepareStatement("SELECT Name, PhotoPath FROM Teachers WHERE TeacherID = ?");
            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                teacherNameLabel.setText(rs.getString("Name").toUpperCase());
                String path = rs.getString("PhotoPath");
                if (path != null && !path.isEmpty()) {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        ImageIcon icon = new ImageIcon(path);
                        Image img = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                        teacherPhotoLabel.setIcon(new ImageIcon(img));
                        teacherPhotoLabel.setText(""); 
                    } else {
                        teacherPhotoLabel.setText("NOT FOUND");
                    }
                } else {
                    teacherPhotoLabel.setText("NO IMAGE");
                }
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
            teacherPhotoLabel.setText("ERROR");
        }
    }
}