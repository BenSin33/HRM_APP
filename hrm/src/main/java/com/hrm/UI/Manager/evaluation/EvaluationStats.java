package com.hrm.UI.Manager.evaluation;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.UI.Manager.common.ManagerStatCard;

import javax.swing.*;
import java.awt.*;

public class EvaluationStats extends JPanel {

    private final ManagerStatCard cardTongNhanVien;
    private final ManagerStatCard cardDaHoanThanh;
    private final ManagerStatCard cardChuaDanhGia;

    public EvaluationStats() {
        setLayout(new GridLayout(1, 3, 16, 0));
        setBackground(ColorScheme.MAIN_BG);
        setPreferredSize(new Dimension(0, 120));

        // Tổng số nhân viên – xanh dương
        cardTongNhanVien = new ManagerStatCard(
                "Tổng số nhân viên",
                "0",
                new Color(219, 234, 254),   // blue-100
                ManagerStatCard.makeTextIcon("👥", new Color(37, 99, 235)),
                new Color(191, 219, 254)    // blue-200
        );

        // Đã hoàn thành – xanh lá, icon check
        cardDaHoanThanh = new ManagerStatCard(
                "Đã hoàn thành",
                "0",
                new Color(220, 252, 231),   // green-100
                ManagerStatCard.makeTextIcon("✓", new Color(22, 163, 74)),
                new Color(187, 247, 208)    // green-200
        );

        // Chưa đánh giá – cam, icon đồng hồ
        cardChuaDanhGia = new ManagerStatCard(
                "Chưa đánh giá",
                "0",
                new Color(255, 237, 213),   // orange-100
                ManagerStatCard.makeTextIcon("⏰", new Color(234, 88, 12)),
                new Color(254, 215, 170)    // orange-200
        );

        add(cardTongNhanVien);
        add(cardDaHoanThanh);
        add(cardChuaDanhGia);
    }

    public void updateStats(int tongNV, int daHoan, int chuaDG) {
        cardTongNhanVien.setValue(String.valueOf(tongNV));
        cardDaHoanThanh.setValue(String.valueOf(daHoan));
        cardChuaDanhGia.setValue(String.valueOf(chuaDG));
    }
}

