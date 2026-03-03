package com.hrm.UI.Employee.AttendanceEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AttendanceManage extends JPanel {
    private String manv;
    private AttendanceHeader headerPanel;
    private AttendanceMonth monthPanel;
    private AttendanceSearch searchPanel;

    public AttendanceManage(String manv) {
        this.manv = manv;
        initUI();
    }

    private void initUI() {
        // Thiết lập Layout chính
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt đồng bộ
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // 1. Khởi tạo phần Header (Chứa Tiêu đề, Đồng hồ, Nút bấm và Thẻ thống kê)
        headerPanel = new AttendanceHeader(manv, this);
        
        // 2. Khởi tạo phần thân dưới (Lịch tháng và tìm kiếm lịch sử)
        monthPanel = new AttendanceMonth(manv);
        searchPanel = new AttendanceSearch(manv);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setOpaque(false);

        JLabel lblHistoryTitle = new JLabel("Lịch sử chấm công chi tiết");
        lblHistoryTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblHistoryTitle.setBorder(new EmptyBorder(10, 0, 0, 0));

        bodyPanel.add(monthPanel);
        bodyPanel.add(Box.createVerticalStrut(10));
        bodyPanel.add(lblHistoryTitle);
        bodyPanel.add(searchPanel);

        // 3. Thêm các thành phần chính vào ScrollPane (đề phòng màn hình nhỏ)
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        mainContent.add(headerPanel, BorderLayout.NORTH);
        mainContent.add(bodyPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        add(scrollPane, BorderLayout.CENTER);
    }


    public void refreshData() {
        removeAll();
        initUI();
        revalidate();
        repaint();
    }
}