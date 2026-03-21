package com.hrm.UI.HR.CategoryTab;

import com.hrm.DTO.DepartmentCategoryDTO;
import com.hrm.UI.component.IFormInput;

import javax.swing.*;
import java.awt.*;

/**
 * Form chỉnh sửa phòng ban
 */
public class DepartmentEditForm extends JPanel implements IFormInput<DepartmentCategoryDTO> {
    private final JTextField txtMaPhongBan;
    private final JTextField txtTenPhongBan;

    public DepartmentEditForm() {
        setLayout(new GridLayout(2, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Mã phòng ban:"));
        txtMaPhongBan = new JTextField();
        txtMaPhongBan.setEditable(false);
        add(txtMaPhongBan);

        add(new JLabel("Tên phòng ban:"));
        txtTenPhongBan = new JTextField();
        add(txtTenPhongBan);
    }

    @Override
    public DepartmentCategoryDTO getFormData() {
        DepartmentCategoryDTO dto = new DepartmentCategoryDTO();
        dto.setMaPhongBan(txtMaPhongBan.getText().trim());
        dto.setTenPhongBan(txtTenPhongBan.getText().trim());
        return dto;
    }

    @Override
    public boolean validateForm() {
        if (txtTenPhongBan.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên phòng ban!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public void clearForm() {
        txtMaPhongBan.setText("");
        txtTenPhongBan.setText("");
    }

    @Override
    public void setFormData(DepartmentCategoryDTO dto) {
        if (dto != null) {
            txtMaPhongBan.setText(dto.getMaPhongBan());
            txtTenPhongBan.setText(dto.getTenPhongBan());
        } else {
            clearForm();
        }
    }

    public void setMaPhongBan(String maPhongBan) {
        txtMaPhongBan.setText(maPhongBan);
    }
}
