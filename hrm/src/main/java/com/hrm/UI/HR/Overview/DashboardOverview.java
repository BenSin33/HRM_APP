package com.hrm.UI.HR.Overview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
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

    private static final Color[] ACTIVITY_COLORS = {
        new Color(16, 185, 129),   // xanh lá
        new Color(59, 130, 246),   // xanh dương
        new Color(245, 158, 11),   // cam
        new Color(168, 85, 247),   // tím
        new Color(239, 68, 68),    // đỏ
        new Color(34, 211, 238),   // cyan
    };

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

    // ================= STAT CARD =================
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
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
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Hoạt động gần đây");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        List<ActivityDTO> activities = activityDAO.getAll();
        int colorIdx = 0;
        for (ActivityDTO activity : activities) {
            Color dot = ACTIVITY_COLORS[colorIdx % ACTIVITY_COLORS.length];
            content.add(makeActivityItem(activity.getContent(), dot));
            content.add(Box.createVerticalStrut(12));
            colorIdx++;
        }

        // Hiển thị thông báo nếu không có dữ liệu
        if (activities.isEmpty()) {
            JLabel empty = new JLabel("Không có hoạt động nào gần đây");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            content.add(empty);
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel makeActivityItem(String text, Color dotColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 9, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(15, 28));
        dot.setOpaque(false);

        JLabel lblText = new JLabel(text);
        lblText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblText.setForeground(new Color(30, 41, 59));

        p.add(dot, BorderLayout.WEST);
        p.add(lblText, BorderLayout.CENTER);

        return p;
    }

    // ================= TASK =================
    private JPanel createTaskPanel() {
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Công việc cần xử lý");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        List<TaskDTO> tasks = taskDAO.getAll();
        for (TaskDTO task : tasks) {
            content.add(makeTaskCard(task));
            content.add(Box.createVerticalStrut(10));
        }

        // Hiển thị thông báo nếu không có dữ liệu
        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("Không có công việc cần xử lý");
            empty.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            empty.setForeground(Color.GRAY);
            content.add(empty);
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel makeTaskCard(TaskDTO task) {
        JPanel card = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 247, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(8, 12, 8, 12));

        JCheckBox cb = new JCheckBox(task.getContent(), task.isCompleted());
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setForeground(task.isCompleted() ? Color.GRAY : new Color(30, 41, 59));
        cb.setOpaque(false);
        cb.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        card.add(cb, BorderLayout.CENTER);

        return card;
    }

    // ================= DEPARTMENT =================
    private JPanel createDepartmentPanel() {
        JPanel panel = createRoundedPanel();
        panel.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel("Tổng quan phòng ban");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        List<DepartmentDTO> departments = departmentDAO.getAll();
        int colorIdx = 0;
        for (DepartmentDTO dept : departments) {
            int empCount = departmentDAO.countEmployees(dept.getMaPhongBan());
            Color dot = ACTIVITY_COLORS[colorIdx % ACTIVITY_COLORS.length];
            content.add(makeDeptItem(dept.getTenPhongBan(), String.valueOf(empCount), dot));
            content.add(Box.createVerticalStrut(10));
            colorIdx++;
        }

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel makeDeptItem(String deptName, String empCount, Color dotColor) {
        JPanel p = new JPanel(new BorderLayout(10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(240, 247, 255));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(dotColor);
                g2.fillOval(0, 7, 8, 8);
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(15, 24));
        dot.setOpaque(false);

        JLabel lblDept = new JLabel(deptName);
        lblDept.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDept.setForeground(new Color(30, 41, 59));

        JLabel lblCount = new JLabel(empCount + " nhân viên");
        lblCount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCount.setForeground(Color.GRAY);

        JPanel textArea = new JPanel(new GridLayout(2, 1, 0, 0));
        textArea.setOpaque(false);
        textArea.add(lblDept);
        textArea.add(lblCount);

        p.add(dot, BorderLayout.WEST);
        p.add(textArea, BorderLayout.CENTER);

        return p;
    }

    // ================= ROUNDED PANEL (giống HomeReport) =================
    private JPanel createRoundedPanel() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(18, 18, 18, 18));
        return p;
    }
}
