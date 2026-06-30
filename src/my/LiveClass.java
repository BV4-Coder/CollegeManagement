package my;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.net.URI;

public class LiveClass extends JFrame {

    // --- THEME COLORS ---
    Color bgLight = new Color(28, 30, 34);
    Color cardColor = new Color(40, 44, 52);
    Color accentColor = new Color(102, 204, 255);
    Color liveRed = new Color(255, 50, 50);

    public LiveClass(String teacherId) {
        // Frame Setup
        setTitle("Live Classroom Portal | GP Talbehat");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(bgLight);
        setLayout(new BorderLayout());

        // --- MAIN UI PANEL ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // 1. Header Label
        JLabel lblStatus = new JLabel("● SECURE LIVE BROADCAST");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblStatus.setForeground(liveRed);
        
        // 2. Control Card
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(650, 480));
        card.setBackground(cardColor);
        card.setLayout(new GridLayout(8, 1, 10, 10));
        card.setBorder(new CompoundBorder(
            new LineBorder(accentColor, 1),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JLabel lblTitle = new JLabel("Teacher: " + teacherId, JLabel.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        // --- TEACHER SECTION ---
        JLabel lblHostInfo = new JLabel("--- HOST PANEL ---", JLabel.CENTER);
        lblHostInfo.setForeground(Color.GRAY);

        JButton btnStart = new JButton("START MEETING AS HOST");
        btnStart.setBackground(new Color(39, 174, 96));
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnStart.setFocusPainted(false);
        btnStart.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- STUDENT SECTION ---
        JLabel lblStudentInfo = new JLabel("--- STUDENT PANEL ---", JLabel.CENTER);
        lblStudentInfo.setForeground(Color.GRAY);

        JTextField txtRoomId = new JTextField("Enter Room ID to Join");
        txtRoomId.setHorizontalAlignment(JTextField.CENTER);
        txtRoomId.setBackground(new Color(60, 64, 72));
        txtRoomId.setForeground(Color.WHITE);
        txtRoomId.setCaretColor(Color.WHITE);

        JButton btnJoin = new JButton("JOIN MEETING (STUDENT)");
        btnJoin.setBackground(new Color(41, 128, 185));
        btnJoin.setForeground(Color.WHITE);
        btnJoin.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // --- HOST LOGIC ---
        btnStart.addActionListener(e -> {
            try {
                String roomName = "GPTalbehat_Class_" + teacherId.replace(" ", "_");
                
                String url = "https://meet.jit.si/" + roomName + 
                             "#config.prejoinPageEnabled=false" +
                             "&config.lobby.enabled=true" +
                             "&config.startWithVideoMuted=false" +
                             "&config.recordingServiceEnabled=true";
                
                Desktop.getDesktop().browse(new URI(url));
                
                copyToClipboard(roomName);
                JOptionPane.showMessageDialog(this, "Session Started Successfully!\n\n" +
                        "1. Room ID: " + roomName + " (Copied)\n" +
                        "2. Students are in Lobby (Grant them permission to enter).\n" +
                        "3. Recording: Inside the meeting, click 3-dots -> Start Recording.");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Browser Error: " + ex.getMessage());
            }
        });

        // --- STUDENT LOGIC ---
        btnJoin.addActionListener(e -> {
            try {
                String room = txtRoomId.getText().trim();
                if(room.isEmpty() || room.equals("Enter Room ID to Join")) {
                    JOptionPane.showMessageDialog(this, "Please enter a Room ID.");
                    return;
                }
                String url = "https://meet.jit.si/" + room;
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Adding components to card
        card.add(lblTitle);
        card.add(lblHostInfo);
        card.add(btnStart);
        card.add(new JSeparator());
        card.add(lblStudentInfo);
        card.add(txtRoomId);
        card.add(btnJoin);
        card.add(new JLabel("Lobby Mode is ON for Security", JLabel.CENTER) {{ setForeground(Color.DARK_GRAY); }});

        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(lblStatus, gbc);
        gbc.gridy = 1;
        mainPanel.add(card, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void copyToClipboard(String text) {
        java.awt.datatransfer.StringSelection selection = new java.awt.datatransfer.StringSelection(text);
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
    }
}