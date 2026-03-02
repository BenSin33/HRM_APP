package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.hrm.DTO.ContractDTO;
import com.hrm.UI.component.IFormInput;

public class ContractEditForm extends JPanel implements IFormInput<ContractDTO> {
    private JTextField txtMaHopDong;
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JTextField txtLoaiHopDong;
    private JTextField txtNgayKy;
    private JTextField txtNgayHetHan;
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

        // Ngày ký
        add(new JLabel("Ngày ký (dd/MM/yyyy):"));
        txtNgayKy = new JTextField();
        add(txtNgayKy);

        // Ngày hết hạn
        add(new JLabel("Ngày hết hạn (dd/MM/yyyy):"));
        txtNgayHetHan = new JTextField();
        add(txtNgayHetHan);

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
            
            if (contract.ngayLamHopDong != null) {
                txtNgayKy.setText(contract.ngayLamHopDong.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            if (contract.hanHopDong != null) {
                txtNgayHetHan.setText(contract.hanHopDong.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            if (contract.luongCoBan != null) {
                txtLuongCoBan.setText(contract.luongCoBan.toString());
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
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            contract.ngayLamHopDong = LocalDate.parse(txtNgayKy.getText(), formatter);
            contract.hanHopDong = LocalDate.parse(txtNgayHetHan.getText(), formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            contract.luongCoBan = new BigDecimal(txtLuongCoBan.getText().replaceAll("[^0-9]", ""));
        } catch (Exception e) {
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
        txtNgayKy.setText("");
        txtNgayHetHan.setText("");
        txtLuongCoBan.setText("");
        cbTrangThai.setSelectedIndex(0);
    }

    @Override
    public boolean validateForm() {
        if (txtLoaiHopDong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập loại hợp đồng!");
            return false;
        }
        if (txtNgayKy.getText().trim().isEmpty() || txtNgayHetHan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ ngày ký và ngày hết hạn!");
            return false;
        }
        if (txtLuongCoBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lương cơ bản!");
            return false;
        }
        return true;
    }
}
