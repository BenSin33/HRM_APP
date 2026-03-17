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
import com.hrm.utils.JDBCConection;

public class AccountEditForm extends JPanel implements IFormInput<AccountManagerDTO> {
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtEmail;
    private JTextField txtDienThoai;
    private JTextField txtPhongBan;
    private JComboBox<String> cbRole;
    private JComboBox<String> cbStatus;
    private JPasswordField txtPassword;
    private List<String> roleIds;
    private Map<String, String> roleMap;

    public AccountEditForm() {
        roleIds = new ArrayList<>();
        roleMap = new HashMap<>();
        setLayout(new GridLayout(8, 2, 10, 10));
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
        add(txtEmail);

        // Điện thoại
        add(new JLabel("Điện thoại:"));
        txtDienThoai = new JTextField();
        add(txtDienThoai);

        // Phòng ban
        add(new JLabel("Phòng ban:"));
        txtPhongBan = new JTextField();
        txtPhongBan.setEditable(false);
        add(txtPhongBan);

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

    @Override
    public void setFormData(AccountManagerDTO account) {
        if (account != null) {
            txtMaNV.setText(account.maNV);
            txtHoTen.setText(account.hoTen);
            txtEmail.setText(account.email != null ? account.email : "");
            txtDienThoai.setText(account.dienThoai != null ? account.dienThoai : "");
            txtPhongBan.setText(account.phongBan);
            cbRole.setSelectedItem(account.roleName);
            cbStatus.setSelectedIndex(account.status == 1 ? 0 : 1);
        }
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

    @Override
    public void clearForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtDienThoai.setText("");
        txtPhongBan.setText("");
        cbRole.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        txtPassword.setText("");
    }

    @Override
    public boolean validateForm() {
        if (txtEmail.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!");
            return false;
        }
        if (!isValidEmail(txtEmail.getText())) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ!");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }
}
