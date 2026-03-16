package com.hrm.UI.Manager.ScheduleTab;

import com.hrm.DTO.Manager.ScheduleDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShiftSelectionDialog extends JDialog {
    private JComboBox<String> shiftComboBox;
    private int result = JOptionPane.CANCEL_OPTION;
    private String selectedShift; // Mã ca (C1, C2, C3, ...)
    private LocalDate selectedDate;
    private String employeeName;
    private List<ScheduleDTO> shifts;

    public ShiftSelectionDialog(JFrame parent, String employeeName, LocalDate date, List<ScheduleDTO> shifts) {
        super(parent, "Chọn Ca Làm Việc", true);
        this.employeeName = employeeName;
        this.selectedDate = date;
        this.shifts = shifts;
        this.selectedShift = null;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        String dateStr = selectedDate.format(DateTimeFormatter.ofPattern("EEEE dd/MM"));
        JLabel titleLabel = new JLabel("<html>Chọn ca làm cho <b>" + employeeName + "</b><br>Ngày: " + dateStr + "</html>");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ===== COMBO BOX =====
        JPanel comboPanel = new JPanel(new BorderLayout(10, 0));
        comboPanel.setBackground(Color.WHITE);
        comboPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JLabel comboLabel = new JLabel("Ca làm:");
        comboLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboLabel.setForeground(new Color(100, 100, 100));
        comboPanel.add(comboLabel, BorderLayout.WEST);

        // Build combo box model
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("-- Chọn ca --");
        for (ScheduleDTO shift : shifts) {
            model.addElement(getShiftDisplayName(shift));
        }
        model.addElement("Ngày OFF"); // Thêm option ngày OFF

        shiftComboBox = new JComboBox<>(model);
        shiftComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        shiftComboBox.setBackground(Color.WHITE);
        shiftComboBox.setSelectedIndex(0);
        comboPanel.add(shiftComboBox, BorderLayout.CENTER);

        mainPanel.add(comboPanel, BorderLayout.CENTER);

        // ===== LEGEND =====
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(new Color(245, 245, 245));
        legendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel legendTitle = new JLabel("Danh sách ca:");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        legendTitle.setForeground(new Color(80, 80, 80));
        legendPanel.add(legendTitle);
        legendPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        for (ScheduleDTO shift : shifts) {
            String shiftLabel = getShiftDisplayName(shift);
            String[] parts = shift.getShift().split("\\|");
            String code = getShiftCode(shift.getShift());

            JPanel shiftItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            shiftItemPanel.setBackground(new Color(245, 245, 245));

            JLabel badge = new JLabel("  " + code + "  ");
            badge.setFont(new Font("Segoe UI", Font.BOLD, 11));
            badge.setBackground(getBgColor(code));
            badge.setForeground(getTxtColor(code));
            badge.setOpaque(true);
            badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));

            JLabel labelItem = new JLabel(shiftLabel);
            labelItem.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            labelItem.setForeground(new Color(80, 80, 80));

            shiftItemPanel.add(badge);
            shiftItemPanel.add(labelItem);
            legendPanel.add(shiftItemPanel);
        }

        // OFF option
        JPanel offItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        offItemPanel.setBackground(new Color(245, 245, 245));
        JLabel offBadge = new JLabel("  OFF  ");
        offBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        offBadge.setBackground(new Color(243, 244, 246));
        offBadge.setForeground(new Color(107, 114, 128));
        offBadge.setOpaque(true);
        offBadge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        JLabel offLabel = new JLabel("Ngày OFF");
        offLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        offLabel.setForeground(new Color(80, 80, 80));
        offItemPanel.add(offBadge);
        offItemPanel.add(offLabel);
        legendPanel.add(offItemPanel);

        JScrollPane scrollPane = new JScrollPane(legendPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.setPreferredSize(new Dimension(400, 120));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        // ===== BUTTONS =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton okButton = new JButton("OK");
        okButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        okButton.setBackground(new Color(59, 130, 246));
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        okButton.setFocusPainted(false);
        okButton.addActionListener(e -> onOK());

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(229, 231, 235));
        cancelButton.setForeground(new Color(75, 85, 99));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> onCancel());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);

        add(mainPanel);
    }

    private void onOK() {
        int selectedIndex = shiftComboBox.getSelectedIndex();
        if (selectedIndex <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy mã ca
        if (selectedIndex == shifts.size() + 1) {
            // Người dùng chọn "Ngày OFF"
            this.selectedShift = "OFF";
        } else {
            // Lấy shift từ danh sách
            this.selectedShift = shifts.get(selectedIndex - 1).getShift();
        }

        this.result = JOptionPane.OK_OPTION;
        setVisible(false);
        dispose();
    }

    private void onCancel() {
        this.result = JOptionPane.CANCEL_OPTION;
        this.selectedShift = null;
        setVisible(false);
        dispose();
    }

    // ===== HELPER METHODS =====
    private String getShiftDisplayName(ScheduleDTO shift) {
        String time = shift.getStartTime() + " - " + shift.getEndTime();
        return shift.getShiftName() + " (" + time + ")";
    }

    private String getShiftCode(String shiftId) {
        switch (shiftId) {
            case "C1": return "HC"; // Hành chính
            case "C2": return "S";  // Ca sáng
            case "C3": return "C";  // Ca chiều
            case "C4": return "T";  // Ca tối
            case "C5": return "S";  // Ca gãy sáng
            case "C6": return "C";  // Ca gãy chiều
            case "C7": return "T";  // Ca tăng cường
            default:   return "?";
        }
    }

    private Color getBgColor(String code) {
        switch (code) {
            case "HC":  return new Color(220, 252, 231); // Xanh lá
            case "S":   return new Color(254, 243, 199); // Vàng
            case "C":   return new Color(219, 234, 254); // Xanh dương
            case "T":   return new Color(243, 232, 255); // Tím
            case "OFF": return new Color(243, 244, 246); // Xám
            default:    return new Color(243, 244, 246);
        }
    }

    private Color getTxtColor(String code) {
        switch (code) {
            case "HC":  return new Color(22, 101, 52);    // Xanh lá đậm
            case "S":   return new Color(146, 64, 14);    // Vàng đậm
            case "C":   return new Color(29, 78, 216);    // Xanh dương đậm
            case "T":   return new Color(107, 33, 168);   // Tím đậm
            case "OFF": return new Color(107, 114, 128);  // Xám đậm
            default:    return new Color(100, 100, 100);
        }
    }

    // ===== GETTERS =====
    public int getResult() {
        return result;
    }

    public String getSelectedShift() {
        return selectedShift;
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }
}
