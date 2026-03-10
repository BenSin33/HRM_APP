package com.hrm.UI.Manager.team;

import com.hrm.UI.Manager.color.ColorScheme;
import com.hrm.UI.Manager.common.ManagerStatCard;

import javax.swing.*;
import java.awt.*;

public class TeamStats extends JPanel {

    private final ManagerStatCard cardTotal;
    private final ManagerStatCard cardActive;
    private final ManagerStatCard cardSenior;
    private final ManagerStatCard cardJunior;

    public TeamStats() {
        setLayout(new GridLayout(1, 4, 16, 0));
        setBackground(ColorScheme.MAIN_BG);

        // Tổng thành viên – xanh dương
        cardTotal = new ManagerStatCard(
                "Tổng thành viên",
                "0",
                new Color(219, 234, 254),   // blue-100
                ManagerStatCard.makeTextIcon("👥", new Color(37, 99, 235)),
                new Color(191, 219, 254)    // blue-200
        );

        // Đang hoạt động – xanh lá
        cardActive = new ManagerStatCard(
                "Đang hoạt động",
                "0",
                new Color(220, 252, 231),   // green-100
                ManagerStatCard.makeTextIcon("✓", new Color(22, 163, 74)),
                new Color(187, 247, 208)    // green-200
        );

        // Senior – tím
        cardSenior = new ManagerStatCard(
                "Senior",
                "0",
                new Color(243, 232, 255),   // purple-100
                ManagerStatCard.makeTextIcon("★", new Color(147, 51, 234)),
                new Color(233, 213, 255)    // purple-200
        );

        // Junior – xanh dương nhạt khác tông
        cardJunior = new ManagerStatCard(
                "Junior",
                "0",
                new Color(224, 242, 254),   // sky-100
                ManagerStatCard.makeTextIcon("⬆", new Color(56, 189, 248)),
                new Color(186, 230, 253)    // sky-200
        );

        add(cardTotal);
        add(cardActive);
        add(cardSenior);
        add(cardJunior);
    }

    public void updateStats(int total, int active, int senior, int junior) {
        cardTotal.setValue(String.valueOf(total));
        cardActive.setValue(String.valueOf(active));
        cardSenior.setValue(String.valueOf(senior));
        cardJunior.setValue(String.valueOf(junior));
    }
}

