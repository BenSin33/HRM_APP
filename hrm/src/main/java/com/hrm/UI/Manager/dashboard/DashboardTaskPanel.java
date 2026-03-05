package com.hrm.UI.Manager.dashboard;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;

public class DashboardTaskPanel extends JPanel {
    private JPanel tasksList;

    public DashboardTaskPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JLabel title = new JLabel("Cần xử lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        tasksList = new JPanel();
        tasksList.setLayout(new BoxLayout(tasksList, BoxLayout.Y_AXIS));
        tasksList.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tasksList);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addTask(String employee, String description, String priority) {
        JPanel row = createTaskRow(employee, description, priority);
        tasksList.add(row);
        tasksList.add(Box.createRigidArea(new Dimension(0, 10)));
        tasksList.revalidate();
        tasksList.repaint();
    }

    private JPanel createTaskRow(String employee, String description, String priority) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Priority dot
        Color priorityColor;
        switch (priority) {
            case "Cao": priorityColor = new Color(239, 68, 68); break;
            case "Trung bình": priorityColor = new Color(251, 146, 60); break;
            default: priorityColor = new Color(34, 197, 94); break;
        }

        JPanel priorityDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(priorityColor);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        priorityDot.setPreferredSize(new Dimension(8, 8));
        priorityDot.setOpaque(false);

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);

        JLabel empLabel = new JLabel(employee);
        empLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        empLabel.setForeground(new Color(30, 30, 30));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100));

        content.add(empLabel);
        content.add(Box.createRigidArea(new Dimension(0, 2)));
        content.add(descLabel);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 15));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(priorityDot);
        leftPanel.add(content);

        row.add(leftPanel, BorderLayout.WEST);

        return row;
    }
}

