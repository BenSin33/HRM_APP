package com.hrm.UI.HR.Attendancetab;

import com.hrm.DAO.HR.AttenDanceDao;
import com.hrm.DTO.HR.AttenDanceDTO.SummaryDTO;

import javax.swing.*;
import java.awt.*;


public class AttenDanceSummary extends JPanel {

    private final AttenDanceDao dao = new AttenDanceDao();

    // Tham chiếu các StartCardPanel để update label
    private StartCardPanel cardOnTime;
    private StartCardPanel cardLate;
    private StartCardPanel cardLeave;
    private StartCardPanel cardAbsent;

    public AttenDanceSummary() {
        setLayout(new GridLayout(1, 4, 16, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Khởi tạo card với dữ liệu mặc định
        cardOnTime = new StartCardPanel(
            "Đi làm đúng giờ", "--",
            new Color(220, 252, 231),
            makeTextIcon("✓", new Color(22, 163, 74)),
            new Color(187, 247, 208)
        );
        cardLate = new StartCardPanel(
            "Đi muộn (tháng này)", "--",
            new Color(255, 237, 213),
            makeTextIcon("⏰", new Color(234, 88, 12)),
            new Color(254, 215, 170)
        );
        cardLeave = new StartCardPanel(
            "Nghỉ có phép", "--",
            new Color(219, 234, 254),
            makeTextIcon("📅", new Color(37, 99, 235)),
            new Color(191, 219, 254)
        );
        cardAbsent = new StartCardPanel(
            "Vắng không phép", "--",
            new Color(254, 226, 226),
            makeTextIcon("✕", new Color(220, 38, 38)),
            new Color(254, 202, 202)
        );

        add(cardOnTime);
        add(cardLate);
        add(cardLeave);
        add(cardAbsent);

        // Load tháng hiện tại ngay khi khởi tạo
        java.time.LocalDate now = java.time.LocalDate.now();
        refreshData(now.getMonthValue(), now.getYear());
    }

    /**
     * Load lại dữ liệu theo tháng/năm được chọn.
     * Gọi từ AttenDanceHeader khi user bấm ◀ ▶.
     *
     * @param month tháng (1–12)
     * @param year  năm
     */
    public void refreshData(int month, int year) {
        // Chạy trên background thread để không block UI
        SwingWorker<SummaryDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected SummaryDTO doInBackground() {
                return dao.getSummary(month, year);
            }

            @Override
            protected void done() {
                try {
                    SummaryDTO dto = get();
                    cardOnTime.updateValue(dto.onTimeRate);
                    cardLate.updateValue(String.valueOf(dto.lateDays));
                    cardLeave.updateValue(String.valueOf(dto.leaveDays));
                    cardAbsent.updateValue(String.valueOf(dto.absentDays));
                } catch (Exception e) {
                    System.err.println("[AttenDanceSummary] refreshData: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private Icon makeTextIcon(String text, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(color);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
        lbl.setSize(lbl.getPreferredSize());
        return new Icon() {
            @Override public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(lbl.getFont());
                g2.setColor(color);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, x, y + fm.getAscent());
                g2.dispose();
            }
            @Override public int getIconWidth()  { return lbl.getPreferredSize().width; }
            @Override public int getIconHeight() { return lbl.getPreferredSize().height; }
        };
    }
}