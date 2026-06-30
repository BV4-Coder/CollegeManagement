package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Calendar;

public class ManageStudent extends JFrame {

    private JTextField rollField, nameField, fatherField, motherField, mobileField, emailField, ageField, adminYearField;
    private JPasswordField passwordField; 
    private JTextArea addressArea;
    private JComboBox<String> branchBox, yearBox, qualBox, dayBox, monthBox, birthYearBox, semBox;
    private JRadioButton maleBtn, femaleBtn;
    private JLabel imgLabel;
    private String selectedPath = ""; 
    
    // --- SHARED THEME COLORS ---
    private final Color THEME_DARK = new Color(40, 44, 52);   
    private final Color FORM_BG = new Color(60, 65, 75);      
    private final Color TEXT_WHITE = Color.WHITE;             
    private final Color FIELD_BG = new Color(80, 85, 95);     
    private final Color BTN_COLOR = new Color(80, 85, 95); 

    public ManageStudent() {
        setTitle("Student Admin Portal | GP Talbehat");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setLocationRelativeTo(null);
        getContentPane().setBackground(FORM_BG);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_DARK);
        headerPanel.setPreferredSize(new Dimension(0, 65));
        JLabel headerLabel = new JLabel("    STUDENT DATABASE MANAGEMENT SYSTEM");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        headerLabel.setForeground(TEXT_WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Container ---
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(FORM_BG);
        container.setBorder(new EmptyBorder(20, 30, 20, 30));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 15, 8, 15);
        g.fill = GridBagConstraints.BOTH;

        // LEFT: Photo Section
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setOpaque(false);
        imgLabel = new JLabel("SELECT PHOTO", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(200, 230));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(FIELD_BG);
        imgLabel.setForeground(Color.LIGHT_GRAY);
        imgLabel.setBorder(new LineBorder(new Color(100, 110, 120), 1));
        imgLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imgLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectImage(); }
        });
        photoPanel.add(imgLabel, BorderLayout.NORTH);
        g.gridx = 0; g.gridy = 0; g.weightx = 0.1; container.add(photoPanel, g);

        // RIGHT: Form Section
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints fc = new GridBagConstraints();
        fc.insets = new Insets(8, 12, 8, 12);
        fc.fill = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;

        addFormalField(formPanel, "Enroll No (Roll No)*", rollField = new JTextField(), 0, 0, fc);
        nameField = new JTextField();
        setInputRestriction(nameField, "[a-zA-Z\\s]*", 50); 
        addFormalField(formPanel, "Full Name*", nameField, 1, 0, fc);
        
        fatherField = new JTextField();
        setInputRestriction(fatherField, "[a-zA-Z\\s]*", 50);
        addFormalField(formPanel, "Father's Name*", fatherField, 0, 1, fc);
        motherField = new JTextField();
        setInputRestriction(motherField, "[a-zA-Z\\s]*", 50);
        addFormalField(formPanel, "Mother's Name*", motherField, 1, 1, fc);

        addFormalField(formPanel, "Date of Birth*", createDobPanel(), 0, 2, fc);
        addFormalField(formPanel, "Calculated Age", ageField = new JTextField(), 1, 2, fc);
        ageField.setEditable(false);

        mobileField = new JTextField();
        setInputRestriction(mobileField, "[0-9]*", 10);
        addFormalField(formPanel, "Mobile No (10 Digits)*", mobileField, 0, 3, fc);
        addFormalField(formPanel, "Email ID*", emailField = new JTextField(), 1, 3, fc);

        JPanel genP = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        genP.setOpaque(false);
        maleBtn = new JRadioButton("Male", true); femaleBtn = new JRadioButton("Female");
        maleBtn.setForeground(TEXT_WHITE); femaleBtn.setForeground(TEXT_WHITE);
        maleBtn.setOpaque(false); femaleBtn.setOpaque(false);
        ButtonGroup bgGroup = new ButtonGroup(); bgGroup.add(maleBtn); bgGroup.add(femaleBtn);
        genP.add(maleBtn); genP.add(femaleBtn);
        addFormalField(formPanel, "Gender*", genP, 0, 4, fc);
        
        adminYearField = new JTextField();
        setInputRestriction(adminYearField, "[0-9]*", 4);
        addFormalField(formPanel, "Admission Year*", adminYearField, 1, 4, fc);

        branchBox = createStyledCombo(new String[]{"Computer Science", "Mining", "Electronics", "Electrical"});
        qualBox = createStyledCombo(new String[]{"10th", "12th", "Diploma"});
        yearBox = createStyledCombo(new String[]{"1st Year", "2nd Year", "3rd Year"});
        semBox = createStyledCombo(new String[]{"1st Sem", "2nd Sem", "3rd Sem", "4th Sem", "5th Sem", "6th Sem"});
        
        JPanel dropPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        dropPanel.setOpaque(false); 
        dropPanel.add(branchBox); dropPanel.add(qualBox); dropPanel.add(yearBox); dropPanel.add(semBox);
        fc.gridwidth = 2; addFormalField(formPanel, "Academic Details*", dropPanel, 0, 5, fc);

        fc.gridwidth = 1;
        passwordField = new JPasswordField();
        addFormalField(formPanel, "Account Password*", passwordField, 0, 6, fc);

        addressArea = new JTextArea(2, 20); addressArea.setLineWrap(true);
        addFormalField(formPanel, "Residential Address*", new JScrollPane(addressArea), 1, 6, fc);

        g.gridx = 1; g.weightx = 0.9; container.add(formPanel, g);
        
        JScrollPane mainScroll = new JScrollPane(container);
        mainScroll.setBorder(null);
        add(mainScroll, BorderLayout.CENTER);

        // FOOTER: Buttons
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footer.setBackground(THEME_DARK);
        String[] bts = {"SEARCH", "ADD", "UPDATE", "DELETE", "CLEAR", "BACK"};
        for(String t : bts) {
            JButton b = new JButton(t);
            b.setPreferredSize(new Dimension(130, 45));
            b.setBackground(BTN_COLOR);
            b.setForeground(TEXT_WHITE);
            b.setOpaque(true); b.setContentAreaFilled(true);
            b.setFocusPainted(false); b.setBorderPainted(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addActionListener(e -> handleAction(t));
            footer.add(b);
        }
        add(footer, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void addFormalField(JPanel p, String lab, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        JPanel panel = new JPanel(new BorderLayout(0, 5)); panel.setOpaque(false);
        JLabel l = new JLabel(lab); l.setForeground(TEXT_WHITE); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        comp.setBackground(FIELD_BG); 
        comp.setForeground(TEXT_WHITE); 
        
        Border line = new LineBorder(new Color(100, 110, 120), 1);
        Border pad = new EmptyBorder(0, 12, 0, 12);
        
        if (comp instanceof JTextField || comp instanceof JPasswordField) {
            comp.setBorder(new CompoundBorder(line, pad));
            comp.setPreferredSize(new Dimension(280, 40));
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            if(comp instanceof JTextField) ((JTextField)comp).setCaretColor(Color.WHITE);
            else ((JPasswordField)comp).setCaretColor(Color.WHITE);
        } else if (comp instanceof JScrollPane) {
            comp.setBorder(line);
            addressArea.setBorder(new EmptyBorder(5, 10, 5, 10));
            addressArea.setBackground(FIELD_BG);
            addressArea.setForeground(TEXT_WHITE);
            addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            addressArea.setCaretColor(Color.WHITE);
            comp.setPreferredSize(new Dimension(280, 80));
        } else if (comp instanceof JComboBox) {
            comp.setBorder(line);
        }
        
        panel.add(l, BorderLayout.NORTH); 
        panel.add(comp, BorderLayout.CENTER);
        p.add(panel, gbc);
    }

    private JComboBox<String> createStyledCombo(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setBackground(FIELD_BG);
        combo.setForeground(TEXT_WHITE);
        combo.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                BasicArrowButton bab = new BasicArrowButton(BasicArrowButton.SOUTH, FIELD_BG, Color.WHITE, Color.WHITE, FIELD_BG);
                bab.setBorder(null);
                return bab;
            }
        });
        return combo;
    }

    private void handleAction(String type) {
        // --- ONLY UPDATED THIS PART FOR BACK BUTTON ---
        if (type.equals("BACK")) { 
            dispose(); 
            new AdminView().setVisible(true); // Isse AdminView open ho jayega
            return; 
        }
        if (type.equals("CLEAR")) { clear(); return; }
        
        String roll = rollField.getText().trim();
        if (roll.isEmpty() && !type.equals("CLEAR")) { 
            JOptionPane.showMessageDialog(this, "Roll No zaroori hai!"); return; 
        }

        try (Connection con = DBConnection.getConnection()) {
            if (type.equals("SEARCH")) {
                PreparedStatement pst = con.prepareStatement("SELECT * FROM Students WHERE RollNo=?");
                pst.setString(1, roll);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("Name"));
                    fatherField.setText(rs.getString("FatherName"));
                    motherField.setText(rs.getString("MotherName"));
                    ageField.setText(rs.getString("Age"));
                    mobileField.setText(rs.getString("Mobile"));
                    emailField.setText(rs.getString("Email"));
                    adminYearField.setText(rs.getString("AdminYear"));
                    addressArea.setText(rs.getString("Address"));
                    branchBox.setSelectedItem(rs.getString("Branch"));
                    qualBox.setSelectedItem(rs.getString("Qualification"));
                    yearBox.setSelectedItem(rs.getString("Year"));
                    semBox.setSelectedItem(rs.getString("Semester"));
                    passwordField.setText(rs.getString("Password"));
                    if("Male".equalsIgnoreCase(rs.getString("Gender"))) maleBtn.setSelected(true); else femaleBtn.setSelected(true);
                    selectedPath = rs.getString("PhotoPath");
                    if(selectedPath != null && !selectedPath.isEmpty()) {
                        imgLabel.setIcon(new ImageIcon(new ImageIcon(selectedPath).getImage().getScaledInstance(200, 230, Image.SCALE_SMOOTH)));
                        imgLabel.setText("");
                    }
                } else { JOptionPane.showMessageDialog(this, "Record Not Found!"); }
            }
            else if (type.equals("ADD")) {
                if(!validateInputs()) return;
                String q = "INSERT INTO Students (RollNo, Name, FatherName, MotherName, Gender, Age, Mobile, Email, AdminYear, Branch, Qualification, Year, Semester, Address, PhotoPath, Password) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement pst = con.prepareStatement(q);
                pst.setString(1, roll);
                pst.setString(2, nameField.getText());
                pst.setString(3, fatherField.getText());
                pst.setString(4, motherField.getText());
                pst.setString(5, maleBtn.isSelected() ? "Male" : "Female");
                pst.setString(6, ageField.getText());
                pst.setString(7, mobileField.getText());
                pst.setString(8, emailField.getText());
                pst.setString(9, adminYearField.getText());
                pst.setString(10, branchBox.getSelectedItem().toString());
                pst.setString(11, qualBox.getSelectedItem().toString());
                pst.setString(12, yearBox.getSelectedItem().toString());
                pst.setString(13, semBox.getSelectedItem().toString());
                pst.setString(14, addressArea.getText());
                pst.setString(15, selectedPath);
                pst.setString(16, new String(passwordField.getPassword()));
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student Added!");
            }
            else if (type.equals("DELETE")) {
                if(JOptionPane.showConfirmDialog(this, "Delete confirm?", "Confirm", 0) == 0) {
                    PreparedStatement pst = con.prepareStatement("DELETE FROM Students WHERE RollNo=?");
                    pst.setString(1, roll);
                    pst.executeUpdate(); clear();
                    JOptionPane.showMessageDialog(this, "Deleted!");
                }
            }
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
    }

    private boolean validateInputs() {
        if(nameField.getText().isEmpty() || rollField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Roll No aur Name bharna zaroori hai!"); return false;
        }
        return true;
    }

    private void setInputRestriction(JTextField field, String regex, int limit) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String next = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (next.matches(regex) && next.length() <= limit) super.replace(fb, offset, length, text, attrs);
            }
        });
    }

    private JPanel createDobPanel() {
        JPanel p = new JPanel(new GridLayout(1, 3, 5, 0)); p.setOpaque(false);
        String[] days = new String[31]; for(int i=0; i<31; i++) days[i] = String.valueOf(i+1);
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String[] years = new String[40]; int currY = Calendar.getInstance().get(Calendar.YEAR);
        for(int i=0; i<40; i++) years[i] = String.valueOf(currY - 15 - i);
        dayBox = createStyledCombo(days); monthBox = createStyledCombo(months); birthYearBox = createStyledCombo(years);
        ActionListener ac = e -> calculateAge();
        dayBox.addActionListener(ac); monthBox.addActionListener(ac); birthYearBox.addActionListener(ac);
        p.add(dayBox); p.add(monthBox); p.add(birthYearBox);
        return p;
    }

    private void calculateAge() {
        try {
            int bYear = Integer.parseInt((String) birthYearBox.getSelectedItem());
            ageField.setText(String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - bYear));
        } catch (Exception e) {}
    }

    private void selectImage() {
        JFileChooser ch = new JFileChooser();
        if (ch.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedPath = ch.getSelectedFile().getAbsolutePath();
            imgLabel.setIcon(new ImageIcon(new ImageIcon(selectedPath).getImage().getScaledInstance(200, 230, Image.SCALE_SMOOTH)));
            imgLabel.setText("");
        }
    }

    private void clear() {
        rollField.setText(""); nameField.setText(""); fatherField.setText(""); motherField.setText("");
        mobileField.setText(""); emailField.setText(""); adminYearField.setText(""); addressArea.setText("");
        passwordField.setText(""); imgLabel.setIcon(null); imgLabel.setText("SELECT PHOTO"); selectedPath = "";
    }
}