package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import com.hrm.DAO.Employee.AttendanceDAO;
import com.hrm.DTO.Employee.AttendanceDTO;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

public class AttendanceHistory extends JPanel {
    private String manv;
    private YearMonth currentMonth;
    private JPanel daysPanel;
    private JLabel lblMonthTitle;
    private AttendanceDAO attendanceDAO = new AttendanceDAO();

    public AttendanceHistory(String manv) {
        this.manv = manv;
        this.currentMonth = YearMonth.now();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Calendar Nav Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        lblMonthTitle = new JLabel("", SwingConstants.CENTER);
        lblMonthTitle.setFont(new Font("Arial", Font.BOLD, 18));

        JButton btnPrev = new JButton("<");
        JButton btnNext = new JButton(">");
        btnPrev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            renderCalendar();
        });
        btnNext.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            renderCalendar();
        });

        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMonthTitle, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        // Days Header (T2 - CN)
        JPanel weekHeader = new JPanel(new GridLayout(1, 7));
        weekHeader.setOpaque(false);
        String[] days = { "T2", "T3", "T4", "T5", "T6", "T7", "CN" };
        for (String day : days) {
            JLabel lbl = new JLabel(day, SwingConstants.CENTER);
            lbl.setFont(new Font("Arial", Font.BOLD, 12));
            lbl.setForeground(Color.GRAY);
            weekHeader.add(lbl);
        }

        daysPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        daysPanel.setOpaque(false);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(weekHeader, BorderLayout.NORTH);
        centerPanel.add(daysPanel, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        renderCalendar();
    }

    public void renderCalendar() {
        daysPanel.removeAll();
        lblMonthTitle.setText("Tháng " + currentMonth.getMonthValue() + " / " + currentMonth.getYear());

        Map<Integer, String> attendanceData = attendanceDAO.getAttendanceMap(manv, currentMonth.getMonthValue(),
                currentMonth.getYear());

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = Mon

        for (int i = 1; i < dayOfWeek; i++)
            daysPanel.add(new JLabel(""));

        int daysInMonth = currentMonth.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            JPanel daySquare = new JPanel(new BorderLayout());
            daySquare.setPreferredSize(new Dimension(50, 50));
            JLabel lblDay = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            daySquare.add(lblDay, BorderLayout.CENTER);

            String status = attendanceData.getOrDefault(day, "NGHI");
            if (status.equals("Đúng giờ")) {
                daySquare.setBackground(new Color(220, 252, 231));
                lblDay.setForeground(new Color(22, 101, 52));
            } else if (status.equals("Đi muộn") || status.equals("Về sớm")) {
                daySquare.setBackground(new Color(255, 247, 237));
                lblDay.setForeground(new Color(154, 52, 18));
            } else {
                daySquare.setBackground(new Color(248, 249, 250));
                lblDay.setForeground(Color.LIGHT_GRAY);
            }

            daySquare.setBorder(new LineBorder(new Color(240, 240, 240)));
            daysPanel.add(daySquare);
        }
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    public JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);

        JLabel lbl = new JLabel("Tra cứu ngày:");
        lbl.setFont(new Font("Arial", Font.BOLD, 13));

        // Tạo bộ chọn Ngày, Tháng, Năm
        JComboBox<Integer> cbDay = new JComboBox<>();
        JComboBox<Integer> cbMonth = new JComboBox<>();
        JComboBox<Integer> cbYear = new JComboBox<>();

        // Đổ dữ liệu (Ví dụ từ năm 2024 đến 2026)
        for (int i = 1; i <= 31; i++)
            cbDay.addItem(i);
        for (int i = 1; i <= 12; i++)
            cbMonth.addItem(i);
        int currentYear = java.time.LocalDate.now().getYear();
        for (int i = currentYear - 2; i <= currentYear + 1; i++)
            cbYear.addItem(i);

        // Thiết lập mặc định là ngày hiện tại
        cbDay.setSelectedItem(java.time.LocalDate.now().getDayOfMonth());
        cbMonth.setSelectedItem(java.time.LocalDate.now().getMonthValue());
        cbYear.setSelectedItem(currentYear);

        JButton btnSearch = new JButton("Tra cứu chi tiết");
        btnSearch.setBackground(new Color(59, 130, 246));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);

        btnSearch.addActionListener(e -> {
            int day = (int) cbDay.getSelectedItem();
            int month = (int) cbMonth.getSelectedItem();
            int year = (int) cbYear.getSelectedItem();

            AttendanceDAO dao = new AttendanceDAO();
            AttendanceDTO result = dao.getAttendanceDetail(manv, day, month, year);

            if (result == null) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy lịch làm việc hoặc dữ liệu chấm công cho ngày " + day + "/" + month + "/"
                                + year,
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
            } else {
                showDetailPopup(result);
            }
        });

        searchPanel.add(lbl);
        searchPanel.add(cbDay);
        searchPanel.add(new JLabel("/"));
        searchPanel.add(cbMonth);
        searchPanel.add(new JLabel("/"));
        searchPanel.add(cbYear);
        searchPanel.add(btnSearch);

        return searchPanel;
    }

    private void showDetailPopup(AttendanceDTO dto) {
        String message = String.format(
                "<html><body style='width: 200px; padding: 5px;'>" +
                        "<h3 style='color:#3b82f6;'>Chi tiết chấm công</h3>" +
                        "<b>Ngày:</b> %s<br>" +
                        "<b>Trạng thái:</b> %s<br>" +
                        "<b>Giờ vào:</b> %s<br>" +
                        "<b>Giờ ra:</b> %s<br>" +
                        "<b>Số giờ làm:</b> %.1f h" +
                        "</body></html>",
                dto.getNgayLamViec(),
                dto.getTrangThai(),
                (dto.getCheckIn() != null ? dto.getCheckIn() : "--:--"),
                (dto.getCheckOut() != null ? dto.getCheckOut() : "Chưa về"),
                dto.getSoGioLam());
        JOptionPane.showMessageDialog(this, message, "Kết quả tìm kiếm", JOptionPane.INFORMATION_MESSAGE);
    }

}