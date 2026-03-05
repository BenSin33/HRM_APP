package com.hrm.UI.Manager.evaluation;
import javax.swing.*;
import java.awt.*;

public class EvaluationRow extends JPanel {
    private String ten;
    private String maNV;
    private String chucVu;
    private String team;
    private String trangThai;

    public EvaluationRow(Object[] row) {
        this.ten = row[0].toString();
        this.maNV = row[1].toString();
        this.chucVu = row[2].toString();
        this.team = row[3].toString();
        this.trangThai = row[4].toString();

        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(15, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Avatar icon
        JPanel avatarPanel = new JPanel(new GridBagLayout());
        avatarPanel.setBackground(new Color(237, 233, 254));
        avatarPanel.setPreferredSize(new Dimension(50, 50));

        JLabel avatarIcon = new JLabel("👤");
        avatarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        avatarIcon.setForeground(new Color(99, 102, 241));
        avatarPanel.add(avatarIcon);

        // Bọc avatar để căn giữa dọc
        JPanel avatarWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        avatarWrapper.setBackground(Color.WHITE);
        avatarWrapper.add(avatarPanel);

        // Thông tin nhân viên
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel tenLabel = new JLabel(ten);
        tenLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel maNVChucVuLabel = new JLabel(maNV + " - " + chucVu);
        maNVChucVuLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        maNVChucVuLabel.setForeground(new Color(100, 100, 100));

        JLabel teamLabel = new JLabel(team);
        teamLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        teamLabel.setForeground(new Color(150, 150, 150));

        infoPanel.add(tenLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(maNVChucVuLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(teamLabel);

        // Trạng thái badge
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 22));
        rightPanel.setBackground(Color.WHITE);

        JLabel badge = new JLabel(trangThai);
        badge.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 14, 5, 14));
        setTrangThaiStyle(badge);
        rightPanel.add(badge);

        add(avatarWrapper, BorderLayout.WEST);
        add(infoPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setBackground(new Color(250, 250, 255));
                infoPanel.setBackground(new Color(250, 250, 255));
                avatarWrapper.setBackground(new Color(250, 250, 255));
                rightPanel.setBackground(new Color(250, 250, 255));
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setBackground(Color.WHITE);
                infoPanel.setBackground(Color.WHITE);
                avatarWrapper.setBackground(Color.WHITE);
                rightPanel.setBackground(Color.WHITE);
            }
        });
    }

    private void setTrangThaiStyle(JLabel label) {
        switch (trangThai) {
            case "Đã hoàn thành":
                label.setBackground(new Color(220, 252, 231));
                label.setForeground(new Color(22, 101, 52));
                break;
            case "Chưa đánh giá":
            default:
                label.setBackground(new Color(254, 243, 199));
                label.setForeground(new Color(146, 64, 14));
                break;
        }
    }

    public String getTrangThai() {
        return trangThai;
    }
}