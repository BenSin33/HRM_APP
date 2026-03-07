package com.hrm.UI.Manager.team;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;


public class TeamHeader extends JPanel {
    private JLabel title;
    private JLabel subtitle;
    private JButton btnAdd;
    private JTextField searchField;

    public TeamHeader() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Left panel with title
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);

        title = new JLabel("Đội nhóm - Phòng DEPT001");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        subtitle = new JLabel("Danh sách thành viên trong team");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        leftPanel.add(title);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        leftPanel.add(subtitle);

        // Right panel with search and add button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);

        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "Tìm kiếm...");
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnAdd = new JButton("➕ Thêm nhân viên");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setBackground(new Color(99, 102, 241));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorderPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        rightPanel.add(searchField);
        rightPanel.add(btnAdd);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    public void setAddButtonListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }

    public String getSearchText() {
        return searchField.getText();
    }
}
