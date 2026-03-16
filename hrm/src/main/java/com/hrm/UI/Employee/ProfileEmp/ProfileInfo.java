package com.hrm.UI.Employee.ProfileEmp;

import com.hrm.DTO.Employee.ProfileDTO;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class ProfileInfo extends JPanel {
    public ProfileInfo(ProfileDTO data) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 246, 250));

        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(245, 246, 250));
        container.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 25, 0);

        // Sử dụng dữ liệu từ DTO thay vì Map
        container.add(createCard("Thông tin cá nhân", new String[][]{
            {"Email", data.email}, {"Số điện thoại", data.sdt},
            {"Ngày sinh", data.ngaySinh}, {"Giới tính", data.gioiTinh},
            {"Trình độ", data.trinhDo}, {"Địa chỉ", data.diaChi}
        }), gbc);

        container.add(createCard("Thông tin công việc", new String[][]{
            {"Phòng ban", data.phongBan}, {"Chức vụ", data.chucVu},
            {"Ngày vào làm", data.ngayVaoLam}, {"Trạng thái", data.trangThai}
        }), gbc);

        container.add(createCard("Thông tin hợp đồng", new String[][]{
            {"Mã hợp đồng", data.maHD}, {"Loại hợp đồng", data.loaiHD},
            {"Ngày ký", data.ngayKy}, {"Ngày hết hạn", data.ngayHetHan},
            {"Lương cơ bản", data.luongCoBan}
        }), gbc);

        gbc.weighty = 1.0;
        container.add(Box.createGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Tinh chỉnh thanh cuộn cho chuyên nghiệp (Mac-style)
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(200, 200, 200);
                this.trackColor = new Color(245, 246, 250);
            }
            @Override
            protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }
            private JButton createZeroButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                return b;
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String[][] details) {
        JPanel card = new JPanel(new BorderLayout(0, 20)); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            new EmptyBorder(25, 30, 25, 30)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(0, 2, 40, 20));
        content.setOpaque(false);

        for (String[] row : details) {
            JPanel f = new JPanel(new BorderLayout(0, 5));
            f.setOpaque(false);
            JLabel k = new JLabel(row[0]);
            k.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            k.setForeground(Color.GRAY);
            JLabel v = new JLabel(row[1] != null ? row[1] : "N/A");
            v.setFont(new Font("Segoe UI", Font.BOLD, 14));
            f.add(k, BorderLayout.NORTH);
            f.add(v, BorderLayout.CENTER);
            content.add(f);
        }
        card.add(content, BorderLayout.CENTER);
        return card;
    }
}