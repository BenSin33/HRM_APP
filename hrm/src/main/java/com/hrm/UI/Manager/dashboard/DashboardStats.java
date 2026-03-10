package com.hrm.UI.Manager.dashboard;

import com.hrm.UI.Manager.common.ManagerStatCard;

import javax.swing.*;
import java.awt.*;

public class DashboardStats extends JPanel {

    private final ManagerStatCard cardNhanVien;
    private final ManagerStatCard cardDonChoDuyet;
    private final ManagerStatCard cardNghiPhep;
    private final ManagerStatCard cardHieuSuat;

    public DashboardStats() {
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);

        // Nhân viên trong team – xanh dương dịu, icon nhóm người
        cardNhanVien = new ManagerStatCard(
                "Nhân viên<br>trong team",
                "0",
                new Color(219, 234, 254),   // blue-100
                ManagerStatCard.makeTextIcon("👥", new Color(37, 99, 235)),
                new Color(191, 219, 254)    // blue-200
        );

        // Đơn chờ duyệt – cam, icon đồng hồ cát
        cardDonChoDuyet = new ManagerStatCard(
                "Đơn chờ duyệt",
                "0",
                new Color(255, 237, 213),   // orange-100
                ManagerStatCard.makeTextIcon("⏳", new Color(234, 88, 12)),
                new Color(254, 215, 170)    // orange-200
        );

        // Nghỉ phép hôm nay – xanh lá, icon lịch
        cardNghiPhep = new ManagerStatCard(
                "Nghỉ phép<br>hôm nay",
                "0",
                new Color(220, 252, 231),   // green-100
                ManagerStatCard.makeTextIcon("📅", new Color(22, 163, 74)),
                new Color(187, 247, 208)    // green-200
        );

        // Hiệu suất trung bình – tím, icon biểu đồ
        cardHieuSuat = new ManagerStatCard(
                "Hiệu suất<br>trung bình",
                "0%",
                new Color(243, 232, 255),   // purple-100
                ManagerStatCard.makeTextIcon("📈", new Color(147, 51, 234)),
                new Color(233, 213, 255)    // purple-200
        );

        add(cardNhanVien);
        add(cardDonChoDuyet);
        add(cardNghiPhep);
        add(cardHieuSuat);
    }

    public void updateStats(int nhanVien, int donChoDuyet, int nghiPhep, String hieuSuat) {
        cardNhanVien.setValue(String.valueOf(nhanVien));
        cardDonChoDuyet.setValue(String.valueOf(donChoDuyet));
        cardNghiPhep.setValue(String.valueOf(nghiPhep));
        cardHieuSuat.setValue(hieuSuat);
    }
}
