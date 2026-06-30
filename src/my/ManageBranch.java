package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageBranch extends JFrame {

    private JTextField codeField, nameField, hodField, seatsField, durationField;
    private JTextArea descArea;
    
    // --- SHARED THEME COLORS (Synchronized with all pages) ---
    private final Color THEME_DARK = new Color(40, 44, 52);   
    private final Color FORM_BG = new Color(60, 65, 75);      
    private final Color TEXT_WHITE = Color.WHITE;             
    private final Color FIELD_BG = new Color(80, 85, 95);     
    private final Color BTN_COLOR = new Color(80, 85, 95); 

    public ManageBranch() {
        setTitle("Branch & Course Management | GP Talbehat");
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
        
        JLabel headerLabel = new JLabel("    MANAGE COLLEGE BRANCHES & DEPARTMENTS");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        headerLabel.setForeground(TEXT_WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);

        // --- Main Form Panel ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(FORM_BG);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Fields ---
        addComp(mainPanel, "BRANCH CODE (Primary Key)*", codeField = new JTextField(), 0, 0, gbc);
        
        nameField = new JTextField();
        setInputRestriction(nameField, "[a-zA-Z ]*", 50);
        addComp(mainPanel, "BRANCH NAME*", nameField, 1, 0, gbc);

        hodField = new JTextField();
        setInputRestriction(hodField, "[a-zA-Z ]*", 50);
        addComp(mainPanel, "HEAD OF DEPARTMENT (HOD)", hodField, 0, 1, gbc);

        seatsField = new JTextField();
        setInputRestriction(seatsField, "[0-9]*", 4);
        addComp(mainPanel, "TOTAL INTAKE SEATS", seatsField, 1, 1, gbc);

        addComp(mainPanel, "DURATION (e.g. 3 Years)", durationField = new JTextField(), 0, 2, gbc);

        // Description Area (Updated with Theme Sync)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        addComp(mainPanel, "BRANCH DESCRIPTION & DETAILS", new JScrollPane(descArea), 0, 3, gbc);

        JScrollPane scroll = new JScrollPane(mainPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // --- Button Panel (Footer) ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        btnPanel.setBackground(THEME_DARK);
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
            btnPanel.add(b);
        }
        add(btnPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleAction(String type) {
        if (type.equals("BACK")) { 
            dispose(); 
            new AdminView().setVisible(true); // Back logic added
            return; 
        }
        if (type.equals("CLEAR")) { clear(); return; }

        try (Connection con = DBConnection.getConnection()) {
            if (type.equals("ADD") || type.equals("UPDATE")) {
                if (codeField.getText().isEmpty() || nameField.getText().isEmpty()) {
                    msg("Code aur Name Enter Karein!"); return;
                }
                String sql = type.equals("ADD") ? 
                    "INSERT INTO Branch (BranchName, HODName, Seats, Duration, Description, BranchCode) VALUES (?,?,?,?,?,?)" :
                    "UPDATE Branch SET BranchName=?, HODName=?, Seats=?, Duration=?, Description=? WHERE BranchCode=?";
                
                PreparedStatement pst = con.prepareStatement(sql);
                pst.setString(1, nameField.getText());
                pst.setString(2, hodField.getText());
                pst.setString(3, seatsField.getText());
                pst.setString(4, durationField.getText());
                pst.setString(5, descArea.getText());
                pst.setString(6, codeField.getText());
                pst.executeUpdate();
                msg("Branch " + type + "ed Successfully!");
            } 
            else if (type.equals("SEARCH")) {
                PreparedStatement pst = con.prepareStatement("SELECT * FROM Branch WHERE BranchCode=?");
                pst.setString(1, codeField.getText());
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    nameField.setText(rs.getString("BranchName"));
                    hodField.setText(rs.getString("HODName"));
                    seatsField.setText(rs.getString("Seats"));
                    durationField.setText(rs.getString("Duration"));
                    descArea.setText(rs.getString("Description"));
                } else { msg("Record Nahi Mila!"); }
            }
            else if (type.equals("DELETE")) {
                if(JOptionPane.showConfirmDialog(this, "Kya delete karna chahte hain?", "Confirm", 0) == 0) {
                    PreparedStatement pst = con.prepareStatement("DELETE FROM Branch WHERE BranchCode=?");
                    pst.setString(1, codeField.getText());
                    pst.executeUpdate(); clear(); msg("Deleted!");
                }
            }
        } catch (Exception ex) { msg("Error: " + ex.getMessage()); }
    }

    private void addComp(JPanel p, String lab, JComponent comp, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        if (y == 3) gbc.gridwidth = 2; else gbc.gridwidth = 1;

        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);
        
        JLabel l = new JLabel(lab);
        l.setForeground(TEXT_WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        comp.setBackground(FIELD_BG);
        comp.setForeground(TEXT_WHITE);
        
        Border line = new LineBorder(new Color(100, 110, 120), 1);
        Border pad = new EmptyBorder(0, 12, 0, 12);
        comp.setBorder(new CompoundBorder(line, pad));

        if (comp instanceof JTextField) {
            comp.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            ((JTextField)comp).setCaretColor(Color.WHITE);
            comp.setPreferredSize(new Dimension(350, 42)); 
        } else if (comp instanceof JScrollPane) {
            // Description area styling sync
            descArea.setBackground(FIELD_BG);
            descArea.setForeground(TEXT_WHITE);
            descArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            descArea.setCaretColor(Color.WHITE);
            descArea.setBorder(new EmptyBorder(8, 12, 8, 12));
            
            comp.setPreferredSize(new Dimension(740, 120));
            ((JScrollPane)comp).setBorder(line);
            ((JScrollPane)comp).getViewport().setBackground(FIELD_BG);
        }

        panel.add(l, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        p.add(panel, gbc);
        gbc.gridwidth = 1;
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

    private void clear() {
        codeField.setText(""); nameField.setText(""); hodField.setText("");
        seatsField.setText(""); durationField.setText(""); descArea.setText("");
        codeField.requestFocus();
    }

    private void msg(String s) { JOptionPane.showMessageDialog(this, s); }
}