package com.hrm.UI.HR.AccountManagerTab;

import com.formdev.flatlaf.FlatClientProperties;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

public class AccountTable extends JPanel {
    
    private JTable accountTable;
    private DefaultTableModel tableModel;

    public AccountTable() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // Tạo model bảng
        String[] columns = {"ID", "Tên tài khoản", "Email", "Phòng ban", "Chức vụ", "Trạng thái", "Ngày tạo", "Hành động"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Thêm dữ liệu mẫu
        addSampleData();

        // Tạo bảng
        accountTable = new JTable(tableModel);
        accountTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        accountTable.setRowHeight(35);
        accountTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.putClientProperty(FlatClientProperties.STYLE, 
            "gridColor: #e0e0e0; background: #ffffff");

        // Đặt độ rộng cột
        accountTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        accountTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        accountTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        accountTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        accountTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // ScrollPane
        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Chân trang - Thông tin phân trang
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        JLabel pageLabel = new JLabel("Hiển thị 1 đến 10 của 156 tài khoản");
        pageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerPanel.add(pageLabel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private void addSampleData() {
        String[][] data = {
            {"1", "nguyen.van.a", "nguyen.van.a@company.com", "IT", "Trưởng phòng", "Hoạt động", "01/01/2024", "Chỉnh sửa"},
            {"2", "hoang.thi.b", "hoang.thi.b@company.com", "Nhân sự", "Nhân viên", "Hoạt động", "15/01/2024", "Chỉnh sửa"},
            {"3", "tran.van.c", "tran.van.c@company.com", "Kinh doanh", "Giám đốc", "Hoạt động", "20/01/2024", "Chỉnh sửa"},
            {"4", "le.thi.d", "le.thi.d@company.com", "Kế toán", "Nhân viên", "Vô hiệu hóa", "25/01/2024", "Chỉnh sửa"},
            {"5", "pham.van.e", "pham.van.e@company.com", "IT", "Nhân viên", "Hoạt động", "28/01/2024", "Chỉnh sửa"},
            {"6", "dang.thi.f", "dang.thi.f@company.com", "Bán hàng", "Trưởng phòng", "Hoạt động", "02/02/2024", "Chỉnh sửa"},
            {"7", "vu.van.g", "vu.van.g@company.com", "Hỗ trợ", "Nhân viên", "Hoạt động", "10/02/2024", "Chỉnh sửa"},
            {"8", "tran.thi.h", "tran.thi.h@company.com", "IT", "Nhân viên", "Hoạt động", "15/02/2024", "Chỉnh sửa"},
            {"9", "nguyen.thi.i", "nguyen.thi.i@company.com", "Nhân sự", "Trưởng phòng", "Hoạt động", "20/02/2024", "Chỉnh sửa"},
            {"10", "hoang.van.j", "hoang.van.j@company.com", "Kinh doanh", "Nhân viên", "Hoạt động", "25/02/2024", "Chỉnh sửa"}
        };

        for (String[] row : data) {
            tableModel.addRow(row);
        }
    }
}
