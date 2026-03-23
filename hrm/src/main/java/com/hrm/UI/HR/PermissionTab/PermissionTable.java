package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
        // NOTE: Use CRUD permissions (Xem/Thêm/Sửa/Xóa) and Approval permission (Duyệt).
        String[] columnNames = {"Chức năng", "Xem", "Thêm", "Sửa", "Xóa", "Duyệt"};
        
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
                if (column == 0) {
                    return false;
                }
                
                // Khóa quyền cho các chức năng đặc biệt
                if (row < currentPermissions.size()) {
                    String machucNang = currentPermissions.get(row).getMachucNang();
                    
                    // CN10 (Lịch làm việc): Khóa THÊM (column 2) và XÓA (column 4)
                    if ("CN10".equals(machucNang) && (column == 2 || column == 4)) {
                        return false;
                    }
                    
                    // CN11 (Quản lý tài khoản): Khóa THÊM (column 2) và DUYỆT (column 5)
                    // Chỉ cho phép Xem (column 1), Sửa (column 3), Xóa (column 4)
                    if ("CN11".equals(machucNang) && (column == 2 || column == 5)) {
                        return false;
                    }
                    
                    // CN05 (Đánh giá hiệu suất): Khóa SỬA (column 3) và XÓA (column 4)
                    // Chỉ cho phép Xem (column 1), Thêm (column 2), Duyệt (column 5)
                    if ("CN05".equals(machucNang) && (column == 3 || column == 4)) {
                        return false;
                    }
                }
                return true;
            }
        };

        this.setModel(model);
        
        // Thêm listener để xử lý logic tước quyền
        model.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                
                if (row < 0 || row >= model.getRowCount()) {
                    return;
                }

                // Nếu cột "Xem" (index 1) bị bỏ, tắt các quyền khác
                if (column == 1) {
                    Boolean quyenXem = (Boolean) model.getValueAt(row, 1);
                    if (!quyenXem) {
                        SwingUtilities.invokeLater(() -> {
                            model.setValueAt(false, row, 2); // Tắt Thêm
                            model.setValueAt(false, row, 3); // Tắt Sửa
                            model.setValueAt(false, row, 4); // Tắt Xóa
                            model.setValueAt(false, row, 5); // Tắt Duyệt
                        });
                    }
                } else if (column > 1) { 
                    // Nếu Thêm/Sửa/Xóa/Duyệt được bật, phải bật Xem
                    Boolean hasPermission = (Boolean) model.getValueAt(row, column);
                    if (hasPermission) {
                        Boolean quyenXem = (Boolean) model.getValueAt(row, 1);
                        if (!quyenXem) {
                            SwingUtilities.invokeLater(() -> {
                                model.setValueAt(true, row, 1); // Bật Xem
                            });
                        }
                    }
                }
            }
        });
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
            
            // Cho CN10 (Lịch làm việc), THÊM và XÓA luôn false
            boolean quyenThem = perm.isQuyenThem();
            boolean quyenXoa = perm.isQuyenXoa();
            if ("CN10".equals(perm.getMachucNang())) {
                quyenThem = false;
                quyenXoa = false;
            }
            
            // Cho CN11 (Quản lý tài khoản), THÊM và DUYỆT luôn false
            boolean quyenDuyet = perm.isQuyenDuyet();
            if ("CN11".equals(perm.getMachucNang())) {
                quyenThem = false;
                quyenDuyet = false;
            }
            
            Object[] row = {
                perm.getTenChucNang() != null ? perm.getTenChucNang() : perm.getMachucNang(),
                perm.isQuyenXem(),
                quyenThem,
                perm.isQuyenSua(),
                quyenXoa,
                quyenDuyet
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
                
                // Cho CN10 (Lịch làm việc), THÊM và XÓA luôn false
                if ("CN10".equals(machucNang)) {
                    quyenThem = false;
                    quyenXoa = false;
                }
                
                // Cho CN11 (Quản lý tài khoản), THÊM và DUYỆT luôn false
                if ("CN11".equals(machucNang)) {
                    quyenThem = false;
                    quyenDuyet = false;
                }
                
                // NOTE: Only CRUD and Approval permissions are used; other rights are forced to false.
                boolean quyenXuatBaoCao = false;
                
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

