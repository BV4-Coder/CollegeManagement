package my;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.*;

public class TeacherProfile extends JFrame {
    private String tId;
    
    Color bgDark = new Color(28, 30, 34);
    Color cardBg = new Color(40, 44, 52);
    Color accentColor = new Color(102, 204, 255);

    public TeacherProfile(String teacherId) {
        this.tId = teacherId;
        
        setTitle("Official Faculty Profile | " + tId);
        setSize(550, 750); // Thoda bada size taaki sab data aa jaye
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgDark);
        setLayout(new BorderLayout());

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(33, 37, 43));
        header.setPreferredSize(new Dimension(0, 70));
        JLabel title = new JLabel("FACULTY DETAILED PROFILE");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(accentColor);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Main Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardBg);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(60, 60, 60), 1),
            new EmptyBorder(25, 30, 25, 30)
        ));

        // Photo Area
        JLabel imgLabel = new JLabel("PHOTO");
        imgLabel.setPreferredSize(new Dimension(140, 140));
        imgLabel.setMaximumSize(new Dimension(140, 140));
        imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        imgLabel.setBorder(new LineBorder(accentColor, 2));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Data Labels (Main Fields from your Image)
        JPanel detailsPanel = new JPanel(new GridLayout(8, 1, 10, 12)); // 8 Rows for main data
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel lblName = createDataLabel("Name");
        JLabel lblId = createDataLabel("Teacher ID: " + tId);
        JLabel lblBranch = createDataLabel("Branch");
        JLabel lblSub = createDataLabel("Subject");
        JLabel lblQual = createDataLabel("Qualification");
        JLabel lblExp = createDataLabel("Experience");
        JLabel lblPhone = createDataLabel("Phone");
        JLabel lblEmail = createDataLabel("Email");

        // DATABASE FETCHING
        try (Connection con = DBConnection.getConnection()) {
            // SQL Server Query (dbo prefix)
            String sql = "SELECT Name, Branch, Subject, Qualification, Experience, Phone, Email, PhotoPath " +
                         "FROM dbo.Teachers WHERE TeacherID = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, tId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblName.setText("NAME:  " + rs.getString("Name").toUpperCase());
                lblBranch.setText("BRANCH:  " + rs.getString("Branch"));
                lblSub.setText("SUBJECT:  " + rs.getString("Subject"));
                lblQual.setText("QUALIFICATION:  " + rs.getString("Qualification"));
                lblExp.setText("EXPERIENCE:  " + rs.getString("Experience") + " Years");
                lblPhone.setText("PHONE:  " + rs.getString("Phone"));
                lblEmail.setText("EMAIL:  " + rs.getString("Email").toLowerCase());

                // Photo load
                String path = rs.getString("PhotoPath");
                if (path != null && !path.isEmpty()) {
                    ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH));
                    imgLabel.setIcon(icon);
                    imgLabel.setText("");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        detailsPanel.add(lblName);
        detailsPanel.add(lblId);
        detailsPanel.add(lblBranch);
        detailsPanel.add(lblSub);
        detailsPanel.add(lblQual);
        detailsPanel.add(lblExp);
        detailsPanel.add(lblPhone);
        detailsPanel.add(lblEmail);

        card.add(imgLabel);
        card.add(detailsPanel);

        // Center Wrapper
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(card);
        add(new JScrollPane(wrapper), BorderLayout.CENTER); // Scroll pane in case of small screen

        // Close Button
        JButton closeBtn = new JButton("CLOSE PROFILE");
        closeBtn.setBackground(new Color(192, 57, 43));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setPreferredSize(new Dimension(150, 40));
        closeBtn.addActionListener(e -> dispose());
        
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10,0,10,0));
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
    }

    private JLabel createDataLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 15));
        l.setForeground(Color.WHITE);
        l.setBorder(new MatteBorder(0, 0, 1, 0, new Color(70, 70, 70))); // Underline effect
        return l;
    }
}