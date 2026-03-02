package com.hrm.UI.HR.Evaluationtab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

/**
 * EvaluationManagement – panel chính của tab Đánh giá hiệu suất.
 *
 * Cấu trúc:
 *  ┌─────────────────────────────────────────────────┐
 *  │  EvaluationHeader  (tiêu đề + nút Tạo mới)      │
 *  ├─────────────────────────────────────────────────┤
 *  │  EvaluationSummary (4 stat cards)               │
 *  ├─────────────────────────────────────────────────┤
 *  │  EvaluationTable   (kỳ selector + bảng)         │
 *  └─────────────────────────────────────────────────┘
 */
public class EvaluationManagement extends JPanel {

    public EvaluationManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        // Nền xám nhạt đồng bộ với các module khác
        putClientProperty(FlatClientProperties.STYLE, "background: #F3F4F6");

        // ── 1. Header ─────────────────────────────────────────────
        add(new EvaluationHeader(), BorderLayout.NORTH);

        // ── 2. Content ────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        content.add(new EvaluationSummary(), BorderLayout.NORTH);
        content.add(new EvaluationTable(),   BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);
    }
}