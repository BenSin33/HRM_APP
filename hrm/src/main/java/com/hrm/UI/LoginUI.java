package com.hrm.UI;

import javax.swing.*;

import com.hrm.Service.AuthenticationService;
import com.hrm.DTO.UserDTO;
import com.hrm.utils.IconResize;

import java.awt.*;
import java.net.URL;

import com.hrm.UI.Employee.EDashboard;

import com.hrm.UI.HR.*;
//import com.hrm.UI.Manager.ManagerDashboard;

public class LoginUI extends JFrame{

    ImageIcon Logo = new ImageIcon("HRM_Icon");

    URL url;
    
    public LoginUI() {
    // 1. Khởi tạo dữ liệu ảnh
    url = getClass().getResource("/icons/HRM_Logo.png");
    ImageIcon logoIcon = (url != null) ? new ImageIcon(url) : new ImageIcon();
    
    // 2. Cấu hình Frame chính
    this.setTitle("Login to HRM System");
    this.setSize(800, 500);
    this.setResizable(false); // Thường Login nên để cố định size cho đẹp
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);
    this.setLayout(new BorderLayout());

    // 3. PANEL BÊN TRÁI (Chứa Logo và Title)
    JPanel leftPanel = new JPanel();
    leftPanel.setBackground(new Color(102, 0, 204));
    leftPanel.setPreferredSize(new Dimension(350, 500));
    
    // Sử dụng GridBagLayout để căn giữa theo chiều dọc
    leftPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();  // Thiết lập chung cho các thành phần bên trong leftPanel
    gbc.gridx = 0; // Luôn nằm ở cột 0
    gbc.insets = new Insets(10, 0, 10, 0); // Khoảng cách giữa các thành phần
    gbc.anchor = GridBagConstraints.CENTER;

    // Logo App (Icon) - Đặt ở hàng 0
    JLabel appIcon = new JLabel();
    appIcon.setIcon(IconResize.resizeIcon(logoIcon, 120, 120)); // Tăng size một chút cho nổi bật
    appIcon.setOpaque(false);
    gbc.gridy = 0; 
    leftPanel.add(appIcon, gbc);

    // Label chữ - Đặt ở hàng 1 (Tự động xuống dưới Logo)
    JLabel logoLabel = new JLabel("HRM SYSTEM");
    logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28)); // Segoe UI trông hiện đại hơn
    logoLabel.setForeground(Color.white);
    gbc.gridy = 1;
    leftPanel.add(logoLabel, gbc);

    // 4. PANEL BÊN PHẢI (Form đăng nhập)
    JPanel rightPanel = new JPanel();
    rightPanel.setBackground(Color.white);
    rightPanel.setLayout(null);

    JLabel welcomeLabel = new JLabel("Welcome to HRM System");
    welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
    welcomeLabel.setBounds(50, 50, 300, 40);
    rightPanel.add(welcomeLabel);

    // Mã nhân viên
    JLabel userLabel = new JLabel("Mã nhân viên (MANV)");
    userLabel.setBounds(50, 130, 150, 20);
    rightPanel.add(userLabel);

    JTextField txtUserName = new JTextField();
    txtUserName.setBounds(50, 155, 300, 40);
    // Tùy chỉnh border cho đẹp hơn
    txtUserName.putClientProperty("JTextField.placeholderText", "Nhập mã nhân viên...");
    rightPanel.add(txtUserName);

    // Password
    JLabel lblPass = new JLabel("Mật khẩu");
    lblPass.setBounds(50, 215, 150, 20);
    rightPanel.add(lblPass);

    JPasswordField txtPass = new JPasswordField();
    txtPass.setBounds(50, 240, 300, 40);
    rightPanel.add(txtPass);

    // Button Đăng nhập
    JButton btnLogin = new JButton("Đăng nhập");
    btnLogin.setBackground(new Color(102, 0, 204));
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setFocusPainted(false);
    btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnLogin.setBounds(50, 310, 300, 45);
    
    // Xử lý sự kiện khi bấm nút Đăng nhập
    btnLogin.addActionListener(e -> {
        String manv = txtUserName.getText();
        String pass = new String(txtPass.getPassword());

        if(manv.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã nhân viên và mật khẩu");
            return;
        }

        // Sử dụng Service để xác thực
        AuthenticationService authService = new AuthenticationService();
        UserDTO userInfo = authService.authenticateByMaNV(manv, pass);

        if(userInfo != null){
            if(authService.isAdmin(userInfo)){
                new HRDashboard();
                JOptionPane.showMessageDialog(this, "Xin chào quản trị viên: " + userInfo.getManv());
            } else if(authService.isManager(userInfo)){
                JOptionPane.showMessageDialog(this, "Xin chào quản lý: " + userInfo.getManv());
                // TODO: Mở Manager Dashboard
                // new ManagerDashboard(userInfo.getManv());
            } else if(authService.isEmployee(userInfo)){
                new EDashboard(userInfo.getManv());
                JOptionPane.showMessageDialog(this, "Xin chào nhân viên: " + userInfo.getManv());
            } else {
                JOptionPane.showMessageDialog(this, "Vai trò không được xác định!");
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Mã nhân viên hoặc mật khẩu không đúng!");
        }
    });
    rightPanel.add(btnLogin);

    // 5. Thêm vào Frame chính
    this.add(leftPanel, BorderLayout.WEST);
    this.add(rightPanel, BorderLayout.CENTER);

    this.setVisible(true);
}

}
