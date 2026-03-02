package com.hrm.UI.HR.Attendancetab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

/**
 * AttenDanceManagement – quản lý 2 view bằng CardLayout:
 *
 *  ┌──────────────────────────────────────────┐
 *  │  HEADER (luôn hiển thị)                  │
 *  ├──────────────────────────────────────────┤
 *  │  VIEW "main"  : Summary + Table          │
 *  │  VIEW "detail": AttenDanceDetail         │
 *  └──────────────────────────────────────────┘
 *
 *  AttenDanceTable gọi showDetail(emp) khi bấm "Chi tiết".
 *  AttenDanceDetail gọi showMain() khi bấm ◀.
 */
public class AttenDanceManagement extends JPanel {

    private static final String VIEW_MAIN   = "main";
    private static final String VIEW_DETAIL = "detail";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    public AttenDanceManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        putClientProperty(FlatClientProperties.STYLE, "background: #F3F4F6");

        // ── 1. Header (luôn trên cùng) ────────────────────────────
        add(new AttenDanceHeader(), BorderLayout.NORTH);

        // ── 2. Card panel ─────────────────────────────────────────
        cardPanel.setOpaque(false);

        // View chính: Summary + Table
        JPanel mainView = new JPanel(new BorderLayout(0, 16));
        mainView.setOpaque(false);

        AttenDanceSummary summary = new AttenDanceSummary();

        // Table cần callback để mở detail → truyền reference tới Management
        AttenDanceTable table = new AttenDanceTable(this::showDetail);

        mainView.add(summary, BorderLayout.NORTH);
        mainView.add(table,   BorderLayout.CENTER);

        cardPanel.add(mainView,          VIEW_MAIN);
        // placeholder cho detail – sẽ được thay mới mỗi lần mở
        cardPanel.add(new JPanel(),      VIEW_DETAIL);

        cardLayout.show(cardPanel, VIEW_MAIN);
        add(cardPanel, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // Chuyển sang view chi tiết của nhân viên
    // emp = {name, id, position, dept, workDays, late, absent}
    // ─────────────────────────────────────────────────────────────
    public void showDetail(Object[] emp) {
        // Xây dựng panel detail mới với callback quay về main
        AttenDanceDetail detail = new AttenDanceDetail(emp, this::showMain);

        // Thay thế card detail cũ
        cardPanel.remove(cardPanel.getComponentCount() - 1); // xóa card detail cũ
        cardPanel.add(detail, VIEW_DETAIL);

        cardLayout.show(cardPanel, VIEW_DETAIL);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // Quay về view tổng (summary + table)
    // ─────────────────────────────────────────────────────────────
    public void showMain() {
        cardLayout.show(cardPanel, VIEW_MAIN);
    }
}