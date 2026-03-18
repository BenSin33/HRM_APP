package com.hrm.UI.Employee.HomeEmp;

import com.hrm.Service.AccountManagerService;
import com.hrm.UI.component.Sidebar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;

public class HomeFooter extends JPanel {

    private String manv;
    private CardLayout cardLayout;
    private JPanel cardContainer;
    private AccountManagerService accountManagerService;
    private Sidebar sidebar;

    public HomeFooter(String manv, CardLayout cardLayout, JPanel cardContainer, Sidebar sidebar) {
        this.manv = manv;
        this.cardLayout = cardLayout;
        this.cardContainer = cardContainer;
        this.accountManagerService = new AccountManagerService();
        this.sidebar = sidebar;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(0, 20, 20, 20));

        // Tiêu đề phần
        JLabel title = new JLabel("Thao tác nhanh");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Grid chứa các nút chức năng (1 hàng, 4 cột)
        JPanel actionsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        actionsGrid.setOpaque(false);

        actionsGrid.add(createActionBtn("Chấm công", new Color(59, 130, 246), "Ghi nhận giờ vào/ra", "ATTENDANCE", createActionIcon("attendance", new Color(59, 130, 246), 26)));
        actionsGrid.add(createActionBtn("Xin nghỉ phép", new Color(37, 99, 235), "Xin nghỉ phép", "LEAVE", createActionIcon("leave", new Color(37, 99, 235), 26)));
        actionsGrid.add(createActionBtn("Bảng lương", new Color(31, 41, 55), "Xem bảng lương", "PAYROLL", createActionIcon("payroll", new Color(31, 41, 55), 26)));
        actionsGrid.add(createActionBtn("Đánh giá", new Color(79, 70, 229), "Đánh giá hiệu suất", "EVALUATION", createActionIcon("evaluation", new Color(79, 70, 229), 26)));

        add(actionsGrid, BorderLayout.CENTER);

        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 15));
        passwordPanel.setOpaque(false);

        JButton btnChangePassword = new JButton("Đổi mật khẩu");
        btnChangePassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnChangePassword.setFocusPainted(false);
        btnChangePassword.setBackground(new Color(59, 130, 246));
        btnChangePassword.setForeground(Color.WHITE);
        btnChangePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChangePassword.addActionListener(e -> openChangePasswordDialog());

        passwordPanel.add(btnChangePassword);
        add(passwordPanel, BorderLayout.SOUTH);
    }

    private void openChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));

        JLabel lblNewPass = new JLabel("Mật khẩu mới:");
        JPasswordField txtNewPass = new JPasswordField();

        JLabel lblConfirmPass = new JLabel("Xác nhận mật khẩu:");
        JPasswordField txtConfirmPass = new JPasswordField();

        panel.add(lblNewPass);
        panel.add(txtNewPass);
        panel.add(lblConfirmPass);
        panel.add(txtConfirmPass);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Đổi mật khẩu",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword = new String(txtNewPass.getPassword()).trim();
        String confirmPassword = new String(txtConfirmPass.getPassword()).trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin mật khẩu.");
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới phải có ít nhất 6 ký tự.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp.");
            return;
        }

        boolean changed = accountManagerService.changePasswordByManv(manv, newPassword);
        if (changed) {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công.");
        } else {
            JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại. Vui lòng thử lại.");
        }
    }

    private JPanel createActionBtn(String label, Color iconColor, String noteString, String cardName, ImageIcon icon) {
        // Tạo panel đại diện cho nút bấm
        JPanel btn = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                // Vẽ viền nhẹ
                g2.setColor(new Color(230, 230, 230));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setPreferredSize(new Dimension(40, 40));

        // Nhãn text
        JLabel lblName = new JLabel(label, SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(new Color(55, 65, 81));

        btn.add(iconLabel, BorderLayout.NORTH);
        btn.add(lblName, BorderLayout.CENTER);

        // Thêm nhãn mô tả
        JLabel lblNote = new JLabel(noteString, SwingConstants.CENTER);
        lblNote.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblNote.setForeground(Color.GRAY);
        btn.add(lblNote, BorderLayout.SOUTH);

        // Hiệu ứng Hover + click chuyển trang
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (cardLayout != null && cardContainer != null && cardName != null) {
                    if (sidebar != null) {
                        sidebar.selectTabByCardName(cardName);
                    } else {
                        cardLayout.show(cardContainer, cardName);
                    }
                }
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(249, 250, 251));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
            }
        });

        return btn;
    }

    private ImageIcon createActionIcon(String type, Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {
            case "attendance":
                g2d.drawOval(3, 3, size - 6, size - 6);
                g2d.drawLine(size / 2, size / 2, size / 2, 7);
                g2d.drawLine(size / 2, size / 2, size - 8, size / 2 + 3);
                break;
            case "leave":
                g2d.drawRoundRect(4, 5, size - 8, size - 8, 4, 4);
                g2d.drawLine(7, 3, 7, 8);
                g2d.drawLine(size - 8, 3, size - 8, 8);
                break;
            case "payroll":
                g2d.drawRoundRect(3, 7, size - 6, size - 12, 4, 4);
                g2d.drawLine(6, size / 2, size - 6, size / 2);
                break;
            case "evaluation":
                Polygon star = new Polygon();
                int cx = size / 2;
                int cy = size / 2;
                int outer = size / 2 - 2;
                int inner = outer / 2;
                for (int i = 0; i < 10; i++) {
                    double angle = Math.toRadians(-90 + i * 36);
                    int r = (i % 2 == 0) ? outer : inner;
                    star.addPoint(cx + (int) (Math.cos(angle) * r), cy + (int) (Math.sin(angle) * r));
                }
                g2d.drawPolygon(star);
                break;
            default:
                g2d.fillOval(4, 4, size - 8, size - 8);
                break;
        }

        g2d.dispose();
        return new ImageIcon(image);
    }
}