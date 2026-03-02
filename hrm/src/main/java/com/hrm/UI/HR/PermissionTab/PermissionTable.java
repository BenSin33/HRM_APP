package com.hrm.UI.HR.PermissionTab;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.hrm.DTO.PermissionDTO;
import com.hrm.Service.PermissionService;
import java.util.List;

public class PermissionTable extends JTable {
    
    private DefaultTableModel model;
    private PermissionService permissionService;
    private String currentRoleId;

    public PermissionTable(){
        String[] columnNames = {"Chức năng (Module)", "Xem", "Thêm", "Sửa", "Xóa"};
        
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
        this.setRowHeight(45);
        this.setShowVerticalLines(false);
        this.getTableHeader().setReorderingAllowed(false);
        
        permissionService = new PermissionService();
    }
    
    /**
     * Cập nhật dữ liệu bảng từ danh sách PermissionDTO
     * @param permissions Danh sách quyền từ database
     * @param roleId ID của vai trò hiện tại
     */
    public void updateData(List<PermissionDTO> permissions, String roleId) {
        this.currentRoleId = roleId;
        
        // Xóa dữ liệu cũ
        model.setRowCount(0);
        
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        
        // Thêm dữ liệu mới vào bảng
        for (PermissionDTO perm : permissions) {
            Object[] row = {
                getChucNangName(perm.getMachucNang()),
                perm.isQuyenXem(),
                perm.isQuyenThem(),
                perm.isQuyenSua(),
                perm.isQuyenXoa()
            };
            model.addRow(row);
        }
    }
    
    /**
     * Lấy tên chức năng từ mã chức năng
     */
    private String getChucNangName(String machucNang) {
        switch(machucNang) {
            case "CN01": return "Quản lý nhân sự";
            case "CN02": return "Quản lý phòng ban";
            case "CN03": return "Quản lý lương";
            case "CN04": return "Quản lý nghỉ phép";
            case "CN05": return "Quản lý hợp đồng";
            case "CN06": return "Quản lý chứng chỉ";
            case "CN07": return "Quản lý đánh giá";
            default: return machucNang;
        }
    }
    
    /**
     * Lưu các thay đổi quyền vào database
     */
    public boolean saveChanges() {
        try {
            int rowCount = model.getRowCount();
            String[] machucNangs = {"CN01", "CN02", "CN03", "CN04", "CN05", "CN06", "CN07"};
            
            for (int i = 0; i < rowCount && i < machucNangs.length; i++) {
                String machucNang = machucNangs[i];
                boolean quyenXem = (boolean) model.getValueAt(i, 1);
                boolean quyenThem = (boolean) model.getValueAt(i, 2);
                boolean quyenSua = (boolean) model.getValueAt(i, 3);
                boolean quyenXoa = (boolean) model.getValueAt(i, 4);
                
                permissionService.updatePermission(currentRoleId, machucNang, 
                                                   quyenXem, quyenThem, quyenSua, quyenXoa);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi lưu quyền: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

