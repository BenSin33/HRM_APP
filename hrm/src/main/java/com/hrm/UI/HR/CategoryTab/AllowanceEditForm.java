package com.hrm.UI.HR.CategoryTab;

import com.hrm.DTO.AllowanceDTO;
import com.hrm.UI.component.IFormInput;

import javax.swing.*;
import java.awt.*;

/**
 * Form chỉnh sửa phụ cấp
 */
public class AllowanceEditForm extends JPanel implements IFormInput<AllowanceDTO> {
    private JTextField txtMaPhucap;
    private JTextField txtTenPhucap;
    private JTextField txtSoTien;

    public AllowanceEditForm() {
        setLayout(new GridLayout(3, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Mã phụ cấp (không chỉnh sửa)
        add(new JLabel("Mã phụ cấp:"));
        txtMaPhucap = new JTextField();
        txtMaPhucap.setEditable(false);
        add(txtMaPhucap);

        // Tên phụ cấp
        add(new JLabel("Tên phụ cấp:"));
        txtTenPhucap = new JTextField();
        add(txtTenPhucap);

        // Số tiền mặc định
        add(new JLabel("Số tiền mặc định:"));
        txtSoTien = new JTextField();
        add(txtSoTien);
    }

    @Override
    public AllowanceDTO getFormData() {
        String tenPhucap = txtTenPhucap.getText().trim();
        
        AllowanceDTO dto = new AllowanceDTO();
        String maStr = txtMaPhucap.getText().trim();
        if (!maStr.isEmpty()) {
            dto.setMaPhucap(Integer.parseInt(maStr));
        }
        dto.setTenPhucap(tenPhucap);
        
        try {
            dto.setSoTienMacDinh(new java.math.BigDecimal(txtSoTien.getText().trim()));
        } catch (NumberFormatException e) {
            dto.setSoTienMacDinh(java.math.BigDecimal.ZERO);
        }
        
        return dto;
    }

    @Override
    public boolean validateForm() {
        String tenPhucap = txtTenPhucap.getText().trim();
        
        if (tenPhucap.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên phụ cấp!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    @Override
    public void clearForm() {
        txtMaPhucap.setText("");
        txtTenPhucap.setText("");
        txtSoTien.setText("0");
    }

    @Override
    public void setFormData(AllowanceDTO dto) {
        if (dto != null) {
            txtMaPhucap.setText(String.valueOf(dto.getMaPhucap()));
            txtTenPhucap.setText(dto.getTenPhucap());
            txtSoTien.setText(dto.getSoTienMacDinh().toPlainString());
        } else {
            clearForm();
        }
    }
}
