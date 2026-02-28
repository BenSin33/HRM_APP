package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ScheduleCenter extends JPanel {
    public ScheduleCenter() {
        setLayout(new GridLayout(1, 7, 15, 0)); // 7 cột cho 7 ngày
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 25, 20, 25));

        // Mock dữ liệu: Thứ 2 đến Thứ 6 làm Hành chính (C1), Thứ 7 CN nghỉ
        add(createDayCard("THỨ HAI", "23", "HC", "Hành chính", "08:00 - 17:30", true));
        add(createDayCard("THỨ BA", "24", "HC", "Hành chính", "08:00 - 17:30", true));
        add(createDayCard("THỨ TƯ", "25", "HC", "Hành chính", "08:00 - 17:30", true));
        add(createDayCard("THỨ NĂM", "26", "HC", "Hành chính", "08:00 - 17:30", true));
        add(createDayCard("THỨ SÁU", "27", "HC", "Hành chính", "08:00 - 17:30", true));
        add(createDayCard("THỨ BẢY", "28", "OFF", "Nghỉ", "-", false));
        add(createDayCard("CHỦ NHẬT", "1", "OFF", "Nghỉ", "-", false));
    }

    private JPanel createDayCard(String dayName, String dayNum, String code, String shiftName, String time, boolean isWorkDay) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));

        // Header của card (Thứ và Ngày)
        JLabel lblDay = new JLabel(dayName);
        lblDay.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDay.setForeground(Color.GRAY);
        lblDay.setBorder(new EmptyBorder(15, 0, 5, 0));

        JLabel lblNum = new JLabel(dayNum);
        lblNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNum.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Nội dung ca làm việc
        JPanel shiftInfo = new JPanel(new GridLayout(3, 1));
        shiftInfo.setPreferredSize(new Dimension(140, 150));
        
        // Màu sắc dựa trên trạng thái làm việc
        if (isWorkDay) {
            shiftInfo.setBackground(new Color(236, 253, 245)); // Xanh lá nhạt
            JLabel lblCode = new JLabel(code, SwingConstants.CENTER);
            lblCode.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblCode.setForeground(new Color(16, 185, 129));
            
            JLabel lblName = new JLabel(shiftName, SwingConstants.CENTER);
            JLabel lblTime = new JLabel(time, SwingConstants.CENTER);
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            shiftInfo.add(lblCode);
            shiftInfo.add(lblName);
            shiftInfo.add(lblTime);
        } else {
            shiftInfo.setBackground(new Color(243, 244, 246)); // Xám nhạt
            JLabel lblCode = new JLabel(code, SwingConstants.CENTER);
            lblCode.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblCode.setForeground(Color.GRAY);
            JLabel lblName = new JLabel("Nghỉ", SwingConstants.CENTER);
            
            shiftInfo.add(lblCode);
            shiftInfo.add(lblName);
            shiftInfo.add(new JLabel(""));
        }

        card.add(lblDay);
        card.add(lblNum);
        card.add(shiftInfo);
        
        return card;
    }
}
