package com.hrm.UI.HR.Leavetab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

/**
 * LeaveManagement – panel chính của tab Nghỉ phép.
 *
 * Cấu trúc:
 *  ┌──────────────────────────────┐
 *  │  LeaveHeader                 │  ← tiêu đề
 *  ├──────────────────────────────┤
 *  │  LeaveSummary  (4 stat card) │  ← thống kê
 *  ├──────────────────────────────┤
 *  │  LeaveTable   (filter+table) │  ← danh sách đơn
 *  └──────────────────────────────┘
 */
public class LeaveManagement extends JPanel {

    public LeaveManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        // Nền xám nhạt đồng bộ với AttenDanceManagement
        putClientProperty(FlatClientProperties.STYLE, "background: #F3F4F6");

        // ── 1. Header ─────────────────────────────────────────────
        add(new LeaveHeader(), BorderLayout.NORTH);

        // ── 2. Content wrapper ────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        content.add(new LeaveSummary(), BorderLayout.NORTH);
        content.add(new LeaveTable(),   BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }
}