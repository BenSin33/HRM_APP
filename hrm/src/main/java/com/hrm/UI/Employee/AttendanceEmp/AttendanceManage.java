package com.hrm.UI.Employee.AttendanceEmp;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AttendanceManage extends JPanel {
    private String manv;
    private AttendanceHeader headerPanel;
    private AttendanceHistory calendarPanel;

    public AttendanceManage(String manv) {
        this.manv = manv;
        
        // Thiết lập Layout chính là BorderLayout để các thành phần xếp chồng theo chiều dọc
        setLayout(new BorderLayout(0, 20));
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt đồng bộ
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Khởi tạo phần Header (Bao gồm Tiêu đề, Clock Card và 4 Stat Cards)
        headerPanel = new AttendanceHeader(manv);
        
        // 2. Khởi tạo phần thân dưới (Lịch sử chấm công dạng Lịch)
        // Chúng ta bọc Calendar vào một ScrollPane để hỗ trợ màn hình nhỏ
        calendarPanel = new AttendanceHistory(manv);
        
        // Tạo một container cho phần nội dung phía dưới để có khoảng trắng và tiêu đề riêng nếu cần
        JPanel bodyPanel = new JPanel(new BorderLayout(0, 10));
        bodyPanel.setOpaque(false);
        
        // Thêm thanh tìm kiếm vào phía trên của Lịch
        bodyPanel.add(calendarPanel.createSearchPanel(), BorderLayout.NORTH);
        bodyPanel.add(calendarPanel, BorderLayout.CENTER);

        // 3. Ghép các thành phần vào Panel chính
        add(headerPanel, BorderLayout.NORTH);
        add(bodyPanel, BorderLayout.CENTER);
    }

    // Phương thức để làm mới toàn bộ dữ liệu khi cần (ví dụ sau khi bấm nút Chấm công)
    public void refreshData() {
        removeAll();
        headerPanel = new AttendanceHeader(manv);
        calendarPanel = new AttendanceHistory(manv);
        
        add(headerPanel, BorderLayout.NORTH);
        add(calendarPanel, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
}
