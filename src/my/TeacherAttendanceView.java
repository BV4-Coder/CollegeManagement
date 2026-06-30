package my;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TeacherAttendanceView extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cbBranch, cbSem;
    private JLabel lblDateTime;
    private JSpinner startSpinner;

    // Design Colors
    private final Color BG_DARK = new Color(28, 30, 34);
    private final Color CARD_BG = new Color(40, 44, 52);
    private final Color TEXT_BLUE = new Color(102, 204, 255);

    public TeacherAttendanceView() {
        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(CARD_BG);
        topPanel.setBorder(new CompoundBorder(new MatteBorder(0, 0, 2, 0, Color.GRAY), new EmptyBorder(10, 10, 10, 10)));

        JPanel controlRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        controlRow.setOpaque(false);

        cbBranch = new JComboBox<>(new String[]{"Computer Science", "Mining", "Electronics", "Mechanical"});
        cbSem = new JComboBox<>(new String[]{"First Semester", "Second Semester", "Third Semester", "Fourth Semester", "Fifth Semester", "Sixth Semester"});

        // Event: Jab Branch ya Sem badle, tab Saved Date apne aap load ho
        cbBranch.addActionListener(e -> loadSavedSessionDate());
        cbSem.addActionListener(e -> loadSavedSessionDate());

        JLabel lblStart = new JLabel("SESSION START:");
        lblStart.setForeground(new Color(241, 196, 15));
        startSpinner = new JSpinner(new SpinnerDateModel());
        startSpinner.setEditor(new JSpinner.DateEditor(startSpinner, "dd-MM-yyyy"));

        JButton btnLoad = new JButton("FETCH STUDENTS");
        btnLoad.setBackground(new Color(52, 152, 219));
        btnLoad.setForeground(Color.WHITE);
        btnLoad.addActionListener(e -> {
            saveSessionDate(); // Date fix/save karo
            loadStudentData(); // Data load karo
        });

        controlRow.add(new JLabel("BRANCH:")).setForeground(TEXT_BLUE);
        controlRow.add(cbBranch);
        controlRow.add(new JLabel("SEM:")).setForeground(TEXT_BLUE);
        controlRow.add(cbSem);
        controlRow.add(lblStart);
        controlRow.add(startSpinner);
        controlRow.add(btnLoad);

        JPanel dateRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        dateRow.setOpaque(false);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd-MMM-yyyy | HH:mm:ss");
        lblDateTime = new JLabel("Today: " + dtf.format(LocalDateTime.now()));
        lblDateTime.setForeground(new Color(241, 196, 15));
        dateRow.add(lblDateTime);

        topPanel.add(controlRow);
        topPanel.add(dateRow);
        add(topPanel, BorderLayout.NORTH);

        // --- TABLE ---
        model = new DefaultTableModel(new String[]{"ROLL NO", "NAME", "OVERALL %", "ACTION (P / A)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return col == 3; }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setBackground(BG_DARK);
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));
        table.getColumnModel().getColumn(3).setCellRenderer(new AttendanceButtonRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new AttendanceButtonEditor(new JCheckBox()));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BOTTOM SAVE BAR ---
        JButton btnSave = new JButton("SUBMIT TODAY'S ATTENDANCE");
        btnSave.setPreferredSize(new Dimension(0, 50));
        btnSave.setBackground(new Color(39, 174, 96));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSave.addActionListener(e -> saveAttendance());
        add(btnSave, BorderLayout.SOUTH);

        new Timer(1000, e -> lblDateTime.setText("Today: " + dtf.format(LocalDateTime.now()))).start();
        
        loadSavedSessionDate(); // App khulte hi pehli baar date load karo
    }

    private void loadSavedSessionDate() {
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT StartDate FROM SemesterSettings WHERE Branch=? AND Semester=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, cbBranch.getSelectedItem().toString());
            pst.setString(2, cbSem.getSelectedItem().toString());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                startSpinner.setValue(rs.getDate("StartDate"));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveSessionDate() {
        try (Connection con = DBConnection.getConnection()) {
            // MERGE logic: Agar settings hain toh update, nahi toh insert
            String sql = "IF EXISTS (SELECT 1 FROM SemesterSettings WHERE Branch=? AND Semester=?) " +
                         "UPDATE SemesterSettings SET StartDate=? WHERE Branch=? AND Semester=? " +
                         "ELSE INSERT INTO SemesterSettings (Branch, Semester, StartDate) VALUES (?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            String b = cbBranch.getSelectedItem().toString();
            String s = cbSem.getSelectedItem().toString();
            java.sql.Date d = new java.sql.Date(((java.util.Date)startSpinner.getValue()).getTime());

            pst.setString(1, b); pst.setString(2, s);
            pst.setDate(3, d); pst.setString(4, b); pst.setString(5, s);
            pst.setString(6, b); pst.setString(7, s); pst.setDate(8, d);
            pst.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadStudentData() {
        model.setRowCount(0);
        String branch = cbBranch.getSelectedItem().toString();
        String sem = cbSem.getSelectedItem().toString();
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String startDate = sdf.format(startSpinner.getValue());
        String endDate = sdf.format(new java.util.Date());

        try (Connection con = DBConnection.getConnection()) {
            // Logic: Total working days vs student's present days
            String sql = "SELECT S.RollNo, S.Name, " +
                         "(SELECT COUNT(DISTINCT A2.AttendanceDate) FROM StudentAttendance A2 " +
                         " JOIN Students S2 ON A2.RollNo = S2.RollNo " +
                         " WHERE S2.Branch = ? AND S2.Semester = ? AND A2.AttendanceDate BETWEEN ? AND ?) as TotalDays, " +
                         "(SELECT COUNT(*) FROM StudentAttendance WHERE RollNo = S.RollNo AND Status='P' AND AttendanceDate BETWEEN ? AND ?) as MyPresent " +
                         "FROM Students S WHERE LTRIM(RTRIM(Branch)) = ? AND LTRIM(RTRIM(Semester)) = ?";
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, branch); pst.setString(2, sem);
            pst.setString(3, startDate); pst.setString(4, endDate);
            pst.setString(5, startDate); pst.setString(6, endDate);
            pst.setString(7, branch); pst.setString(8, sem);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int totalClasses = rs.getInt("TotalDays");
                int presentCount = rs.getInt("MyPresent");
                double perc = (totalClasses > 0) ? (presentCount * 100.0 / totalClasses) : 0.0;
                model.addRow(new Object[]{rs.getString("RollNo"), rs.getString("Name"), String.format("%.2f", perc) + "%", "P"});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void saveAttendance() {
        if (model.getRowCount() == 0) return;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false); 
            java.sql.Date aaj = new java.sql.Date(System.currentTimeMillis());
            
            String deleteSql = "DELETE FROM StudentAttendance WHERE AttendanceDate = ? AND RollNo IN (SELECT RollNo FROM Students WHERE Branch=? AND Semester=?)";
            PreparedStatement delPst = con.prepareStatement(deleteSql);
            delPst.setDate(1, aaj);
            delPst.setString(2, cbBranch.getSelectedItem().toString());
            delPst.setString(3, cbSem.getSelectedItem().toString());
            delPst.executeUpdate();

            String insertSql = "INSERT INTO StudentAttendance (RollNo, AttendanceDate, Status) VALUES (?, ?, ?)";
            PreparedStatement insPst = con.prepareStatement(insertSql);
            for (int i = 0; i < model.getRowCount(); i++) {
                insPst.setString(1, model.getValueAt(i, 0).toString());
                insPst.setDate(2, aaj);
                insPst.setString(3, model.getValueAt(i, 3).toString());
                insPst.addBatch();
            }
            insPst.executeBatch();
            con.commit(); 
            JOptionPane.showMessageDialog(this, "Attendance Recorded!");
            loadStudentData(); 
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Renderer & Editor for Buttons
    class AttendanceButtonRenderer extends JPanel implements TableCellRenderer {
        public AttendanceButtonRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5)); setOpaque(false); }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
            removeAll();
            JButton bP = new JButton("P"); JButton bA = new JButton("A");
            if ("P".equals(v)) { bP.setBackground(Color.GREEN); bA.setBackground(Color.GRAY); }
            else { bP.setBackground(Color.GRAY); bA.setBackground(Color.RED); }
            add(bP); add(bA);
            return this;
        }
    }

    class AttendanceButtonEditor extends DefaultCellEditor {
        private String val;
        public AttendanceButtonEditor(JCheckBox c) { super(c); }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean isS, int r, int c) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            JButton bP = new JButton("P"); JButton bA = new JButton("A");
            bP.addActionListener(e -> { val = "P"; fireEditingStopped(); });
            bA.addActionListener(e -> { val = "A"; fireEditingStopped(); });
            p.add(bP); p.add(bA);
            return p;
        }
        @Override public Object getCellEditorValue() { return val; }
    }
}