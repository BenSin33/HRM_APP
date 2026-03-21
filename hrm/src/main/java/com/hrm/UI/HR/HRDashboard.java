package com.hrm.UI.HR;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.hrm.DTO.UserDTO;
import com.hrm.DAO.NhanVienDAO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.Service.PermissionService;
import com.hrm.UI.HR.AccountManagerTab.AccountManagerPanel;
import com.hrm.UI.HR.Attendancetab.AttenDanceManagement;
import com.hrm.UI.HR.ContractTab.ContractManagement;
import com.hrm.UI.HR.Department.DepartmentManagementPanel;
import com.hrm.UI.HR.EmployeeTab.EmployeeManagementPanel;
import com.hrm.UI.HR.Evaluationtab.EvaluationManagement;
import com.hrm.UI.HR.Leavetab.LeaveManagement;
import com.hrm.UI.HR.Overview.DashboardOverview;
import com.hrm.UI.HR.CategoryTab.*;
import com.hrm.UI.HR.PermissionTab.MainPermissionPanel;
import com.hrm.UI.HR.SalaryTab.SalaryManagement;
import com.hrm.UI.component.Sidebar;
import com.hrm.UI.component.SidebarTab;
import com.hrm.utils.SessionManager;

public class HRDashboard extends JFrame {
    
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private PermissionService permissionService;
    private NhanVienDAO nhanVienDAO;
    
    public HRDashboard(){
        permissionService = new PermissionService();
        nhanVienDAO = new NhanVienDAO();
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        this.setTitle("HR Dashboard");
        this.setSize(1200, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setLayout(new BorderLayout());

        // cấu hình sidebar và nội dung
        List<SidebarTab> HRTabs = new ArrayList<>();
        // NOTE: Use base CNxx codes (stored in DB) to make permissions effective.
        if (permissionService.canView(currentUser, "CN01")) { // CN01: Quản lý nhân sự (dashboard entry point)
            HRTabs.add(new SidebarTab("TỔNG QUAN", "DASHBOARD"));
        }
        // NOTE: Employee management maps to CN01 in DB.
        if (permissionService.canView(currentUser, "CN01")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ NHÂN VIÊN", "EMPLOYEE_MANAGEMENT"));
        }
        // NOTE: Department management maps to CN07 in DB.
        if (permissionService.canView(currentUser, "CN07")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ PHÒNG BAN", "DEPARTMENT_MANAGEMENT"));
        }
        // NOTE: Attendance maps to CN03 in DB.
        if (permissionService.canView(currentUser, "CN03")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ CHẤM CÔNG", "ATTENDANCE_MANAGEMENT"));
        }
        // NOTE: Leave maps to CN04 in DB.
        if (permissionService.canView(currentUser, "CN04")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ NGHỈ PHÉP", "LEAVE_MANAGEMENT"));
        }
        // NOTE: Evaluation maps to CN05 in DB.
        if (permissionService.canView(currentUser, "CN05")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ ĐÁNH GIÁ", "EVALUATION_MANAGEMENT"));
        }
        // NOTE: Payroll maps to CN02 in DB.
        if (permissionService.canView(currentUser, "CN02")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ LƯƠNG", "PAYROLL_MANAGEMENT"));
        }
        
        // Special check for Permission Management - Only HR Head (PB01 + CV01) can access
        if (isHRAdmin(currentUser)) {
            HRTabs.add(new SidebarTab("PHÂN QUYỀN", "PERMISSION_MANAGEMENT"));
        }

        // NOTE: Contract maps to CN06 in DB.
        if (permissionService.canView(currentUser, "CN06")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ HỢP ĐỒNG", "CONTRACT_MANAGEMENT"));
        }
        // NOTE: Account management not in DB list; map to CN01 (HR core) for now.
        if (permissionService.canView(currentUser, "CN01")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ TÀI KHOẢN", "ACCOUNT_MANAGEMENT"));
        }
        // NOTE: Category management maps to CN09 in DB.
        if (permissionService.canView(currentUser, "CN09")) {
            HRTabs.add(new SidebarTab("QUẢN LÝ DANH MỤC", "CATEGORY_MANAGEMENT"));
        }


        HRTabs.add(new SidebarTab("ĐĂNG XUẤT", "LOGOUT"));
        // register dashboard/overview panel first so the default tab can display
        contentPanel.add(new DashboardOverview(), "DASHBOARD");

        contentPanel.add(createDashboardPanel(new EmployeeManagementPanel()), "EMPLOYEE_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new DepartmentManagementPanel()), "DEPARTMENT_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new ContractManagement()), "CONTRACT_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new SalaryManagement()), "PAYROLL_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new AttenDanceManagement()), "ATTENDANCE_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new LeaveManagement()), "LEAVE_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new EvaluationManagement()), "EVALUATION_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new MainPermissionPanel()), "PERMISSION_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new AccountManagerPanel()), "ACCOUNT_MANAGEMENT");
        contentPanel.add(createDashboardPanel(new CategoryPanel()), "CATEGORY_MANAGEMENT");

        Sidebar sidebar = new Sidebar(contentPanel, cardLayout, HRTabs); // tạo sidebar

        this.add(sidebar, BorderLayout.WEST); // thêm sidebar vào giao diện chính
        this.add(contentPanel, BorderLayout.CENTER); // thêm content panel vào giao diện chính
        this.setVisible(true);

    }

    private boolean isHRAdmin(UserDTO user) {
        if (user == null || !"R1".equals(user.getRoleId())) {
            return false;
        }
        NhanVienDTO employeeDetails = nhanVienDAO.findById(user.getManv());
        if (employeeDetails == null) {
            return false;
        }
        // Chỉ nhân viên là Trưởng phòng (CV01) của phòng Nhân sự (PB01) mới có quyền phân quyền
        boolean isHRDepartment = "PB01".equals(employeeDetails.getMaphongban());
        boolean isHeadOfDepartment = "CV01".equals(employeeDetails.getMachucvu());

        return isHRDepartment && isHeadOfDepartment;
    }

    private JPanel createDashboardPanel(JPanel panel) {
        // do not add panels directly to the frame here; container panels are
        // managed by the CardLayout on contentPanel instead.
        return panel;
    }
    
}
