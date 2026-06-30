package my;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class AdminView extends JFrame {

    // --- Color Palette ---
    private final Color SIDEBAR_BG = new Color(46, 50, 57);   
    private final Color BUTTON_BG = new Color(60, 65, 75);    
    private final Color HOVER_COLOR = new Color(80, 85, 95);  
    private final Color CARD_DARK = new Color(46, 50, 57);    
    private final Color TEXT_WHITE = Color.WHITE;

    public AdminView() {
        setTitle("Admin Dashboard - Government Polytechnic Talbehat");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); 
        setLayout(new BorderLayout());

        // --- 1. Top Bar ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(33, 37, 43)); 
        topBar.setPreferredSize(new Dimension(0, 60));
        
        JLabel welcomeLabel = new JLabel("    Admin Control Panel");
        welcomeLabel.setForeground(TEXT_WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        topBar.add(welcomeLabel, BorderLayout.WEST);
        
        // Close Button for Undecorated Frame
        JButton btnExit = new JButton("X");
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(180, 40, 40));
        btnExit.setFocusPainted(false);
        btnExit.setBorderPainted(false);
        btnExit.addActionListener(e -> System.exit(0));
        topBar.add(btnExit, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // --- 2. Sidebar ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG); 
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] navItems = {"Dashboard", "Manage Teachers", "Manage Employe", "Manage Course", "Manage Students", "Manage Sub","Notice Board", "Logout"};
        
        for (String item : navItems) {
            JButton navBtn = new JButton(item);
            navBtn.setMaximumSize(new Dimension(230, 45)); 
            navBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            navBtn.setBackground(BUTTON_BG); 
            navBtn.setForeground(TEXT_WHITE);
            navBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            navBtn.setFocusPainted(false);
            navBtn.setBorderPainted(false); 
            navBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            navBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) { navBtn.setBackground(HOVER_COLOR); }
                public void mouseExited(java.awt.event.MouseEvent evt) { navBtn.setBackground(BUTTON_BG); }
            });

            navBtn.addActionListener(e -> {
                String choice = item.toLowerCase();
                
                if(choice.equals("logout") || choice.equals("dashboard")) {
                    int a = choice.equals("logout") ? JOptionPane.showConfirmDialog(this, "Are you sure?", "Logout", JOptionPane.YES_NO_OPTION) : JOptionPane.YES_OPTION;
                    if (a == JOptionPane.YES_OPTION) {
                        dispose();
                        new DashBoard().setVisible(true); 
                    }
                } else {
                    dispose(); 
                    if(choice.equals("manage students")) new ManageStudent().setVisible(true);
                    else if(choice.equals("manage employe")) new ManageEmploye().setVisible(true);
                    else if(choice.equals("manage teachers")) new ManageTeacher().setVisible(true);
                    else if(choice.equals("manage course")) new ManageBranch().setVisible(true);
                    else if(choice.equals("manage sub")) new AdminSubjectControl().setVisible(true);
                    else if(choice.equals("notice board")) new latestUPdate().setVisible(true);
                }
            });
            
            sidebar.add(navBtn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10))); 
        }
        add(sidebar, BorderLayout.WEST);

        // --- 3. Content Area ---
        JPanel contentArea = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 25));
        contentArea.setBackground(BUTTON_BG); 
        contentArea.setBorder(new EmptyBorder(20, 20, 20, 20));

        contentArea.add(createStatCard("Total Students", getCount("Students"), CARD_DARK));
        contentArea.add(createStatCard("Total Teachers", getCount("Teachers"), CARD_DARK));
        contentArea.add(createStatCard("Total Employees", getCount("Employees"), CARD_DARK));
        contentArea.add(createStatCard("Total Branches", getCount("Branch"), CARD_DARK));
        contentArea.add(createStatCard("Live Notices", getCount("Notices"), CARD_DARK));

        add(contentArea, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String getCount(String tableName) {
        int total = 0;
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) return "0";
            ResultSet rs = con.createStatement().executeQuery("SELECT COUNT(*) AS total FROM " + tableName);
            if (rs.next()) total = rs.getInt("total");
        } catch (Exception e) { return "0"; }
        return String.valueOf(total);
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setPreferredSize(new Dimension(210, 120)); 
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setForeground(TEXT_WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(TEXT_WHITE);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 35));

        card.add(lblTitle); card.add(lblValue);
        return card;
    }
}