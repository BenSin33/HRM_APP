package com.hrm.UI.HR.Evaluationtab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import java.awt.*;

public class EvaluationManagement extends JPanel {

    public EvaluationManagement() {
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        putClientProperty(FlatClientProperties.STYLE, "background: #F3F4F6");

        // ── 2. Content (khởi tạo trước để truyền vào Header) ─────
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);

        EvaluationSummary summary = new EvaluationSummary();
        EvaluationTable   table   = new EvaluationTable(summary);

        content.add(summary, BorderLayout.NORTH);
        content.add(table,   BorderLayout.CENTER);

        // ── 1. Header (truyền callback refresh khi tạo đợt mới) ──
        EvaluationHeader header = new EvaluationHeader(() -> {
            table.reloadPeriods();   // reload ComboBox + bảng
        });

        add(header,  BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }
}