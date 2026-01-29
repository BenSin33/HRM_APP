package com.hrm.UI.component;

import javax.swing.*;
import java.awt.*;

public class CRUDDialog<T> extends JDialog {
    private IFormInput<T> formInput;
    private JButton btnSave = new JButton("Xác nhận");
    private boolean confirmed = false;

    public CRUDDialog(JFrame parent, String title, JPanel formPanel, T data) {
        super(parent, title, true);
        this.formInput = (IFormInput<T>) formPanel;

        if (data != null) {
            formInput.setFormData(data); // Chế độ Sửa
            btnSave.setText("Cập nhật");
        } else {
            formInput.clearForm();       // Chế độ Thêm
            btnSave.setText("Lưu mới");
        }

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        
        JPanel pnlBtns = new JPanel();
        btnSave.setBackground(new Color(102, 0, 204)); // Màu tím đặc trưng
        btnSave.setForeground(Color.WHITE);
        pnlBtns.add(btnSave);
        add(pnlBtns, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            if (formInput.validateForm()) {
                confirmed = true;
                dispose();
            }
        });
        pack();
        setLocationRelativeTo(parent);
    }

    public T getResult() { return confirmed ? formInput.getFormData() : null; }
}