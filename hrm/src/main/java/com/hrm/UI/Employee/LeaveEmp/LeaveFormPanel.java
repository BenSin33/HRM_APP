package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.UI.component.IFormInput;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

public class LeaveFormPanel extends JPanel implements IFormInput<Map<String, Object>> {
    private JComboBox<String> cbLeaveType;
    private JTextArea taReason;
    private JSpinner spStartDate;
    private JSpinner spEndDate;

    public LeaveFormPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Loại nghỉ phép
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel lblType = new JLabel("Loại nghỉ phép:");
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(lblType, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        cbLeaveType = new JComboBox<>(new String[]{"Có lương", "Không lương"});
        cbLeaveType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbLeaveType.setPreferredSize(new Dimension(250, 35));
        add(cbLeaveType, gbc);

        // Ngày bắt đầu
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel lblStartDate = new JLabel("Ngày bắt đầu:");
        lblStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(lblStartDate, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        spStartDate = new JSpinner(new SpinnerDateModel());
        spStartDate.setEditor(new JSpinner.DateEditor(spStartDate, "dd/MM/yyyy"));
        spStartDate.setPreferredSize(new Dimension(250, 35));
        add(spStartDate, gbc);

        // Ngày làm lại
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel lblEndDate = new JLabel("Ngày làm lại:");
        lblEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(lblEndDate, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        spEndDate = new JSpinner(new SpinnerDateModel());
        spEndDate.setEditor(new JSpinner.DateEditor(spEndDate, "dd/MM/yyyy"));
        spEndDate.setPreferredSize(new Dimension(250, 35));
        add(spEndDate, gbc);

        // Lý do nghỉ
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel lblReason = new JLabel("Lý do nghỉ:");
        lblReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(lblReason, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        taReason = new JTextArea(4, 20);
        taReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taReason.setLineWrap(true);
        taReason.setWrapStyleWord(true);
        add(new JScrollPane(taReason), gbc);
    }

    @Override
    public Map<String, Object> getFormData() {
        Map<String, Object> data = new HashMap<>();
        data.put("loainghi", cbLeaveType.getSelectedItem());
        data.put("lydonghi", taReason.getText().trim());

        java.util.Date startUtil = (java.util.Date) spStartDate.getValue();
        java.util.Date endUtil = (java.util.Date) spEndDate.getValue();
        data.put("ngaynghi", startUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        data.put("ngaylamlai", endUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return data;
    }

    @Override
    public boolean validateForm() {
        if (taReason.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do nghỉ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        java.util.Date startUtil = (java.util.Date) spStartDate.getValue();
        java.util.Date endUtil = (java.util.Date) spEndDate.getValue();
        LocalDate startDate = startUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (startDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu nghỉ không được ở quá khứ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public void clearForm() {
        cbLeaveType.setSelectedIndex(0);
        taReason.setText("");
        spStartDate.setValue(java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        spEndDate.setValue(java.util.Date.from(LocalDate.now().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    @Override
    public void setFormData(Map<String, Object> data) {
        String loainghi = (String) data.get("loainghi");
        if (loainghi != null) cbLeaveType.setSelectedItem(loainghi);

        java.sql.Date dbNgaynghi = (java.sql.Date) data.get("ngaynghi");
        if (dbNgaynghi != null) spStartDate.setValue(new java.util.Date(dbNgaynghi.getTime()));

        java.sql.Date dbNgaylamlai = (java.sql.Date) data.get("ngaylamlai");
        if (dbNgaylamlai != null) spEndDate.setValue(new java.util.Date(dbNgaylamlai.getTime()));

        String lydonghi = (String) data.get("lydonghi");
        if (lydonghi != null) taReason.setText(lydonghi);
    }
}
