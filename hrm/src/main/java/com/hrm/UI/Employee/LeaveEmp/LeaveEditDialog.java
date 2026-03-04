package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import com.hrm.DAO.Employee.LeaveDAO;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class LeaveEditDialog extends JDialog {
    private JComboBox<String> cmbLeaveType;
    private JSpinner spnStartDate;
    private JSpinner spnEndDate;
    private JTextArea taReason;
    private boolean isSubmitted = false;
    private Map<String, Object> leaveData;

    public LeaveEditDialog(Window parentWindow, Map<String, Object> leaveData) {
        super(JOptionPane.getFrameForComponent((Component) parentWindow), "Chỉnh sửa đơn nghỉ phép", ModalityType.APPLICATION_MODAL);
        
        this.leaveData = leaveData;
        initComponents();
        setLocationRelativeTo(parentWindow);
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setResizable(false);

        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBorder(new EmptyBorder(10, 10, 10, 10));
        pnlContent.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Loại nghỉ phép
        JLabel lblLeaveType = new JLabel("Loại nghỉ phép:");
        lblLeaveType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy = 0;
        pnlContent.add(lblLeaveType, gbc);

        cmbLeaveType = new JComboBox<>(new String[]{"Có lương", "Không lương"});
        cmbLeaveType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbLeaveType.setPreferredSize(new Dimension(0, 35));
        gbc.gridy = 1;
        pnlContent.add(cmbLeaveType, gbc);
        gbc.gridy++;

        // Ngày bắt đầu
        JLabel lblStartDate = new JLabel("Ngày bắt đầu:");
        lblStartDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy++;
        pnlContent.add(lblStartDate, gbc);

        spnStartDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorStart = new JSpinner.DateEditor(spnStartDate, "dd/MM/yyyy");
        spnStartDate.setEditor(editorStart);
        spnStartDate.setPreferredSize(new Dimension(0, 35));
        gbc.gridy++;
        pnlContent.add(spnStartDate, gbc);
        gbc.gridy++;

        // Ngày kết thúc
        JLabel lblEndDate = new JLabel("Ngày kết thúc:");
        lblEndDate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy++;
        pnlContent.add(lblEndDate, gbc);

        spnEndDate = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorEnd = new JSpinner.DateEditor(spnEndDate, "dd/MM/yyyy");
        spnEndDate.setEditor(editorEnd);
        spnEndDate.setPreferredSize(new Dimension(0, 35));
        gbc.gridy++;
        pnlContent.add(spnEndDate, gbc);
        gbc.gridy++;

        // Lý do
        JLabel lblReason = new JLabel("Lý do:");
        lblReason.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gbc.gridy++;
        pnlContent.add(lblReason, gbc);

        taReason = new JTextArea(4, 20);
        taReason.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taReason.setLineWrap(true);
        taReason.setWrapStyleWord(true);
        taReason.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(taReason);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        gbc.gridy++;
        pnlContent.add(scrollPane, gbc);
        gbc.gridy++;

        // Nút Cập nhật & Hủy
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdate.setPreferredSize(new Dimension(100, 40));
        btnUpdate.setBackground(new Color(34, 197, 94));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.addActionListener(e -> updateLeaveRequest());
        pnlButtons.add(btnUpdate);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dispose());
        pnlButtons.add(btnCancel);

        gbc.gridy++;
        pnlContent.add(pnlButtons, gbc);

        getContentPane().add(pnlContent);
        
        // Pre-populate data
        populateData();
    }

    private void populateData() {
        String loainghi = (String) leaveData.get("loainghi");
        if (loainghi != null) {
            cmbLeaveType.setSelectedItem(loainghi);
        }

        java.sql.Date dbNgaynghi = (java.sql.Date) leaveData.get("ngaynghi");
        if (dbNgaynghi != null) {
            spnStartDate.setValue(new java.util.Date(dbNgaynghi.getTime()));
        } else {
            spnStartDate.setValue(new java.util.Date());
        }

        java.sql.Date dbNgaylamlai = (java.sql.Date) leaveData.get("ngaylamlai");
        if (dbNgaylamlai != null) {
            spnEndDate.setValue(new java.util.Date(dbNgaylamlai.getTime()));
        } else {
            spnEndDate.setValue(new java.util.Date());
        }

        String lydonghi = (String) leaveData.get("lydonghi");
        if (lydonghi != null) {
            taReason.setText(lydonghi);
        }
    }

    private void updateLeaveRequest() {
        String selectedType = (String) cmbLeaveType.getSelectedItem();
        java.util.Date startDate = (java.util.Date) spnStartDate.getValue();
        java.util.Date endDate = (java.util.Date) spnEndDate.getValue();
        String reason = taReason.getText().trim();

        // Validation
        if (reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        java.time.LocalDate startLocal = new java.sql.Date(startDate.getTime()).toLocalDate();
        java.time.LocalDate endLocal = new java.sql.Date(endDate.getTime()).toLocalDate();

        if (startLocal.isAfter(endLocal)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được sau ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update trong database
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("manghiphep", leaveData.get("manghiphep"));
        updateData.put("loainghi", selectedType);
        updateData.put("lydonghi", reason);
        updateData.put("ngaynghi", startLocal);
        updateData.put("ngaylamlai", endLocal);

        LeaveDAO dao = new LeaveDAO();
        if (dao.updateLeaveRequest(updateData)) {
            JOptionPane.showMessageDialog(this, "Cập nhật đơn nghỉ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            isSubmitted = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật đơn nghỉ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }
}
