package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class AttenDanceHeader extends JPanel {
    private JLabel monthLabel;
    private LocalDate currentMonth;

    public AttenDanceHeader(){
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // ── 1. Title panel ────────────────────────────────────────
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);

        // Icon đồng hồ – dùng emoji nếu không có file icon
        JLabel title = new JLabel();
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        title.setIconTextGap(8);
        try {
            ImageIcon clockIcon = new ImageIcon(getClass().getResource("/icons/clock.png"));
            title.setIcon(clockIcon);
        } catch (Exception ignored) {
            title.setText("🕐 Quản lý chấm công");
        }
        if (title.getText().isEmpty()) title.setText("Quản lý chấm công");

        // ✅ Màu title đậm, chữ lớn – khớp ảnh (đen đậm, không tím)
        title.setForeground(new Color(17, 24, 39));
        title.putClientProperty("FlatLaf.style", "font: bold $h1.font");

        JLabel subtitle = new JLabel("Theo dõi thời gian làm việc và chuyên cần của nhân viên");
        subtitle.setForeground(new Color(107, 114, 128));

        titlePanel.add(title);
        titlePanel.add(subtitle);

        // ── 2. Month picker ───────────────────────────────────────
        currentMonth = LocalDate.now().withDayOfMonth(1);
        monthLabel = new JLabel(getMonthYearText(), SwingConstants.CENTER);
        monthLabel.putClientProperty("FlatLaf.style", "font: bold +1");
        monthLabel.setForeground(new Color(17, 24, 39));

        // lock width
        JLabel sample = new JLabel("Tháng 12, 2026");
        sample.setFont(monthLabel.getFont());
        Dimension fixed = sample.getPreferredSize();
        monthLabel.setPreferredSize(fixed);
        monthLabel.setMinimumSize(fixed);
        monthLabel.setMaximumSize(fixed);

        JButton prevButton = new JButton("◀");
        JButton nextButton = new JButton("▶");
        styleNavButton(prevButton);
        styleNavButton(nextButton);

        prevButton.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            monthLabel.setText(getMonthYearText());
        });
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            monthLabel.setText(getMonthYearText());
        });

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        datePanel.setOpaque(false);
        datePanel.add(prevButton);
        datePanel.add(monthLabel);
        datePanel.add(nextButton);

        // ✅ Card trắng bo góc quanh month picker – khớp ảnh
        JPanel monthBox = new JPanel(new BorderLayout());
        monthBox.setBackground(Color.WHITE);
        monthBox.putClientProperty("FlatLaf.style",
                "arc:12; background:#FFFFFF; borderColor:#E5E7EB; borderWidth:1; shadow:sm");
        monthBox.add(datePanel, BorderLayout.CENTER);

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(monthBox);

        add(titlePanel,  BorderLayout.WEST);
        add(rightWrapper, BorderLayout.EAST);
    }

    private void styleNavButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(107, 114, 128));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String getMonthYearText() {
        // ✅ Format "Tháng 2, 2026" – dùng số thay vì tên tiếng Việt dài
        int month = currentMonth.getMonthValue();
        int year  = currentMonth.getYear();
        return "Tháng " + month + ", " + year;
    }
}