package com.hrm.UI.Manager.ScheduleTab;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class ScheduleLegend extends JPanel {
    
    public ScheduleLegend() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        setBackground(ColorScheme.MAIN_BG);
        setPreferredSize(new Dimension(0, 70));

        add(createLegendItem("S", "Ca Sáng", "08:00 - 12:00", new Color(254, 243, 199), new Color(146, 64, 14)));
        add(createLegendItem("C", "Ca Chiều", "13:00 - 17:00", new Color(219, 234, 254), new Color(29, 78, 216)));
        add(createLegendItem("T", "Ca Tối", "18:00 - 22:00", new Color(243, 232, 255), new Color(107, 33, 168)));
        add(createLegendItem("HC", "Hành chính", "08:00 - 17:30", new Color(220, 252, 231), new Color(22, 101, 52)));
        add(createLegendItem("OFF", "Nghỉ", "-", new Color(243, 244, 246), new Color(107, 114, 128)));
    }

    private JPanel createLegendItem(String code, String label, String time, Color bgColor, Color textColor) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(2, 5, 2, 10)
        ));

        JLabel badge = new JLabel(code);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        badge.setForeground(textColor);
        badge.setBackground(bgColor);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel timeLabel = new JLabel(time);
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeLabel.setForeground(new Color(100, 100, 100));

        textPanel.add(nameLabel);
        textPanel.add(timeLabel);

        item.add(badge);
        item.add(textPanel);

        return item;
    }
}