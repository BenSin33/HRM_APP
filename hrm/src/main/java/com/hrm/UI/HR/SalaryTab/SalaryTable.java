package com.hrm.UI.HR.SalaryTab;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.UI.component.TableActionCellEditor;
import com.hrm.UI.component.TableActionCellRenderer;

public class SalaryTable extends JPanel {
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private int hoveredRow = -1;
    private TableActionCellRenderer renderer;

    public SalaryTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponent();
    }

    private void initComponent() {
        String[] columnNames = {"Mã NV", "Họ và tên", "Phòng ban", "Lương cơ bản", "Thưởng", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Thao tác"};

        // 1. Tạo Model và thêm dữ liệu tĩnh
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho edit các ô, chỉ xử lý click trực tiếp
            }
        };

        // Thêm dữ liệu mẫu để xem thử
        tableModel.addRow(new Object[]{"NV001", "Nguyễn Văn A", "Kỹ thuật", "20,000,000", "2,000,000", "500,000", "21,500,000", "Chờ duyệt", ""});
        tableModel.addRow(new Object[]{"NV002", "Trần Thị B", "Nhân sự", "15,000,000", "1,000,000", "0", "16,000,000", "Đã thanh toán", ""});

        // Tạo bảng với override prepareRenderer để handle hover
        salaryTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer cellRenderer, int row, int column) {
                Component c = super.prepareRenderer(cellRenderer, row, column);
                if (column == 8 && cellRenderer instanceof TableActionCellRenderer) {
                    ((TableActionCellRenderer)cellRenderer).setHovered(row == hoveredRow);
                }
                return c;
            }
        };
        salaryTable.setRowHeight(50); // Chiều cao dòng lớn để chứa nút bấm thoải mái

        // 2. Thiết lập cột Thao tác (Cột index 8) - CHỈ DÙNG RENDERER
        renderer = new TableActionCellRenderer();
        salaryTable.getColumnModel().getColumn(8).setCellRenderer(renderer);
        
        // 3. Xử lý sự kiện mouse move để hiệu ứng hover
        salaryTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = salaryTable.rowAtPoint(e.getPoint());
                int col = salaryTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 8) {
                    if (hoveredRow != row) {
                        hoveredRow = row;
                        salaryTable.repaint();
                    }
                } else {
                    if (hoveredRow != -1) {
                        hoveredRow = -1;
                        salaryTable.repaint();
                    }
                }
            }
        });
        
        // 4. Xử lý sự kiện click trực tiếp trên icon
        salaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salaryTable.rowAtPoint(e.getPoint());
                int col = salaryTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 8) { // Cột Thao tác
                    Object employeeId = tableModel.getValueAt(row, 0);
                    Rectangle cellRect = salaryTable.getCellRect(row, col, false);
                    
                    // Lấy vị trí tương đối trong ô
                    int relativeX = e.getX() - (int)cellRect.getX();
                    
                    // Chia vùng click: Nếu click phía trái = Sửa, phía phải = Xóa
                    if (relativeX < cellRect.getWidth() / 2) {
                        handleEdit(employeeId);
                    } else {
                        handleDelete(employeeId);
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                salaryTable.repaint();
            }
        });

        // Style cho bảng
        salaryTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "background: #f1f5f9; font: bold");
        
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 15; border: 0,0,0,0");
        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleEdit(Object employeeId) {
        System.out.println("Sửa dữ liệu: " + employeeId);
        JOptionPane.showMessageDialog(this, "Sửa nhân viên: " + employeeId);
    }

    private void handleDelete(Object employeeId) {
        System.out.println("Xóa mã: " + employeeId);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn chắc chắn muốn xóa nhân viên: " + employeeId + "?",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int row = salaryTable.getSelectedRow();
            if (row != -1) {
                tableModel.removeRow(row);
            }
        }
    }
}