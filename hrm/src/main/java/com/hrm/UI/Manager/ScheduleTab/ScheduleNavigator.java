package com.hrm.UI.Manager.ScheduleTab;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ScheduleNavigator extends JPanel {
    private JLabel lblWeekRange;
    private LocalDate currentMonday;
    private Consumer<LocalDate> onWeekChanged;

    public ScheduleNavigator(LocalDate currentMonday) {
        this.currentMonday = currentMonday;
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setBackground(ColorScheme.MAIN_BG);

        JPanel navBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        navBox.setBackground(Color.WHITE);
        navBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton btnPrev = createNavButton("<");
        JButton btnNext = createNavButton(">");

        lblWeekRange = new JLabel();
        lblWeekRange.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblWeekRange.setPreferredSize(new Dimension(200, 30));
        lblWeekRange.setHorizontalAlignment(SwingConstants.CENTER);

        btnPrev.addActionListener(e -> { 
            this.currentMonday = this.currentMonday.minusWeeks(1); 
            updateWeekLabel();
            notifyWeekChanged();
        });
        
        btnNext.addActionListener(e -> { 
            this.currentMonday = this.currentMonday.plusWeeks(1);  
            updateWeekLabel();
            notifyWeekChanged();
        });

        navBox.add(btnPrev);
        navBox.add(lblWeekRange);
        navBox.add(btnNext);
        add(navBox);

        updateWeekLabel();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(Color.WHITE);
        btn.setForeground(new Color(100, 100, 100));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateWeekLabel() {
        LocalDate sunday = currentMonday.plusDays(6);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");
        lblWeekRange.setText(currentMonday.format(fmt) + " - " + sunday.format(fmt));
    }

    private void notifyWeekChanged() {
        if (onWeekChanged != null) {
            onWeekChanged.accept(currentMonday);
        }
    }

    public LocalDate getCurrentMonday() {
        return currentMonday;
    }

    public void setCurrentMonday(LocalDate currentMonday) {
        this.currentMonday = currentMonday;
        updateWeekLabel();
    }

    public void setOnWeekChanged(Consumer<LocalDate> onWeekChanged) {
        this.onWeekChanged = onWeekChanged;
    }
}

