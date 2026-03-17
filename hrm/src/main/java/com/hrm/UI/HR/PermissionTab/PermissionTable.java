package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.hrm.DTO.PermissionDTO;
import com.hrm.Service.PermissionService;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionTable extends JTable {
    
    private DefaultTableModel model;
    private PermissionService permissionService;
    private String currentRoleId;
    private String currentManv;
    private List<PermissionDTO> currentPermissions;

    public PermissionTable(){
        String[] columnNames = {"Chức năng", "Xem", "Thêm", "Sửa", "Xóa", "Duyệt", "Xuất BC"};
        
        // Dữ liệu mẫu ban đầu
        Object[][] data = {};

        model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Trả về Boolean.class để JTable tự động hiển thị Checkbox
                return columnIndex == 0 ? String.class : Boolean.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Không cho sửa tên Module (cột 0)
                return column != 0;
            }
        };

        this.setModel(model);
        this.setRowHeight(40);
        this.setShowVerticalLines(false);
        this.setGridColor(new Color(220, 220, 220));
        this.getTableHeader().setReorderingAllowed(false);
        this.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        // Tùy chỉnh header
        this.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        this.getTableHeader().setBackground(new Color(248, 249, 250));
        this.getTableHeader().setForeground(new Color(75, 85, 99));
        this.getTableHeader().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        permissionService = new PermissionService();
        currentPermissions = new ArrayList<>();
    }
    
    /**
     * Cập nhật dữ liệu bảng từ danh sách PermissionDTO
     */
    public void updateData(List<PermissionDTO> permissions, String roleId) {
        this.currentRoleId = roleId;
        this.currentManv = null;
        reloadModel(permissions);
    }

    public void updateUserData(List<PermissionDTO> permissions, String roleId, String manv) {
        this.currentRoleId = roleId;
        this.currentManv = manv;
        reloadModel(permissions);
    }

    private void reloadModel(List<PermissionDTO> permissions) {
        currentPermissions = new ArrayList<>();
        
        // Xóa dữ liệu cũ
        model.setRowCount(0);
        
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        
        // Thêm dữ liệu mới vào bảng
        for (PermissionDTO perm : permissions) {
            currentPermissions.add(perm);
            Object[] row = {
                perm.getTenChucNang() != null ? perm.getTenChucNang() : perm.getMachucNang(),
                perm.isQuyenXem(),
                perm.isQuyenThem(),
                perm.isQuyenSua(),
                perm.isQuyenXoa(),
                perm.isQuyenDuyet(),
                perm.isQuyenXuatBaoCao()
            };
            model.addRow(row);
        }
    }
    
    /**
     * Lưu các thay đổi quyền vào database
     */
    public boolean saveChanges() {
        try {
            int rowCount = model.getRowCount();
            if (rowCount != currentPermissions.size()) {
                return false;
            }
            
            for (int i = 0; i < rowCount; i++) {
                PermissionDTO permission = currentPermissions.get(i);
                String machucNang = permission.getMachucNang();
                boolean quyenXem = (boolean) model.getValueAt(i, 1);
                boolean quyenThem = (boolean) model.getValueAt(i, 2);
                boolean quyenSua = (boolean) model.getValueAt(i, 3);
                boolean quyenXoa = (boolean) model.getValueAt(i, 4);
                boolean quyenDuyet = (boolean) model.getValueAt(i, 5);
                boolean quyenXuatBaoCao = (boolean) model.getValueAt(i, 6);
                
                boolean updated;
                if (isEditingUserPermissions()) {
                    updated = permissionService.updateUserPermission(currentManv, machucNang,
                            quyenXem, quyenThem, quyenSua, quyenXoa, quyenDuyet, quyenXuatBaoCao);
                } else {
                    updated = permissionService.updatePermission(currentRoleId, machucNang,
                            quyenXem, quyenThem, quyenSua, quyenXoa, quyenDuyet, quyenXuatBaoCao);
                }

                if (!updated) {
                    return false;
                }

                permission.setQuyenXem(quyenXem);
                permission.setQuyenThem(quyenThem);
                permission.setQuyenSua(quyenSua);
                permission.setQuyenXoa(quyenXoa);
                permission.setQuyenDuyet(quyenDuyet);
                permission.setQuyenXuatBaoCao(quyenXuatBaoCao);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu quyền: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean isEditingUserPermissions() {
        return currentManv != null && !currentManv.trim().isEmpty();
    }

    public boolean clearUserPermissions() {
        if (!isEditingUserPermissions()) {
            return false;
        }
        return permissionService.clearUserPermissions(currentManv);
    }
}

