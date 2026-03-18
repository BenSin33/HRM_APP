package com.hrm.UI.Employee.PayrollEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.hrm.Service.Employee.PayrollService;

import java.awt.*;
import java.time.LocalDate;
import java.util.Map;

public class PayrollSummary extends JPanel {
    public PayrollSummary(String manv) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 25, 10, 25));

        // Lấy dữ liệu thực lĩnh từ database
        PayrollService dao = new PayrollService();
        Map<String, Object> payroll = dao.getCurrentMonthPayroll(manv);
        
        String amountDisplay = "0";
        String monthDisplay = "01/2026";
        
        if (payroll != null && !payroll.isEmpty()) {
            double thuclinh = (Double) payroll.get("thuclinh");
            int thang = (Integer) payroll.get("thang");
            int nam = (Integer) payroll.get("nam");
            
            amountDisplay = String.format("%,.0f", thuclinh);
            monthDisplay = String.format("%02d/%d", thang, nam);
        }

        JPanel blueCard = new JPanel(new BorderLayout());
        blueCard.setBackground(new Color(59, 130, 246));
        blueCard.setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblTitle = new JLabel("Tổng lương thực lĩnh (Tháng " + monthDisplay + ")");
        lblTitle.setForeground(new Color(219, 234, 254));
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblAmount = new JLabel(amountDisplay + " đ");
        lblAmount.setForeground(Color.WHITE);
        lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 42));

        JLabel lblIcon = new JLabel("$");
        lblIcon.setFont(new Font("Arial", Font.BOLD, 60));
        lblIcon.setForeground(new Color(255, 255, 255, 60));

        blueCard.add(lblTitle, BorderLayout.NORTH);
        blueCard.add(lblAmount, BorderLayout.CENTER);
        blueCard.add(lblIcon, BorderLayout.EAST);

        add(blueCard, BorderLayout.CENTER);
    }
}