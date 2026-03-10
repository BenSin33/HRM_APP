package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.function.BiConsumer;

/**
 * AttenDanceHeader – header với điều hướng tháng.
 *
 * Thêm constructor nhận BiConsumer<Integer,Integer> onMonthChanged
 * để thông báo cho Management khi user đổi tháng/năm.
 * Callback nhận (month, year).
 */
public class AttenDanceHeader extends JPanel {

    private JLabel monthLabel;
    private LocalDate currentMonth;

    /** Callback báo Management khi tháng thay đổi: (month, year) */
    private final BiConsumer<Integer, Integer> onMonthChanged;

    // ── Constructor đầy đủ ────────────────────────────────────────
    public AttenDanceHeader(BiConsumer<Integer, Integer> onMonthChanged) {
        this.onMonthChanged = onMonthChanged;
        init();
    }

    /** Constructor không callback – tương thích ngược */
    public AttenDanceHeader() {
        this(null);
    }

    private void init() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // ── 1. Title ──────────────────────────────────────────────
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel();
        title.setHorizontalTextPosition(SwingConstants.RIGHT);
        title.setIconTextGap(8);
        try {
            ImageIcon clockIcon = new ImageIcon(getClass().getResource("/icons/clock.png"));
            title.setIcon(clockIcon);
        } catch (Exception ignored) {}
        title.setText("Quản lý chấm công");
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
            fireMonthChanged();
        });
        nextButton.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            monthLabel.setText(getMonthYearText());
            fireMonthChanged();
        });

        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        datePanel.setOpaque(false);
        datePanel.add(prevButton);
        datePanel.add(monthLabel);
        datePanel.add(nextButton);

        JPanel monthBox = new JPanel(new BorderLayout());
        monthBox.setBackground(Color.WHITE);
        monthBox.putClientProperty("FlatLaf.style",
            "arc:12; background:#FFFFFF; border:1,1,1,1,#E5E7EB; shadow:sm");
        monthBox.add(datePanel, BorderLayout.CENTER);

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(monthBox);

        add(titlePanel,   BorderLayout.WEST);
        add(rightWrapper, BorderLayout.EAST);
    }

    /** Gọi callback khi tháng thay đổi */
    private void fireMonthChanged() {
        if (onMonthChanged != null) {
            onMonthChanged.accept(currentMonth.getMonthValue(), currentMonth.getYear());
        }
    }

    private void styleNavButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(107, 114, 128));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private String getMonthYearText() {
        return "Tháng " + currentMonth.getMonthValue() + ", " + currentMonth.getYear();
    }

    /** Trả về tháng hiện tại đang hiển thị */
    public LocalDate getCurrentMonth() { return currentMonth; }
}