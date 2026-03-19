package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.hrm.DTO.ContractDTO;
import com.hrm.UI.component.IFormInput;
import com.hrm.utils.FormValidator;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.util.Calendar;

public class ContractEditForm extends JPanel implements IFormInput<ContractDTO> {
    private JTextField txtMaHopDong;
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JTextField txtLoaiHopDong;
    private JDateChooser dateNgayKy;
    private JDateChooser dateNgayHetHan;
    private JTextField txtLuongCoBan;
    private JComboBox<String> cbTrangThai;

    public ContractEditForm() {
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Mã hợp đồng
        add(new JLabel("Mã hợp đồng:"));
        txtMaHopDong = new JTextField();
        txtMaHopDong.setEditable(false);
        add(txtMaHopDong);

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

        // Phòng ban
        add(new JLabel("Phòng ban:"));
        txtPhongBan = new JTextField();
        txtPhongBan.setEditable(false);
        add(txtPhongBan);

        // Loại hợp đồng
        add(new JLabel("Loại hợp đồng:"));
        txtLoaiHopDong = new JTextField();
        add(txtLoaiHopDong);

        // Ngày ký - Date Picker
        add(new JLabel("Ngày ký:"));
        dateNgayKy = new JDateChooser();
        dateNgayKy.setDateFormatString("dd/MM/yyyy");
        dateNgayKy.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(dateNgayKy);

        // Ngày hết hạn - Date Picker
        add(new JLabel("Ngày hết hạn:"));
        dateNgayHetHan = new JDateChooser();
        dateNgayHetHan.setDateFormatString("dd/MM/yyyy");
        dateNgayHetHan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(dateNgayHetHan);

        // Lương cơ bản
        add(new JLabel("Lương cơ bản:"));
        txtLuongCoBan = new JTextField();
        add(txtLuongCoBan);

        // Trạng thái
        add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{"Còn hiệu lực", "Hết hạn", "Chấm dứt"});
        add(cbTrangThai);
    }

    @Override
    public void setFormData(ContractDTO contract) {
        if (contract != null) {
            txtMaHopDong.setText(contract.maHopDong);
            txtMaNV.setText(contract.maNV);
            txtHoTen.setText(contract.hoTen);
            txtPhongBan.setText(contract.phongBan);
            txtLoaiHopDong.setText(contract.loaiHopDong);
            
            // Set date pickers
            if (contract.ngayLamHopDong != null) {
                Calendar cal = Calendar.getInstance();
                cal.set(contract.ngayLamHopDong.getYear(), 
                        contract.ngayLamHopDong.getMonthValue() - 1, 
                        contract.ngayLamHopDong.getDayOfMonth());
                dateNgayKy.setDate(cal.getTime());
            }
            if (contract.hanHopDong != null) {
                Calendar cal = Calendar.getInstance();
                cal.set(contract.hanHopDong.getYear(), 
                        contract.hanHopDong.getMonthValue() - 1, 
                        contract.hanHopDong.getDayOfMonth());
                dateNgayHetHan.setDate(cal.getTime());
            }
            
            // Set salary without formatting
            if (contract.luongCoBan != null) {
                txtLuongCoBan.setText(contract.luongCoBan.toPlainString());
            }
            cbTrangThai.setSelectedItem(contract.trangThai);
        }
    }

    @Override
    public ContractDTO getFormData() {
        ContractDTO contract = new ContractDTO();
        contract.maHopDong = txtMaHopDong.getText();
        contract.maNV = txtMaNV.getText();
        contract.hoTen = txtHoTen.getText();
        contract.phongBan = txtPhongBan.getText();
        contract.loaiHopDong = txtLoaiHopDong.getText();
        
        // Get dates from date pickers
        try {
            if (dateNgayKy.getDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateNgayKy.getDate());
                contract.ngayLamHopDong = LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
                );
            }
            if (dateNgayHetHan.getDate() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dateNgayHetHan.getDate());
                contract.hanHopDong = LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Parse salary - remove all non-digit characters except decimal point
        try {
            String salaryText = txtLuongCoBan.getText().trim();
            // Remove currency symbols, commas, and spaces
            salaryText = salaryText.replaceAll("[^0-9.]", "");
            if (!salaryText.isEmpty()) {
                contract.luongCoBan = new BigDecimal(salaryText);
            } else {
                contract.luongCoBan = BigDecimal.ZERO;
            }
        } catch (Exception e) {
            e.printStackTrace();
            contract.luongCoBan = BigDecimal.ZERO;
        }
        
        contract.trangThai = (String) cbTrangThai.getSelectedItem();
        return contract;
    }

    @Override
    public void clearForm() {
        txtMaHopDong.setText("");
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtPhongBan.setText("");
        txtLoaiHopDong.setText("");
        dateNgayKy.setDate(null);
        dateNgayHetHan.setDate(null);
        txtLuongCoBan.setText("");
        cbTrangThai.setSelectedIndex(0);
    }

    @Override
    public boolean validateForm() {
        if (txtLoaiHopDong.getText().trim().isEmpty()) {
            FormValidator.showError(this, "Vui lòng nhập loại hợp đồng!");
            return false;
        }
        if (dateNgayKy.getDate() == null || dateNgayHetHan.getDate() == null) {
            FormValidator.showError(this, "Vui lòng chọn ngày ký và ngày hết hạn!");
            return false;
        }
        if (txtLuongCoBan.getText().trim().isEmpty()) {
            FormValidator.showError(this, "Vui lòng nhập lương cơ bản!");
            return false;
        }
        return true;
    }
}
