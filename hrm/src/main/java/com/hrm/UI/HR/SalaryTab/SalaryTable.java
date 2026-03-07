package com.hrm.UI.HR.SalaryTab;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.Employee.SalaryDAO;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.Service.SalaryService;
import com.hrm.UI.component.CRUDDialog;

public class SalaryTable extends JPanel {
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private SalaryDAO salaryDAO;
    private SalaryService salaryService;
    private DecimalFormat df = new DecimalFormat("#,###");
    private JTextField searchField;
    private List<SalaryDTO> allSalaries;
    private YearMonth currentMonth;

    public SalaryTable() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        salaryDAO = new SalaryDAO();
        salaryService = new SalaryService();
        allSalaries = new ArrayList<>();
        currentMonth = YearMonth.now();
        initComponent();
    }

    private void initComponent() {
        // Thanh tìm kiếm
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Bảng dữ liệu với đầy đủ cột từ database
        String[] columnNames = {
            "Mã lương", "Mã NV", "Họ tên", "Tháng", "Năm", 
            "Lương cơ bản", "Số ngày công", "Tổng phụ cấp", "Tổng khấu trừ", 
            "Ngày chốt", "Thực lĩnh", "Trạng thái", "Tình trạng TT", "Thao tác"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadSalaryData();

        salaryTable = new JTable(tableModel);
        salaryTable.setRowHeight(40);
        salaryTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        // Thiết lập độ rộng cột
        salaryTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Mã lương
        salaryTable.getColumnModel().getColumn(1).setPreferredWidth(70);  // Mã NV
        salaryTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Họ tên
        salaryTable.getColumnModel().getColumn(3).setPreferredWidth(60);  // Tháng
        salaryTable.getColumnModel().getColumn(4).setPreferredWidth(60);  // Năm
        salaryTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Lương cơ bản
        salaryTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Số ngày công
        salaryTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Tổng phụ cấp
        salaryTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Tổng khấu trừ
        salaryTable.getColumnModel().getColumn(9).setPreferredWidth(90);  // Ngày chốt
        salaryTable.getColumnModel().getColumn(10).setPreferredWidth(100);// Thực lĩnh
        salaryTable.getColumnModel().getColumn(11).setPreferredWidth(80); // Trạng thái
        salaryTable.getColumnModel().getColumn(12).setPreferredWidth(100);// Tình trạng TT
        salaryTable.getColumnModel().getColumn(13).setPreferredWidth(100);// Thao tác
        
        // Xử lý click vào cột Thao tác
        salaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salaryTable.rowAtPoint(e.getPoint());
                int col = salaryTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 13) { // Cột Thao tác
                    Rectangle cellRect = salaryTable.getCellRect(row, col, false);
                    int relativeX = e.getX() - (int)cellRect.getX();
                    
                    if (relativeX < cellRect.getWidth() / 2) {
                        handleEdit(row);
                    } else {
                        handleDelete(row);
                    }
                }
            }
        });

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

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        
        JLabel lblSearch = new JLabel("Tìm kiếm:");
        searchField = new JTextField(20);
        searchField.putClientProperty(FlatClientProperties.STYLE, "arc: 8");
        
        // Real-time search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });
        
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        return searchPanel;
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0);
        
        if (keyword.isEmpty()) {
            // Hiển thị tất cả dữ liệu
            displaySalaries(allSalaries);
        } else {
            // Tìm kiếm trong dữ liệu đã load
            List<SalaryDTO> searchResults = new ArrayList<>();
            String lowerKeyword = keyword.toLowerCase();
            
            for (SalaryDTO salary : allSalaries) {
                if ((salary.maLuong != null && salary.maLuong.toLowerCase().contains(lowerKeyword)) ||
                    (salary.maNV != null && salary.maNV.toLowerCase().contains(lowerKeyword)) ||
                    (salary.hoTen != null && salary.hoTen.toLowerCase().contains(lowerKeyword))) {
                    searchResults.add(salary);
                }
            }
            
            displaySalaries(searchResults);
        }
    }

    private void displaySalaries(List<SalaryDTO> salaries) {
        tableModel.setRowCount(0);
        for (SalaryDTO salary : salaries) {
            // Convert TRANGTHAI: 0 = Chưa khóa, 1 = Đã khóa
            String statusDisplay = "0".equals(salary.trangThai) || salary.trangThai == null ? 
                "Chưa khóa" : "1".equals(salary.trangThai) ? "Đã khóa" : salary.trangThai;
            
            tableModel.addRow(new Object[]{
                salary.maLuong,
                salary.maNV,
                salary.hoTen,
                salary.thang,
                salary.nam,
                df.format(salary.luongCoBan != null ? salary.luongCoBan.doubleValue() : 0),
                String.format("%.1f", salary.soNgayCong),
                df.format(salary.tongPhucap != null ? salary.tongPhucap.doubleValue() : 0),
                df.format(salary.tongKhauTru != null ? salary.tongKhauTru.doubleValue() : 0),
                salary.ngayChot != null ? salary.ngayChot : "Chưa chốt",
                df.format(salary.thucLinh != null ? salary.thucLinh.doubleValue() : 0),
                statusDisplay,
                salary.tinhTrangThanToan != null ? salary.tinhTrangThanToan : "Chưa thanh toán",
                "Sửa | Xóa"
            });
        }
    }

    private void handleEdit(int row) {
        if (row < 0 || row >= allSalaries.size()) return;
        
        // Lấy mã lương từ row
        String maLuong = (String) tableModel.getValueAt(row, 0);
        
        // Tìm dữ liệu tương ứng
        SalaryDTO salary = null;
        for (SalaryDTO s : allSalaries) {
            if (s.maLuong.equals(maLuong)) {
                salary = s;
                break;
            }
        }
        
        if (salary == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu lương!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
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

    private void handleDelete(int row) {
        if (row < 0 || row >= allSalaries.size()) return;
        
        String maLuong = (String) tableModel.getValueAt(row, 0);
        String maNV = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn chắc chắn muốn xóa dữ liệu lương của " + maNV + "?",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Chức năng xóa không được phép. Chỉ được cập nhật trạng thái.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadSalaryData() {
        if (currentMonth == null) {
            // Nếu chọn "Xem tất cả"
            allSalaries = salaryDAO.getAllSalaries();
        } else {
            // Lọc theo tháng/năm
            allSalaries = salaryDAO.getSalariesByMonthYear(currentMonth.getMonthValue(), currentMonth.getYear());
        }
        
        displaySalaries(allSalaries);
    }

    public void refreshData() {
        searchField.setText("");
        loadSalaryData();
    }

    public void loadSalaryDataByMonth(int thang, int nam) {
        currentMonth = YearMonth.of(nam, thang);
        searchField.setText("");
        loadSalaryData();
    }

    public void loadAllSalaryData() {
        currentMonth = null;
        searchField.setText("");
        loadSalaryData();
    }
}
