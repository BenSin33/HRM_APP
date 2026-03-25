package com.hrm.UI.HR.PermissionTab;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.PositionDAO;
import com.hrm.DTO.PositionDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        // Main vertical box layout for form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 10, 32));

        // Section: Role
        JLabel lblRoleSection = new JLabel("CẤU HÌNH ROLE");
        lblRoleSection.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRoleSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblRoleSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        formPanel.add(lblRoleSection);

        JPanel roleRow = new JPanel(new GridLayout(1, 2, 12, 0));
        roleRow.setOpaque(false);
        JLabel lblRoleId = new JLabel("Mã role:");
        tfRoleId = new JTextField(suggestedRoleId, 15);
        tfRoleId.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        tfRoleId.setToolTipText("Để trống để tự động sinh (ví dụ: R4)");
        roleRow.add(lblRoleId);
        roleRow.add(tfRoleId);
        roleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(roleRow);

        formPanel.add(Box.createVerticalStrut(8));

        JPanel nameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        nameRow.setOpaque(false);
        JLabel lblRoleName = new JLabel("Tên role:");
        tfRoleName = new JTextField(20);
        tfRoleName.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        tfRoleName.setToolTipText("Ví dụ: Supervisor, Guest");
        nameRow.add(lblRoleName);
        nameRow.add(tfRoleName);
        nameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameRow);

        formPanel.add(Box.createVerticalStrut(18));

        // Section: Position
        JLabel lblPosSection = new JLabel("CHỨC VỤ LIÊN KẾT");
        lblPosSection.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPosSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPosSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        formPanel.add(lblPosSection);

        cbAddNewPosition = new JCheckBox("Thêm chức vụ mới");
        cbAddNewPosition.setFont(labelFont);
        cbAddNewPosition.setBackground(Color.WHITE);
        cbAddNewPosition.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(cbAddNewPosition);

        JPanel posRow = new JPanel(new GridLayout(1, 2, 12, 0));
        posRow.setOpaque(false);
        JLabel lblChoosePos = new JLabel("Chọn chức vụ:");
        cbPosition = new JComboBox<>();
        cbPosition.setFont(labelFont);
        cbPosition.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        for (PositionDTO p : positions) {
            cbPosition.addItem(p.getMaChucVu() + " - " + p.getTenViTri());
        }
        if (cbPosition.getItemCount() == 0) {
            cbPosition.addItem("Không có chức vụ");
        }
        posRow.add(lblChoosePos);
        posRow.add(cbPosition);
        posRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(posRow);

        // Panel thêm chức vụ mới (ẩn ban đầu)
        newPositionPanel = new JPanel();
        newPositionPanel.setLayout(new BoxLayout(newPositionPanel, BoxLayout.Y_AXIS));
        newPositionPanel.setBackground(new Color(250, 250, 255));
        newPositionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 222), 1),
                new EmptyBorder(10, 10, 10, 10)));
        newPositionPanel.setVisible(false);

        JPanel newPosIdRow = new JPanel(new GridLayout(1, 2, 12, 0));
        newPosIdRow.setOpaque(false);
        JLabel lblNewPosId = new JLabel("Mã chức vụ:");
        tfNewPositionId = new JTextField(positionDAO.generateNextMaChucVu(), 10);
        tfNewPositionId.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        newPosIdRow.add(lblNewPosId);
        newPosIdRow.add(tfNewPositionId);
        newPosIdRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPositionPanel.add(newPosIdRow);

        newPositionPanel.add(Box.createVerticalStrut(6));

        JPanel newPosNameRow = new JPanel(new GridLayout(1, 2, 12, 0));
        newPosNameRow.setOpaque(false);
        JLabel lblNewPosName = new JLabel("Tên chức vụ:");
        tfNewPositionName = new JTextField(15);
        tfNewPositionName.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        newPosNameRow.add(lblNewPosName);
        newPosNameRow.add(tfNewPositionName);
        newPosNameRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPositionPanel.add(newPosNameRow);

        newPositionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(newPositionPanel);

        cbAddNewPosition.addActionListener(e -> {
            newPositionPanel.setVisible(cbAddNewPosition.isSelected());
            cbPosition.setEnabled(!cbAddNewPosition.isSelected());
            pack();
            setLocationRelativeTo(parent);
        });

        formPanel.add(Box.createVerticalStrut(14));

        add(formPanel, BorderLayout.CENTER);

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
