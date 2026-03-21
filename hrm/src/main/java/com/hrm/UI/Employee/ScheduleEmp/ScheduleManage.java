package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import java.awt.*;

/**
 * Lớp quản lý chính cho tab Lịch làm việc của nhân viên.
 * Kết hợp Header (Tiêu đề/Điều hướng), Center (Lịch 7 ngày) và Footer (Chú thích).
 */
import java.time.LocalDate;

public class ScheduleManage extends JPanel {
    private String manv;
    private ScheduleHeader header;
    private ScheduleCenter center;
    private ScheduleFooter footer;
    private LocalDate weekStart; // ngày đầu tuần đang xem

    public ScheduleManage(String manv) {
        this.manv = manv;
        // Xác định ngày đầu tuần hiện tại theo chuẩn ISO (Thứ 2)
        LocalDate now = LocalDate.now();
        weekStart = now.minusDays(now.getDayOfWeek().getValue() - 1);

        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 249, 250));

        header = new ScheduleHeader(weekStart);
        center = new ScheduleCenter(weekStart, manv, this::goToPrevWeek, this::goToNextWeek);
        footer = new ScheduleFooter();
        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
    }

    // Chuyển sang tuần trước
    private void goToPrevWeek() {
        weekStart = weekStart.minusWeeks(1);
        reloadWeek();
    }

    // Chuyển sang tuần sau
    private void goToNextWeek() {
        weekStart = weekStart.plusWeeks(1);
        reloadWeek();
    }

    // Tải lại UI theo tuần mới
    private void reloadWeek() {
        remove(header);
        remove(center);
        remove(footer);
        header = new ScheduleHeader(weekStart);
        center = new ScheduleCenter(weekStart, manv, this::goToPrevWeek, this::goToNextWeek);
        footer = new ScheduleFooter();
        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    /**
     * Phương thức làm mới dữ liệu lịch làm việc.
     * Hữu ích khi người dùng chuyển tuần hoặc dữ liệu từ DB thay đổi.
     */
    public void refreshSchedule() {
        removeAll();
        header = new ScheduleHeader(weekStart);
        center = new ScheduleCenter(weekStart, manv, this::goToPrevWeek, this::goToNextWeek);
        footer = new ScheduleFooter();
        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }
}