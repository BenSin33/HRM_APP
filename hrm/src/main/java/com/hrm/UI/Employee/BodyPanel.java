package com.hrm.UI.Employee;

import javax.swing.*;
import java.awt.*;

public class BodyPanel extends JPanel {
    public BodyPanel() {
        setLayout(new BorderLayout(0, 30)); // Khoảng cách giữa các phần là 30px
        setOpaque(false);

        // 1. Panel chứa 4 thẻ thống kê trên cùng
        JPanel gridPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        gridPanel.setOpaque(false);

        gridPanel.add(new StatCardPanel("Số ngày phép còn lại", "12 ngày", Color.BLUE));
        gridPanel.add(new StatCardPanel("Lương tháng này", "22.5 triệu", Color.GREEN));
        gridPanel.add(new StatCardPanel("Số giờ làm tháng này", "168 giờ", Color.MAGENTA));
        gridPanel.add(new StatCardPanel("Điểm đánh giá Q4", "95/100", Color.ORANGE));

        add(gridPanel, BorderLayout.NORTH);

        // 2. Phần "Hoạt động gần đây" (Ví dụ một panel trắng lớn)
        JPanel activityPanel = new JPanel();
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // Bạn có thể vẽ thêm danh sách hoạt động vào đây...
        activityPanel.add(new JLabel("Hoạt động gần đây sẽ hiển thị tại đây..."));
        
        // Thêm bo góc cho activityPanel tương tự như StatCard nếu muốn
        add(activityPanel, BorderLayout.CENTER);
    }
}
