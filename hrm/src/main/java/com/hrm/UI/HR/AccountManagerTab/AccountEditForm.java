package com.hrm.UI.HR.AccountManagerTab;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.hrm.DTO.AccountManagerDTO;
import com.hrm.UI.component.IFormInput;
import com.hrm.utils.FormValidator;
import com.hrm.utils.JDBCConection;

public class AccountEditForm extends JPanel implements IFormInput<AccountManagerDTO> {
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtDienThoai;
    private JTextField txtPhongBan;
    private JTextField txtChucVu;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbStatus;
    private JPasswordField txtPassword;
    private List<String> roleIds;
    private Map<String, String> roleMap;
    private String currentUserMaNV;
    private String currentUserPosition;
    private String currentUserDepartment;

    public AccountEditForm() {
        roleIds = new ArrayList<>();
        roleMap = new HashMap<>();
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Mã nhân viên
        add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField();
        txtMaNV.setEditable(false);
        add(txtMaNV);

        // Họ tên
        add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        txtHoTen.setEditable(false);
        add(txtHoTen);

        // Email
        add(new JLabel("Email:"));
        txtEmail = new JTextField();
        txtEmail.setEditable(false);
        add(txtEmail);

        // Điện thoại
        add(new JLabel("Điện thoại:"));
        txtDienThoai = new JTextField();
        txtDienThoai.setEditable(false);
        add(txtDienThoai);

        // Phòng ban
        add(new JLabel("Phòng ban:"));
        txtPhongBan = new JTextField();
        txtPhongBan.setEditable(false);
        add(txtPhongBan);

        // Chức vụ
        add(new JLabel("Chức vụ:"));
        txtChucVu = new JTextField();
        txtChucVu.setEditable(false);
        add(txtChucVu);

        // Vai trò
        add(new JLabel("Vai trò:"));
        cbRole = new JComboBox<>();
        loadRoles();
        cbRole.setEnabled(false);
        cbRole.setToolTipText("Vai trò được tự động phân theo chức vụ nhân viên");
        add(cbRole);

        // Trạng thái
        add(new JLabel("Trạng thái:"));
        cbStatus = new JComboBox<>(new String[]{"Kích hoạt", "Vô hiệu hóa"});
        add(cbStatus);

        // Mật khẩu (tuỳ chọn)
        add(new JLabel("Mật khẩu mới (để trống nếu không đổi):"));
        txtPassword = new JPasswordField();
        add(txtPassword);
    }

    private void loadRoles() {
        String sql = "SELECT ROLEID, ROLENAME FROM role ORDER BY ROLENAME ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String roleId = rs.getString("ROLEID");
                String roleName = rs.getString("ROLENAME");
                roleIds.add(roleId);
                roleMap.put(roleName, roleId);
                cbRole.addItem(roleName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentUser(String maNV, String chucVu, String phongBan) {
        this.currentUserMaNV = maNV;
        this.currentUserPosition = chucVu;
        this.currentUserDepartment = phongBan;
    }

    @Override
    public void setFormData(AccountManagerDTO account) {
        if (account != null) {
            txtMaNV.setText(account.maNV);
            txtHoTen.setText(account.hoTen);
            txtEmail.setText(account.email != null ? account.email : "");
            txtDienThoai.setText(account.dienThoai != null ? account.dienThoai : "");
            txtPhongBan.setText(account.phongBan);
            txtChucVu.setText(getChucVuName(account.maNV));
            cbRole.setSelectedItem(account.roleName);
            cbStatus.setSelectedIndex(account.status == 1 ? 0 : 1);
            
            // Chỉ cho phép sửa role nếu người dùng hiện tại là Trưởng phòng
            boolean isHeadOfDepartment = "CV01".equals(currentUserPosition);
            cbRole.setEnabled(isHeadOfDepartment);
            if (!isHeadOfDepartment) {
                cbRole.setToolTipText("Chỉ Trưởng phòng mới có thể thay đổi vai trò");
            } else {
                cbRole.setToolTipText("Chọn vai trò cho nhân viên");
            }
        }
    }

    private String getChucVuName(String maNV) {
        String sql = "SELECT cv.TENVITRI FROM nhanvien nv " +
                     "JOIN chucvu cv ON nv.MACHUCVU = cv.MACHUCVU " +
                     "WHERE nv.MANV = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TENVITRI");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public AccountManagerDTO getFormData() {
        AccountManagerDTO account = new AccountManagerDTO();
        account.maNV = txtMaNV.getText();
        account.hoTen = txtHoTen.getText();
        account.email = txtEmail.getText();
        account.dienThoai = txtDienThoai.getText();
        account.phongBan = txtPhongBan.getText();
        account.roleName = (String) cbRole.getSelectedItem();
        account.roleId = roleMap.get(account.roleName);
        account.status = cbStatus.getSelectedIndex() == 0 ? 1 : 0;
        
        return account;
    }

    private String getRolePositionMapping(String roleName) {
        // Mapping vai trò với chức vụ phù hợp
        if ("Admin".equals(roleName)) {
            return "CV01 (Trưởng phòng) hoặc CV03 (Nhân viên nhân sự) ở phòng Nhân sự";
        } else if ("Manager".equals(roleName)) {
            return "CV01 (Trưởng phòng)";
        } else if ("Employee".equals(roleName)) {
            return "CV02 (Nhân viên) hoặc CV03 (Nhân viên nhân sự)";
        }
        // Role tùy chỉnh: phù hợp với mọi chức vụ
        return "Phù hợp với mọi chức vụ";
    }

    private boolean isRolePositionCompatible(String roleName, String positionCode) {
        // Kiểm tra xem chức vụ có phù hợp với vai trò không
        if ("Admin".equals(roleName)) {
            return "CV01".equals(positionCode) || "CV03".equals(positionCode);
        } else if ("Manager".equals(roleName)) {
            return "CV01".equals(positionCode);
        } else if ("Employee".equals(roleName)) {
            return "CV02".equals(positionCode) || "CV03".equals(positionCode);
        }
        // Role tùy chỉnh (Supervisor, Guest, R4...) -> phù hợp với mọi chức vụ
        return true;
    }

    private String getPositionCode(String maNV) {
        String sql = "SELECT MACHUCVU FROM nhanvien WHERE MANV = ?";
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("MACHUCVU");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void clearForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtDienThoai.setText("");
        txtPhongBan.setText("");
        txtChucVu.setText("");
        cbRole.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtPassword.setText("");
    }

    @Override
    public boolean validateForm() {
        String email = txtEmail.getText().trim();
        String dienThoai = txtDienThoai.getText().trim();
        
        // Validate Email
        if (email.isEmpty()) {
            FormValidator.showError(this, "Vui lòng nhập email!");
            return false;
        }
        if (!FormValidator.isValidEmail(email)) {
            FormValidator.showError(this, "Email không hợp lệ! (Định dạng: abc@domain.com)");
            return false;
        }
        
        // Validate Điện thoại
        if (!dienThoai.isEmpty() && !FormValidator.isValidPhone(dienThoai)) {
            FormValidator.showError(this, "Điện thoại không hợp lệ! (Chỉ chứa số, tối thiểu 9 chữ số)");
            return false;
        }
        
        // Validate mật khẩu nếu nhập
        String password = new String(txtPassword.getPassword()).trim();
        if (!password.isEmpty() && password.length() < 4) {
            FormValidator.showError(this, "Mật khẩu tối thiểu 4 ký tự!");
            return false;
        }
        
        // Validate role-position compatibility
        String selectedRole = (String) cbRole.getSelectedItem();
        String maNV = txtMaNV.getText();
        String positionCode = getPositionCode(maNV);
        
        if (selectedRole != null && !isRolePositionCompatible(selectedRole, positionCode)) {
            String requiredPosition = getRolePositionMapping(selectedRole);
            JOptionPane.showMessageDialog(this, 
                "Chức vụ hiện tại không phù hợp với vai trò '" + selectedRole + "'!\n\n" +
                "Vai trò '" + selectedRole + "' yêu cầu:\n" + requiredPosition + "\n\n" +
                "Vui lòng thay đổi chức vụ của nhân viên trước khi phân công vai trò.",
                "Lỗi kiểm tra chức vụ",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }
}
