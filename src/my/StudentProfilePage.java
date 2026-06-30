package my;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class StudentProfilePage extends JFrame {
    private String rollNo;
    private JLabel lblPhoto;
    private JPanel infoPanel;

    // --- FORMAL COLORS ---
    private final Color PRIMARY_RED = new Color(180, 40, 40);
    private final Color BG_COLOR = new Color(18, 18, 18);
    private final Color TEXT_MAIN = Color.WHITE;
    private final Color TEXT_LABEL = new Color(170, 170, 170);

    public StudentProfilePage(String rollNo) {
        this.rollNo = rollNo;
        setTitle("Student Official Profile | Government Polytechnic");
        setSize(650, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // --- 1. Header (Formal Banner) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_RED);
        headerPanel.setPreferredSize(new Dimension(0, 70));
        
        JLabel headerTitle = new JLabel("STUDENT PROFILE CARD", SwingConstants.CENTER);
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerTitle.setForeground(Color.WHITE);
        headerPanel.add(headerTitle, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Main Body (Scrollable) ---
        JPanel mainBody = new JPanel();
        mainBody.setLayout(new BoxLayout(mainBody, BoxLayout.Y_AXIS));
        mainBody.setOpaque(false);
        mainBody.setBorder(new EmptyBorder(30, 60, 30, 60));

        // Photo Section with Border
        lblPhoto = new JLabel("NO PHOTO", SwingConstants.CENTER);
        lblPhoto.setPreferredSize(new Dimension(140, 140));
        lblPhoto.setMaximumSize(new Dimension(140, 140));
        lblPhoto.setBorder(BorderFactory.createLineBorder(PRIMARY_RED, 2));
        lblPhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPhoto.setForeground(TEXT_LABEL);
        mainBody.add(lblPhoto);
        
        mainBody.add(Box.createRigidArea(new Dimension(0, 30)));

        // Info Container
        infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Professional alignment ke liye addProfileLine function
        addProfileLine("Full Name");
        addProfileLine("Roll Number");
        addProfileLine("Father's Name");
        addProfileLine("Mother's Name");
        addProfileLine("Gender");
        addProfileLine("Date of Birth");
        addProfileLine("Mobile No");
        addProfileLine("Email ID");
        addProfileLine("Branch");
        addProfileLine("Semester");
        addProfileLine("Current Year");
        addProfileLine("Admission Year");
        addProfileLine("Qualification");
        addProfileLine("Address");

        mainBody.add(infoPanel);

        JScrollPane sp = new JScrollPane(mainBody);
        sp.setBorder(null);
        sp.getViewport().setBackground(BG_COLOR);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        // --- 3. Footer Button ---
        JButton btnClose = new JButton("CLOSE PROFILE");
        btnClose.setBackground(new Color(45, 45, 45));
        btnClose.setForeground(Color.WHITE);
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnClose.setPreferredSize(new Dimension(0, 50));
        btnClose.setFocusPainted(false);
        btnClose.setBorder(null);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        add(btnClose, BorderLayout.SOUTH);

        loadData();
    }

    private void addProfileLine(String title) {
        JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(600, 40));

        // Label (Fixed width for alignment)
        JLabel t = new JLabel(title);
        t.setPreferredSize(new Dimension(160, 30)); // Isse saare ':-' ek line mein aayenge
        t.setForeground(TEXT_LABEL);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel separator = new JLabel(" :-   ");
        separator.setForeground(PRIMARY_RED);
        separator.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Value
        JLabel v = new JLabel("Loading...");
        v.setName(title);
        v.setForeground(TEXT_MAIN);
        v.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        line.add(t);
        line.add(separator);
        line.add(v);

        infoPanel.add(line);
        // Border line for formal look
        infoPanel.add(new JSeparator(JSeparator.HORIZONTAL)); 
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void loadData() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM dbo.Students WHERE RollNo = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, rollNo);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                updateValue("Full Name", rs.getString("Name"));
                updateValue("Roll Number", rs.getString("RollNo"));
                updateValue("Father's Name", rs.getString("FatherName"));
                updateValue("Mother's Name", rs.getString("MotherName"));
                updateValue("Gender", rs.getString("Gender"));
                updateValue("Date of Birth", rs.getString("DOB"));
                updateValue("Mobile No", rs.getString("Mobile"));
                updateValue("Email ID", rs.getString("Email"));
                updateValue("Branch", rs.getString("Branch"));
                updateValue("Semester", rs.getString("Semester"));
                updateValue("Current Year", rs.getString("Year"));
                updateValue("Admission Year", rs.getString("AdminYear"));
                updateValue("Qualification", rs.getString("Qualification"));
                updateValue("Address", rs.getString("Address"));

                String path = rs.getString("PhotoPath");
                if (path != null && !path.isEmpty()) {
                    ImageIcon icon = new ImageIcon(path);
                    Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                    lblPhoto.setIcon(new ImageIcon(img));
                    lblPhoto.setText("");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateValue(String name, String value) {
        for (Component line : infoPanel.getComponents()) {
            if (line instanceof JPanel) {
                for (Component c : ((JPanel) line).getComponents()) {
                    if (c instanceof JLabel && name.equals(c.getName())) {
                        ((JLabel) c).setText(value != null ? value : "N/A");
                    }
                }
            }
        }
    }
}