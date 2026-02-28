package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import java.awt.*;

/**
 * Lớp quản lý chính cho tab Lịch làm việc của nhân viên.
 * Kết hợp Header (Tiêu đề/Điều hướng), Center (Lịch 7 ngày) và Footer (Chú thích).
 */
public class ScheduleManage extends JPanel {
    private String manv;
    private ScheduleHeader header;
    private ScheduleCenter center;
    private ScheduleFooter footer;

    public ScheduleManage(String manv) {
        this.manv = manv;
        
        // Thiết lập layout chính là BorderLayout để các thành phần khít với nhau
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt đồng bộ toàn hệ thống

        // 1. Khởi tạo các thành phần con
        header = new ScheduleHeader();
        center = new ScheduleCenter();
        footer = new ScheduleFooter();

        // 2. Thêm các thành phần vào Panel chính theo vị trí
        add(header, BorderLayout.NORTH);  // Phần tiêu đề và chọn tuần
        add(center, BorderLayout.CENTER); // Phần nội dung chính hiển thị 7 cột lịch
        add(footer, BorderLayout.SOUTH);  // Phần chú thích các loại ca làm việc bên dưới
    }

    /**
     * Phương thức làm mới dữ liệu lịch làm việc.
     * Hữu ích khi người dùng chuyển tuần hoặc dữ liệu từ DB thay đổi.
     */
    public void refreshSchedule() {
        // Xóa các component cũ
        removeAll();
        
        // Khởi tạo lại với dữ liệu mới (Nếu sau này bạn truyền Date vào)
        header = new ScheduleHeader();
        center = new ScheduleCenter();
        footer = new ScheduleFooter();
        
        add(header, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);
        
        // Vẽ lại giao diện
        revalidate();
        repaint();
    }
}