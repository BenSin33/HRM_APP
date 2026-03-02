package com.hrm.UI.HR.PermissionTab;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class PermissionTable extends JTable {

    public PermissionTable(){
        String[] columnNames = {"chức năng (module)", "Xem",
                                "Thêm", "Sửa", "Xóa"};
        
        //dữ liệu mẫu
        Object[][] data = {
            {"Quản lý nhân sự", true, false, false, false},
            {"Quản lý phòng ban", true, false, false, false},
            {"Quản lý Lương", true, false, false, false},
            {"Quản lý nghỉ phép", true, false, false, false},

        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Trả về Boolean.class để JTable tự động hiển thị Checkbox cho cột 1-5
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
    }
    
}
