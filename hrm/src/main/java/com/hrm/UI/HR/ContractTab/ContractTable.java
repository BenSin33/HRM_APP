package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import com.formdev.flatlaf.FlatClientProperties;
import com.hrm.DAO.ContractDAO;
import com.hrm.DTO.ContractDTO;
import com.hrm.UI.component.CRUDDialog;

public class ContractTable extends JPanel {
    private JTable contractTable;
    private DefaultTableModel tableModel;
    private int hoveredRow = -1;
    private ContractTableRenderer renderer;
    private ContractDAO contractDAO;
    private DecimalFormat df = new DecimalFormat("#,###");
    private ContractManagement parentPanel;

    public ContractTable() {
        setLayout(new BorderLayout());
        setOpaque(false);
        contractDAO = new ContractDAO();
        initComponent();
    }

    public void setParentPanel(ContractManagement parentPanel) {
        this.parentPanel = parentPanel;
    }

    private void initComponent() {
        String[] columnNames = {"MÃ HD", "NHÂN VIÊN", "LOẠI HỢP ĐỒNG", "THỜI HẠN", "LƯƠNG", "TRẠNG THÁI", "THAO TÁC"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Load dữ liệu từ database
        loadContractData();

        // Tạo bảng với override prepareRenderer
        contractTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer cellRenderer, int row, int column) {
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
        
        // Tìm hợp đồng từ database
        ContractDTO contract = contractDAO.getContractByMa(contractId.toString());
        if (contract == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tạo form edit
        ContractEditForm editForm = new ContractEditForm();
        CRUDDialog<ContractDTO> dialog = new CRUDDialog<>(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            "Cập nhật hợp đồng",
            editForm,
            contract
        );
        
        dialog.setVisible(true);
        
        ContractDTO updatedContract = dialog.getResult();
        if (updatedContract != null) {
            if (contractDAO.updateContract(updatedContract)) {
                JOptionPane.showMessageDialog(this, "Cập nhật hợp đồng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (parentPanel != null) {
                    parentPanel.refreshData();
                } else {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDelete(Object contractId) {
        System.out.println("Xóa hợp đồng: " + contractId);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn chắc chắn muốn xóa hợp đồng: " + contractId + "?",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (contractDAO.deleteContract(contractId.toString())) {
                int row = contractTable.getSelectedRow();
                if (row != -1) {
                    tableModel.removeRow(row);
                }
                JOptionPane.showMessageDialog(this, "Xóa hợp đồng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                if (parentPanel != null) {
                    parentPanel.refreshData();
                } else {
                    refreshData();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadContractData() {
        List<ContractDTO> contracts = contractDAO.getAllContracts();
        
        for (ContractDTO contract : contracts) {
            LocalDate today = LocalDate.now();
            String trangThai = contract.trangThai;
            
            String ngayKy = contract.ngayLamHopDong != null ? 
                contract.ngayLamHopDong.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
            String ngayHetHan = contract.hanHopDong != null ? 
                contract.hanHopDong.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";
            
            tableModel.addRow(new Object[]{
                contract.maHopDong,
                contract.hoTen + "\n" + contract.maNV + " - " + contract.phongBan,
                contract.loaiHopDong,
                ngayKy + "\n" + ngayHetHan,
                df.format(contract.luongCoBan != null ? contract.luongCoBan.doubleValue() : 0) + " đ",
                trangThai,
                ""
            });
        }
    }

    // Public method để refresh dữ liệu từ Tab
    public void refreshData() {
        tableModel.setRowCount(0);
        loadContractData();
    }
}
