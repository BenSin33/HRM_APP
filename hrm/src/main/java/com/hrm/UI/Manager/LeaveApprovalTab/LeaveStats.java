package com.hrm.UI.Manager.LeaveApprovalTab;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.UI.Manager.common.ManagerStatCard;

import javax.swing.*;
import java.awt.*;

public class LeaveStats extends JPanel {

    private final ManagerStatCard cardChoDuyet;
    private final ManagerStatCard cardDaDuyet;
    private final ManagerStatCard cardTuChoi;

    public LeaveStats() {
        setLayout(new GridLayout(1, 3, 16, 0));
        setBackground(ColorScheme.MAIN_BG);
        setPreferredSize(new Dimension(0, 110));

        // Chờ duyệt – cam, icon đồng hồ cát
        cardChoDuyet = new ManagerStatCard(
                "Chờ duyệt",
                "0",
                new Color(255, 237, 213),   // orange-100
                ManagerStatCard.makeTextIcon("⏳", new Color(234, 88, 12)),
                new Color(254, 215, 170)    // orange-200
        );

        // Đã duyệt – xanh lá, icon check
        cardDaDuyet = new ManagerStatCard(
                "Đã duyệt",
                "0",
                new Color(220, 252, 231),   // green-100
                ManagerStatCard.makeTextIcon("✓", new Color(22, 163, 74)),
                new Color(187, 247, 208)    // green-200
        );

        // Từ chối – đỏ, icon dấu ✕
        cardTuChoi = new ManagerStatCard(
                "Từ chối",
                "0",
                new Color(254, 226, 226),   // red-100
                ManagerStatCard.makeTextIcon("✕", new Color(220, 38, 38)),
                new Color(254, 202, 202)    // red-200
        );

        add(cardChoDuyet);
        add(cardDaDuyet);
        add(cardTuChoi);
    }

    public void updateStats(int choDuyet, int daDuyet, int tuChoi) {
        cardChoDuyet.setValue(String.valueOf(choDuyet));
        cardDaDuyet.setValue(String.valueOf(daDuyet));
        cardTuChoi.setValue(String.valueOf(tuChoi));
    }
}


