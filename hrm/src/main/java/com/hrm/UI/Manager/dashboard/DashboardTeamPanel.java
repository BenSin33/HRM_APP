package com.hrm.UI.Manager.dashboard;


import javax.swing.*;
import java.awt.*;

public class DashboardTeamPanel extends JPanel {
    private JPanel membersList;

    public DashboardTeamPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel("Thành viên trong team");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        membersList = new JPanel();
        membersList.setLayout(new BoxLayout(membersList, BoxLayout.Y_AXIS));
        membersList.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(membersList);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addMember(String name, String role, String id) {
        JPanel row = createMemberRow(name, role, id);
        membersList.add(row);
        membersList.add(Box.createRigidArea(new Dimension(0, 10)));
        membersList.revalidate();
        membersList.repaint();
    }

    public void clearMembers() {
        if (membersList != null) {
            membersList.removeAll();
            membersList.revalidate();
            membersList.repaint();
        }
    }

    private JPanel createMemberRow(String name, String role, String id) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Avatar
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);

        JLabel avatarLabel = new JLabel(getInitials(name));
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatarLabel.setForeground(Color.WHITE);
        avatar.add(avatarLabel);

        // Info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(30, 30, 30));

        JLabel roleLabel = new JLabel(role + " • " + id);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(100, 100, 100));

        info.add(nameLabel);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(roleLabel);

        row.add(avatar, BorderLayout.WEST);
        row.add(info, BorderLayout.CENTER);

        return row;
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] parts = name.trim().split(" ");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase()
                 + String.valueOf(parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return String.valueOf(name.charAt(0)).toUpperCase();
    }
}
