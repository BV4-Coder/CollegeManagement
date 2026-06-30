package my;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

public class About extends JFrame {

    public About() {
        setTitle("About Us - GP Talbehat");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel();
        header.setBackground(new Color(139, 0, 0));
        JLabel title = new JLabel("Useful Links", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Serif", Font.BOLD, 25));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // --- Main Panel ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JLabel infoLabel = new JLabel("Click on the links below to open in Browser:", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Links create karein
        JLabel link1 = createHyperlink("1. URISE Portal (GP Talbehat)", "https://urise.up.gov.in/poly/3340");
        JLabel link2 = createHyperlink("2. Official College Website", "https://governmentpolytechnictalbehat.com/");

        mainPanel.add(infoLabel);
        mainPanel.add(link1);
        mainPanel.add(link2);

        add(mainPanel, BorderLayout.CENTER);

        // --- Back Button Logic ---
        JButton closeBtn = new JButton("Back to Dashboard");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        closeBtn.setBackground(new Color(60, 60, 60));
        closeBtn.setForeground(Color.WHITE);
        
        closeBtn.addActionListener(e -> {
            dispose(); // Is About page ko band karega
            new DashBoard().setVisible(true); // DashBoard page ko firse open karega
        });
        
        JPanel footer = new JPanel();
        footer.add(closeBtn);
        add(footer, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JLabel createHyperlink(String text, String url) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 18));
        label.setForeground(Color.BLUE);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Link nahi khul raha!");
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setText("<html><u>" + text + "</u></html>");
            }
            @Override
            public void mouseExited(MouseEvent e) {
                label.setText(text);
            }
        });
        return label;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new About());
    }
}