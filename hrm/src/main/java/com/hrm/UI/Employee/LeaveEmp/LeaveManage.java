package com.hrm.UI.Employee.LeaveEmp;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import com.hrm.UI.component.CRUDDialog;
import com.hrm.Service.Employee.LeaveService;

/**
 * Lớp quản lý chính cho tab Nghỉ phép của nhân viên.
 * Kết hợp LeaveHeader (Thống kê/Nút tạo đơn) và LeaveHistory (Danh sách đơn).
 */
public class LeaveManage extends JPanel {
    private String manv;
    private LeaveHeader header;
    private LeaveHistory history;

    public LeaveManage(String manv) {
        this.manv = manv;
        initUI();
    }

    private void initUI() {
        // Thiết lập layout chính
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(248, 249, 250)); // Màu nền xám nhạt đồng bộ thiết kế

        // 1. Khởi tạo các thành phần con
        header = new LeaveHeader(manv);
        history = new LeaveHistory(manv, this::refreshLeaveHistory);
        
        // Kết nối sự kiện cho nút tạo đơn mới
        header.addCreateLeaveListener(e -> openLeaveDialog());

        // 2. Tạo một Panel chứa nội dung chính để có thể cuộn
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(248, 249, 250));

        // Thêm Header vào phía trên của container (hoặc NORTH của BorderLayout tùy sở thích)
        // Ở đây ta thêm Header vào NORTH để nó luôn cố định khi cuộn lịch sử
        
        // 3. Sử dụng JScrollPane cho phần Lịch sử đơn
        JScrollPane scrollPane = new JScrollPane(history);
        scrollPane.setBorder(null); // Xóa viền của scrollpane
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Cuộn mượt hơn
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        // 4. Sắp xếp các thành phần vào Layout chính
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void openLeaveDialog() {
        LeaveFormPanel formPanel = new LeaveFormPanel();
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        CRUDDialog<Map<String, Object>> dialog = new CRUDDialog<>(frame, "Tạo đơn nghỉ phép", formPanel, null);
        dialog.setVisible(true);

        Map<String, Object> result = dialog.getResult();
        if (result != null) {
            LeaveService dao = new LeaveService();
            result.put("manghiphep", dao.generateLeaveRequestId());
            result.put("manv", manv);
            if (dao.insertLeaveRequest(result)) {
                JOptionPane.showMessageDialog(this, "Gửi đơn nghỉ phép thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshLeaveHistory();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi gửi đơn! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshLeaveHistory() {
        // Tải lại dữ liệu từ database
        history = new LeaveHistory(manv, this::refreshLeaveHistory);
        
        // Xóa các thành phần cũ và thêm lại
        removeAll();
        
        JScrollPane scrollPane = new JScrollPane(history);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));
        
        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }
    
    /**
     * Phương thức làm mới dữ liệu (gọi sau khi tạo đơn mới thành công)
     */
    public void refreshData() {
        // Xóa nội dung cũ
        removeAll();
        
        // Khởi tạo lại các thành phần với dữ liệu mới từ DB
        header = new LeaveHeader(manv);
        history = new LeaveHistory(manv);
        
        // Thiết lập lại giao diện
        initUI();
        
        // Vẽ lại
        revalidate();
        repaint();
    }
    
    // Getter để các lớp con có thể truy cập mã nhân viên nếu cần
    public String getManv() {
        return manv;
    }
}