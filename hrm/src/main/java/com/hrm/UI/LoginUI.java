package com.hrm.UI;

import javax.swing.*;

import com.hrm.DAO.UserDAO;
import com.hrm.utils.IconResize;

import java.awt.*;
import java.net.URL;
import com.hrm.UI.HR.*;

public class LoginUI extends JFrame{

    ImageIcon icon = new ImageIcon("HRM_Icon");

    URL url;
    
    public LoginUI(){

        url = getClass().getResource("/icons/HRM_Icon.png");
        ImageIcon icon = (url != null) ? new ImageIcon(url) : new ImageIcon();
        
        this.setTitle("Login to HRM System");
        this.setSize(800, 500);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        JLabel AppIcon = new JLabel();
        AppIcon.setIcon(IconResize.resizeIcon(icon, 100, 100));
        
        
        // 
        JPanel LeftPanel = new JPanel();
        
        LeftPanel.setBackground(new Color(102,0,204));
        LeftPanel.setPreferredSize(new Dimension (350,500));
        LeftPanel.setLayout(new GridBagLayout());

        JLabel LogoLabel = new JLabel("HRM SYSTEM");
        LogoLabel.setFont ( new Font("Times new roman", Font.BOLD, 28));
        LogoLabel.setForeground(Color.white);

        LeftPanel.add(LogoLabel);
        LeftPanel.add(AppIcon);

        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.white);
        rightPanel.setLayout(null);

        JLabel WelcomeLabel = new JLabel("Welcome to HRM system");
        WelcomeLabel.setFont(new Font ("Times new roman", Font.BOLD, 24));
        WelcomeLabel.setBounds(50,80,300,30);
        rightPanel.add(WelcomeLabel);

        JLabel UserLabel = new JLabel("Username");
        UserLabel.setBounds(50,150,150,20);
        rightPanel.add(UserLabel);

        JTextField txtUserName = new JTextField();
        txtUserName.setBounds(50,175,300,40);
        rightPanel.add(txtUserName);

        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setBounds(50, 230, 150, 20);
        rightPanel.add(lblPass);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBounds(50, 255, 300, 40);
        rightPanel.add(txtPass);

        JButton btnLogin = new JButton("Đăng nhập");
        btnLogin.setBackground(new Color(102, 0, 204));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(50, 320, 300, 45);

        btnLogin.addActionListener(e -> {
            String user = txtUserName.getText();
            String pass = new String (txtPass.getPassword());

            if(user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tài khoản và mật khẩu");
                return;
            }

            UserDAO userDAO = new UserDAO();
            String[] info = userDAO.authenticate(user, pass);

            if( info != null){

                String manv = info[0];
                String roleId = info[1];

                if(roleId.equals("R1")){

                    new HRDashboard();
                    JOptionPane.showMessageDialog(this, "Xin chào quản trị viên:" + manv);
                } else {
                    JOptionPane.showMessageDialog(this, "Xin chào nhân viên:" + manv);
                }
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không đúng!");
            }
        });

        rightPanel.add(btnLogin);

        // Thêm vào Frame chính
        add(LeftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);


        this.setVisible(true);

    }

}
