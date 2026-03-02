package com.hrm.UI.Employee.HomeEmp;

import javax.swing.*;
import java.awt.*;

public class HomeManage extends JPanel {

    public HomeManage(String manv) {
        // Sử dụng BorderLayout cho container chính
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));

        // Tạo một Panel chứa toàn bộ nội dung (Content Panel)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // 1. Thêm phần Header (Lời chào + 4 thẻ thống kê)
        contentPanel.add(new HomeHeader(manv));

        // Khoảng cách giữa các phần
        contentPanel.add(Box.createVerticalStrut(10));

        // 2. Thêm phần Hoạt động & Lịch (Phần ở giữa)
        contentPanel.add(new HomeReport(manv));

        // Khoảng cách
        contentPanel.add(Box.createVerticalStrut(10));

        // 3. Thêm phần Thao tác nhanh (Phần dưới cùng)
        contentPanel.add(new HomeFooter());

        // Cuối cùng thêm một khoảng trống linh hoạt phía dưới để các phần không bị giãn quá mức
        contentPanel.add(Box.createVerticalGlue());

        // Bọc contentPanel vào JScrollPane để có thể cuộn trang
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null); // Xóa viền ScrollPane để giao diện phẳng (flat)
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Giúp cuộn mượt hơn
        scrollPane.getViewport().setBackground(new Color(248, 249, 250)); // Đồng bộ màu nền

        add(scrollPane, BorderLayout.CENTER);
    }
}