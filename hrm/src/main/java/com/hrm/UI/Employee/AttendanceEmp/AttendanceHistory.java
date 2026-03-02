package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import com.hrm.utils.JDBCConection;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

public class AttendanceHistory extends JPanel {
    private String manv;
    private YearMonth currentMonth;
    private JPanel daysPanel;
    private JLabel lblMonthTitle;

    public AttendanceHistory(String manv) {
        this.manv = manv;
        this.currentMonth = YearMonth.now();
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // --- 1. Header của Lịch (Thanh điều hướng tháng) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        lblMonthTitle = new JLabel("", SwingConstants.CENTER);
        lblMonthTitle.setFont(new Font("Arial", Font.BOLD, 18));
        updateMonthTitle();

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        
        btnPrev.addActionListener(e -> { currentMonth = currentMonth.minusMonths(1); renderCalendar(); });
        btnNext.addActionListener(e -> { currentMonth = currentMonth.plusMonths(1); renderCalendar(); });

        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMonthTitle, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        // --- 2. Thứ trong tuần (T2 - CN) ---
        JPanel weekHeader = new JPanel(new GridLayout(1, 7));
        weekHeader.setOpaque(false);
        String[] days = {"T2", "T3", "T4", "T5", "T6", "T7", "CN"};
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setForeground(Color.GRAY);
            weekHeader.add(lbl);
        }

        // --- 3. Container cho các ngày ---
        daysPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        daysPanel.setOpaque(false);

        add(header, BorderLayout.NORTH);
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(weekHeader, BorderLayout.NORTH);
        centerPanel.add(daysPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        renderCalendar();
    }

    private void updateMonthTitle() {
        lblMonthTitle.setText("Tháng " + currentMonth.getMonthValue() + " / " + currentMonth.getYear());
    }

    private void renderCalendar() {
        daysPanel.removeAll();
        updateMonthTitle();

        // Lấy dữ liệu chấm công của tháng từ DB
        Map<Integer, String> attendanceData = fetchMonthlyAttendance();

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 (T2) -> 7 (CN)

        // Ô trống trước ngày mùng 1
        for (int i = 1; i < dayOfWeek; i++) {
            daysPanel.add(new JLabel(""));
        }

        // Vẽ các ngày trong tháng
        int daysInMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JPanel daySquare = new JPanel(new BorderLayout());
            daySquare.setPreferredSize(new Dimension(50, 50));
            
            JLabel lblDay = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            daySquare.add(lblDay, BorderLayout.CENTER);

            // Xác định màu sắc dựa trên trạng thái
            String status = attendanceData.getOrDefault(day, "NGHI");
            if (status.equals("Đúng giờ")) {
                daySquare.setBackground(new Color(220, 252, 231)); // Xanh nhạt
                lblDay.setForeground(new Color(22, 101, 52));     // Xanh đậm
            } else if (status.equals("Đi muộn")) {
                daySquare.setBackground(new Color(255, 247, 237)); // Cam nhạt
                lblDay.setForeground(new Color(154, 52, 18));      // Cam đậm
            } else {
                daySquare.setBackground(new Color(254, 242, 242)); // Đỏ nhạt
                lblDay.setForeground(new Color(153, 27, 27));      // Đỏ đậm
            }
            
            daySquare.setBorder(new LineBorder(new Color(240, 240, 240)));
            daysPanel.add(daySquare);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private Map<Integer, String> fetchMonthlyAttendance() {
        Map<Integer, String> data = new HashMap<>();
        String sql = "SELECT DAY(NGAYLAMVIEC), TRANGTHAI FROM chamcong " +
                     "WHERE MANV = ? AND MONTH(NGAYLAMVIEC) = ? AND YEAR(NGAYLAMVIEC) = ?";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, manv);
            ps.setInt(2, currentMonth.getMonthValue());
            ps.setInt(3, currentMonth.getYear());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data.put(rs.getInt(1), rs.getString(2));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return data;
    }
    public JPanel createSearchPanel() {
    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    searchPanel.setOpaque(false);
    
    JLabel lbl = new JLabel("Tìm kiếm ngày: ");
    JTextField txtDate = new JTextField(15);
    txtDate.setText("yyyy-mm-dd");
    JButton btnSearch = new JButton("Tra cứu");

    btnSearch.addActionListener(e -> {
        String date = txtDate.getText();
        // Logic: Hiển thị một JDialog hoặc Popup chi tiết check-in/out của ngày đó
        JOptionPane.showMessageDialog(this, "Thông tin ngày " + date + ": Đúng giờ (8.0h)");
    });

    searchPanel.add(lbl);
    searchPanel.add(txtDate);
    searchPanel.add(btnSearch);
    return searchPanel;
}
}