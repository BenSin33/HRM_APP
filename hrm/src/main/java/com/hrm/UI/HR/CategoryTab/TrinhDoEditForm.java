package com.hrm.UI.HR.CategoryTab;

import com.hrm.DTO.TrinhDoDTO;
import com.hrm.UI.component.IFormInput;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Form chỉnh sửa trình độ
 */
public class TrinhDoEditForm extends JPanel implements IFormInput<TrinhDoDTO> {
    private final JTextField txtMaTrinhDo;
    private final JTextField txtTrinhDo;
    private final JTextField txtHeSo;

    public TrinhDoEditForm() {
        setLayout(new GridLayout(3, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Mã trình độ:"));
        txtMaTrinhDo = new JTextField();
        txtMaTrinhDo.setEditable(false);
        add(txtMaTrinhDo);

        add(new JLabel("Tên trình độ:"));
        txtTrinhDo = new JTextField();
        add(txtTrinhDo);

        add(new JLabel("Hệ số trình độ:"));
        txtHeSo = new JTextField();
        add(txtHeSo);
    }

    @Override
    public TrinhDoDTO getFormData() {
        TrinhDoDTO dto = new TrinhDoDTO();
        dto.setMaTrinhDo(txtMaTrinhDo.getText().trim());
        dto.setTrinhDo(txtTrinhDo.getText().trim());
        try {
            dto.setHeSoTrinhDo(new BigDecimal(txtHeSo.getText().trim()));
        } catch (NumberFormatException e) {
            dto.setHeSoTrinhDo(BigDecimal.ZERO);
        }
        return dto;
    }

    @Override
    public boolean validateForm() {
        if (txtTrinhDo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên trình độ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            BigDecimal hs = new BigDecimal(txtHeSo.getText().trim());
            if (hs.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this,
                        "Hệ số trình độ không được âm!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Hệ số trình độ phải là số hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    @Override
    public void clearForm() {
        txtMaTrinhDo.setText("");
        txtTrinhDo.setText("");
        txtHeSo.setText("1.00");
    }

    @Override
    public void setFormData(TrinhDoDTO dto) {
        if (dto != null) {
            txtMaTrinhDo.setText(dto.getMaTrinhDo());
            txtTrinhDo.setText(dto.getTrinhDo());
            txtHeSo.setText(dto.getHeSoTrinhDo() != null ? dto.getHeSoTrinhDo().toPlainString() : "1.00");
        } else {
            clearForm();
        }
    }

    public void setMaTrinhDo(String maTrinhDo) {
        txtMaTrinhDo.setText(maTrinhDo);
    }
}
