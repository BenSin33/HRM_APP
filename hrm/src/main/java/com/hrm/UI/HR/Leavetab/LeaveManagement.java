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
 *  │  LeaveSummary  (4 stat card) │  ← thống kê – tự refresh khi có thay đổi
 *  ├──────────────────────────────┤
 *  │  LeaveTable   (filter+table) │  ← danh sách đơn + duyệt / từ chối
 *  └──────────────────────────────┘
 *
 * Luồng dữ liệu khi duyệt đơn:
 *   User bấm ✓/✕ trong LeaveTable
 *     → LeaveTable.dao.updateStatus() ghi DB
 *     → gọi onDataChanged callback
 *     → LeaveManagement.onDataChanged()
 *     → summary.refreshData()  (cập nhật 4 stat card)
 *     → table.refreshData()    (reload bảng – đã tự gọi bên trong)
 */
public class LeaveManagement extends JPanel {

    private LeaveSummary summary;
    private LeaveTable   leaveTable;

    public LeaveManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        putClientProperty(FlatClientProperties.STYLE, "background: #F3F4F6");

        // ── 1. Header ─────────────────────────────────────────────
        add(new LeaveHeader(), BorderLayout.NORTH);

        // ── 2. Content ────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        summary    = new LeaveSummary();
        leaveTable = new LeaveTable(this::onDataChanged);

        content.add(summary,    BorderLayout.NORTH);
        content.add(leaveTable, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }

    /**
     * Gọi khi LeaveTable báo có thay đổi dữ liệu (duyệt / từ chối đơn).
     * Refresh Summary để 4 stat card hiển thị con số mới nhất.
     */
    private void onDataChanged() {
        summary.refreshData();
    }
}