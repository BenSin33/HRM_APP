package com.hrm.UI.Employee.ProfileEmp;

import com.hrm.DTO.Employee.ProfileDTO;

import java.awt.*;
import javax.swing.*;

public class ProfileSidebar extends JPanel {
    
    public ProfileSidebar(ProfileDTO data) {
        setPreferredSize(new Dimension(300, 0)); // Tăng chiều rộng lên một chút cho thoáng
        setBackground(Color.WHITE);
        // Viền phải mảnh ngăn cách với phần Info
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(235, 235, 235)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(40));

        // 1. Avatar hình tròn với Gradient
        String firstLetter = (data.hoTen != null && !data.hoTen.isEmpty()) 
                             ? data.hoTen.substring(0, 1).toUpperCase() : "?";
        
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Đổ màu Gradient cho Avatar
                GradientPaint gp = new GradientPaint(0, 0, new Color(59, 130, 246), 100, 100, new Color(37, 99, 235));
                g2.setPaint(gp);
                g2.fillOval(0, 0, 100, 100);
                
                // Vẽ chữ cái đầu
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 40));
                FontMetrics fm = g2.getFontMetrics();
                int x = (100 - fm.stringWidth(firstLetter)) / 2;
                int y = ((100 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(firstLetter, x, y);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(100, 100));
        avatarCircle.setMaximumSize(new Dimension(100, 100));
        avatarCircle.setOpaque(false);
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(avatarCircle);

        add(Box.createVerticalStrut(15));

        // 2. Tên nhân viên
        JLabel lblName = new JLabel(data.hoTen);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblName.setForeground(new Color(33, 37, 41));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblName);

        add(Box.createVerticalStrut(5));

        // 3. Chức vụ
        JLabel lblRole = new JLabel(data.chucVu);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblRole.setForeground(Color.GRAY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblRole);

        add(Box.createVerticalStrut(15));

        // 4. Status Badge (Nhãn trạng thái)
        JLabel lblStatus = new JLabel(data.trangThai != null ? data.trangThai : "Đang làm việc");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(new Color(22, 163, 74)); // Màu xanh lá
        lblStatus.setBackground(new Color(220, 252, 231)); // Nền xanh nhạt
        lblStatus.setOpaque(true);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Bo góc cho Badge (Dùng mẹo nhỏ hoặc Override paintComponent nếu cần bo tròn mạnh)
        add(lblStatus);

        add(Box.createVerticalStrut(30));
        
        // Đường kẻ ngang phân cách
        JSeparator line = new JSeparator();
        line.setMaximumSize(new Dimension(220, 1));
        line.setForeground(new Color(240, 240, 240));
        add(line);

        add(Box.createVerticalStrut(30));

        // 5. Thông tin nhanh phía dưới
        add(createSidebarItem("Mã nhân viên", data.maNV));
        add(Box.createVerticalStrut(15));
        add(createSidebarItem("Email", data.email));
        add(Box.createVerticalStrut(15));
        add(createSidebarItem("Số điện thoại", data.sdt));

        add(Box.createVerticalGlue());
    }

    private JPanel createSidebarItem(String label, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(240, 45));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(new Font("Segoe UI", Font.BOLD, 14));
        val.setForeground(new Color(50, 50, 50));
        val.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(lbl);
        panel.add(val);
        return panel;
    }
}