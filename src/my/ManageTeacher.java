package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

public class ManageTeacher extends JFrame {

    private JTextField idField, nameField, branchField, subjectField;
    private JTextField qualField, expField, phoneField, emailField, dobField, ageField;
    private JPasswordField passField; 
    private JComboBox<String> genderBox;
    private JLabel imgLabel;
    private String selectedPath = "";
    
    // --- EXACT THEME COLORS ---
    private final Color THEME_DARK = new Color(40, 44, 52);   
    private final Color FORM_BG = new Color(60, 65, 75);      
    private final Color TEXT_COLOR = Color.WHITE;             
    private final Color FIELD_BG = new Color(80, 85, 95);     
    private final Color BTN_COLOR = new Color(80, 85, 95); 

    public ManageTeacher() {
        setTitle("Teacher Administration Portal | GP Talbehat");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); 
        setLocationRelativeTo(null);
        getContentPane().setBackground(FORM_BG);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(THEME_DARK);
        header.setPreferredSize(new Dimension(0, 65));
        
        JLabel title = new JLabel("    TEACHER MANAGEMENT DATABASE", SwingConstants.LEFT);
        title.setForeground(TEXT_COLOR);
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        header.add(title, BorderLayout.CENTER);
        
        add(header, BorderLayout.NORTH);

        // --- Main Form Panel ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(FORM_BG); 
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Photo Section
        imgLabel = new JLabel("SELECT PHOTO", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(200, 230));
        imgLabel.setBorder(new LineBorder(new Color(100, 110, 120), 2));
        imgLabel.setBackground(FIELD_BG);
        imgLabel.setForeground(TEXT_COLOR);
        imgLabel.setOpaque(true);
        imgLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imgLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectImage(); }
        });
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 4;
        mainPanel.add(imgLabel, gbc);

        gbc.gridheight = 1; 

        // Fields Initialization
        addFormalField(mainPanel, "TEACHER ID*", idField = new JTextField(), 1, 0, gbc);
        nameField = new JTextField();
        addFormalField(mainPanel, "FULL NAME*", nameField, 2, 0, gbc);
        
        branchField = new JTextField();
        addFormalField(mainPanel, "BRANCH*", branchField, 1, 1, gbc);
        subjectField = new JTextField();
        addFormalField(mainPanel, "SUBJECT*", subjectField, 2, 1, gbc);
        
        addFormalField(mainPanel, "QUALIFICATION*", qualField = new JTextField(), 1, 2, gbc);
        expField = new JTextField();
        addFormalField(mainPanel, "EXPERIENCE (YEARS)*", expField, 2, 2, gbc);
        
        phoneField = new JTextField();
        addFormalField(mainPanel, "MOBILE NUMBER*", phoneField, 1, 3, gbc);
        emailField = new JTextField();
        addFormalField(mainPanel, "EMAIL ADDRESS*", emailField, 2, 3, gbc);
        
        dobField = new JTextField();
        dobField.setEditable(false);
        dobField.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                String date = new CalendarWindow(ManageTeacher.this).getPickedDate();
                if(!date.isEmpty()) { dobField.setText(date); calculateAge(); }
            }
        });
        addFormalField(mainPanel, "DOB (CLICK)*", dobField, 1, 4, gbc);
        ageField = new JTextField();
        ageField.setEditable(false);
        addFormalField(mainPanel, "AGE", ageField, 2, 4, gbc);

        String[] genders = {"Select Gender", "Male", "Female", "Other"};
        genderBox = new JComboBox<>(genders);
        genderBox.setBackground(FIELD_BG);
        genderBox.setForeground(TEXT_COLOR);
        addFormalField(mainPanel, "GENDER*", genderBox, 1, 5, gbc);
        
        passField = new JPasswordField();
        addFormalField(mainPanel, "PASSWORD*", passField, 2, 5, gbc);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        // --- Footer Buttons ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        footer.setBackground(THEME_DARK); 
        String[] btnLabels = {"SEARCH", "ADD", "UPDATE", "DELETE", "CLEAR", "BACK"};
        for (String label : btnLabels) { addBtn(footer, label, BTN_COLOR); }
        add(footer, BorderLayout.SOUTH);
        
        setVisible(true);
    }

    private void addFormalField(JPanel p, String lab, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        JPanel panel = new JPanel(new BorderLayout(0, 5)); 
        panel.setOpaque(false);
        
        JLabel l = new JLabel(lab); 
        l.setForeground(TEXT_COLOR);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        comp.setBackground(FIELD_BG);
        comp.setForeground(TEXT_COLOR);
        
        if (comp instanceof javax.swing.text.JTextComponent) {
            ((javax.swing.text.JTextComponent) comp).setCaretColor(TEXT_COLOR);
        }
        
        comp.setBorder(new LineBorder(new Color(100, 110, 120), 1));
        comp.setPreferredSize(new Dimension(320, 40));
        
        panel.add(l, BorderLayout.NORTH); 
        panel.add(comp, BorderLayout.CENTER);
        p.add(panel, gbc);
    }

    private void addBtn(JPanel p, String txt, Color c) {
        JButton b = new JButton(txt); 
        b.setPreferredSize(new Dimension(130, 45));
        b.setBackground(c); 
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            if(txt.equals("BACK")) { 
                dispose(); 
                new AdminView().setVisible(true); // AdminView call kiya gaya
            } 
            else if(txt.equals("CLEAR")) clearFields();
            else if(txt.equals("ADD")) handleSQL("INSERT"); 
            else if(txt.equals("UPDATE")) handleSQL("UPDATE");
            else if(txt.equals("SEARCH")) searchData();
            else if(txt.equals("DELETE")) deleteData();
        });
        p.add(b);
    }

    private void handleSQL(String type) {
        if(idField.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "ID required!"); return; }
        String sql = (type.equals("INSERT")) ? 
            "INSERT INTO Teachers (Name, Branch, Subject, Qualification, Experience, Phone, Email, PhotoPath, Password, DOB, Age, Gender, TeacherID) VALUES (?,?,?,?,?,?,?,?,HASHBYTES('SHA2_256', ?),?,?,?,?)" :
            "UPDATE Teachers SET Name=?, Branch=?, Subject=?, Qualification=?, Experience=?, Phone=?, Email=?, PhotoPath=?, Password=HASHBYTES('SHA2_256', ?), DOB=?, Age=?, Gender=? WHERE TeacherID=?";
        
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nameField.getText());
            pst.setString(2, branchField.getText());
            pst.setString(3, subjectField.getText());
            pst.setString(4, qualField.getText());
            pst.setString(5, expField.getText());
            pst.setString(6, phoneField.getText());
            pst.setString(7, emailField.getText());
            pst.setString(8, selectedPath);
            pst.setString(9, new String(passField.getPassword()));
            pst.setString(10, dobField.getText());
            pst.setString(11, ageField.getText());
            pst.setString(12, genderBox.getSelectedItem().toString());
            pst.setString(13, idField.getText());
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Success!");
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private void searchData() {
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement pst = con.prepareStatement("SELECT * FROM Teachers WHERE TeacherID=?")) {
            pst.setString(1, idField.getText());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("Name")); 
                branchField.setText(rs.getString("Branch"));
                subjectField.setText(rs.getString("Subject")); 
                qualField.setText(rs.getString("Qualification"));
                expField.setText(rs.getString("Experience")); 
                phoneField.setText(rs.getString("Phone"));
                emailField.setText(rs.getString("Email")); 
                dobField.setText(rs.getString("DOB"));
                ageField.setText(rs.getString("Age")); 
                genderBox.setSelectedItem(rs.getString("Gender"));
                selectedPath = rs.getString("PhotoPath");
                displayImage(selectedPath);
            } else { JOptionPane.showMessageDialog(this, "Not Found!"); }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void deleteData() {
        if(JOptionPane.showConfirmDialog(this, "Delete?", "Confirm", 0) == 0) {
            try (Connection con = DBConnection.getConnection(); 
                 PreparedStatement pst = con.prepareStatement("DELETE FROM Teachers WHERE TeacherID=?")) {
                pst.setString(1, idField.getText()); pst.executeUpdate(); clearFields();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private void calculateAge() {
        try {
            LocalDate dob = LocalDate.parse(dobField.getText().trim());
            ageField.setText(String.valueOf(Period.between(dob, LocalDate.now()).getYears()));
        } catch (Exception ex) { ageField.setText(""); }
    }

    private void selectImage() {
        JFileChooser ch = new JFileChooser();
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedPath = ch.getSelectedFile().getAbsolutePath();
            displayImage(selectedPath);
        }
    }

    private void displayImage(String path) {
        if (path != null && !path.isEmpty()) {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(200, 230, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(img)); imgLabel.setText("");
        }
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); branchField.setText(""); subjectField.setText("");
        qualField.setText(""); expField.setText(""); phoneField.setText(""); emailField.setText("");
        dobField.setText(""); ageField.setText(""); passField.setText("");
        genderBox.setSelectedIndex(0); imgLabel.setIcon(null); imgLabel.setText("SELECT PHOTO");
    }

    class CalendarWindow {
        private int month, year;
        private String daySelected = "";
        private JDialog d;
        private JButton[] button = new JButton[42];
        public CalendarWindow(JFrame parent) {
            d = new JDialog(); d.setModal(true); d.setLayout(new BorderLayout());
            Calendar cal = Calendar.getInstance(); month = cal.get(Calendar.MONTH); year = cal.get(Calendar.YEAR);
            JPanel p1 = new JPanel(new GridLayout(7, 7));
            for (int x = 0; x < button.length; x++) {
                final int selection = x; button[x] = new JButton();
                if (x > 6) button[x].addActionListener(ae -> { daySelected = button[selection].getActionCommand(); d.dispose(); });
                p1.add(button[x]);
            }
            d.add(p1, BorderLayout.CENTER);
            displayDate(); d.pack(); d.setLocationRelativeTo(parent); d.setVisible(true);
        }
        public void displayDate() {
            Calendar cal = Calendar.getInstance(); cal.set(year, month, 1);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int x = 6 + dayOfWeek, dCount = 1; dCount <= daysInMonth; x++, dCount++) {
                button[x].setText("" + dCount); button[x].setActionCommand("" + dCount);
            }
        }
        public String getPickedDate() {
            if (daySelected.equals("")) return "";
            return String.format("%04d-%02d-%02d", year, month + 1, Integer.parseInt(daySelected));
        }
    }
}