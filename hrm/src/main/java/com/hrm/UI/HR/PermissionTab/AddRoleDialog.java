package com.hrm.UI.HR.PermissionTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.PositionDAO;
import com.hrm.DTO.PositionDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Color.WHITE);

        positions = positionDAO.getAllPositions();

        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(16, 20, 8, 20));

        Font titleFont = new Font("Segoe UI", Font.BOLD, 13);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        // --- Khối Role (không dùng ký tự Unicode trang trí — tránh ô vuông trên font hệ thống) ---
        JPanel roleSection = new JPanel(new GridBagLayout());
        roleSection.setBackground(Color.WHITE);
        TitledBorder roleTitle = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                "Cấu hình Role");
        roleTitle.setTitleFont(titleFont);
        roleTitle.setTitleColor(new Color(55, 65, 81));
        roleSection.setBorder(roleTitle);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.gridy = 0;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        JLabel lblId = new JLabel("Mã role");
        lblId.setFont(labelFont);
        roleSection.add(lblId, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        tfRoleId = new JTextField();
        if (suggestedRoleId != null && !suggestedRoleId.isBlank()) {
            tfRoleId.setText(suggestedRoleId.trim());
        }
        tfRoleId.setFont(labelFont);
        tfRoleId.setColumns(18);
        tfRoleId.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        tfRoleId.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: R4 (để trống = tự sinh)");
        tfRoleId.setToolTipText("Để trống để hệ thống tự gán mã (ví dụ R4, R5…)");
        roleSection.add(tfRoleId, g);

        g.gridx = 0;
        g.gridy = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        JLabel lblName = new JLabel("Tên role");
        lblName.setFont(labelFont);
        roleSection.add(lblName, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        tfRoleName = new JTextField();
        tfRoleName.setFont(labelFont);
        tfRoleName.setColumns(18);
        tfRoleName.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        tfRoleName.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Ví dụ: Supervisor, Guest");
        tfRoleName.setToolTipText("Tên hiển thị của role");
        roleSection.add(tfRoleName, g);

        root.add(roleSection);
        root.add(Box.createVerticalStrut(14));

        // --- Khối chức vụ ---
        JPanel posSection = new JPanel(new GridBagLayout());
        posSection.setBackground(Color.WHITE);
        TitledBorder posTitle = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 210)),
                "Chức vụ liên kết");
        posTitle.setTitleFont(titleFont);
        posTitle.setTitleColor(new Color(55, 65, 81));
        posSection.setBorder(posTitle);

        g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        cbAddNewPosition = new JCheckBox("Thêm chức vụ mới");
        cbAddNewPosition.setFont(labelFont);
        cbAddNewPosition.setBackground(Color.WHITE);
        cbAddNewPosition.addActionListener(e -> {
            newPositionPanel.setVisible(cbAddNewPosition.isSelected());
            cbPosition.setEnabled(!cbAddNewPosition.isSelected());
            pack();
            setLocationRelativeTo(parent);
        });
        posSection.add(cbAddNewPosition, g);

        g.gridy = 1;
        g.gridwidth = 1;
        g.weightx = 0;
        g.fill = GridBagConstraints.NONE;
        g.gridx = 0;
        JLabel lblPick = new JLabel("Chọn chức vụ");
        lblPick.setFont(labelFont);
        posSection.add(lblPick, g);

        g.gridx = 1;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        cbPosition = new JComboBox<>();
        cbPosition.setFont(labelFont);
        cbPosition.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        for (PositionDTO p : positions) {
            cbPosition.addItem(p.getMaChucVu() + " - " + p.getTenViTri());
        }
        if (cbPosition.getItemCount() == 0) {
            cbPosition.addItem("Không có chức vụ");
        }
        cbPosition.setPrototypeDisplayValue("CV99 - Tên chức vụ mẫu dài");
        posSection.add(cbPosition, g);

        newPositionPanel = new JPanel(new GridBagLayout());
        newPositionPanel.setBackground(new Color(248, 249, 252));
        newPositionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 222), 1),
                new EmptyBorder(10, 10, 10, 10)));
        newPositionPanel.setVisible(false);

        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(6, 6, 6, 6);
        g2.anchor = GridBagConstraints.WEST;

        g2.gridx = 0;
        g2.gridy = 0;
        g2.weightx = 0;
        g2.fill = GridBagConstraints.NONE;
        JLabel l1 = new JLabel("Mã chức vụ");
        l1.setFont(labelFont);
        newPositionPanel.add(l1, g2);
        g2.gridx = 1;
        g2.weightx = 1;
        g2.fill = GridBagConstraints.HORIZONTAL;
        tfNewPositionId = new JTextField(positionDAO.generateNextMaChucVu());
        tfNewPositionId.setFont(labelFont);
        tfNewPositionId.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        newPositionPanel.add(tfNewPositionId, g2);

        g2.gridx = 0;
        g2.gridy = 1;
        g2.weightx = 0;
        g2.fill = GridBagConstraints.NONE;
        JLabel l2 = new JLabel("Tên chức vụ");
        l2.setFont(labelFont);
        newPositionPanel.add(l2, g2);
        g2.gridx = 1;
        g2.weightx = 1;
        g2.fill = GridBagConstraints.HORIZONTAL;
        tfNewPositionName = new JTextField();
        tfNewPositionName.setFont(labelFont);
        tfNewPositionName.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        newPositionPanel.add(tfNewPositionName, g2);

        g.gridx = 0;
        g.gridy = 2;
        g.gridwidth = 2;
        g.weightx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        posSection.add(newPositionPanel, g);

        root.add(posSection);

        add(root, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 16, 12, 16));

        JButton btnCancel = new JButton("Hủy");
        btnCancel.setFont(labelFont);
        btnCancel.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #e5e7eb; foreground: #374151");
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });

        JButton btnOK = new JButton("Thêm");
        btnOK.setFont(labelFont);
        btnOK.putClientProperty(FlatClientProperties.STYLE, "arc: 8; background: #7e22ce; foreground: #ffffff");
        btnOK.addActionListener(e -> validateAndClose());

        btnPanel.add(btnCancel);
        btnPanel.add(btnOK);
        add(btnPanel, BorderLayout.SOUTH);

        setMinimumSize(new Dimension(420, 280));
        pack();
        setLocationRelativeTo(parent);
        setResizable(true);
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
