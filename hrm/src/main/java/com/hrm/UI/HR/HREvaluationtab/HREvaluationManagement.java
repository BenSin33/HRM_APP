package com.hrm.UI.HR.HREvaluationtab;

import com.hrm.DAO.NhanVienDAO;
import com.hrm.DTO.UserDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.utils.SessionManager;

import javax.swing.*;
import java.awt.*;

/**
 * HREvaluationManagement - Tab đánh giá nhân viên cho HR staff
 * Kiểm tra quyền: Chức vụ trưởng phòng (CV01) hoặc có role admin (R2)
 */
public class HREvaluationManagement extends JPanel {
    private NhanVienDAO nhanVienDAO;
    private HREvaluationPanel evaluationPanel;
    private boolean hasAccess;
    
    public HREvaluationManagement() {
        setLayout(new BorderLayout());
        
        nhanVienDAO = new NhanVienDAO();
        
        // Kiểm tra quyền truy cập
        hasAccess = checkAccessPermission();
        
        if (hasAccess) {
            // Tạo panel đánh giá (tái sử dụng từ Manager)
            evaluationPanel = new HREvaluationPanel();
            add(evaluationPanel, BorderLayout.CENTER);
        } else {
            // Hiển thị thông báo không có quyền
            showNoAccessMessage();
        }
    }
    
    /**
     * Kiểm tra quyền truy cập:
     * - Phải là trưởng phòng (CV01) hoặc
     * - Phải có role admin (R2)
     */
    private boolean checkAccessPermission() {
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        
        if (currentUser == null) {
            return false;
        }
        
        // Lấy thông tin nhân viên
        NhanVienDTO hrStaff = nhanVienDAO.findById(currentUser.getManv());
        
        if (hrStaff == null) {
            return false;
        }
        
        // Kiểm tra chức vụ trưởng phòng (CV01)
        boolean isHeadOfDepartment = "CV01".equals(hrStaff.getMachucvu());
        
        // Kiểm tra có role admin (R2)
        boolean hasAdminRole = "R2".equals(currentUser.getRoleId());
        
        return isHeadOfDepartment || hasAdminRole;
    }
    
    /**
     * Hiển thị thông báo không có quyền truy cập
     */
    private void showNoAccessMessage() {
        setBackground(new Color(245, 245, 245));
        
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        messagePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel("🔒");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Truy cập bị từ chối");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descriptionLabel = new JLabel("Bạn không có quyền truy cập chức năng đánh giá nhân viên.");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(100, 100, 100));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel detailLabel = new JLabel("Yêu cầu: Chức vụ Trưởng phòng hoặc Role Admin");
        detailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        detailLabel.setForeground(new Color(150, 150, 150));
        detailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        messagePanel.add(Box.createVerticalGlue());
        messagePanel.add(iconLabel);
        messagePanel.add(Box.createVerticalStrut(20));
        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(10));
        messagePanel.add(descriptionLabel);
        messagePanel.add(Box.createVerticalStrut(5));
        messagePanel.add(detailLabel);
        messagePanel.add(Box.createVerticalGlue());
        
        add(messagePanel, BorderLayout.CENTER);
    }
}
