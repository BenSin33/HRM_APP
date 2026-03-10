package com.hrm.UI.Manager.LeaveApprovalTab;

import com.hrm.Service.NghiPhepService;
import javax.swing.*;
import java.awt.*;

public class LeaveCard extends JPanel {
    private String ten;
    private String maNV;
    private String trangThai;
    private String loaiNghi;
    private String soNgay;
    private String tuNgay;
    private String denNgay;
    private String lyDo;
    private String ngayDuyet;
    private String maNghiPhep;
    
    private NghiPhepService service;
    private Runnable refreshCallback;

    public LeaveCard(Object[] row, NghiPhepService service, Runnable refreshCallback) {
        // Cột theo thứ tự .sql:
        // [0]=MANGHIPHEP, [1]=MANV, [2]=LOAINGHI, [3]=LYDONGHI, 
        // [4]=NGAYNGHI, [5]=NGAYLAMLAI, [6]=NGUOIDUYET, [7]=NGAYDUYET, [8]=TRANGTHAI, [9]=LYDOTUCHOI, [10]=TENNV
        this.maNghiPhep = row[0].toString();      // MANGHIPHEP
        this.maNV = row[1].toString();            // MANV
        this.loaiNghi = row[2].toString();        // LOAINGHI
        this.lyDo = row[3].toString();            // LYDONGHI (lý do nghỉ)
        this.tuNgay = row[4].toString();          // NGAYNGHI (ngày nghỉ)
        this.denNgay = row[5].toString();         // NGAYLAMLAI (ngày làm lại)
        this.soNgay = "";                         // Tính từ tuNgay -> denNgay (tùy chọn)
        this.trangThai = row[8].toString();       // TRANGTHAI
        this.ten = row[10].toString();            // TENNV (for display)
        this.ngayDuyet = "";                      // Không dùng
        
        this.service = service;
        this.refreshCallback = refreshCallback;

        initComponent();
    }

    private void initComponent() {
        setLayout(new BorderLayout(15, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        // TOP ROW
        add(createTopRow(), BorderLayout.NORTH);
        
        // CENTER
        add(createCenterPanel(), BorderLayout.CENTER);
    }

    private JPanel createTopRow() {
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        namePanel.setBackground(Color.WHITE);

        JLabel tenLabel = new JLabel(ten);
        tenLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel maNVLabel = new JLabel("(" + maNV + ")");
        maNVLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        maNVLabel.setForeground(new Color(130, 130, 130));

        JLabel trangThaiLabel = new JLabel(trangThai);
        trangThaiLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trangThaiLabel.setOpaque(true);
        trangThaiLabel.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        setTrangThaiStyle(trangThaiLabel);

        namePanel.add(tenLabel);
        namePanel.add(maNVLabel);
        namePanel.add(trangThaiLabel);
        topRow.add(namePanel, BorderLayout.WEST);

        // Buttons for pending requests
        System.out.println("[LeaveCard] " + ten + " | Trạng thái: [" + trangThai + "]");
        
        if (trangThai != null && trangThai.trim().equals("Chờ duyệt")) {
            System.out.println("[LeaveCard] → Hiện buttons DUYỆT/TỪ CHỐI");
            topRow.add(createActionButtons(), BorderLayout.EAST);
        } else {
            System.out.println("[LeaveCard] → KHÔNG hiện buttons (trạng thái: " + trangThai + ")");
        }

        return topRow;
    }

    private JPanel createActionButtons() {
        JPanel btnPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        btnPanel.setBackground(Color.WHITE);

        JButton btnDuyet = new JButton("✓  Duyệt");
        btnDuyet.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDuyet.setBackground(new Color(34, 197, 94));
        btnDuyet.setForeground(Color.WHITE);
        btnDuyet.setFocusPainted(false);
        btnDuyet.setBorderPainted(false);
        btnDuyet.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDuyet.setPreferredSize(new Dimension(120, 38));
        btnDuyet.addActionListener(e -> handleApprove());

        JButton btnTuChoi = new JButton("✗  Từ chối");
        btnTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTuChoi.setBackground(new Color(239, 68, 68));
        btnTuChoi.setForeground(Color.WHITE);
        btnTuChoi.setFocusPainted(false);
        btnTuChoi.setBorderPainted(false);
        btnTuChoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTuChoi.setPreferredSize(new Dimension(120, 38));
        btnTuChoi.addActionListener(e -> handleReject());

        btnPanel.add(btnDuyet);
        btnPanel.add(btnTuChoi);

        return btnPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);

        // Info panel
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 20, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        infoPanel.add(createInfoItem("Loại nghỉ:", loaiNghi, true));
        infoPanel.add(createInfoItem("Số ngày:", soNgay, true));
        infoPanel.add(createInfoItem("Từ ngày:", tuNgay, true));
        infoPanel.add(createInfoItem("Đến ngày:", denNgay, true));

        // Reason panel
        JPanel lyDoPanel = new JPanel(new BorderLayout());
        lyDoPanel.setBackground(new Color(250, 250, 250));
        lyDoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1, true),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel lyDoTitle = new JLabel("Lý do:");
        lyDoTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lyDoTitle.setForeground(new Color(100, 100, 100));

        JLabel lyDoContent = new JLabel(lyDo);
        lyDoContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lyDoPanel.add(lyDoTitle, BorderLayout.NORTH);
        lyDoPanel.add(lyDoContent, BorderLayout.CENTER);

        // Footer
        JLabel ngayDuyetLabel = new JLabel("Ngày duyệt: " + ngayDuyet);
        ngayDuyetLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        ngayDuyetLabel.setForeground(new Color(150, 150, 150));

        centerPanel.add(infoPanel);
        centerPanel.add(lyDoPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(ngayDuyetLabel);

        return centerPanel;
    }

    private JPanel createInfoItem(String label, String value, boolean valueBold) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(Color.WHITE);

        JLabel labelPart = new JLabel(label);
        labelPart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelPart.setForeground(new Color(100, 100, 100));

        JLabel valuePart = new JLabel(value);
        valuePart.setFont(new Font("Segoe UI", valueBold ? Font.BOLD : Font.PLAIN, 14));

        item.add(labelPart);
        item.add(valuePart);
        return item;
    }

    private void setTrangThaiStyle(JLabel label) {
        switch (trangThai) {
            case "Chờ duyệt":
                label.setBackground(new Color(254, 243, 199));
                label.setForeground(new Color(146, 64, 14));
                break;
            case "Đã duyệt":
                label.setBackground(new Color(220, 252, 231));
                label.setForeground(new Color(22, 101, 52));
                break;
            default:
                label.setBackground(new Color(243, 244, 246));
                label.setForeground(new Color(107, 114, 128));
                break;
        }
    }
    
    private void handleApprove() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn duyệt đơn nghỉ phép của " + ten + "?",
            "Xác nhận duyệt",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            String nguoiDuyet = "Manager"; // TODO: Lấy từ session
            boolean success = service.duyetDon(maNghiPhep, nguoiDuyet);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã duyệt đơn nghỉ phép thành công!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh UI
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi duyệt đơn!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleReject() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc muốn từ chối đơn nghỉ phép của " + ten + "?\n" +
            "Lưu ý: Đơn sẽ bị xóa người duyệt và trở về trạng thái Chờ duyệt.",
            "Xác nhận từ chối",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = service.tuChoiDon(maNghiPhep, "Manager", "Từ chối đơn nghỉ phép"); // TODO: Lấy lý do từ dialog
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Đã từ chối đơn nghỉ phép!", 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh UI
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi từ chối đơn!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public String getTrangThai() {
        return trangThai;
    }
}