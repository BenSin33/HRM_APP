package com.hrm.UI.HR.SalaryTab;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.UI.component.IFormInput;

public class SalaryEditForm extends JPanel implements IFormInput<SalaryDTO> {
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JSpinner spThang;
    private JSpinner spNam;
    private JTextField txtLuongCoBan;
    private JTextField txtSoNgayCong;
    private JTextField txtTongPhucap;
    private JTextField txtTongKhauTru;
    private JTextField txtThucLinh;
    private JComboBox<String> cbTrangThai;

    public SalaryEditForm() {
        setLayout(new GridLayout(11, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Mã nhân viên
        add(new JLabel("Mã NV:"));
        txtMaNV = new JTextField();
        txtMaNV.setEditable(false);
        add(txtMaNV);

        // Họ tên
        add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        txtHoTen.setEditable(false);
        add(txtHoTen);

        // Phòng ban
        add(new JLabel("Phòng ban:"));
        txtPhongBan = new JTextField();
        txtPhongBan.setEditable(false);
        add(txtPhongBan);

        // Tháng
        add(new JLabel("Tháng:"));
        spThang = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        add(spThang);

        // Năm
        add(new JLabel("Năm:"));
        spNam = new JSpinner(new SpinnerNumberModel(2024, 2000, 2100, 1));
        add(spNam);

        // Lương cơ bản
        add(new JLabel("Lương cơ bản:"));
        txtLuongCoBan = new JTextField();
        add(txtLuongCoBan);

        // Số ngày công
        add(new JLabel("Số ngày công:"));
        txtSoNgayCong = new JTextField();
        add(txtSoNgayCong);

        // Tổng phụ cấp
        add(new JLabel("Tổng phụ cấp:"));
        txtTongPhucap = new JTextField();
        add(txtTongPhucap);

        // Tổng khấu trừ
        add(new JLabel("Tổng khấu trừ:"));
        txtTongKhauTru = new JTextField();
        add(txtTongKhauTru);

        // Thực lĩnh
        add(new JLabel("Thực lĩnh:"));
        txtThucLinh = new JTextField();
        txtThucLinh.setEditable(false);
        add(txtThucLinh);

        // Trạng thái
        add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Nháp", "Đã chốt", "Đã thanh toán"});
        add(cbTrangThai);
    }

    @Override
    public void setFormData(SalaryDTO salary) {
        if (salary != null) {
            txtMaNV.setText(salary.maNV);
            txtHoTen.setText(salary.hoTen);
            txtPhongBan.setText(salary.phongBan);
            spThang.setValue(salary.thang);
            spNam.setValue(salary.nam);
            txtLuongCoBan.setText(salary.luongCoBan != null ? salary.luongCoBan.toString() : "");
        txtSoNgayCong.setText(salary.soNgayCong > 0 ? String.valueOf(salary.soNgayCong) : "");
            txtTongPhucap.setText(salary.tongPhucap != null ? salary.tongPhucap.toString() : "");
            txtTongKhauTru.setText(salary.tongKhauTru != null ? salary.tongKhauTru.toString() : "");
            txtThucLinh.setText(salary.thucLinh != null ? salary.thucLinh.toString() : "");
            cbTrangThai.setSelectedItem(salary.trangThai);
        }
    }

    @Override
    public SalaryDTO getFormData() {
        SalaryDTO salary = new SalaryDTO();
        salary.maNV = txtMaNV.getText();
        salary.hoTen = txtHoTen.getText();
        salary.phongBan = txtPhongBan.getText();
        salary.thang = (Integer) spThang.getValue();
        salary.nam = (Integer) spNam.getValue();
        
        try {
            salary.luongCoBan = new BigDecimal(txtLuongCoBan.getText().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            salary.luongCoBan = BigDecimal.ZERO;
        }
        
        try {
            salary.soNgayCong = Float.parseFloat(txtSoNgayCong.getText());
        } catch (Exception e) {
            salary.soNgayCong = 0;
        }
        
        try {
            salary.tongPhucap = new BigDecimal(txtTongPhucap.getText().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            salary.tongPhucap = BigDecimal.ZERO;
        }
        
        try {
            salary.tongKhauTru = new BigDecimal(txtTongKhauTru.getText().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            salary.tongKhauTru = BigDecimal.ZERO;
        }
        
        salary.trangThai = (String) cbTrangThai.getSelectedItem();
        return salary;
    }

    @Override
    public void clearForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhongBan.setText("");
        spThang.setValue(1);
        spNam.setValue(2024);
        txtLuongCoBan.setText("");
        txtSoNgayCong.setText("");
        txtTongPhucap.setText("");
        txtTongKhauTru.setText("");
        txtThucLinh.setText("");
        cbTrangThai.setSelectedIndex(0);
    }

    @Override
    public boolean validateForm() {
        if (txtLuongCoBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lương cơ bản!");
            return false;
        }
        if (txtSoNgayCong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số ngày công!");
            return false;
        }
        return true;
    }
}
