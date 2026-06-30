package my;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.sql.*;

public class DashBoard extends JFrame {

    private JTextField userField;
    private JPasswordField passField;
    private String currentRole = "Admin";
    private String currentPlaceholder = "Enter Admin ID";

    public DashBoard() {
        setTitle("Government Polytechnic Talbehat - Login");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setLayout(new BorderLayout());

        // ---------- HEADER ----------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(46, 50, 57));
        headerPanel.setPreferredSize(new Dimension(0, 80));

        URL logoURL = getClass().getResource("/my/co.jpg");
        if (logoURL != null) {
            ImageIcon logoIcon = new ImageIcon(logoURL);
            Image scaledLogo = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
            headerPanel.add(logoLabel, BorderLayout.WEST);
        }

        JLabel titleLabel = new JLabel("Government Polytechnic Talbehat, Lalitpur", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // ---------- SIDEBAR ----------
        JPanel sidePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 25));
        sidePanel.setBackground(new Color(60, 60, 60));
        sidePanel.setPreferredSize(new Dimension(250, 0));

        String[] menuItems = {"About", "Contact Us", "Back", "Logout"};
        for (String item : menuItems) {
            JButton btn = new JButton(item);
            btn.setPreferredSize(new Dimension(210, 50));
            
            // Uniform Project Color
            btn.setBackground(new Color(46, 50, 57));
            
            btn.setForeground(Color.WHITE);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 15));

            btn.addActionListener(e -> {
                if (item.equals("About")) {
                    dispose();
                    new About();
                } else if (item.equals("Contact Us")) {
                    JOptionPane.showMessageDialog(this,
                            "📞 Principal: +91 94xxxxxx01\n📞 Admission Cell: +91 94xxxxxx02",
                            "Contact", JOptionPane.INFORMATION_MESSAGE);
                } else if (item.equals("Back")) {
                    dispose();
                    new MYe();
                } else if (item.equals("Logout")) {
                    int a = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Logout", JOptionPane.YES_NO_OPTION);
                    if (a == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                }
            });
            sidePanel.add(btn);
        }
        add(sidePanel, BorderLayout.WEST);

        // ---------- MAIN BACKGROUND ----------
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                URL bgURL = getClass().getResource("/my/collage.jpeg");
                if (bgURL != null) {
                    Image img = new ImageIcon(bgURL).getImage();
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        // ---------- LOGIN BOX ----------
        JPanel loginContainer = new JPanel();
        loginContainer.setLayout(new BoxLayout(loginContainer, BoxLayout.Y_AXIS));
        loginContainer.setOpaque(false);

        int formWidth = 460;

        JPanel tabBar = new JPanel(new GridLayout(1, 3));
        tabBar.setBackground(new Color(60, 65, 75));
        Dimension tabDim = new Dimension(formWidth, 60);
        tabBar.setPreferredSize(tabDim);
        tabBar.setMaximumSize(tabDim);

        String[] tabs = {"Admin", "Teacher", "Student"};
        for (String t : tabs) {
            JButton tb = new JButton(t);
            tb.setForeground(Color.WHITE);
            tb.setFont(new Font("Arial", Font.BOLD, 18));
            tb.setContentAreaFilled(false);
            tb.setFocusPainted(false);
            tb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(46, 50, 57)));
            tb.addActionListener(e -> updateFields(t));
            tabBar.add(tb);
        }

        JPanel glassBox = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(46, 50, 57, 195));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        glassBox.setLayout(new BoxLayout(glassBox, BoxLayout.Y_AXIS));
        glassBox.setPreferredSize(new Dimension(formWidth, 420));
        glassBox.setMaximumSize(new Dimension(formWidth, 420));
        glassBox.setOpaque(false);

        JLabel loginTitle = new JLabel("Login Form");
        loginTitle.setFont(new Font("Serif", Font.BOLD, 30));
        loginTitle.setForeground(Color.WHITE);
        loginTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        userField = new JTextField(currentPlaceholder);
        userField.setMaximumSize(new Dimension(380, 50));
        userField.setFont(new Font("Arial", Font.PLAIN, 18));
        userField.setForeground(Color.GRAY);
        userField.setBackground(new Color(46, 50, 57));
        userField.setCaretColor(Color.WHITE);
        userField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setupPlaceholder(userField, currentPlaceholder);

        passField = new JPasswordField("Enter Password");
        passField.setMaximumSize(new Dimension(380, 50));
        passField.setFont(new Font("Arial", Font.PLAIN, 18));
        passField.setForeground(Color.GRAY);
        passField.setBackground(new Color(46, 50, 57));
        passField.setCaretColor(Color.WHITE);
        passField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        passField.setEchoChar((char) 0);
        setupPassPlaceholder(passField, "Enter Password");

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(46, 50, 57));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 22));
        loginBtn.setMaximumSize(new Dimension(200, 60));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> performLogin());

        glassBox.add(Box.createRigidArea(new Dimension(0, 40)));
        glassBox.add(loginTitle);
        glassBox.add(Box.createRigidArea(new Dimension(0, 40)));
        glassBox.add(userField);
        glassBox.add(Box.createRigidArea(new Dimension(0, 20)));
        glassBox.add(passField);
        glassBox.add(Box.createRigidArea(new Dimension(0, 50)));
        glassBox.add(loginBtn);

        loginContainer.add(tabBar);
        loginContainer.add(Box.createRigidArea(new Dimension(0, 20)));
        loginContainer.add(glassBox);

        mainPanel.add(loginContainer);
        add(mainPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void performLogin() {
        String user = userField.getText();
        String pass = new String(passField.getPassword());

        if (user.equals(currentPlaceholder) || pass.equals("Enter Password")) {
            JOptionPane.showMessageDialog(this, "Please enter ID and Password");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            String sql = currentRole.equals("Admin") ?
                    "SELECT * FROM Admin WHERE Username=? AND Password=?" :
                    currentRole.equals("Teacher") ?
                            "SELECT * FROM Teachers WHERE TeacherID=? AND Password = HASHBYTES('SHA2_256', ?)" :
                            "SELECT * FROM Students WHERE RollNo=? AND Password=?";

            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass); 
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                dispose();
                if (currentRole.equals("Admin")) new AdminView().setVisible(true);
                else if (currentRole.equals("Teacher")) new Teacherview(user).setVisible(true);
                else {
                    JFrame studentFrame = new JFrame("Student Dashboard");
                    studentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    studentFrame.setSize(1200, 800);
                    studentFrame.add(new StudentView(user)); 
                    studentFrame.setLocationRelativeTo(null);
                    studentFrame.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials for " + currentRole);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    private void updateFields(String role) {
        currentRole = role;
        currentPlaceholder = role.equals("Admin") ? "Enter Admin ID"
                : role.equals("Teacher") ? "Enter Teacher ID" : "Enter Enrollment No";

        userField.setText(currentPlaceholder);
        userField.setForeground(Color.GRAY);
        passField.setText("Enter Password");
        passField.setEchoChar((char) 0);
        passField.setForeground(Color.GRAY);
    }

    private void setupPlaceholder(JTextField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(currentPlaceholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(currentPlaceholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void setupPassPlaceholder(JPasswordField field, String placeholder) {
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('●');
                    field.setForeground(Color.WHITE);
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
}