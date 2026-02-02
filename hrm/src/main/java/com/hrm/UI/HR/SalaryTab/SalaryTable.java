package com.hrm.UI.HR.SalaryTab;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.UI.component.TableActionCellEditor;


public class SalaryTable extends JPanel {

    private JTable salaryTable;
    private DefaultTableModel tableModel;  // Mô hình dữ liệu bảng

    public SalaryTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponent();
    }

    private void initComponent() {

        String[] columnNames = {"Mã NV", "Họ và tên", "Phòng ban", "Lương cơ bản", "Thưởng", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Thao tác"};

        tableModel = new DefaultTableModel(columnNames,0) {     // Tạo mô hình dữ liệu bảng với cột "Thao tác" không thể chỉnh sửa
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Chỉ cột "Thao tác" bấm được
            }
        };
        
        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(45); // Đặt chiều cao hàng

        // Áp dụng kiểu dáng FlatLaf cho bảng
        // Hiển thị đường kẻ ngang giữa các dòng
        // Khoảng cách giữa các ô: ngang 0, dọc 1
        // Màu nền khi chọn dòng
        // Màu chữ khi chọn dòng
        salaryTable.putClientProperty(
            "FlatClientProperties.STYLE",
            """
            showHorizontalLines: true;
            intercellSpacing: 0 1;
            selectionBackground: #f3e8ff;
            selectionForeground: #000000;
            """
        );

        /*
        // Đăng ký Editor cho nút "Tính lại" (Sử dụng component bạn đã viết)
        // Lưu ý: handler ở đây sẽ gọi sang lớp Service để tính toán thực tế
        salaryTable.getColumnModel().getColumn(8).setCellEditor(new TableActionCellEditor<>(null, data -> {
            // // Dự định: Gọi SalaryService.recalculate(data)
            System.out.println("Đang tính lại cho nhân viên: " + data);
        }, () -> getSelectedRecord())); */

        /* 
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 1,1,1,1,#e5e7eb");
        add(scrollPane, BorderLayout.CENTER);

        */
    }
/* 
        // Hàm giả lập để lấy dữ liệu record từ dòng được chọn
        private SalaryRecord getSelectedRecord() {
        // // Dự định: Trả về đối tượng SalaryRecord tương ứng với row trong table
        return null; */




}

