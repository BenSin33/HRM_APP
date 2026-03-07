package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.DAO.Employee.LeaveDAO;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class LeaveEditDialog extends JDialog {
    private JComboBox<String> cbLeaveType;
    private JTextArea taReason;
    private JSpinner spStartDate;
    private JSpinner spEndDate;
    private boolean submitted = false;
    private Map<String, Object> leaveData;
    
    public LeaveEditDialog(Window owner, Map<String, Object> leaveData) {
        super(owner, "CHỈNH SỬA ĐƠN NGHỈ PHÉP", ModalityType.APPLICATION_MODAL);
        this.leaveData = leaveData;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(owner);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(248, 249, 250));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Loại nghỉ phép
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblType = new JLabel("Loại nghỉ phép:");
        lblType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblType, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cbLeaveType = new JComboBox<>(new String[]{"Có lương", "Không lương"});
        cbLeaveType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cbLeaveType.setPreferredSize(new Dimension(0, 35));
        formPanel.add(cbLeaveType, gbc);
        
        // Ngày bắt đầu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblStartDate = new JLabel("Ngày bắt đầu:");
        lblStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblStartDate, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        spStartDate = new JSpinner(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor deStart = new JSpinner.DateEditor(spStartDate, "dd/MM/yyyy");
        spStartDate.setEditor(deStart);
        spStartDate.setPreferredSize(new Dimension(0, 35));
        formPanel.add(spStartDate, gbc);
        
        // Ngày kết thúc
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblEndDate = new JLabel("Ngày làm lại:");
        lblEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblEndDate, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        spEndDate = new JSpinner(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor deEnd = new JSpinner.DateEditor(spEndDate, "dd/MM/yyyy");
        spEndDate.setEditor(deEnd);
        spEndDate.setPreferredSize(new Dimension(0, 35));
        formPanel.add(spEndDate, gbc);
        
        // Lý do nghỉ
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel lblReason = new JLabel("Lý do nghỉ:");
        lblReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(lblReason, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        taReason = new JTextArea(4, 20);
        taReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taReason.setLineWrap(true);
        taReason.setWrapStyleWord(true);
        JScrollPane spReason = new JScrollPane(taReason);
        formPanel.add(spReason, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnUpdate.setPreferredSize(new Dimension(100, 35));
        btnUpdate.setBackground(new Color(59, 130, 246));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateLeaveRequest());
        
        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.addActionListener(e -> dispose());
        
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        
        // Populate data
        populateData();
    }
    
    private void populateData() {
        String loainghi = (String) leaveData.get("loainghi");
        if (loainghi != null) {
            cbLeaveType.setSelectedItem(loainghi);
        }

        java.sql.Date dbNgaynghi = (java.sql.Date) leaveData.get("ngaynghi");
        if (dbNgaynghi != null) {
            spStartDate.setValue(new java.util.Date(dbNgaynghi.getTime()));
        } else {
            spStartDate.setValue(new java.util.Date());
        }

        java.sql.Date dbNgaylamlai = (java.sql.Date) leaveData.get("ngaylamlai");
        if (dbNgaylamlai != null) {
            spEndDate.setValue(new java.util.Date(dbNgaylamlai.getTime()));
        } else {
            spEndDate.setValue(new java.util.Date());
        }

        String lydonghi = (String) leaveData.get("lydonghi");
        if (lydonghi != null) {
            taReason.setText(lydonghi);
        }
    }

    private void updateLeaveRequest() {
        String loaiNghi = (String) cbLeaveType.getSelectedItem();
        String lyDo = taReason.getText().trim();
        
        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do nghỉ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Chuyển đổi date từ spinner
        java.util.Date startDateUtil = (java.util.Date) spStartDate.getValue();
        java.util.Date endDateUtil = (java.util.Date) spEndDate.getValue();
        LocalDate startDate = startDateUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endDateUtil.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        // Kiểm tra ngày bắt đầu không được sau ngày kết thúc
        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update trong database
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("manghiphep", leaveData.get("manghiphep"));
        updateData.put("loainghi", loaiNghi);
        updateData.put("lydonghi", lyDo);
        updateData.put("ngaynghi", startDate);
        updateData.put("ngaylamlai", endDate);

        LeaveDAO dao = new LeaveDAO();
        if (dao.updateLeaveRequest(updateData)) {
            JOptionPane.showMessageDialog(this, "Cập nhật đơn nghỉ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            submitted = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật đơn nghỉ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
