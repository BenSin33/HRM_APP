package com.hrm.UI.HR.Overview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import com.hrm.DAO.HR.ActivityDAO;
import com.hrm.DAO.HR.DepartmentDAO;
import com.hrm.DAO.HR.OverviewDAO;
import com.hrm.DAO.HR.TaskDAO;
import com.hrm.DTO.HR.ActivityDTO;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.DTO.HR.OverviewDTO;
import com.hrm.DTO.HR.TaskDTO;

public class DashboardOverview extends JPanel {
    private OverviewDAO overviewDAO = new OverviewDAO();
    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private ActivityDAO activityDAO = new ActivityDAO();
    private TaskDAO taskDAO = new TaskDAO();

    public DashboardOverview() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createMainContent(), BorderLayout.CENTER);
    }

    // ================= MAIN =================
    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        // Header
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Tổng quan");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel sub = new JLabel("Xin chào! Đây là bảng điều khiển của bạn.");
        sub.setForeground(Color.DARK_GRAY);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        header.add(title);
        header.add(Box.createVerticalStrut(5));
        header.add(sub);

        // Cards - fetch from database
        JPanel cards = new JPanel(new GridLayout(1, 4, 20, 0));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(20, 0, 20, 0));

        OverviewDTO overview = overviewDAO.getOverview();
        cards.add(createStatCard("Tổng nhân viên", String.valueOf(overview.getTotalEmployees()), new Color(59, 130, 246)));
        cards.add(createStatCard("Đang làm việc", String.valueOf(overview.getWorkingEmployees()), new Color(34, 197, 94)));
        cards.add(createStatCard("Nghỉ phép hôm nay", String.valueOf(overview.getOnLeaveToday()), new Color(234, 179, 8)));
        
        // Format salary
        BigDecimal salary = overview.getTotalSalaryThisMonth();
        String formattedSalary = formatSalary(salary);
        cards.add(createStatCard("Tổng lương tháng", formattedSalary, new Color(168, 85, 247)));

        // Bottom panels
        JPanel bottom = new JPanel(new GridLayout(1, 3, 20, 0));
        bottom.setOpaque(false);

        bottom.add(createActivityPanel());
        bottom.add(createTaskPanel());
        bottom.add(createDepartmentPanel());

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(cards, BorderLayout.NORTH);
        wrapper.add(bottom, BorderLayout.CENTER);

        main.add(header, BorderLayout.NORTH);
        main.add(wrapper, BorderLayout.CENTER);

        return main;
    }

    private String formatSalary(BigDecimal salary) {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        if (salary.compareTo(new BigDecimal(1_000_000_000)) >= 0) {
            BigDecimal billion = salary.divide(new BigDecimal(1_000_000_000));
            return billion.toPlainString() + " tỷ";
        } else if (salary.compareTo(new BigDecimal(1_000_000)) >= 0) {
            BigDecimal million = salary.divide(new BigDecimal(1_000_000));
            return million.toPlainString() + " triệu";
        }
        return salary.toPlainString();
    }

    private ImageIcon loadDeleteIcon() {
        java.net.URL url = getClass().getResource("/icons/delete_button.png");
        if (url == null) {
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // ================= STAT CARD =================
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lbValue = new JLabel(value);
        lbValue.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lbValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lbTitle = new JLabel(title);
        lbTitle.setForeground(Color.DARK_GRAY);
        lbTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));

        card.add(lbValue);
        card.add(Box.createVerticalStrut(8));
        card.add(lbTitle);
        card.add(Box.createVerticalStrut(12));
        card.add(colorBar);

        return card;
    }

    // ================= ACTIVITY =================
    private JPanel createActivityPanel() {
        WhitePanel wp = createWhitePanel("Hoạt động gần đây");
        JPanel content = wp.content;
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Load activities from DAO
        List<ActivityDTO> activities = activityDAO.getAll();
        for (ActivityDTO activity : activities) {
            content.add(makeActivityBullet(activity));
        }

        content.add(Box.createVerticalStrut(10));

        // Add button
        JButton addBtn = new JButton("+ Thêm hoạt động");
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(new Color(59, 130, 246));
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Nhập nội dung hoạt động:");
            if (input != null && !input.trim().isEmpty()) {
                ActivityDTO newActivity = new ActivityDTO(input.trim());
                activityDAO.add(newActivity);
                content.add(makeActivityBullet(newActivity), content.getComponentCount() - 2);
                content.revalidate();
                content.repaint();
            }
        });

        content.add(addBtn);

        wp.wrapper.remove(wp.content);
        wp.wrapper.add(scrollPane, BorderLayout.CENTER);

        return wp.wrapper;
    }

    private JPanel makeActivityBullet(ActivityDTO activity) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JLabel label = new JLabel("• " + activity.getContent());
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(Color.BLACK);

        JButton deleteBtn = new JButton(loadDeleteIcon());
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorder(null);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(20, 20));
        deleteBtn.addActionListener(e -> {
            activityDAO.delete(activity.getId());
            ((JPanel) panel.getParent()).remove(panel);
            ((JPanel) panel.getParent()).revalidate();
            ((JPanel) panel.getParent()).repaint();
        });

        panel.add(label);
        panel.add(Box.createHorizontalGlue());
        panel.add(deleteBtn);
        panel.setBorder(new EmptyBorder(3, 0, 3, 0));

        return panel;
    }

    // ================= TASK =================
    private JPanel createTaskPanel() {
        WhitePanel wp = createWhitePanel("Công việc cần xử lý");
        JPanel content = wp.content;
        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);

        // Load tasks from DAO
        List<TaskDTO> tasks = taskDAO.getAll();
        for (TaskDTO task : tasks) {
            content.add(makeTaskCheckBox(task));
        }

        content.add(Box.createVerticalStrut(10));

        // Add button
        JButton addBtn = new JButton("+ Thêm công việc");
        addBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(new Color(59, 130, 246));
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        addBtn.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(this, "Nhập nội dung công việc:");
            if (input != null && !input.trim().isEmpty()) {
                TaskDTO newTask = new TaskDTO(input.trim());
                taskDAO.add(newTask);
                content.add(makeTaskCheckBox(newTask), content.getComponentCount() - 2);
                content.revalidate();
                content.repaint();
            }
        });

        content.add(addBtn);

        wp.wrapper.remove(wp.content);
        wp.wrapper.add(scrollPane, BorderLayout.CENTER);

        return wp.wrapper;
    }

    private JPanel makeTaskCheckBox(TaskDTO task) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);

        JCheckBox cb = new JCheckBox(task.getContent(), task.isCompleted());
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(Color.BLACK);
        cb.setOpaque(false);
        cb.setBorder(new EmptyBorder(3, 0, 3, 0));
        cb.addActionListener(e -> {
            taskDAO.updateCompleted(task.getId(), cb.isSelected());
        });

        JButton deleteBtn = new JButton(loadDeleteIcon());
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorder(null);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        deleteBtn.setPreferredSize(new Dimension(20, 20));
        deleteBtn.addActionListener(e -> {
            taskDAO.delete(task.getId());
            ((JPanel) panel.getParent()).remove(panel);
            ((JPanel) panel.getParent()).revalidate();
            ((JPanel) panel.getParent()).repaint();
        });

        panel.add(cb);
        panel.add(Box.createHorizontalGlue());
        panel.add(deleteBtn);

        return panel;
    }

    // ================= DEPARTMENT =================
    private JPanel createDepartmentPanel() {
        WhitePanel wp = createWhitePanel("Tổng quan phòng ban");
        JPanel content = wp.content;

        // Load departments from database and count employees
        List<DepartmentDTO> departments = departmentDAO.getAll();
        for (DepartmentDTO dept : departments) {
            int empCount = departmentDAO.countEmployees(dept.getMaPhongBan());
            content.add(makeBullet(dept.getTenPhongBan() + ": " + empCount));
        }

        return wp.wrapper;
    }

    // ===== helper class =====
    private static class WhitePanel {
        JPanel wrapper;
        JPanel content;
    }

    private WhitePanel createWhitePanel(String title) {
        WhitePanel wp = new WhitePanel();

        wp.wrapper = new JPanel(new BorderLayout());
        wp.wrapper.setBackground(Color.WHITE);
        wp.wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lbTitle = new JLabel(title);
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));

        wp.content = new JPanel();
        wp.content.setOpaque(false);
        wp.content.setLayout(new BoxLayout(wp.content, BoxLayout.Y_AXIS));
        wp.content.setBorder(new EmptyBorder(10, 0, 0, 0));

        wp.wrapper.add(lbTitle, BorderLayout.NORTH);
        wp.wrapper.add(wp.content, BorderLayout.CENTER);

        return wp;
    }

    private JLabel makeBullet(String text) {
        JLabel label = new JLabel("• " + text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(Color.BLACK);
        label.setBorder(new EmptyBorder(3, 0, 3, 0));
        return label;
    }
}