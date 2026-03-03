package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.hrm.DAO.Employee.ScheduleDAO;
import com.hrm.DTO.Employee.ScheduleDTO;

import java.time.LocalDate;
import java.time.ZoneId;

public class ScheduleCenter extends JPanel {
    public ScheduleCenter(LocalDate weekStart, String manv, Runnable onPrev, Runnable onNext) {
        setLayout(new GridLayout(1, 7, 15, 0));
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 25, 20, 25));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        setPreferredSize(new Dimension(980, 340));

        ScheduleDAO dao = new ScheduleDAO();
        List<ScheduleDTO> schedules = dao.getSchedulesForEmployeeAndWeek(manv, weekStart);
        String[] days = {"THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY", "CHỦ NHẬT"};
        SimpleDateFormat dayNumFormat = new SimpleDateFormat("dd/MM");
        // Xác định ngày bắt đầu tuần (thứ 2)
        LocalDate monday = weekStart;
        if (weekStart.getDayOfWeek().getValue() != 1) {
            // Nếu weekStart không phải thứ 2, lùi về thứ 2 gần nhất
            monday = weekStart.minusDays((weekStart.getDayOfWeek().getValue() + 6) % 7);
        }
        // Tạo mảng ngày: thứ 2 -> chủ nhật
        LocalDate[] weekDays = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = monday.plusDays(i);
        }
        // Đưa Chủ nhật xuống cuối cùng (nếu cần)
        LocalDate[] orderedDays = new LocalDate[7];
        for (int i = 0; i < 6; i++) {
            orderedDays[i] = weekDays[i]; // Thứ 2 -> Thứ 7
        }
        orderedDays[6] = weekDays[0].plusDays(6); // Chủ nhật

        for (int i = 0; i < 7; i++) {
            LocalDate d = orderedDays[i];
            Date date = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ScheduleDTO found = null;
            for (ScheduleDTO s : schedules) {
                if (sameDay(s.getDate(), date)) {
                    found = s;
                    break;
                }
            }
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BorderLayout());
            dayPanel.setOpaque(false);
            dayPanel.setPreferredSize(new Dimension(150, 260));
            dayPanel.setMaximumSize(new Dimension(150, 260));
            JPanel card = null;
            if (found != null) {
                String start = found.getStartTime();
                String end = found.getEndTime();
                String time = "-";
                if (start != null && end != null) {
                    // Chỉ lấy giờ và phút
                    if (start.length() >= 5 && end.length() >= 5) {
                        time = start.substring(0,5) + " - " + end.substring(0,5);
                    } else {
                        time = start + " - " + end;
                    }
                }
                String note = found.getDescription() != null ? found.getDescription() : "";
                card = createDayCard(days[i], dayNumFormat.format(date), found.getShift(), found.getShiftName(), time, true, note);
            } else {
                card = createDayCard(days[i], dayNumFormat.format(date), "OFF", "Nghỉ", "-", false, "");
            }
            if (i == 0) {
                JButton btnPrev = new JButton("<");
                btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btnPrev.setFocusPainted(false);
                btnPrev.setPreferredSize(new Dimension(28, 28));
                btnPrev.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                btnPrev.addActionListener(e -> onPrev.run());
                dayPanel.add(btnPrev, BorderLayout.WEST);
            }
            dayPanel.add(card, BorderLayout.CENTER);
            if (i == 6) {
                JButton btnNext = new JButton(">");
                btnNext.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btnNext.setFocusPainted(false);
                btnNext.setPreferredSize(new Dimension(28, 28));
                btnNext.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                btnNext.addActionListener(e -> onNext.run());
                dayPanel.add(btnNext, BorderLayout.EAST);
            }
            add(dayPanel);
        }
    }

    // So sánh 2 ngày chỉ theo ngày/tháng/năm
    private boolean sameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
            && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
            && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    // Overload: nhận thêm ghi chú
    private JPanel createDayCard(String dayName, String dayNum, String code, String shiftName, String time, boolean isWorkDay, String note) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));
        card.setMaximumSize(new Dimension(140, 260));
        card.setPreferredSize(new Dimension(140, 260));

        // Header của card (Thứ và Ngày)
        JLabel lblDay = new JLabel(dayName);
        lblDay.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDay.setForeground(Color.GRAY);
        lblDay.setBorder(new EmptyBorder(10, 0, 4, 0));

        JLabel lblNum = new JLabel(dayNum);
        lblNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNum.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Màu cho từng ca làm
        Color[] bgColors = {new Color(255, 251, 235), new Color(239, 246, 255), new Color(245, 243, 255), new Color(236, 253, 245), new Color(255, 237, 237), new Color(237, 255, 245), new Color(237, 241, 255)};
        Color[] fgColors = {new Color(245, 158, 11), new Color(59, 130, 246), new Color(139, 92, 246), new Color(16, 185, 129), new Color(220, 38, 38), new Color(13, 148, 136), new Color(59, 130, 246)};
        int colorIdx = 0;
        if (isWorkDay) {
            // Đặt màu theo mã ca
            if (code != null && !code.equals("")) {
                // Chuyển mã ca thành số index
                colorIdx = Math.abs(code.hashCode()) % bgColors.length;
            }
        }

        JPanel shiftInfo = new JPanel();
        shiftInfo.setLayout(new BoxLayout(shiftInfo, BoxLayout.Y_AXIS));
        shiftInfo.setPreferredSize(new Dimension(140, 120));
        shiftInfo.setMaximumSize(new Dimension(140, 120));
        if (isWorkDay) {
            shiftInfo.setBackground(bgColors[colorIdx]);
            JLabel lblCode = new JLabel(code, SwingConstants.CENTER);
            lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblCode.setForeground(fgColors[colorIdx]);
            lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblCode.setBorder(new EmptyBorder(0, 0, 4, 0));
            JLabel lblName = new JLabel(shiftName, SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblName.setBorder(new EmptyBorder(0, 0, 4, 0));
            JLabel lblTime = new JLabel(time, SwingConstants.CENTER);
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblTime.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblTime.setVisible(true);
            shiftInfo.add(lblCode);
            shiftInfo.add(lblName);
            shiftInfo.add(lblTime);

            // Hiển thị ghi chú phía dưới nếu có
            JLabel lblNote = new JLabel();
            lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblNote.setForeground(new Color(120, 120, 120));
            lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblNote.setText("<html><body style='width:130px;word-wrap:break-word;'>" + note.replace("\n", "<br>") + "</body></html>");
            lblNote.setBorder(new EmptyBorder(0, 6, 0, 10));
            shiftInfo.add(lblNote);
        } else {
            shiftInfo.setBackground(new Color(243, 244, 246));
            JLabel lblCode = new JLabel(code, SwingConstants.CENTER);
            lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblCode.setForeground(Color.GRAY);
            lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblCode.setBorder(new EmptyBorder(0, 0, 2, 0));
            JLabel lblName = new JLabel("Nghỉ", SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            shiftInfo.add(lblCode);
            shiftInfo.add(lblName);
            shiftInfo.add(Box.createVerticalStrut(10));
        }

        card.add(lblDay);
        card.add(lblNum);
        card.add(shiftInfo);
        return card;
    }
}
