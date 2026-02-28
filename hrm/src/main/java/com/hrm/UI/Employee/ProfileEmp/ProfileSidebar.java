package com.hrm.UI.Employee.ProfileEmp;

import java.awt.*;
import javax.swing.*;
import java.util.Map;


public class ProfileSidebar extends JPanel {
    public ProfileSidebar(Map<String, String> info) {
        setPreferredSize(new Dimension(280, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(Box.createVerticalStrut(40));

        // 1. Vẽ Avatar tròn với chữ cái đầu của tên
        String firstLetter = info.get("name").substring(0, 1).toUpperCase();
        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(59, 130, 246)); // Màu xanh chủ đạo
                g2.fillOval(0, 0, 100, 100);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 40));
                FontMetrics fm = g2.getFontMetrics();
                int x = (100 - fm.stringWidth(firstLetter)) / 2;
                int y = ((100 - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(firstLetter, x, y);
                g2.dispose();
            }
        };
        avatarCircle.setMaximumSize(new Dimension(100, 100));
        avatarCircle.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(avatarCircle);

        add(Box.createVerticalStrut(20));

        // 2. Tên nhân viên
        JLabel lblName = new JLabel(info.get("name"));
        lblName.setFont(new Font("Arial", Font.BOLD, 18));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblName);

        // 3. Chức vụ (Senior Developer, v.v.)
        JLabel lblRole = new JLabel(info.get("role"));
        lblRole.setFont(new Font("Arial", Font.PLAIN, 14));
        lblRole.setForeground(Color.GRAY);
        lblRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblRole);

        // 4. Mã nhân viên
        JLabel lblId = new JLabel("Mã NV: " + info.get("id"));
        lblId.setFont(new Font("Arial", Font.ITALIC, 12));
        lblId.setForeground(new Color(160, 160, 160));
        lblId.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblId);

        add(Box.createVerticalStrut(15));

        // 5. Badge Trạng thái
        JLabel lblStatus = new JLabel("  " + info.get("status") + "  ");
        lblStatus.setOpaque(true);
        lblStatus.setBackground(new Color(220, 252, 231)); // Xanh lá nhạt
        lblStatus.setForeground(new Color(21, 128, 61));    // Xanh lá đậm
        lblStatus.setFont(new Font("Arial", Font.BOLD, 11));
        lblStatus.setBorder(BorderFactory.createLineBorder(new Color(187, 247, 208)));
        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblStatus);
        
        // Thêm một vài thông tin phụ cho chuyên nghiệp
        add(Box.createVerticalStrut(30));
        addDetailRow("Email", info.get("email"));
        addDetailRow("SĐT", info.get("sdt"));

        add(Box.createVerticalGlue());
    }

    private void addDetailRow(String label, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label + ": ");
        lbl.setForeground(Color.GRAY);
        lbl.setFont(new Font("Arial", Font.PLAIN, 11));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.BOLD, 11));
        row.add(lbl);
        row.add(val);
        add(row);
    }
}
