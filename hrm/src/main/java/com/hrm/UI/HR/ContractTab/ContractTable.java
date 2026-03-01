package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.formdev.flatlaf.FlatClientProperties;

public class ContractTable extends JPanel {
    private JTable contractTable;
    private DefaultTableModel tableModel;
    private int hoveredRow = -1;
    private ContractTableRenderer renderer;

    public ContractTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponent();
    }

    private void initComponent() {
        String[] columnNames = {"MÃ HD", "NHÂN VIÊN", "LOẠI HỢP ĐỒNG", "THỜI HẠN", "LƯƠNG", "TRẠNG THÁI", "THAO TÁC"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Thêm dữ liệu mẫu
        tableModel.addRow(new Object[]{
            "HD001", 
            "Nguyễn Văn A\nNV001 - Senior Developer",
            "Không xác định", 
            "15/1/2023\nKhông xác định",
            "25.000.000 đ", 
            "Hiệu lực",
            ""
        });
        tableModel.addRow(new Object[]{
            "HD002",
            "Trần Thị B\nNV002 - Manager",
            "Không xác định",
            "1/6/2022\nKhông xác định",
            "35.000.000 đ",
            "Hiệu lực",
            ""
        });

        // Tạo bảng với override prepareRenderer
        contractTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer cellRenderer, int row, int column) {
                Component c = super.prepareRenderer(cellRenderer, row, column);
                if (column == 6 && cellRenderer instanceof ContractTableRenderer) {
                    ((ContractTableRenderer)cellRenderer).setHovered(row == hoveredRow);
                }
                return c;
            }
        };
        contractTable.setRowHeight(50);
        contractTable.getTableHeader().setBackground(new Color(241, 245, 249));
        contractTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Cấu hình cột
        renderer = new ContractTableRenderer();
        contractTable.getColumnModel().getColumn(6).setCellRenderer(renderer);
        contractTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        // Xử lý mouse hover
        contractTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = contractTable.rowAtPoint(e.getPoint());
                int col = contractTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 6) {
                    if (hoveredRow != row) {
                        hoveredRow = row;
                        contractTable.repaint();
                    }
                } else {
                    if (hoveredRow != -1) {
                        hoveredRow = -1;
                        contractTable.repaint();
                    }
                }
            }
        });

        // Xử lý mouse click
        contractTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = contractTable.rowAtPoint(e.getPoint());
                int col = contractTable.columnAtPoint(e.getPoint());
                
                if (row != -1 && col == 6) {
                    Object contractId = tableModel.getValueAt(row, 0);
                    Rectangle cellRect = contractTable.getCellRect(row, col, false);
                    int relativeX = e.getX() - (int)cellRect.getX();
                    
                    int buttonWidth = cellRect.width / 2;
                    
                    if (relativeX < buttonWidth) {
                        handleEdit(contractId);
                    } else {
                        handleDelete(contractId);
                    }
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                contractTable.repaint();
            }
        });

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.putClientProperty(FlatClientProperties.STYLE,
        "arc: 15; background: #ffffff; border: 1,1,1,1,#e5e7eb");
        wrap.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(contractTable);
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "border: 0,0,0,0");
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);

        wrap.add(scrollPane);
        add(wrap, BorderLayout.CENTER);
    }

    private void handleEdit(Object contractId) {
        System.out.println("Sửa hợp đồng: " + contractId);
        JOptionPane.showMessageDialog(this, "Sửa hợp đồng: " + contractId);
    }

    private void handleDelete(Object contractId) {
        System.out.println("Xóa hợp đồng: " + contractId);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn chắc chắn muốn xóa hợp đồng: " + contractId + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            int row = contractTable.getSelectedRow();
            if (row != -1) {
                tableModel.removeRow(row);
            }
        }
    }
}
