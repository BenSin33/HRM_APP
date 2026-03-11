package com.hrm.UI.Manager.evaluation.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScoreRadioPanel extends JPanel {
    private String maTieuChi;
    private String tenTieuChi;
    private ButtonGroup buttonGroup;
    private JRadioButton[] radioButtons;
    private boolean locked;

    public ScoreRadioPanel(String maTieuChi, String tenTieuChi, int selectedScore, boolean locked) {
        this.maTieuChi = maTieuChi;
        this.tenTieuChi = tenTieuChi;
        this.locked = locked;
        
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Mã tiêu chí
        JLabel maLabel = new JLabel(maTieuChi);
        maLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        maLabel.setPreferredSize(new Dimension(60, 30));
        add(maLabel, BorderLayout.WEST);

        // Tên tiêu chí
        JLabel tenLabel = new JLabel(tenTieuChi);
        tenLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tenLabel.setPreferredSize(new Dimension(200, 30));
        add(tenLabel, BorderLayout.CENTER);

        // Panel chứa Radio Button
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        radioPanel.setBackground(Color.WHITE);
        
        buttonGroup = new ButtonGroup();
        radioButtons = new JRadioButton[11]; // 0-10
        
        for (int i = 0; i <= 10; i++) {
            JRadioButton rb = new JRadioButton(String.valueOf(i));
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rb.setBackground(Color.WHITE);
            
            // QUAN TRỌNG: Enable/disable theo trạng thái locked
            rb.setEnabled(!locked);
            
            // Thêm tooltip để biết có thể click không
            if (locked) {
                rb.setToolTipText("Đã khóa, không thể chỉnh sửa");
            }
            
            // Màu sắc theo điểm
            if (i <= 3) {
                rb.setForeground(new Color(239, 68, 68)); // Đỏ
            } else if (i <= 6) {
                rb.setForeground(new Color(251, 146, 60)); // Cam
            } else {
                rb.setForeground(new Color(34, 197, 94)); // Xanh lá
            }
            
            // Chọn radio nếu là điểm hiện tại
            if (i == selectedScore) {
                rb.setSelected(true);
            }
            
            // THÊM MOUSE LISTENER ĐỂ KIỂM TRA CLICK
            final int score = i;
            rb.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!locked) {
                        System.out.println("Đã chọn điểm " + score + " cho " + maTieuChi);
                    }
                }
            });
            
            buttonGroup.add(rb);
            radioButtons[i] = rb;
            radioPanel.add(rb);
        }
        
        add(radioPanel, BorderLayout.EAST);
    }

    public String getMaTieuChi() {
        return maTieuChi;
    }

    public int getSelectedScore() {
        for (int i = 0; i <= 10; i++) {
            if (radioButtons[i].isSelected()) {
                return i;
            }
        }
        return 0;
    }

    public void resetToZero() {
        if (!locked) {
            radioButtons[0].setSelected(true);
        }
    }
    public void setEnabled(boolean enabled) {
    for (JRadioButton rb : radioButtons) {
        rb.setEnabled(enabled);
    }
    repaint();
}
}