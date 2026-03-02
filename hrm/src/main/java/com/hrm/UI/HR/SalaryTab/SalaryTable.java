package com.hrm.UI.HR.SalaryTab;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.Employee.SalaryDAO;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.UI.component.TableActionCellEditor;
import com.hrm.UI.component.TableActionCellRenderer;
import com.hrm.UI.component.CRUDDialog;

public class SalaryTable extends JPanel {
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private int hoveredRow = -1;
    private TableActionCellRenderer renderer;
    private SalaryDAO salaryDAO;
    private DecimalFormat df = new DecimalFormat("#,###");

    public SalaryTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        salaryDAO = new SalaryDAO();
        initComponent();
    }

    private void initComponent() {
        String[] columnNames = {"Mã NV", "Họ và tên", "Phòng ban", "Lương cơ bản", "Thưởng", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Thao tác"};

        // 1. Tạo Model và thêm dữ liệu từ database
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho edit các ô, chỉ xử lý click trực tiếp
            }
        };

        // Load dữ liệu từ database
        loadSalaryData();

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
        
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.putClientProperty(FlatClientProperties.STYLE,
            "arc: 15; background: #ffffff; border: 1,1,1,1,#e5e7eb");
        wrap.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(salaryTable);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,0,0");
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        
        wrap.add(scrollPane, BorderLayout.CENTER);
        add(wrap, BorderLayout.CENTER);
    }

    private void handleEdit(Object employeeId) {
        System.out.println("Sửa dữ liệu: " + employeeId);
        
        // Tìm mã lương theo mã nhân viên
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        
        SalaryDTO salary = salaryDAO.getSalaryByMaNV(employeeId.toString(), thang, nam);
        if (salary == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tạo form edit
        SalaryEditForm editForm = new SalaryEditForm();
        CRUDDialog<SalaryDTO> dialog = new CRUDDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Cập nhật dữ liệu lương",
            editForm,
            salary
        );
        
        dialog.setVisible(true);
        
        SalaryDTO updatedSalary = dialog.getResult();
        if (updatedSalary != null) {
            if (salaryDAO.updateSalary(updatedSalary)) {
                JOptionPane.showMessageDialog(this, "Cập nhật lương thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete(Object employeeId) {
        System.out.println("Xóa mã: " + employeeId);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn chắc chắn muốn xóa dữ liệu lương của nhân viên: " + employeeId + "?",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int row = salaryTable.getSelectedRow();
            if (row != -1) {
                // Note: SalaryDTO không có phương thức delete, chỉ có update trạng thái
                // Nếu muốn xóa, có thể update trạng thái thành "Đã xóa" hoặc thêm method delete vào SalaryDAO
                JOptionPane.showMessageDialog(this, "Xóa dữ liệu lương không được phép (chỉ cập nhật trạng thái được cho phép)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void loadSalaryData() {
        // Lấy tháng/năm hiện tại
        LocalDate now = LocalDate.now();
        int thang = now.getMonthValue();
        int nam = now.getYear();
        
        System.out.println("DEBUG: Loading salary data for month " + thang + "/" + nam);
        List<SalaryDTO> salaries = salaryDAO.getSalariesByMonthYear(thang, nam);
        System.out.println("DEBUG: Retrieved " + salaries.size() + " salary records");
        
        // Nếu không có dữ liệu cho tháng hiện tại, thử lấy tất cả dữ liệu
        if (salaries.isEmpty()) {
            System.out.println("DEBUG: No data for current month, loading all salaries");
            salaries = salaryDAO.getAllSalaries();
            System.out.println("DEBUG: Retrieved " + salaries.size() + " total salary records");
        }
        
        for (SalaryDTO salary : salaries) {
            System.out.println("DEBUG: Adding row for " + salary.maNV + " - " + salary.hoTen);
            tableModel.addRow(new Object[]{
                salary.maNV,
                salary.hoTen,
                salary.phongBan,
                df.format(salary.luongCoBan != null ? salary.luongCoBan.doubleValue() : 0),
                df.format(salary.tongPhucap != null ? salary.tongPhucap.doubleValue() : 0),
                df.format(salary.tongKhauTru != null ? salary.tongKhauTru.doubleValue() : 0),
                df.format(salary.thucLinh != null ? salary.thucLinh.doubleValue() : 0),
                salary.trangThai != null ? salary.trangThai : "Chưa xác định",
                ""
            });
        }
    }

    // Public method để refresh dữ liệu từ Tab
    public void refreshData() {
        tableModel.setRowCount(0);
        loadSalaryData();
    }

    // Public method để lấy dữ liệu lương theo tháng/năm
    public void loadSalaryDataByMonth(int thang, int nam) {
        tableModel.setRowCount(0);
        
        List<SalaryDTO> salaries = salaryDAO.getSalariesByMonthYear(thang, nam);
        
        for (SalaryDTO salary : salaries) {
            tableModel.addRow(new Object[]{
                salary.maNV,
                salary.hoTen,
                salary.phongBan,
                df.format(salary.luongCoBan != null ? salary.luongCoBan.doubleValue() : 0),
                df.format(salary.tongPhucap != null ? salary.tongPhucap.doubleValue() : 0),
                df.format(salary.tongKhauTru != null ? salary.tongKhauTru.doubleValue() : 0),
                df.format(salary.thucLinh != null ? salary.thucLinh.doubleValue() : 0),
                salary.trangThai != null ? salary.trangThai : "Chưa xác định",
                ""
            });
        }
    }
}