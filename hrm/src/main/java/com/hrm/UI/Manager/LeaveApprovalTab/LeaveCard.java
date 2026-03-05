package com.hrm.UI.Manager.LeaveApprovalTab;
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
    private String ngayGui;

    public LeaveCard(Object[] row) {
        this.ten = row[0].toString();
        this.maNV = row[1].toString();
        this.trangThai = row[2].toString();
        this.loaiNghi = row[3].toString();
        this.soNgay = row[4].toString();
        this.tuNgay = row[5].toString();
        this.denNgay = row[6].toString();
        this.lyDo = row[7].toString();
        this.ngayGui = row[8].toString();

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
        if (trangThai.equals("Chờ duyệt")) {
            topRow.add(createActionButtons(), BorderLayout.EAST);
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

        JButton btnTuChoi = new JButton("✗  Từ chối");
        btnTuChoi.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnTuChoi.setBackground(new Color(239, 68, 68));
        btnTuChoi.setForeground(Color.WHITE);
        btnTuChoi.setFocusPainted(false);
        btnTuChoi.setBorderPainted(false);
        btnTuChoi.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTuChoi.setPreferredSize(new Dimension(120, 38));

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
        infoPanel.add(createInfoItem("Số ngày:", soNgay + " ngày", true));
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
        JLabel ngayGuiLabel = new JLabel("Gửi đơn: " + ngayGui);
        ngayGuiLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        ngayGuiLabel.setForeground(new Color(150, 150, 150));

        centerPanel.add(infoPanel);
        centerPanel.add(lyDoPanel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(ngayGuiLabel);

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
            case "Từ chối":
                label.setBackground(new Color(254, 226, 226));
                label.setForeground(new Color(185, 28, 28));
                break;
        }
    }

    public String getTrangThai() {
        return trangThai;
    }
}

