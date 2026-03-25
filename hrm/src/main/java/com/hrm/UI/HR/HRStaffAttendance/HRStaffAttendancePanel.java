package com.hrm.UI.HR.HRStaffAttendance;

import com.hrm.DTO.UserDTO;
import com.hrm.utils.SessionManager;
import com.hrm.UI.Employee.AttendanceEmp.AttendanceManage;

import javax.swing.*;
import java.awt.*;

/**
 * Wrapper panel cho HR nhân viên (role=R1 nhưng CV!=CV01)
 * để sử dụng giao diện checkin/checkout từ AttendanceEmp
 */
public class HRStaffAttendancePanel extends JPanel {
    private AttendanceManage attendanceManage;
    
    public HRStaffAttendancePanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi: Không tìm thấy thông tin người dùng", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Reuse AttendanceManage từ Employee module với mã nhân viên của HR staff hiện tại
        attendanceManage = new AttendanceManage(currentUser.getManv());
        add(attendanceManage, BorderLayout.CENTER);
    }
}
