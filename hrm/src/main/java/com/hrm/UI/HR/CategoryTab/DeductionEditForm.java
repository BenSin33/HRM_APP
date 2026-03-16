package com.hrm.UI.HR.CategoryTab;

import com.hrm.DTO.DeductionDTO;
import com.hrm.UI.component.IFormInput;

import javax.swing.*;
import java.awt.*;

/**
 * Form chỉnh sửa khấu trừ
 */
public class DeductionEditForm extends JPanel implements IFormInput<DeductionDTO> {
    private JTextField txtMaKhauTru;
    private JTextField txtTenKhauTru;
    private JTextField txtSoTien;

    public DeductionEditForm() {
        setLayout(new GridLayout(3, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Mã khấu trừ (không chỉnh sửa)
        add(new JLabel("Mã khấu trừ:"));
        txtMaKhauTru = new JTextField();
        txtMaKhauTru.setEditable(false);
        add(txtMaKhauTru);

        // Tên khấu trừ
        add(new JLabel("Tên khấu trừ:"));
        txtTenKhauTru = new JTextField();
        add(txtTenKhauTru);

        // Số tiền mặc định
        add(new JLabel("Số tiền mặc định:"));
        txtSoTien = new JTextField();
        add(txtSoTien);
    }

    @Override
    public DeductionDTO getFormData() {
        String tenKhauTru = txtTenKhauTru.getText().trim();
        
        DeductionDTO dto = new DeductionDTO();
        String maStr = txtMaKhauTru.getText().trim();
        if (!maStr.isEmpty()) {
            dto.setMaKhauTru(Integer.parseInt(maStr));
        }
        dto.setTenKhauTru(tenKhauTru);
        
        try {
            dto.setSoTienMacDinh(new java.math.BigDecimal(txtSoTien.getText().trim()));
        } catch (NumberFormatException e) {
            dto.setSoTienMacDinh(java.math.BigDecimal.ZERO);
        }
        
        return dto;
    }

    @Override
    public boolean validateForm() {
        String tenKhauTru = txtTenKhauTru.getText().trim();
        
        if (tenKhauTru.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên khấu trừ!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    @Override
    public void clearForm() {
        txtMaKhauTru.setText("");
        txtTenKhauTru.setText("");
        txtSoTien.setText("0");
    }

    @Override
    public void setFormData(DeductionDTO dto) {
        if (dto != null) {
            txtMaKhauTru.setText(String.valueOf(dto.getMaKhauTru()));
            txtTenKhauTru.setText(dto.getTenKhauTru());
            txtSoTien.setText(dto.getSoTienMacDinh().toPlainString());
        } else {
            clearForm();
        }
    }
}
