package com.hrm.UI.HR.Attendancetab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

/**
 * AttenDanceManagement – controller chính của tab Chấm công.
 *
 * Chịu trách nhiệm:
 *  1. Dàn layout Header / Summary / Table / Detail
 *  2. Truyền month/year từ Header xuống Summary + Table mỗi khi user đổi tháng
 *  3. Chuyển view giữa "main" (Summary + Table) và "detail" (AttenDanceDetail)
 */
public class AttenDanceManagement extends JPanel {

    private static final String VIEW_MAIN   = "main";
    private static final String VIEW_DETAIL = "detail";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     cardPanel  = new JPanel(cardLayout);

    // Giữ reference để có thể gọi refreshData khi đổi tháng
    private AttenDanceSummary summary;
    private AttenDanceTable   table;

    // Tháng/năm hiện tại đang xem (cần truyền vào Detail)
    private int currentMonth;
    private int currentYear;

    public AttenDanceManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        putClientProperty(FlatClientProperties.STYLE, "background: #F3F4F6");

        // Khởi tạo tháng hiện tại
        java.time.LocalDate now = java.time.LocalDate.now();
        currentMonth = now.getMonthValue();
        currentYear  = now.getYear();

        // ── 1. Header – truyền callback khi đổi tháng ─────────────
        AttenDanceHeader header = new AttenDanceHeader(this::onMonthChanged);
        add(header, BorderLayout.NORTH);

        // ── 2. Card panel ─────────────────────────────────────────
        cardPanel.setOpaque(false);

        // View chính: Summary + Table
        JPanel mainView = new JPanel(new BorderLayout(0, 16));
        mainView.setOpaque(false);

        summary = new AttenDanceSummary();
        table   = new AttenDanceTable(this::showDetail);

        mainView.add(summary, BorderLayout.NORTH);
        mainView.add(table,   BorderLayout.CENTER);

        cardPanel.add(mainView,     VIEW_MAIN);
        cardPanel.add(new JPanel(), VIEW_DETAIL);

        cardLayout.show(cardPanel, VIEW_MAIN);
        add(cardPanel, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    // Callback từ AttenDanceHeader khi user bấm ◀ ▶
    // ─────────────────────────────────────────────────────────────
    private void onMonthChanged(int month, int year) {
        currentMonth = month;
        currentYear  = year;

        // Refresh cả Summary lẫn Table
        summary.refreshData(month, year);
        table.refreshData(month, year);
    }

    // ─────────────────────────────────────────────────────────────
    // Chuyển sang view chi tiết
    // emp = {hoTen, manv, chucVu, phongBan, workDays, lateDays, absentDays}
    // ─────────────────────────────────────────────────────────────
    public void showDetail(Object[] emp) {
        // Truyền đúng tháng/năm đang xem để Detail load đúng dữ liệu
        AttenDanceDetail detail = new AttenDanceDetail(
                emp, currentMonth, currentYear, this::showMain);

        cardPanel.remove(cardPanel.getComponentCount() - 1);
        cardPanel.add(detail, VIEW_DETAIL);

        cardLayout.show(cardPanel, VIEW_DETAIL);
        cardPanel.revalidate();
        cardPanel.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    // Quay về view tổng
    // ─────────────────────────────────────────────────────────────
    public void showMain() {
        cardLayout.show(cardPanel, VIEW_MAIN);
    }
}