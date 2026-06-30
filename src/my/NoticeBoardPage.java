package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.io.File;

public class NoticeBoardPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public NoticeBoardPage() {
        setTitle("GP Talbehat | Notice Board");
        setSize(800, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(40, 44, 52)); // Dark Theme

        // Header
        JLabel lbl = new JLabel("OFFICIAL NOTICES & UPDATES", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(20,0,20,0));
        add(lbl, BorderLayout.NORTH);

        // Table setup (LatestUpdates table ke columns ke hisaab se)
        model = new DefaultTableModel(new String[]{"Notice Title", "Type", "File Path"}, 0);
        table = new JTable(model);
        table.setBackground(new Color(60, 65, 75));
        table.setForeground(Color.WHITE);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Hide Path Column (Optional: User ko path dikhane ki zarurat nahi hoti)
        // table.getColumnModel().getColumn(2).setMinWidth(0);
        // table.getColumnModel().getColumn(2).setMaxWidth(0);

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(new Color(40, 44, 52));
        sp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(sp, BorderLayout.CENTER);

        // View Button
        JButton btnView = new JButton("VIEW SELECTED NOTICE");
        btnView.setBackground(new Color(46, 204, 113)); // Green color
        btnView.setForeground(Color.WHITE);
        btnView.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnView.setFocusPainted(false);
        btnView.setPreferredSize(new Dimension(0, 60));
        
        btnView.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                String path = table.getValueAt(row, 2).toString();
                openNotice(path);
            } else {
                JOptionPane.showMessageDialog(this, "Pehle koi notice select karein!");
            }
        });
        add(btnView, BorderLayout.SOUTH);

        loadNotices();
    }

    private void loadNotices() {
        // Query aapki 'LatestUpdates' table se data legi
        String sql = "SELECT FileName, FileType, FullPath FROM LatestUpdates"; 
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            model.setRowCount(0); 
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("FileName"), 
                    rs.getString("FileType"),
                    rs.getString("FullPath") 
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Notice load error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openNotice(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(this, "File nahi mili! Shayad delete ho gayi hai.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Notice kholne mein error: " + ex.getMessage());
        }
    }
}