package com.hrm.UI.HR.SalaryTab;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.UI.component.IFormInput;
import com.hrm.DAO.AllowanceDAO;
import com.hrm.DAO.DeductionDAO;
import com.hrm.utils.JDBCConection;

public class SalaryEditForm extends JPanel implements IFormInput<SalaryDTO> {
    private JTextField txtMaLuong;  // Hidden field for maLuong
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JTextField txtChucVu;
    private JTextField txtLuongCoBan;
    private JTextField txtHesoTrinhDo;
    private JTextField txtSoNgayCong;

    private JTextField txtSoNgayCongChuan; // NEW: Số ngày công chuẩn

    private JTextField txtTongPhucap;
    private JTextField txtTongKhauTru;
    private JTextField txtThucLinh;
    private JComboBox<String> cbTrangThai;
    private JComboBox<String> cbTinhTrangThanToan;

    public SalaryEditForm() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Mã lương (ẩn)
        txtMaLuong = new JTextField();
        txtMaLuong.setVisible(false);

        // Panel chính chứa các trường
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(8, 2, 20, 15));

        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Thông tin nhân viên (read-only)
        addFormField(mainPanel, "Mã NV:", txtMaNV = createReadOnlyField(), true);
        addFormField(mainPanel, "Họ tên:", txtHoTen = createReadOnlyField(), true);
        
        addFormField(mainPanel, "Phòng ban:", txtPhongBan = createReadOnlyField(), true);
        addFormField(mainPanel, "Chức vụ:", txtChucVu = createReadOnlyField(), true);

        // Thông tin lương
        addFormField(mainPanel, "Lương cơ bản (VND):", txtLuongCoBan = createReadOnlyField(), true);
        addFormField(mainPanel, "Hệ số trình độ:", txtHesoTrinhDo = createReadOnlyField(), true);

        // Ngày công
        addFormField(mainPanel, "Số ngày công:", txtSoNgayCong = createEditableField(), false);
        addFormField(mainPanel, "Số ngày công chuẩn:", txtSoNgayCongChuan = createEditableField("26"), false);


        // Phụ cấp và khấu trừ
        addFormField(mainPanel, "Tổng phụ cấp (VND):", txtTongPhucap = createReadOnlyField(), true);
        addFormField(mainPanel, "Tổng khấu trừ (VND):", txtTongKhauTru = createReadOnlyField(), true);

        // Thực lĩnh
        addFormField(mainPanel, "Thực lĩnh (VND):", txtThucLinh = createReadOnlyField(), true);
        addFormField(mainPanel, "Trạng thái:", cbTrangThai = new JComboBox<>(new String[]{"Chưa khóa", "Đã khóa"}), false);

        // Tình trạng thanh toán
        addFormField(mainPanel, "Tình trạng thanh toán:", cbTinhTrangThanToan = new JComboBox<>(new String[]{"Chưa thanh toán", "Đã thanh toán"}), false);
        mainPanel.add(new JLabel("")); // Placeholder

        add(mainPanel, BorderLayout.CENTER);
    }

    private JTextField createReadOnlyField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return field;
    }

    private JTextField createEditableField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return field;
    }

    private JTextField createEditableField(String defaultValue) {
        JTextField field = new JTextField(defaultValue);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return field;
    }

    private void addFormField(JPanel panel, String labelText, JComponent field, boolean isReadOnly) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(60, 60, 60));
        
        panel.add(label);
        
        if (field instanceof JTextField && isReadOnly) {
            ((JTextField) field).setEditable(false);
            ((JTextField) field).setBackground(new Color(240, 240, 240));
        } else if (field instanceof JComboBox) {
            ((JComboBox<?>) field).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        } else if (field instanceof JTextField) {
            ((JTextField) field).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        
        panel.add(field);
    }

    @Override
    public void setFormData(SalaryDTO salary) {
        if (salary != null) {
            txtMaLuong.setText(salary.maLuong != null ? salary.maLuong : "");
            txtMaNV.setText(salary.maNV);
            txtHoTen.setText(salary.hoTen);
            txtPhongBan.setText(salary.phongBan);
            txtChucVu.setText(getChucVuName(salary.maNV));
            txtLuongCoBan.setText(salary.luongCoBan != null ? String.format("%,.0f", salary.luongCoBan.doubleValue()) : "");
            txtHesoTrinhDo.setText(salary.hesotrinhdo != null ? salary.hesotrinhdo.toString() : "1.00");
            txtSoNgayCong.setText(salary.soNgayCong > 0 ? String.valueOf(salary.soNgayCong) : "");
            txtSoNgayCongChuan.setText(salary.soNgayCongChuan > 0 ? String.valueOf(salary.soNgayCongChuan) : "26");

            
            // Tự động lấy tổng phụ cấp và khấu trừ từ database
            try {
                AllowanceDAO allowanceDAO = new AllowanceDAO();
                BigDecimal totalAllowance = allowanceDAO.getTotalAllowances();
                txtTongPhucap.setText(String.format("%,.0f", totalAllowance.doubleValue()));
                
                DeductionDAO deductionDAO = new DeductionDAO();
                BigDecimal totalDeduction = deductionDAO.getTotalDeductions();
                txtTongKhauTru.setText(String.format("%,.0f", totalDeduction.doubleValue()));
            } catch (Exception e) {
                e.printStackTrace();
                // Nếu lỗi, sử dụng giá trị từ salary object
                txtTongPhucap.setText(salary.tongPhucap != null ? String.format("%,.0f", salary.tongPhucap.doubleValue()) : "");
                txtTongKhauTru.setText(salary.tongKhauTru != null ? String.format("%,.0f", salary.tongKhauTru.doubleValue()) : "");
            }
            
            txtThucLinh.setText(salary.thucLinh != null ? String.format("%,.0f", salary.thucLinh.doubleValue()) : "");
            cbTrangThai.setSelectedItem(salary.trangThai);
            cbTinhTrangThanToan.setSelectedItem(salary.tinhTrangThanToan != null ? salary.tinhTrangThanToan : "Chưa thanh toán");
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
    public SalaryDTO getFormData() {
        SalaryDTO salary = new SalaryDTO();
        
        // Lấy maLuong từ hidden field
        salary.maLuong = txtMaLuong.getText();
        
        salary.maNV = txtMaNV.getText();
        salary.hoTen = txtHoTen.getText();
        salary.phongBan = txtPhongBan.getText();
        
        // Lương cơ bản - giữ nguyên decimal
        try {
            String luongCoBasnStr = txtLuongCoBan.getText().trim();
            if (!luongCoBasnStr.isEmpty()) {
                salary.luongCoBan = new BigDecimal(luongCoBasnStr.replaceAll("[^0-9.]", "").replaceAll(",", ""));
            } else {
                salary.luongCoBan = BigDecimal.ZERO;
            }
        } catch (Exception e) {
            salary.luongCoBan = BigDecimal.ZERO;
        }

        // Hệ số trình độ - lấy từ trường hiển thị
        try {
            String hesoStr = txtHesoTrinhDo.getText().trim();
            if (!hesoStr.isEmpty()) {
                salary.hesotrinhdo = new BigDecimal(hesoStr);
            } else {
                salary.hesotrinhdo = new BigDecimal("1.00");
            }
        } catch (Exception e) {
            salary.hesotrinhdo = new BigDecimal("1.00");
        }
        
        // Số ngày công
        try {
            String soNgayCongStr = txtSoNgayCong.getText().trim();
            if (!soNgayCongStr.isEmpty()) {
                salary.soNgayCong = Float.parseFloat(soNgayCongStr);
            } else {
                salary.soNgayCong = 0;
            }
        } catch (Exception e) {
            salary.soNgayCong = 0;
        }

        // Số ngày công chuẩn (NEW)
        try {
            String soNgayCongChuanStr = txtSoNgayCongChuan.getText().trim();
            if (!soNgayCongChuanStr.isEmpty()) {
                salary.soNgayCongChuan = Float.parseFloat(soNgayCongChuanStr);
            } else {
                salary.soNgayCongChuan = 26; // Mặc định 26 ngày
            }
        } catch (Exception e) {
            salary.soNgayCongChuan = 26;
        }
        
        // Tổng phụ cấp - giữ nguyên decimal
        try {
            String tongPhucapStr = txtTongPhucap.getText().trim();
            if (!tongPhucapStr.isEmpty()) {
                salary.tongPhucap = new BigDecimal(tongPhucapStr.replaceAll("[^0-9.]", "").replaceAll(",", ""));
            } else {
                salary.tongPhucap = BigDecimal.ZERO;
            }
        } catch (Exception e) {
            salary.tongPhucap = BigDecimal.ZERO;
        }
        
        // Tổng khấu trừ - giữ nguyên decimal
        try {
            String tongKhauTruStr = txtTongKhauTru.getText().trim();
            if (!tongKhauTruStr.isEmpty()) {
                salary.tongKhauTru = new BigDecimal(tongKhauTruStr.replaceAll("[^0-9.]", "").replaceAll(",", ""));
            } else {
                salary.tongKhauTru = BigDecimal.ZERO;
            }
        } catch (Exception e) {
            salary.tongKhauTru = BigDecimal.ZERO;
        }
        
        // Công thức tính thực lĩnh (Updated):
        // (lương cơ bản * hệ số trình độ * (số ngày công / số ngày công chuẩn)) + tổng phụ cấp + phụ cấp chức vụ - tổng khấu trừ
        BigDecimal heSoTrinhDo = salary.hesotrinhdo != null ? salary.hesotrinhdo : new BigDecimal("1.00");
        BigDecimal phucapChucVu = salary.phucapChucVu != null ? salary.phucapChucVu : BigDecimal.ZERO;
        BigDecimal ngayCongRatio = new BigDecimal(salary.soNgayCong).divide(new BigDecimal(salary.soNgayCongChuan), 4, java.math.RoundingMode.HALF_UP);

        
        salary.thucLinh = salary.luongCoBan
                            .multiply(heSoTrinhDo)
                            .multiply(ngayCongRatio)
                            .add(salary.tongPhucap)
                            .add(phucapChucVu)
                            .subtract(salary.tongKhauTru)
                            .setScale(2, java.math.RoundingMode.HALF_UP);
        
        // Chuyển đổi trạng thái từ text sang numeric code
        String selectedStatus = (String) cbTrangThai.getSelectedItem();
        if (selectedStatus != null) {
            switch (selectedStatus) {
                case "Chưa khóa" -> salary.trangThai = "0";
                case "Đã khóa" -> salary.trangThai = "1";
                default -> salary.trangThai = "0";
            }
        } else {
            salary.trangThai = "0";
        }
        
        salary.tinhTrangThanToan = (String) cbTinhTrangThanToan.getSelectedItem();
        return salary;
    }

    @Override
    public void clearForm() {
        txtMaLuong.setText("");
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhongBan.setText("");
        txtLuongCoBan.setText("");
        txtHesoTrinhDo.setText("");
        txtSoNgayCong.setText("");
        txtSoNgayCongChuan.setText("26");
        txtTongPhucap.setText("");
        txtTongKhauTru.setText("");
        txtThucLinh.setText("");
        cbTrangThai.setSelectedIndex(0);
        cbTinhTrangThanToan.setSelectedIndex(0);
    }

    @Override
    public boolean validateForm() {
        // Kiểm tra số ngày công
        String soNgayCongStr = txtSoNgayCong.getText().trim();
        if (soNgayCongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số ngày công!");
            return false;
        }
        
        try {
            float soNgayCong = Float.parseFloat(soNgayCongStr);
            if (soNgayCong < 0) {
                JOptionPane.showMessageDialog(this, "Số ngày công không được âm!");
                return false;
            }
            
            // Giới hạn số ngày công theo tháng (tối đa 31)
            if (soNgayCong > 31) {
                JOptionPane.showMessageDialog(this, "Số ngày công không được vượt quá 31 ngày!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số ngày công phải là số hợp lệ!");
            return false;
        }
        
        // Kiểm tra số ngày công chuẩn (NEW)
        String soNgayCongChuanStr = txtSoNgayCongChuan.getText().trim();
        if (soNgayCongChuanStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số ngày công chuẩn!");
            return false;
        }
        
        try {
            float soNgayCongChuan = Float.parseFloat(soNgayCongChuanStr);
            if (soNgayCongChuan <= 0) {
                JOptionPane.showMessageDialog(this, "Số ngày công chuẩn phải > 0!");
                return false;
            }
            
            if (soNgayCongChuan > 31) {
                JOptionPane.showMessageDialog(this, "Số ngày công chuẩn không được vượt quá 31 ngày!");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số ngày công chuẩn phải là số hợp lệ!");

            return false;
        }
        
        return true;
    }
}
