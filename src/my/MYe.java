package my;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MYe {
    public MYe() {

        JFrame frame = new JFrame();
        
        // -------- TASKBAR ICON ADDED HERE --------
        try {
    Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/my/welcome.png"));
    frame.setIconImage(icon);
} catch (Exception e) {
    e.printStackTrace();
}
        // -----------------------------------------

        frame.setSize(1200, 750);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true);
        frame.setLayout(new BorderLayout());

        // -------- BACKGROUND PANEL --------
        JPanel background = new JPanel() {
            Image img = new ImageIcon(getClass().getResource("/my/welcome.png")).getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        background.setLayout(null);

        // -------- TRANSPARENT GLASS PANEL --------
        JPanel glass = new JPanel();
        glass.setBounds(300, 170, 600, 350);
        glass.setOpaque(false);
        glass.setLayout(null);

        // -------- TITLE --------
        JLabel title = new JLabel("WELCOME", SwingConstants.CENTER);
        title.setBounds(0, 30, 600, 60);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));

        // -------- SUB TITLE --------
        JLabel sub = new JLabel("Government Polytechnic Talbehat, Lalitpur", SwingConstants.CENTER);
        sub.setBounds(0, 95, 600, 40);
        sub.setForeground(Color.WHITE);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        // -------- DESCRIPTION TEXT --------
        JLabel line1 = new JLabel(
                "<html><div style='text-align:center;'>"
                + "**Our institution is dedicated to making students<br>"
                + "self-reliant and skilled professionals through<br>"
                + "technical education. Let's take a step towards<br>"
                + "a bright future.**"
                + "</div></html>",
                SwingConstants.CENTER
        );
        line1.setBounds(0, 145, 600, 160);
        line1.setForeground(Color.WHITE);
        line1.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        // -------- BUTTON --------
        JButton startBtn = new JButton("Get Started");
        startBtn.setBounds(220, 300, 160, 45);
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startBtn.setBackground(new Color(0,0,0));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBorderPainted(false);
        startBtn.setFocusPainted(false);
        startBtn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 

        // -------- BUTTON ACTION PERFORMED --------
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); 
                new DashBoard(); 
            }
        });

        // -------- ADD COMPONENTS --------
        glass.add(title);
        glass.add(sub);
        glass.add(line1);
        glass.add(startBtn);

        background.add(glass);
        frame.add(background);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new MYe();
    }
}