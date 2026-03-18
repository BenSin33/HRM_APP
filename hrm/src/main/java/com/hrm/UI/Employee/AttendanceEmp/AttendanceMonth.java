package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import com.hrm.Service.Employee.AttendanceService;
import com.hrm.DTO.Employee.AttendanceDTO;

public class AttendanceMonth extends JPanel {
    private String manv;
    private YearMonth currentMonth;
    private JPanel daysPanel;
    private JLabel lblMonthTitle;
    private AttendanceService attendanceDAO = new AttendanceService();

    public AttendanceMonth(String manv) {
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
            lbl.setFont(new Font("Arial", Font.BOLD, 20));
            lbl.setForeground(new Color(44, 62, 80));
            weekHeader.add(lbl);
        }

        daysPanel = new JPanel(new GridLayout(0, 7, 8, 8));
        daysPanel.setOpaque(false);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);
        centerPanel.add(weekHeader, BorderLayout.NORTH);
        centerPanel.add(daysPanel, BorderLayout.CENTER);

        // Chú thích màu và trạng thái
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 5));
        legendPanel.setOpaque(false);
        legendPanel.add(createLegendItem(new Color(220, 252, 231), new Color(22, 101, 52), "Đúng giờ"));
        legendPanel.add(createLegendItem(new Color(255, 247, 237), new Color(154, 52, 18), "Đi muộn/Về sớm"));
        legendPanel.add(createLegendItem(new Color(248, 249, 250), Color.LIGHT_GRAY, "Nghỉ"));
        centerPanel.add(legendPanel, BorderLayout.SOUTH);

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
            daySquare.setPreferredSize(new Dimension(60, 60));
            daySquare.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            JLabel lblDay = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            lblDay.setFont(new Font("Arial", Font.BOLD, 22));
            daySquare.add(lblDay, BorderLayout.CENTER);

            String status = attendanceData.getOrDefault(day, "NGHI");
            if ("1".equals(status) || "Đúng giờ".equals(status)) {
                daySquare.setBackground(new Color(220, 252, 231)); // xanh lá nhạt
                lblDay.setForeground(new Color(22, 101, 52)); // xanh lá đậm
            } else if ("0".equals(status) || "Đi muộn".equals(status) || "Về sớm".equals(status) || "Đi muộn/Về sớm".equals(status)) {
                daySquare.setBackground(new Color(255, 247, 237)); // cam nhạt
                lblDay.setForeground(new Color(154, 52, 18)); // cam đậm
            } else {
                daySquare.setBackground(new Color(248, 249, 250)); // xám nhạt
                lblDay.setForeground(Color.LIGHT_GRAY);
            }

            daySquare.setBorder(new LineBorder(new Color(200, 200, 200), 2));
            final int selectedDay = day;
            daySquare.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showAttendanceDetailForDay(selectedDay);
                }
            });
            daysPanel.add(daySquare);
        }
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void showAttendanceDetailForDay(int day) {
        AttendanceDTO detail = attendanceDAO.getAttendanceDetail(manv, day, currentMonth.getMonthValue(), currentMonth.getYear());
        String dateText = String.format("%02d/%02d/%d", day, currentMonth.getMonthValue(), currentMonth.getYear());

        if (detail == null) {
            JOptionPane.showMessageDialog(this,
                    "Ngày " + dateText + " chưa có dữ liệu chấm công.",
                    "Chi tiết chấm công",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String caLam = detail.getMaCaLam() != null ? detail.getMaCaLam() : "-";
        String checkIn = detail.getCheckIn() != null ? detail.getCheckIn().toString().substring(0, 5) : "-";
        String checkOut = detail.getCheckOut() != null ? detail.getCheckOut().toString().substring(0, 5) : "-";
        String soGioLam = detail.getSoGioLam() > 0 ? String.format("%.1f giờ", detail.getSoGioLam()) : "-";
        String trangThai = detail.getTrangThai() != null ? detail.getTrangThai() : "-";

        String message = "Ngày: " + dateText
                + "\nCa làm: " + caLam
                + "\nGiờ vào: " + checkIn
                + "\nGiờ ra: " + checkOut
                + "\nSố giờ làm: " + soGioLam
                + "\nTrạng thái: " + trangThai;

        JOptionPane.showMessageDialog(this,
                message,
                "Chi tiết chấm công",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Tạo chú thích màu
    private JPanel createLegendItem(Color bg, Color fg, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        JLabel colorBox = new JLabel();
        colorBox.setPreferredSize(new Dimension(22, 22));
        colorBox.setOpaque(true);
        colorBox.setBackground(bg);
        colorBox.setBorder(new LineBorder(new Color(180,180,180), 1));
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.PLAIN, 15));
        lbl.setForeground(fg);
        panel.add(colorBox);
        panel.add(lbl);
        return panel;
    }
}