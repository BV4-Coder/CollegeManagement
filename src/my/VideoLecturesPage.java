package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VideoLecturesPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public VideoLecturesPage() {
        setTitle("Video Lectures");
        setSize(800, 500);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 18, 18));

        // Header
        JLabel lbl = new JLabel("Available Video Lectures", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(Color.WHITE);
        lbl.setBorder(new EmptyBorder(20,0,20,0));
        add(lbl, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new String[]{"Video Title", "Video Path/URL"}, 0);
        table = new JTable(model);
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(new Color(18, 18, 18));
        sp.setBorder(BorderFactory.createEmptyBorder());
        add(sp, BorderLayout.CENTER);

        // Play Button
        JButton btnPlay = new JButton("PLAY SELECTED VIDEO");
        btnPlay.setBackground(new Color(180, 40, 40));
        btnPlay.setForeground(Color.WHITE);
        btnPlay.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPlay.setFocusPainted(false);
        btnPlay.setPreferredSize(new Dimension(0, 50));
        
        btnPlay.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row != -1) {
                String path = table.getValueAt(row, 1).toString();
                playVideo(path);
            } else {
                JOptionPane.showMessageDialog(this, "Pehle list se video select karein!");
            }
        });
        add(btnPlay, BorderLayout.SOUTH);

        loadVideos();
    }

    private void loadVideos() {
        // Line 62: Database query update ki gayi hai aapke screenshot ke mutabiq
        String sql = "SELECT Title, VideoURL FROM dbo.Videos"; 
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            model.setRowCount(0); // Purana data clear karne ke liye
            while(rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("Title"), 
                    rs.getString("VideoURL") // Screenshot mein column 'VideoURL' hai
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data load error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void playVideo(String path) {
        try {
            java.io.File videoFile = new java.io.File(path);
            if (videoFile.exists()) {
                // Agar local file path hai (V:\...) toh default player mein khulega
                Desktop.getDesktop().open(videoFile);
            } else {
                // Agar URL hai toh browser mein khulega
                Desktop.getDesktop().browse(new java.net.URI(path));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Video chalu nahi ho payi: " + ex.getMessage());
        }
    }
}