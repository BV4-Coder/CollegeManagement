package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;
import java.sql.*;

public class latestUPdate extends JFrame {

    private JTable fileTable;
    private DefaultTableModel tableModel;
    private JLabel previewLabel;
    
    // --- SHARED THEME COLORS ---
    private final Color THEME_DARK = new Color(40, 44, 52);   
    private final Color FORM_BG = new Color(60, 65, 75);      
    private final Color TEXT_WHITE = Color.WHITE;             
    private final Color FIELD_BG = new Color(80, 85, 95);     
    private final Color BTN_COLOR = new Color(80, 85, 95); 

    public latestUPdate() {
        setTitle("Latest Updates Manager | GP Talbehat");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setBackground(FORM_BG);
        setLayout(new BorderLayout());

        // --- 1. Header ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(THEME_DARK);
        headerPanel.setPreferredSize(new Dimension(0, 65));
        
        JLabel headerLabel = new JLabel("    LATEST UPDATES: DATABASE MANAGER");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 19));
        headerLabel.setForeground(TEXT_WHITE);
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Close Button for Undecorated Window
        JButton btnExit = new JButton("X");
        btnExit.setForeground(Color.WHITE);
        btnExit.setBackground(new Color(180, 40, 40));
        btnExit.setFocusPainted(false);
        btnExit.setBorderPainted(false);
        btnExit.addActionListener(e -> dispose());
        headerPanel.add(btnExit, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Center Panel ---
        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setBackground(FORM_BG);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        tableModel = new DefaultTableModel(new String[]{"File Name", "Type", "Full Path"}, 0);
        fileTable = new JTable(tableModel);
        fileTable.setRowHeight(40);
        fileTable.setBackground(FIELD_BG);
        fileTable.setForeground(TEXT_WHITE);
        fileTable.setGridColor(new Color(70, 75, 85));
        
        JScrollPane scrollPane = new JScrollPane(fileTable);
        scrollPane.getViewport().setBackground(FORM_BG);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Preview Section
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(THEME_DARK);
        previewPanel.setPreferredSize(new Dimension(350, 0));
        previewLabel = new JLabel("No Image Selected", SwingConstants.CENTER);
        previewLabel.setForeground(Color.LIGHT_GRAY);
        previewPanel.add(previewLabel, BorderLayout.CENTER);
        mainContent.add(previewPanel, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);

        // --- 3. Footer ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        footer.setBackground(THEME_DARK);

        JButton uploadPhotoBtn = new JButton("UPLOAD PHOTO");
        JButton uploadPdfBtn = new JButton("UPLOAD PDF");
        JButton viewBtn = new JButton("VIEW FILE");
        JButton removeBtn = new JButton("REMOVE");
        JButton backBtn = new JButton("BACK");

        styleButton(uploadPhotoBtn, new Color(46, 204, 113));
        styleButton(uploadPdfBtn, new Color(52, 152, 219));
        styleButton(viewBtn, new Color(243, 156, 18));
        styleButton(removeBtn, new Color(231, 76, 60));
        styleButton(backBtn, BTN_COLOR);

        // Selection Logic
        fileTable.getSelectionModel().addListSelectionListener(e -> {
            int row = fileTable.getSelectedRow();
            if (row != -1) {
                String path = tableModel.getValueAt(row, 2).toString();
                if (path.toLowerCase().matches(".*\\.(jpg|png|jpeg)")) showPreview(path);
                else { previewLabel.setIcon(null); previewLabel.setText("PDF (No Preview)"); }
            }
        });

        // Button Actions
        uploadPhotoBtn.addActionListener(e -> chooseFile("Images", new String[]{"jpg", "jpeg", "png"}));
        uploadPdfBtn.addActionListener(e -> chooseFile("PDF Documents", new String[]{"pdf"}));
        
        viewBtn.addActionListener(e -> {
            int row = fileTable.getSelectedRow();
            if (row != -1) openFile(tableModel.getValueAt(row, 2).toString());
        });

        removeBtn.addActionListener(e -> removeFromDB());

        backBtn.addActionListener(e -> {
            dispose();
            // AdminView tabhi khulega jab aapka Dashboard Admin flow mein ho
            new AdminView().setVisible(true);
        });

        footer.add(uploadPhotoBtn); footer.add(uploadPdfBtn); footer.add(viewBtn);
        footer.add(removeBtn); footer.add(backBtn);
        add(footer, BorderLayout.SOUTH);

        loadDataFromDB(); 
        setLocationRelativeTo(null);
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void chooseFile(String desc, String[] exts) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(desc, exts));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            saveToDB(f.getName(), desc, f.getAbsolutePath());
        }
    }

    private void saveToDB(String name, String type, String path) {
        String sql = "INSERT INTO LatestUpdates (FileName, FileType, FullPath) VALUES (?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, type);
            pst.setString(3, path);
            pst.executeUpdate();
            loadDataFromDB();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    private void loadDataFromDB() {
        tableModel.setRowCount(0);
        String sql = "SELECT FileName, FileType, FullPath FROM LatestUpdates";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3)});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void removeFromDB() {
        int row = fileTable.getSelectedRow();
        if (row == -1) return;
        
        String path = tableModel.getValueAt(row, 2).toString();
        String sql = "DELETE FROM LatestUpdates WHERE FullPath = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, path);
            pst.executeUpdate();
            loadDataFromDB();
            previewLabel.setIcon(null);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void openFile(String path) {
        try { Desktop.getDesktop().open(new File(path)); } 
        catch (Exception ex) { JOptionPane.showMessageDialog(this, "Cannot open file!"); }
    }

    private void showPreview(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image img = icon.getImage().getScaledInstance(320, 320, Image.SCALE_SMOOTH);
            previewLabel.setIcon(new ImageIcon(img));
            previewLabel.setText("");
        } catch (Exception e) { previewLabel.setText("Preview Error"); }
    }
}