package com.hrm.UI.HR.SalaryTab;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.UI.component.IFormInput;
import com.hrm.DAO.AllowanceDAO;
import com.hrm.DAO.DeductionDAO;

public class SalaryEditForm extends JPanel implements IFormInput<SalaryDTO> {
    private JTextField txtMaLuong;  // Hidden field for maLuong
    private JTextField txtMaNV;
    private JTextField txtHoTen;
    private JTextField txtPhongBan;
    private JSpinner spThang;
    private JSpinner spNam;
    private JTextField txtLuongCoBan;
    private JTextField txtHesoTrinhDo;
    private JTextField txtSoNgayCong;
    private JTextField txtSoNgayCongThucTe; // Số ngày công thực tế (22 ngày)
    private JTextField txtTongPhucap;
    private JTextField txtTongKhauTru;
    private JTextField txtThucLinh;
    private JComboBox<String> cbTrangThai;
    private JComboBox<String> cbTinhTrangThanToan;

    public SalaryEditForm() {
        setLayout(new GridLayout(14, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Mã lương (ẩn)
        txtMaLuong = new JTextField();
        txtMaLuong.setVisible(false);

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

        // Hệ số trình độ
        add(new JLabel("Hệ số trình độ:"));
        txtHesoTrinhDo = new JTextField();
        txtHesoTrinhDo.setEditable(false);
        add(txtHesoTrinhDo);

        // Số ngày công (lấy từ chấm công)
        add(new JLabel("Số ngày công:"));
        txtSoNgayCong = new JTextField();
        add(txtSoNgayCong);

        // Số ngày công thực tế (mặc định 22 ngày)
        add(new JLabel("Số ngày công TT (mặc định 22):"));
        txtSoNgayCongThucTe = new JTextField("22");
        add(txtSoNgayCongThucTe);

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
        cbTrangThai = new JComboBox<>(new String[]{"Chưa khóa", "Đã khóa"});
        add(cbTrangThai);

        // Tình trạng thanh toán
        add(new JLabel("Tình trạng thanh toán:"));
        cbTinhTrangThanToan = new JComboBox<>(new String[]{"Chưa thanh toán", "Đã thanh toán"});
        add(cbTinhTrangThanToan);
    }

    @Override
    public void setFormData(SalaryDTO salary) {
        if (salary != null) {
            txtMaLuong.setText(salary.maLuong != null ? salary.maLuong : "");
            txtMaNV.setText(salary.maNV);
            txtHoTen.setText(salary.hoTen);
            txtPhongBan.setText(salary.phongBan);
            spThang.setValue(salary.thang);
            spNam.setValue(salary.nam);
            txtLuongCoBan.setText(salary.luongCoBan != null ? salary.luongCoBan.toString() : "");
            txtHesoTrinhDo.setText(salary.hesotrinhdo != null ? salary.hesotrinhdo.toString() : "1.00");
            txtSoNgayCong.setText(salary.soNgayCong > 0 ? String.valueOf(salary.soNgayCong) : "");
            txtSoNgayCongThucTe.setText("22"); // Mặc định 22 ngày công thực tế
            
            // Tự động lấy tổng phụ cấp và khấu trừ từ database
            try {
                AllowanceDAO allowanceDAO = new AllowanceDAO();
                BigDecimal totalAllowance = allowanceDAO.getTotalAllowances();
                txtTongPhucap.setText(totalAllowance.toString());
                
                DeductionDAO deductionDAO = new DeductionDAO();
                BigDecimal totalDeduction = deductionDAO.getTotalDeductions();
                txtTongKhauTru.setText(totalDeduction.toString());
            } catch (Exception e) {
                e.printStackTrace();
                // Nếu lỗi, sử dụng giá trị từ salary object
                txtTongPhucap.setText(salary.tongPhucap != null ? salary.tongPhucap.toString() : "");
                txtTongKhauTru.setText(salary.tongKhauTru != null ? salary.tongKhauTru.toString() : "");
            }
            
            txtThucLinh.setText(salary.thucLinh != null ? salary.thucLinh.toString() : "");
            cbTrangThai.setSelectedItem(salary.trangThai);
            cbTinhTrangThanToan.setSelectedItem(salary.tinhTrangThanToan != null ? salary.tinhTrangThanToan : "Chưa thanh toán");
        }
    }

    @Override
    public SalaryDTO getFormData() {
        SalaryDTO salary = new SalaryDTO();
        
        // Lấy maLuong từ hidden field
        salary.maLuong = txtMaLuong.getText();
        
        salary.maNV = txtMaNV.getText();
        salary.hoTen = txtHoTen.getText();
        salary.phongBan = txtPhongBan.getText();
        salary.thang = (Integer) spThang.getValue();
        salary.nam = (Integer) spNam.getValue();
        
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

        // Số ngày công thực tế
        float soNgayCongThucTe = 22; // Mặc định
        try {
            String soNgayStr = txtSoNgayCongThucTe.getText().trim();
            if (!soNgayStr.isEmpty()) {
                soNgayCongThucTe = Float.parseFloat(soNgayStr);
            }
        } catch (Exception e) {
            soNgayCongThucTe = 22;
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
        
        // Công thức tính thực lĩnh:
        // (lương cơ bản * hệ số trình độ * (số ngày công / số ngày công thực tế)) + tổng phụ cấp - tổng khấu trừ
        BigDecimal heSoTrinhDo = salary.hesotrinhdo != null ? salary.hesotrinhdo : new BigDecimal("1.00");
        BigDecimal ngayCongRatio = new BigDecimal(salary.soNgayCong).divide(new BigDecimal(soNgayCongThucTe), 4, java.math.RoundingMode.HALF_UP);
        
        salary.thucLinh = salary.luongCoBan
                            .multiply(heSoTrinhDo)
                            .multiply(ngayCongRatio)
                            .add(salary.tongPhucap)
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
        spThang.setValue(1);
        spNam.setValue(2024);
        txtLuongCoBan.setText("");
        txtHesoTrinhDo.setText("");
        txtSoNgayCong.setText("");
        txtSoNgayCongThucTe.setText("22");
        txtTongPhucap.setText("");
        txtTongKhauTru.setText("");
        txtThucLinh.setText("");
        cbTrangThai.setSelectedIndex(0);
        cbTinhTrangThanToan.setSelectedIndex(0);
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
