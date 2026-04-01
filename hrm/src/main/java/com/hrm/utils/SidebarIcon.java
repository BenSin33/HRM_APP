package com.hrm.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

public class SidebarIcon {

    private static final Map<String, String> ICON_PATHS = new HashMap<>();
    private static final int ICON_SIZE = 32;

    static {
        // Dashboard
        ICON_PATHS.put("DASHBOARD", "/icons/sidebar/overview.png");
        ICON_PATHS.put("MANAGER_DASHBOARD", "/icons/sidebar/overview.png");
        
        // HR & Management
        ICON_PATHS.put("EMPLOYEE_MANAGEMENT", "/icons/sidebar/nhanvien.png");
        ICON_PATHS.put("TEAM_MANAGEMENT", "/icons/sidebar/nhanvien.png");
        
        // Department
        ICON_PATHS.put("DEPARTMENT_MANAGEMENT", "/icons/sidebar/phongban.png");
        
        // Attendance
        ICON_PATHS.put("ATTENDANCE_MANAGEMENT", "/icons/sidebar/chamcong.png");
        ICON_PATHS.put("CHAMCONG", "/icons/sidebar/chamcong.png");
        
        // Leave/Nghỉ Phép
        ICON_PATHS.put("LEAVE_MANAGEMENT", "/icons/sidebar/nghiphep.png");
        ICON_PATHS.put("LEAVE_APPROVAL", "/icons/sidebar/nghiphep.png");
        ICON_PATHS.put("NGHIPHEP", "/icons/sidebar/nghiphep.png");
        
        // Evaluation/Đánh Giá
        ICON_PATHS.put("EVALUATION_MANAGEMENT", "/icons/sidebar/danhgia.png");
        ICON_PATHS.put("HR_EVALUATION_MANAGEMENT", "/icons/sidebar/danhgia.png");
        ICON_PATHS.put("PERFORMANCE_EVALUATION", "/icons/sidebar/danhgia.png");
        ICON_PATHS.put("DANHGIA", "/icons/sidebar/danhgia.png");
        
        // Schedule/Lịch
        ICON_PATHS.put("SCHEDULE_MANAGEMENT", "/icons/sidebar/lich.png");
        ICON_PATHS.put("LICH", "/icons/sidebar/lich.png");
        
        // Payroll/Lương
        ICON_PATHS.put("PAYROLL_MANAGEMENT", "/icons/sidebar/luong.png");
        ICON_PATHS.put("LUONG", "/icons/sidebar/luong.png");
        
        // Contract/Hợp Đồng
        ICON_PATHS.put("CONTRACT_MANAGEMENT", "/icons/sidebar/hopdong.png");
        ICON_PATHS.put("HOPDONG", "/icons/sidebar/hopdong.png");
        
        // Account/Tài Khoản
        ICON_PATHS.put("ACCOUNT_MANAGEMENT", "/icons/sidebar/taikhoan.png");
        ICON_PATHS.put("TAIKHOAN", "/icons/sidebar/taikhoan.png");
        
        // Category/Danh Mục
        ICON_PATHS.put("CATEGORY_MANAGEMENT", "/icons/sidebar/danhmuc.png");
        ICON_PATHS.put("DANHMUC", "/icons/sidebar/danhmuc.png");
        
        // Permission/Phân Quyền
        ICON_PATHS.put("PERMISSION_MANAGEMENT", "/icons/sidebar/phanquyen.png");
        ICON_PATHS.put("PHANQUYEN", "/icons/sidebar/phanquyen.png");
        
        // Employee Dashboard
        ICON_PATHS.put("PROFILE", "/icons/sidebar/nhanvien.png");
        ICON_PATHS.put("ATTENDANCE", "/icons/sidebar/chamcong.png");
        ICON_PATHS.put("SCHEDULE", "/icons/sidebar/lich.png");
        ICON_PATHS.put("PAYROLL", "/icons/sidebar/luong.png");
        ICON_PATHS.put("LEAVE", "/icons/sidebar/nghiphep.png");
        ICON_PATHS.put("EVALUATION", "/icons/sidebar/danhgia.png");
        
        // Legacy mappings
        ICON_PATHS.put("NHANVIEN", "/icons/sidebar/nhanvien.png");
        ICON_PATHS.put("PHONGBAN", "/icons/sidebar/phongban.png");
        ICON_PATHS.put("TANGCA", "/icons/sidebar/tangca.png");
        
        // Logout
        ICON_PATHS.put("LOGOUT", "/icons/sidebar/logout.png");
    }

    public static ImageIcon getIcon(String cardName) {
        String path = ICON_PATHS.get(cardName);
        if (path != null) {
            java.net.URL url = SidebarIcon.class.getResource(path);
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                return resizeIcon(icon, ICON_SIZE, ICON_SIZE);
            }
        }
        return createDefaultIcon(cardName);
    }

    private static ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        if (icon == null || icon.getImage() == null) {
            return icon;
        }

        Image img = icon.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        return new ImageIcon(resized);
    }

    private static ImageIcon createDefaultIcon(String cardName) {
        BufferedImage img = new BufferedImage(ICON_SIZE, ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(200, 180, 255));
        g.fillOval(4, 4, 24, 24);
        g.dispose();
        return new ImageIcon(img);
    }
}
