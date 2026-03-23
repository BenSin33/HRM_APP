package com.hrm.UI.HR.PermissionTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.PositionDAO;
import com.hrm.DTO.PositionDTO;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Dialog thêm role mới kèm chức vụ mới.
 * Cho phép nhập tên role, mã role, và tùy chọn thêm chức vụ mới.
 */
public class AddRoleDialog extends JDialog {

    private JTextField tfRoleName;
    private JTextField tfRoleId;
    private JComboBox<String> cbPosition;
    private JTextField tfNewPositionName;
    private JTextField tfNewPositionId;
    private JCheckBox cbAddNewPosition;
    private JPanel newPositionPanel;
    private boolean confirmed;

    private final PositionDAO positionDAO = new PositionDAO();
    private final List<PositionDTO> positions;

    public AddRoleDialog(Frame parent, String suggestedRoleId) {
        super(parent, "Thêm cấu hình quyền role & chức vụ", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        positions = positionDAO.getAllPositions();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // === Role section ===
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblRoleSection = new JLabel("═══ Cấu hình Role ═══");
        lblRoleSection.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblRoleSection, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Mã role:"), gbc);
        gbc.gridx = 1;
        tfRoleId = new JTextField(suggestedRoleId, 15);
        tfRoleId.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        tfRoleId.setToolTipText("Để trống để tự động sinh (ví dụ: R4)");
        formPanel.add(tfRoleId, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tên role:"), gbc);
        gbc.gridx = 1;
        tfRoleName = new JTextField(20);
        tfRoleName.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        tfRoleName.setToolTipText("Ví dụ: Supervisor, Guest");
        formPanel.add(tfRoleName, gbc);

        // === Position section ===
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JLabel lblPosSection = new JLabel("═══ Chức vụ liên kết ═══");
        lblPosSection.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPosSection.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        formPanel.add(lblPosSection, gbc);

        // Checkbox thêm chức vụ mới
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        cbAddNewPosition = new JCheckBox("Thêm chức vụ mới");
        cbAddNewPosition.setBackground(Color.WHITE);
        cbAddNewPosition.addActionListener(e -> {
            newPositionPanel.setVisible(cbAddNewPosition.isSelected());
            cbPosition.setEnabled(!cbAddNewPosition.isSelected());
            pack();
            setLocationRelativeTo(parent);
        });
        formPanel.add(cbAddNewPosition, gbc);

        // Combo box chọn chức vụ có sẵn
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Chọn chức vụ:"), gbc);
        gbc.gridx = 1;
        cbPosition = new JComboBox<>();
        cbPosition.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        for (PositionDTO p : positions) {
            cbPosition.addItem(p.getMaChucVu() + " - " + p.getTenViTri());
        }
        if (cbPosition.getItemCount() == 0) {
            cbPosition.addItem("Không có chức vụ");
        }
        formPanel.add(cbPosition, gbc);

        // Panel thêm chức vụ mới (ẩn ban đầu)
        newPositionPanel = new JPanel(new GridBagLayout());
        newPositionPanel.setBackground(new Color(250, 250, 255));
        newPositionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        newPositionPanel.setVisible(false);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.insets = new Insets(3, 3, 3, 3);
        gbc2.anchor = GridBagConstraints.WEST;
        gbc2.fill = GridBagConstraints.HORIZONTAL;

        gbc2.gridx = 0; gbc2.gridy = 0;
        newPositionPanel.add(new JLabel("Mã chức vụ:"), gbc2);
        gbc2.gridx = 1;
        tfNewPositionId = new JTextField(positionDAO.generateNextMaChucVu(), 10);
        tfNewPositionId.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        newPositionPanel.add(tfNewPositionId, gbc2);

        gbc2.gridx = 0; gbc2.gridy = 1;
        newPositionPanel.add(new JLabel("Tên chức vụ:"), gbc2);
        gbc2.gridx = 1;
        tfNewPositionName = new JTextField(15);
        tfNewPositionName.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        newPositionPanel.add(tfNewPositionName, gbc2);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        formPanel.add(newPositionPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Hủy");
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #e5e7eb; foreground: #374151");
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnOK = new JButton("Thêm");
        btnOK.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #7e22ce; foreground: #ffffff");
        btnOK.addActionListener(e -> validateAndClose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnOK);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void validateAndClose() {
        if (tfRoleName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên role!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            tfRoleName.requestFocus();
            return;
        }

        if (cbAddNewPosition.isSelected()) {
            if (tfNewPositionName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên chức vụ mới!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                tfNewPositionName.requestFocus();
                return;
            }
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getRoleName() {
        return tfRoleName.getText().trim();
    }

    public String getRoleId() {
        String id = tfRoleId.getText().trim();
        return id.isEmpty() ? null : id.toUpperCase();
    }

    public boolean isAddingNewPosition() {
        return cbAddNewPosition.isSelected();
    }

    public String getSelectedPositionId() {
        if (cbAddNewPosition.isSelected()) return null;
        String selected = (String) cbPosition.getSelectedItem();
        if (selected == null || selected.isEmpty()) return null;
        int dashIdx = selected.indexOf(" - ");
        return dashIdx > 0 ? selected.substring(0, dashIdx) : selected;
    }

    public PositionDTO getNewPosition() {
        if (!cbAddNewPosition.isSelected()) return null;
        PositionDTO dto = new PositionDTO();
        dto.setMaChucVu(tfNewPositionId.getText().trim().isEmpty()
                ? positionDAO.generateNextMaChucVu()
                : tfNewPositionId.getText().trim().toUpperCase());
        dto.setTenViTri(tfNewPositionName.getText().trim());
        dto.setPhuCapChucVu(BigDecimal.ZERO);
        return dto;
    }
}
