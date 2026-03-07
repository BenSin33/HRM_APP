package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.hrm.DTO.ContractDTO;
import com.hrm.UI.component.IFormInput;
import com.hrm.utils.JDBCConection;

public class ContractAddForm extends JPanel implements IFormInput<ContractDTO> {
    private JTextField txtMaHopDong;
    private JComboBox<String> cbMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JTextField txtLoaiHopDong;
    private JTextField txtNgayKy;
    private JTextField txtNgayHetHan;
    private JTextField txtLuongCoBan;
    private List<String> employeeIds;
    private java.util.Map<String, String> employeeMap;

    public ContractAddForm() {
        employeeIds = new ArrayList<>();
        employeeMap = new java.util.HashMap<>();
        setLayout(new GridLayout(9, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Mã hợp đồng
        add(new JLabel("Mã hợp đồng:"));
        txtMaHopDong = new JTextField();
        add(txtMaHopDong);

        // Mã nhân viên (Combobox)
        add(new JLabel("Mã nhân viên:"));
        cbMaNV = new JComboBox<>();
        loadEmployees();
        cbMaNV.addActionListener(e -> updateEmployeeInfo());
        add(cbMaNV);

        // Họ tên (Read-only)
        add(new JLabel("Họ tên:"));
        txtHoTen = new JTextField();
        txtHoTen.setEditable(false);
        add(txtHoTen);

        // Phòng ban (Read-only)
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
        txtNgayKy.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        add(txtNgayKy);

        // Ngày hết hạn
        add(new JLabel("Ngày hết hạn (dd/MM/yyyy):"));
        txtNgayHetHan = new JTextField();
        add(txtNgayHetHan);

        // Lương cơ bản
        add(new JLabel("Lương cơ bản:"));
        txtLuongCoBan = new JTextField();
        add(txtLuongCoBan);
    }

    private void loadEmployees() {
        String sql = "SELECT MANV, HOTEN, TENPHONGBAN FROM nhanvien nv " +
                     "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                     "WHERE nv.MANV NOT IN (SELECT MANV FROM hopdong) " +
                     "ORDER BY nv.HOTEN ASC";
        
        try (Connection conn = JDBCConection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String maNV = rs.getString("MANV");
                String hoTen = rs.getString("HOTEN");
                String phongBan = rs.getString("TENPHONGBAN");
                
                employeeIds.add(maNV);
                String displayText = maNV + " - " + hoTen + " (" + phongBan + ")";
                employeeMap.put(displayText, maNV);
                cbMaNV.addItem(displayText);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateEmployeeInfo() {
        String selected = (String) cbMaNV.getSelectedItem();
        if (selected != null && !selected.isEmpty()) {
            String maNV = employeeMap.get(selected);
            if (maNV != null) {
                // Fetch employee info
                String sql = "SELECT HOTEN, TENPHONGBAN FROM nhanvien nv " +
                             "JOIN phongban pb ON nv.MAPHONGBAN = pb.MAPHONGBAN " +
                             "WHERE nv.MANV = ?";
                
                try (Connection conn = JDBCConection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, maNV);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            txtHoTen.setText(rs.getString("HOTEN"));
                            txtPhongBan.setText(rs.getString("TENPHONGBAN"));
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setFormData(ContractDTO contract) {
        // Not used for add form
    }

    @Override
    public ContractDTO getFormData() {
        ContractDTO contract = new ContractDTO();
        contract.maHopDong = txtMaHopDong.getText();
        
        String selected = (String) cbMaNV.getSelectedItem();
        if (selected != null) {
            contract.maNV = employeeMap.get(selected);
        }
        
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
        
        return contract;
    }

    @Override
    public void clearForm() {
        txtMaHopDong.setText("");
        cbMaNV.setSelectedIndex(0);
        txtHoTen.setText("");
        txtPhongBan.setText("");
        txtLoaiHopDong.setText("");
        txtNgayKy.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtNgayHetHan.setText("");
        txtLuongCoBan.setText("");
    }

    @Override
    public boolean validateForm() {
        if (txtMaHopDong.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hợp đồng!");
            return false;
        }
        if (cbMaNV.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!");
            return false;
        }
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
