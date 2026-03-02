package com.hrm.UI.HR.SalaryTab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class SalaryManagement extends JPanel {
    public SalaryManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");

        // 1. Khởi tạo Header và truyền sự kiện (Xử lý lỗi không tìm thấy constructor)
        SalaryHeader header = new SalaryHeader(
            e -> handleLockSalary(),   // Sự kiện khi bấm Khóa lương
            e -> handleUnlockSalary()  // Sự kiện khi bấm Mở khóa
        );
        this.add(header, BorderLayout.NORTH);

        // 2. Nội dung chính
        JPanel content = new JPanel(new BorderLayout(0, 20));
        content.setOpaque(false);

        // Phần B: Thống kê (Summary)
        content.add(new SalarySummary(), BorderLayout.NORTH);
        
        // Phần C: Bảng dữ liệu
        SalaryTable salaryTableContent = new SalaryTable();
        content.add(salaryTableContent, BorderLayout.CENTER);

        this.add(content, BorderLayout.CENTER);
    }

    private void handleLockSalary() {
        // Dự định: Gọi xuống Service để đổi trạng thái toàn bộ record sang 'locked'
        JOptionPane.showMessageDialog(this, "Đã thực hiện khóa lương tháng này!");
    }

    private void handleUnlockSalary() {
        // Dự định: Cho phép chỉnh sửa lại bảng lương
        System.out.println("Mở khóa bảng lương...");
    }
}