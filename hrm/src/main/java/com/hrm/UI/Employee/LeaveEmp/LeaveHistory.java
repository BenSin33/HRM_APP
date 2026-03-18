package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.Service.Employee.LeaveService;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class LeaveHistory extends JPanel {
    private String manv;
    private Runnable onDataChanged;
    
    public LeaveHistory(String manv) {
        this(manv, null);
    }
    
    public LeaveHistory(String manv, Runnable onDataChanged) {
        this.manv = manv;
        this.onDataChanged = onDataChanged;
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel lblTitle = new JLabel("Lịch sử đơn nghỉ phép");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(lblTitle);
        add(Box.createVerticalStrut(15));

        // Lấy dữ liệu từ database
        LeaveService dao = new LeaveService();
        List<Map<String, Object>> leaves = dao.getLeaveRequestsByEmployee(manv);

        if (leaves != null && !leaves.isEmpty()) {
            for (Map<String, Object> leave : leaves) {
                String manghiphep = (String) leave.get("manghiphep");
                String loainghi = (String) leave.get("loainghi");
                java.sql.Date dbNgaynghi = (java.sql.Date) leave.get("ngaynghi");
                java.sql.Date dbNgaylamlai = (java.sql.Date) leave.get("ngaylamlai");
                String lydonghi = (String) leave.get("lydonghi");
                String nguoiduyet = (String) leave.get("nguoiduyet");
                
                String dateRange = "";
                if (dbNgaynghi != null && dbNgaylamlai != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    dateRange = sdf.format(dbNgaynghi) + " - " + sdf.format(dbNgaylamlai);
                }
                
                // Xác định trạng thái
                boolean isDraft = nguoiduyet == null;
                String status = isDraft ? "Chờ duyệt" : ("Đã duyệt bởi: " + nguoiduyet);
                Color statusColor = isDraft ? new Color(255, 193, 7) : new Color(34, 197, 94);
                
                Map<String, Object> leaveMap = new java.util.HashMap<>(leave);
                add(createLeaveItem(loainghi, dateRange, lydonghi, status, statusColor, isDraft, leaveMap));
                add(Box.createVerticalStrut(10));
            }
        } else {
            JLabel lblNoData = new JLabel("Không có đơn nghỉ phép");
            lblNoData.setForeground(Color.GRAY);
            lblNoData.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            add(lblNoData);
        }
    }

    private JPanel createLeaveItem(String type, String date, String reason, String status, Color statusColor, boolean isDraft, Map<String, Object> leaveData) {
        JPanel item = new JPanel(new BorderLayout(20, 0));
        item.setBackground(new Color(250, 250, 250));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        item.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 5));
        info.setOpaque(false);
        
        JLabel lblType = new JLabel(type + " - " + date);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 15));
        
        JLabel lblReason = new JLabel("Lý do: " + reason);
        lblReason.setForeground(Color.GRAY);
        lblReason.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        info.add(lblType);
        info.add(lblReason);

        // Panel phải chứa status và button edit nếu là draft
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);
        
        JLabel lblStatus = new JLabel(status);
        lblStatus.setForeground(statusColor);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(lblStatus);
        
        if (isDraft) {
            JButton btnEdit = new JButton("Chỉnh sửa", createPencilIcon(Color.WHITE, 12));
            btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnEdit.setPreferredSize(new Dimension(110, 30));
            btnEdit.setBackground(new Color(59, 130, 246));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setIconTextGap(6);
            btnEdit.addActionListener(e -> editLeaveRequest(leaveData));
            rightPanel.add(btnEdit);
        }

        item.add(info, BorderLayout.CENTER);
        item.add(rightPanel, BorderLayout.EAST);
        return item;
    }
    
    private void editLeaveRequest(Map<String, Object> leaveData) {
        LeaveEditDialog dialog = new LeaveEditDialog(SwingUtilities.getWindowAncestor(this), leaveData);
        dialog.setVisible(true);
        
        if (dialog.isSubmitted() && onDataChanged != null) {
            onDataChanged.run();
        }
    }

    private ImageIcon createPencilIcon(Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        g2d.drawLine(2, size - 3, size - 5, 4);
        g2d.drawLine(size - 6, 2, size - 3, 5);
        g2d.drawLine(2, size - 3, 4, size - 1);

        g2d.dispose();
        return new ImageIcon(image);
    }
}