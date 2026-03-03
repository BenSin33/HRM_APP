package com.hrm.UI.HR.SalaryTab;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.Employee.SalaryDAO;
import com.hrm.DTO.Employee.SalaryDTO;
import com.hrm.Service.SalaryService;
import com.hrm.UI.component.TableActionCellEditor;
import com.hrm.UI.component.TableActionCellRenderer;
import com.hrm.UI.component.CRUDDialog;

public class SalaryTable extends JPanel {
    private JTable salaryTable;
    private DefaultTableModel tableModel;
    private int hoveredRow = -1;
    private TableActionCellRenderer renderer;
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
        
        // Bảng dữ liệu
        String[] columnNames = {"Mã NV", "Họ và tên", "Phòng ban", "Lương cơ bản", "Thưởng", "Khấu trừ", "Thực lĩnh", "Trạng thái", "Thao tác"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadSalaryData();

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
        salaryTable.setRowHeight(50);

        renderer = new TableActionCellRenderer();
        salaryTable.getColumnModel().getColumn(8).setCellRenderer(renderer);
        
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
        
        salaryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = salaryTable.rowAtPoint(e.getPoint());
                int col = salaryTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 8) {
                    Object employeeId = tableModel.getValueAt(row, 0);
                    Rectangle cellRect = salaryTable.getCellRect(row, col, false);
                    
                    int relativeX = e.getX() - (int)cellRect.getX();
                    
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
            // Tìm kiếm trong dữ liệu hiện tại
            List<SalaryDTO> searchResults = salaryService.searchSalaries(
                currentMonth.getMonthValue(), 
                currentMonth.getYear(), 
                keyword
            );
            displaySalaries(searchResults);
        }
    }

    private void displaySalaries(List<SalaryDTO> salaries) {
        tableModel.setRowCount(0);
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

    private void handleEdit(Object employeeId) {
        System.out.println("Sửa dữ liệu: " + employeeId);
        
        // Tìm kiếm dữ liệu từ danh sách đã load
        SalaryDTO salary = null;
        for (SalaryDTO s : allSalaries) {
            if (s.maNV.equals(employeeId.toString())) {
                salary = s;
                break;
            }
        }
        
        // Nếu không tìm thấy trong danh sách hiện tại, tìm kiếm trong database
        if (salary == null) {
            salary = salaryDAO.getSalaryByMaNV(employeeId.toString(), 
                currentMonth.getMonthValue(), currentMonth.getYear());
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

    private void handleDelete(Object employeeId) {
        System.out.println("Xóa mã: " + employeeId);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn chắc chắn muốn xóa dữ liệu lương của nhân viên: " + employeeId + "?",
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu lương không được phép (chỉ cập nhật trạng thái được cho phép)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadSalaryData() {
        int thang = currentMonth.getMonthValue();
        int nam = currentMonth.getYear();
        
        System.out.println("DEBUG: Loading salary data for month " + thang + "/" + nam);
        allSalaries = salaryDAO.getSalariesByMonthYear(thang, nam);
        System.out.println("DEBUG: Retrieved " + allSalaries.size() + " salary records");
        
        if (allSalaries.isEmpty()) {
            System.out.println("DEBUG: No data for current month, loading all salaries");
            allSalaries = salaryDAO.getAllSalaries();
            System.out.println("DEBUG: Retrieved " + allSalaries.size() + " total salary records");
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
}