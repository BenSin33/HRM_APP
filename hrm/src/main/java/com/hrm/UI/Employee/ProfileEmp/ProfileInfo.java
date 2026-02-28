package com.hrm.UI.Employee.ProfileEmp;
import java.awt.*;
import java.util.Map;

import javax.swing.*;

class ProfileInfo extends JPanel {
    public ProfileInfo(Map<String, String> data) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        setBackground(new Color(245, 246, 250));

        // Group 1: Thông tin cá nhân
        add(createCard("Thông tin cá nhân", new String[][]{
            {"Email", data.get("email")},
            {"Số điện thoại", data.get("sdt")},
            {"Giới tính", data.get("gioiTinh")},
            {"Địa chỉ", data.get("diaChi")}
        }, 400, 200));

        // Group 2: Thông tin công việc
        add(createCard("Thông tin công việc", new String[][]{
            {"Phòng ban", data.get("phongBan")},
            {"Chức vụ", data.get("chucVu")},
            {"Ngày vào làm", data.get("ngayVaoLam")}
        }, 400, 200));
    }

    private JPanel createCard(String title, String[][] details, int w, int h) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(w, h));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(details.length, 2, 5, 5));
        content.setOpaque(false);
        for(String[] row : details) {
            content.add(new JLabel(row[0] + ":"));
            content.add(new JLabel("<html><b>" + row[1] + "</b></html>"));
        }
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}
