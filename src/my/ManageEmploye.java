package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageEmploye extends JFrame {

    private JTextField idField, nameField, ageField, aadharField, mobileField, salaryField;
    private JTextArea addressArea;
    private JRadioButton maleBtn, femaleBtn;
    private JLabel imgLabel;
    private String selectedPath = "";

    // --- THEME COLORS ---
    private final Color THEME_DARK = new Color(40, 44, 52);   
    private final Color FORM_BG = new Color(60, 65, 75);      
    private final Color TEXT_WHITE = Color.WHITE;             
    private final Color FIELD_BG = new Color(80, 85, 95);     
    private final Color BTN_COLOR = new Color(80, 85, 95); 

    public ManageEmploye() {
        setTitle("Employee Registry | GP Talbehat");
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
        
        JLabel headerLabel = new JLabel("    STAFF & FACULTY DATABASE MANAGEMENT");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        headerLabel.setForeground(TEXT_WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Form Panel ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(FORM_BG);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 15, 12, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Photo Section
        imgLabel = new JLabel("UPLOAD PHOTO", SwingConstants.CENTER);
        imgLabel.setPreferredSize(new Dimension(200, 230));
        imgLabel.setBorder(new LineBorder(new Color(100, 110, 120), 2));
        imgLabel.setOpaque(true);
        imgLabel.setBackground(FIELD_BG);
        imgLabel.setForeground(TEXT_WHITE);
        imgLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        imgLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selectImage(); }
        });
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 4;
        mainPanel.add(imgLabel, gbc);
        gbc.gridheight = 1;

        // --- Fields ---
        addComp(mainPanel, "EMPLOYEE ID*", idField = new JTextField(), 1, 0, gbc);
        nameField = new JTextField();
        setInputRestriction(nameField, "[a-zA-Z ]*", 50); 
        addComp(mainPanel, "FULL NAME*", nameField, 2, 0, gbc);
        
        ageField = new JTextField();
        setInputRestriction(ageField, "[0-9]*", 2);
        addComp(mainPanel, "AGE*", ageField, 1, 1, gbc);

        // Gender Panel
        JPanel genP = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)); 
        genP.setOpaque(false);
        maleBtn = new JRadioButton("Male", true); 
        femaleBtn = new JRadioButton("Female");
        maleBtn.setForeground(TEXT_WHITE); femaleBtn.setForeground(TEXT_WHITE);
        maleBtn.setOpaque(false); femaleBtn.setOpaque(false);
        ButtonGroup bgGroup = new ButtonGroup(); bgGroup.add(maleBtn); bgGroup.add(femaleBtn);
        genP.add(maleBtn); genP.add(femaleBtn);
        addComp(mainPanel, "GENDER*", genP, 2, 1, gbc);

        aadharField = new JTextField();
        setInputRestriction(aadharField, "[0-9]*", 12);
        addComp(mainPanel, "AADHAR NUMBER (12 Digits)*", aadharField, 1, 2, gbc);

        mobileField = new JTextField();
        setInputRestriction(mobileField, "[0-9]*", 10);
        addComp(mainPanel, "MOBILE NO (10 Digits)*", mobileField, 2, 2, gbc);

        salaryField = new JTextField();
        setInputRestriction(salaryField, "[0-9]*", 8);
        addComp(mainPanel, "MONTHLY SALARY (₹)*", salaryField, 1, 3, gbc);

        // Address Area (Updated with JScrollPane)
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 2;
        addComp(mainPanel, "RESIDENTIAL ADDRESS*", new JScrollPane(addressArea), 1, 4, gbc);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // --- Button Panel ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        pnlButtons.setBackground(THEME_DARK);
        String[] bts = {"SEARCH", "ADD", "UPDATE", "DELETE", "CLEAR", "BACK"};
        for(String t : bts) {
            JButton b = new JButton(t);
            b.setPreferredSize(new Dimension(130, 45));
            b.setBackground(BTN_COLOR); 
            b.setForeground(TEXT_WHITE);
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setFocusPainted(false); 
            b.setBorderPainted(false);
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            b.addActionListener(e -> handleAction(t));
            pnlButtons.add(b);
        }
        add(pnlButtons, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addComp(JPanel p, String lab, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        JPanel panel = new JPanel(new BorderLayout(0, 5)); 
        panel.setOpaque(false);
        
        JLabel l = new JLabel(lab); 
        l.setForeground(TEXT_WHITE); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        Border line = new LineBorder(new Color(100, 110, 120), 1);
        Border pad = new EmptyBorder(0, 12, 0, 12); 
        
        comp.setBackground(FIELD_BG); 
        comp.setForeground(TEXT_WHITE);

        if (comp instanceof JTextField) {
            comp.setBorder(new CompoundBorder(line, pad));
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            ((JTextField)comp).setCaretColor(Color.WHITE);
            comp.setPreferredSize(new Dimension(320, 40)); 
        } 
        else if (comp instanceof JScrollPane) {
            addressArea.setBackground(FIELD_BG);
            addressArea.setForeground(TEXT_WHITE);
            addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            addressArea.setCaretColor(Color.WHITE);
            addressArea.setBorder(new EmptyBorder(8, 12, 8, 12)); 
            
            comp.setPreferredSize(new Dimension(670, 80));
            ((JScrollPane)comp).setBorder(line);
            ((JScrollPane)comp).getViewport().setBackground(FIELD_BG);
        }
        
        panel.add(l, BorderLayout.NORTH); 
        panel.add(comp, BorderLayout.CENTER);
        p.add(panel, gbc);
    }

    private void handleAction(String type) {
        if (type.equals("BACK")) { 
            dispose(); 
            new AdminView().setVisible(true); 
            return; 
        }
        if (type.equals("CLEAR")) { clear(); return; }

        try (Connection con = DBConnection.getConnection()) {
            if (type.equals("ADD") || type.equals("UPDATE")) {
                if (idField.getText().isEmpty() || nameField.getText().isEmpty()) { msg("ID aur Name bhariye!"); return; }
                String sql = type.equals("ADD") ? 
                    "INSERT INTO Employees (Name, Age, Aadhar, Mobile, Salary, Gender, Address, PhotoPath, EmpID) VALUES (?,?,?,?,?,?,?,?,?)" :
                    "UPDATE Employees SET Name=?, Age=?, Aadhar=?, Mobile=?, Salary=?, Gender=?, Address=?, PhotoPath=? WHERE EmpID=?";
                
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, ageField.getText());
                pst.setString(3, aadharField.getText());
                pst.setString(4, mobileField.getText());
                pst.setString(5, salaryField.getText());
                pst.setString(6, maleBtn.isSelected() ? "Male" : "Female");
                pst.setString(7, addressArea.getText());
                pst.setString(8, selectedPath);
                pst.setString(9, idField.getText());
                pst.executeUpdate();
                msg("Data " + type + "ed!");
            } 
            else if (type.equals("SEARCH")) {
                PreparedStatement pst = con.prepareStatement("SELECT * FROM Employees WHERE EmpID=?");
                pst.setString(1, idField.getText());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("Name")); ageField.setText(rs.getString("Age"));
                    aadharField.setText(rs.getString("Aadhar")); mobileField.setText(rs.getString("Mobile"));
                    salaryField.setText(rs.getString("Salary")); addressArea.setText(rs.getString("Address"));
                    if("Male".equals(rs.getString("Gender"))) maleBtn.setSelected(true); else femaleBtn.setSelected(true);
                    selectedPath = rs.getString("PhotoPath"); displayImage(selectedPath);
                } else { msg("Employee record not found!"); }
            }
            else if (type.equals("DELETE")) {
                if(JOptionPane.showConfirmDialog(this, "Confirm Delete?", "Delete", 0) == 0) {
                    PreparedStatement pst = con.prepareStatement("DELETE FROM Employees WHERE EmpID=?");
                    pst.setString(1, idField.getText());
                    pst.executeUpdate(); clear(); msg("Deleted!");
                }
            }
        } catch (Exception ex) { msg("Database Error: " + ex.getMessage()); }
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
            imgLabel.setIcon(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(200, 230, Image.SCALE_SMOOTH)));
            imgLabel.setText("");
        }
    }

    private void clear() {
        idField.setText(""); nameField.setText(""); aadharField.setText("");
        mobileField.setText(""); salaryField.setText(""); addressArea.setText("");
        ageField.setText(""); imgLabel.setIcon(null); imgLabel.setText("UPLOAD PHOTO");
        selectedPath = "";
    }

    private void setInputRestriction(JTextField field, String regex, int limit) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String nextText = fb.getDocument().getText(0, fb.getDocument().getLength()) + text;
                if (nextText.matches(regex) && nextText.length() <= limit) super.replace(fb, offset, length, text, attrs);
            }
        });
    }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
}