package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.Service.Employee.LeaveService;
import java.awt.*;
import java.awt.event.ActionListener;

public class LeaveHeader extends JPanel {
    private JButton btnCreate;
    private String manv;
    private static final int ANNUAL_LEAVE_LIMIT = 12; // 12 paid leave days per year
    
    public LeaveHeader(String manv) {
        this.manv = manv;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(15, 25, 15, 25));

        // Tiêu đề và nút "Tạo đơn mới"
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("Nghỉ phép");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        btnCreate = new JButton("+ Tạo đơn mới");
        btnCreate.setBackground(new Color(59, 130, 246));
        btnCreate.setForeground(Color.WHITE);
        btnCreate.setFocusPainted(false);
        btnCreate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCreate.setPreferredSize(new Dimension(180, 45));
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));

        top.add(lblTitle, BorderLayout.WEST);
        top.add(btnCreate, BorderLayout.EAST);

        // Thẻ thống kê
        JPanel stats = new JPanel(new GridLayout(1, 3, 20, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(20, 0, 10, 0));
        
        // Fetch leave statistics from database
        LeaveService leaveDAO = new LeaveService();
        int approvedPaidLeave = leaveDAO.getApprovedPaidLeaveCount(manv);
        int totalLeaveRequests = leaveDAO.getTotalLeaveRequestCount(manv);
        int unpaidLeave = leaveDAO.getUnpaidLeaveCount(manv);
        int remainingAnnualLeave = ANNUAL_LEAVE_LIMIT - approvedPaidLeave;
        
        stats.add(new StatCard("Phép năm còn lại", String.valueOf(remainingAnnualLeave), "ngày", new Color(59, 130, 246)));
        stats.add(new StatCard("Đơn nghỉ phép đã gửi", String.valueOf(totalLeaveRequests), "đơn", new Color(34, 197, 94)));
        stats.add(new StatCard("Phép việc riêng", String.valueOf(unpaidLeave), "đơn", new Color(168, 85, 247)));

        add(top, BorderLayout.NORTH);
        add(stats, BorderLayout.CENTER);
    }
    
    public void addCreateLeaveListener(ActionListener listener) {
        btnCreate.addActionListener(listener);
    }

    private class StatCard extends JPanel {
        public StatCard(String title, String val, String unit, Color color) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 20, 15, 20)
            ));
            
            JLabel lblT = new JLabel(title);
            lblT.setForeground(Color.GRAY);
            JLabel lblV = new JLabel(val);
            lblV.setFont(new Font("Segoe UI", Font.BOLD, 28));
            lblV.setForeground(color);
            JLabel lblU = new JLabel(unit);
            lblU.setForeground(Color.GRAY);

            add(lblT, BorderLayout.NORTH);
            add(lblV, BorderLayout.CENTER);
            add(lblU, BorderLayout.SOUTH);
        }
    }
}