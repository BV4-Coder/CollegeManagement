package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;

public class MySubjectView extends JFrame {
    
    Color bgLight = new Color(28, 30, 34);
    Color cardColor = new Color(40, 44, 52);
    Color buttonTextColor = new Color(102, 204, 255);
    Color highlightColor = new Color(0, 150, 136); // Teal highlight

    public MySubjectView(String loggedInTeacherId) {
        setTitle("Branch Time Table");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(bgLight);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Header
        JLabel head = new JLabel("BRANCH TIME TABLE (YOUR CLASS IS HIGHLIGHTED)", JLabel.CENTER);
        head.setFont(new Font("Segoe UI", Font.BOLD, 22));
        head.setForeground(buttonTextColor);
        head.setBorder(new EmptyBorder(25, 0, 25, 0));
        add(head, BorderLayout.NORTH);

        // Table Columns (TeacherID hidden rahega)
        String[] columns = {"Day", "Subject", "Time Slot", "Room No", "TeacherID"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(model);
        
        // --- HIGHLIGHTING RENDERER ---
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                
                // 4th index pe TeacherID hai
                String rowTeacherId = t.getModel().getValueAt(row, 4).toString();
                
                if (rowTeacherId.equals(loggedInTeacherId)) {
                    c.setBackground(highlightColor);
                    c.setForeground(Color.WHITE);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 15));
                } else {
                    c.setBackground(cardColor);
                    c.setForeground(Color.LIGHT_GRAY);
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                }
                return c;
            }
        });

        table.setRowHeight(45);
        table.setFillsViewportHeight(true);
        table.setGridColor(new Color(60, 60, 60));

        // Teacher ID column hide karna
        table.getColumnModel().getColumn(4).setMinWidth(0);
        table.getColumnModel().getColumn(4).setMaxWidth(0);

        // Database Fetch
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT * FROM TimeTable";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("Day"), 
                    rs.getString("Subject"), 
                    rs.getString("TimeSlot"), 
                    rs.getString("RoomNo"),
                    rs.getString("TeacherID")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        sp.getViewport().setBackground(bgLight);
        add(sp, BorderLayout.CENTER);

        JButton closeBtn = new JButton("CLOSE");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Is method ki wajah se ye file direct RUN hogi
    public static void main(String[] args) {
        // Test karne ke liye T101 pass kiya
        new MySubjectView("T101");
    }
}