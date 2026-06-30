package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class AdminSubjectControl extends JFrame {
    
    private final Color THEME_DARK = new Color(40, 44, 52);   
    private final Color FORM_BG = new Color(60, 65, 75);      
    private final Color TEXT_WHITE = Color.WHITE;             
    private final Color FIELD_BG = new Color(80, 85, 95);     
    private final Color BTN_COLOR = new Color(80, 85, 95); 

    JTextField txtSubName, txtRoom, txtTime; 
    JComboBox<String> cbBranch, cbSem, cbDay, cbDuration;
    JTable table;
    DefaultTableModel model;
    String selectedID = ""; 

    public AdminSubjectControl() {
        setTitle("Admin - Timetable Control");
        setSize(1200, 800); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); 
        getContentPane().setBackground(FORM_BG);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_DARK);
        headerPanel.setPreferredSize(new Dimension(0, 65));
        JLabel headerLabel = new JLabel("    MANAGE SUBJECTS & CLASS TIMETABLE");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        headerLabel.setForeground(TEXT_WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(THEME_DARK); 
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        addInput(leftPanel, "BRANCH NAME", cbBranch = new JComboBox<>(new String[]{"Mining", "Computer Science", "Electrical", "Elecronice"}));
        addInput(leftPanel, "SEMESTER", cbSem = new JComboBox<>(new String[]{"FIRST SEM", "SECOND SEM", "THIRD SEM", "FOURTH SEM", "FIFTH SEM", "SIXTH SEM"}));
        addInput(leftPanel, "WEEK DAY", cbDay = new JComboBox<>(new String[]{"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"}));
        addInput(leftPanel, "TIME SLOT (e.g. 10:00-11:00)", txtTime = new JTextField()); 
        addInput(leftPanel, "LECTURE SPAN", cbDuration = new JComboBox<>(new String[]{"1", "2", "3"}));
        addInput(leftPanel, "SUBJECT NAME", txtSubName = new JTextField());
        addInput(leftPanel, "ROOM / LAB NO", txtRoom = new JTextField());

        JButton btnAdd = new JButton("ADD ENTRY");
        JButton btnUpdate = new JButton("UPDATE");
        JButton btnDelete = new JButton("DELETE");
        JButton btnClear = new JButton("CLEAR");
        JButton btnBack = new JButton("BACK");

        styleBtn(btnAdd, new Color(46, 204, 113));
        styleBtn(btnUpdate, new Color(52, 152, 219));
        styleBtn(btnDelete, new Color(231, 76, 60));
        styleBtn(btnClear, BTN_COLOR);
        styleBtn(btnBack, new Color(100, 100, 100));

        leftPanel.add(btnAdd); leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(btnUpdate); leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(btnDelete); leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(btnClear); leftPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        leftPanel.add(btnBack);
        add(leftPanel, BorderLayout.WEST);

        // Table Panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(FORM_BG);
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        model = new DefaultTableModel(new String[]{"ID", "Branch", "Sem", "Day", "Time", "Subject", "Room", "Span"}, 0);
        table = new JTable(model);
        table.setRowHeight(38);
        table.setBackground(FIELD_BG);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(70, 75, 85));
        table.setSelectionBackground(new Color(52, 152, 219));
        
        JTableHeader head = table.getTableHeader();
        head.setBackground(THEME_DARK);
        head.setForeground(Color.WHITE);
        head.setPreferredSize(new Dimension(0, 45));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(FORM_BG);
        rightPanel.add(scroll, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);

        // Actions
        btnAdd.addActionListener(e -> performAction("INSERT"));
        btnUpdate.addActionListener(e -> performAction("UPDATE"));
        btnDelete.addActionListener(e -> performAction("DELETE"));
        btnClear.addActionListener(e -> clearForm());
        btnBack.addActionListener(e -> { this.dispose(); new AdminView().setVisible(true); });

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                selectedID = model.getValueAt(row, 0).toString();
                cbBranch.setSelectedItem(model.getValueAt(row, 1).toString());
                cbSem.setSelectedItem(model.getValueAt(row, 2).toString());
                cbDay.setSelectedItem(model.getValueAt(row, 3).toString());
                txtTime.setText(model.getValueAt(row, 4).toString());
                txtSubName.setText(model.getValueAt(row, 5).toString());
                txtRoom.setText(model.getValueAt(row, 6).toString());
                cbDuration.setSelectedItem(model.getValueAt(row, 7).toString());
            }
        });

        refreshTable();
    }

    private void performAction(String type) {
        String timeVal = txtTime.getText().trim();
        if((type.equals("INSERT") || type.equals("UPDATE")) && timeVal.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Time Slot!"); return;
        }
        String sql = "";
        if(type.equals("INSERT")) sql = "INSERT INTO Subjects (Branch, Semester, Day, TimeSlot, SubjectName, RoomNo, Duration) VALUES (?,?,?,?,?,?,?)";
        else if(type.equals("UPDATE")) sql = "UPDATE Subjects SET Branch=?, Semester=?, Day=?, TimeSlot=?, SubjectName=?, RoomNo=?, Duration=? WHERE SubID=?";
        else if(type.equals("DELETE")) sql = "DELETE FROM Subjects WHERE SubID=?";

        try (Connection con = DBConnection.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            if(!type.equals("DELETE")) {
                pst.setString(1, cbBranch.getSelectedItem().toString());
                pst.setString(2, cbSem.getSelectedItem().toString());
                pst.setString(3, cbDay.getSelectedItem().toString());
                pst.setString(4, timeVal);
                pst.setString(5, txtSubName.getText());
                pst.setString(6, txtRoom.getText());
                pst.setInt(7, Integer.parseInt(cbDuration.getSelectedItem().toString()));
                if(type.equals("UPDATE")) pst.setString(8, selectedID);
            } else pst.setString(1, selectedID);
            pst.executeUpdate();
            refreshTable();
            clearForm();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshTable() {
        model.setRowCount(0);
        try (Connection con = DBConnection.getConnection()) {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM Subjects ORDER BY Day, TimeSlot");
            while (rs.next()) {
                model.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void clearForm() {
        txtTime.setText(""); txtSubName.setText(""); txtRoom.setText(""); 
        table.clearSelection(); selectedID = "";
    }

    private void addInput(JPanel p, String label, JComponent comp) {
        JLabel l = new JLabel(label); 
        l.setForeground(new Color(180, 185, 195)); 
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        p.add(l);
        p.add(Box.createRigidArea(new Dimension(0, 5)));
        comp.setBackground(FIELD_BG);
        comp.setForeground(Color.WHITE);
        if(comp instanceof JTextField) {
            ((JTextField)comp).setCaretColor(Color.WHITE);
            comp.setBorder(new EmptyBorder(0, 12, 0, 12)); 
        } else comp.setBorder(null);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); 
        p.add(comp);
        p.add(Box.createRigidArea(new Dimension(0, 12)));
    }

    private void styleBtn(JButton b, Color c) {
        b.setBackground(c); b.setForeground(Color.WHITE); 
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    }
}