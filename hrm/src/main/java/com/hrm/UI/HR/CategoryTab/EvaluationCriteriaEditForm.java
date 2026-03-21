package com.hrm.UI.HR.CategoryTab;

import com.hrm.DTO.EvaluationCriteriaDTO;
import com.hrm.UI.component.IFormInput;

import javax.swing.*;
import java.awt.*;

/**
 * Form chỉnh sửa tiêu chí đánh giá
 */
public class EvaluationCriteriaEditForm extends JPanel implements IFormInput<EvaluationCriteriaDTO> {
    private final JTextField txtMaTieuChi;
    private final JTextField txtTenTieuChi;
    private final JTextField txtDiem;

    public EvaluationCriteriaEditForm() {
        setLayout(new GridLayout(3, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Mã tiêu chí:"));
        txtMaTieuChi = new JTextField();
        txtMaTieuChi.setEditable(false);
        add(txtMaTieuChi);

        add(new JLabel("Tên tiêu chí:"));
        txtTenTieuChi = new JTextField();
        add(txtTenTieuChi);

        add(new JLabel("Điểm:"));
        txtDiem = new JTextField();
        add(txtDiem);
    }

    @Override
    public EvaluationCriteriaDTO getFormData() {
        EvaluationCriteriaDTO dto = new EvaluationCriteriaDTO();
        dto.setMaTieuChi(txtMaTieuChi.getText().trim());
        dto.setTenTieuChi(txtTenTieuChi.getText().trim());
        try {
            dto.setDiem(Integer.parseInt(txtDiem.getText().trim()));
        } catch (NumberFormatException e) {
            dto.setDiem(0);
        }
        return dto;
    }

    @Override
    public boolean validateForm() {
        if (txtTenTieuChi.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên tiêu chí!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int diem = Integer.parseInt(txtDiem.getText().trim());
            if (diem < 0) {
                JOptionPane.showMessageDialog(this,
                        "Điểm không được âm!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Điểm phải là số nguyên hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void clearForm() {
        txtMaTieuChi.setText("");
        txtTenTieuChi.setText("");
        txtDiem.setText("0");
    }

    @Override
    public void setFormData(EvaluationCriteriaDTO dto) {
        if (dto != null) {
            txtMaTieuChi.setText(dto.getMaTieuChi());
            txtTenTieuChi.setText(dto.getTenTieuChi());
            txtDiem.setText(String.valueOf(dto.getDiem()));
        } else {
            clearForm();
        }
    }

    public void setMaTieuChi(String maTieuChi) {
        txtMaTieuChi.setText(maTieuChi);
    }
}
