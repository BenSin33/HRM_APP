package com.hrm.UI.HR.SalaryTab;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.UI.component.TableActionCellEditor;
import com.hrm.UI.component.TableActionCellRenderer;

public class SalaryTable extends JPanel {
    private JTable salaryTable;
    private DefaultTableModel tableModel;

    public SalaryTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponent();
    }

    private void initComponent() {
        String[] columnNames = {"Mã NV", "Họ và tên", "Phòng ban", "Lương cơ bản", "Thưởng", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Thao tác"};
        var column = salaryTable.getColumnModel().getColumn(8);
        column.setCellRenderer(new TableActionCellRenderer() );
        column.setCellEditor(new TableActionCellEditor<>(
            new JPanel(),
            handler,
            () -> {
                int row = salaryTable.getSelectedRow();
                return row != -1 ? tableModel.getValueAt(row, 0) : null;
            }
        ));

        // 1. Tạo Model và thêm dữ liệu tĩnh
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Chỉ cột Thao tác mới cho phép click
            }
        };

        // Thêm dữ liệu mẫu để xem thử
        tableModel.addRow(new Object[]{"NV001", "Nguyễn Văn A", "Kỹ thuật", "20,000,000", "2,000,000", "500,000", "21,500,000", "Chờ duyệt", ""});
        tableModel.addRow(new Object[]{"NV002", "Trần Thị B", "Nhân sự", "15,000,000", "1,000,000", "0", "16,000,000", "Đã thanh toán", ""});

        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(50); // Chiều cao dòng lớn để chứa nút bấm thoải mái

        // 2. Thiết lập cột Thao tác (Cột index 8)
        salaryTable.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor<>(
            new JPanel(), // Placeholder cho Form nhập liệu (ví dụ SalaryForm)
            new TableActionCellEditor.ActionHandler<Object>() {
                @Override
                public void onEdit(Object data) {
                    System.out.println("Sửa dữ liệu: " + data);
                }

                @Override
                public void onDelete(Object data) {
                    // Logic khi nhấn xác nhận xóa ở DeleteButton truyền về đây
                    System.out.println("Xóa mã: " + data);
                    // tableModel.removeRow(salaryTable.getSelectedRow());
                }
            },
            () -> {
                // Supplier: Lấy mã NV ở cột 0 của dòng đang chọn để truyền cho nút Xóa
                int row = salaryTable.getSelectedRow();
                return row != -1 ? salaryTable.getValueAt(row, 0) : "Unknown";
            }
        ));

        // Style cho bảng
        salaryTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "background: #f1f5f9; font: bold");
        
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 0,0,0,0");
        add(scrollPane, BorderLayout.CENTER);
    }
}