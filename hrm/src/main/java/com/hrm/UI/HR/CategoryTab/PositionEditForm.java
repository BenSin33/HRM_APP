package com.hrm.UI.HR.CategoryTab;

import com.hrm.DTO.PositionDTO;
import com.hrm.UI.component.IFormInput;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Form chỉnh sửa chức vụ
 */
public class PositionEditForm extends JPanel implements IFormInput<PositionDTO> {
    private final JTextField txtMaChucVu;
    private final JTextField txtTenViTri;
    private final JTextField txtPhuCap;

    public PositionEditForm() {
        setLayout(new GridLayout(3, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Mã chức vụ:"));
        txtMaChucVu = new JTextField();
        txtMaChucVu.setEditable(false);
        add(txtMaChucVu);

        add(new JLabel("Tên vị trí:"));
        txtTenViTri = new JTextField();
        add(txtTenViTri);

        add(new JLabel("Phụ cấp chức vụ:"));
        txtPhuCap = new JTextField();
        add(txtPhuCap);
    }

    @Override
    public PositionDTO getFormData() {
        PositionDTO dto = new PositionDTO();
        dto.setMaChucVu(txtMaChucVu.getText().trim());
        dto.setTenViTri(txtTenViTri.getText().trim());
        try {
            dto.setPhuCapChucVu(new BigDecimal(txtPhuCap.getText().trim()));
        } catch (NumberFormatException e) {
            dto.setPhuCapChucVu(BigDecimal.ZERO);
        }
        return dto;
    }

    @Override
    public boolean validateForm() {
        String tenViTri = txtTenViTri.getText().trim();
        if (tenViTri.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên vị trí!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String phuCapText = txtPhuCap.getText().trim();
        if (phuCapText.isEmpty()) {
            txtPhuCap.setText("0");
            return true;
        }

        try {
            BigDecimal money = new BigDecimal(phuCapText);
            if (money.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Phụ cấp chức vụ không được âm!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Phụ cấp chức vụ phải là số hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void clearForm() {
        txtMaChucVu.setText("");
        txtTenViTri.setText("");
        txtPhuCap.setText("0");
    }

    @Override
    public void setFormData(PositionDTO dto) {
        if (dto != null) {
            txtMaChucVu.setText(dto.getMaChucVu());
            txtTenViTri.setText(dto.getTenViTri());
            txtPhuCap.setText(dto.getPhuCapChucVu() != null ? dto.getPhuCapChucVu().toPlainString() : "0");
        } else {
            clearForm();
        }
    }

    public void setMaChucVu(String maChucVu) {
        txtMaChucVu.setText(maChucVu);
    }
}
