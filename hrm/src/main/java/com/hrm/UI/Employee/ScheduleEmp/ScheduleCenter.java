package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.hrm.Service.Employee.ScheduleService;
import com.hrm.DTO.Employee.ScheduleDTO;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.ZoneId;

public class ScheduleCenter extends JPanel {
    private String manv;
    private ScheduleService dao;

    public ScheduleCenter(LocalDate weekStart, String manv, Runnable onPrev, Runnable onNext) {
        this.manv = manv;
        this.dao = new ScheduleService();
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(248, 249, 250));
        
        // PHẦN 1: Lịch tuần
        JPanel weekPanel = createWeekPanel(weekStart, manv, onPrev, onNext);
        weekPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));
        add(weekPanel);
        
        // PHẦN 2: Tìm kiếm theo ngày
        JPanel searchPanel = createSearchPanel(manv);
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        add(searchPanel);
        
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 560));
    }

    // PHẦN 1: Tạo panel lịch tuần
    private JPanel createWeekPanel(LocalDate weekStart, String manv, Runnable onPrev, Runnable onNext) {
        JPanel weekPanel = new JPanel();
        weekPanel.setLayout(new GridLayout(1, 7, 15, 0));
        weekPanel.setBackground(new Color(248, 249, 250));
        weekPanel.setBorder(new EmptyBorder(10, 25, 20, 25));
        
        List<ScheduleDTO> schedules = dao.getSchedulesForEmployeeAndWeek(manv, weekStart);
        String[] days = {"THỨ HAI", "THỨ BA", "THỨ TƯ", "THỨ NĂM", "THỨ SÁU", "THỨ BẢY", "CHỦ NHẬT"};
        SimpleDateFormat dayNumFormat = new SimpleDateFormat("dd/MM");
        
        LocalDate monday = weekStart;
        if (weekStart.getDayOfWeek().getValue() != 1) {
            monday = weekStart.minusDays((weekStart.getDayOfWeek().getValue() + 6) % 7);
        }
        
        LocalDate[] weekDays = new LocalDate[7];
        for (int i = 0; i < 7; i++) {
            weekDays[i] = monday.plusDays(i);
        }
        
        LocalDate[] orderedDays = new LocalDate[7];
        for (int i = 0; i < 6; i++) {
            orderedDays[i] = weekDays[i];
        }
        orderedDays[6] = weekDays[0].plusDays(6);

        for (int i = 0; i < 7; i++) {
            LocalDate d = orderedDays[i];
            Date date = Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
            ScheduleDTO found = null;
            for (ScheduleDTO s : schedules) {
                if (sameDay(s.getDate(), date)) {
                    found = s;
                    break;
                }
            }
            JPanel dayPanel = new JPanel();
            dayPanel.setLayout(new BorderLayout());
            dayPanel.setOpaque(false);
            dayPanel.setPreferredSize(new Dimension(150, 260));
            dayPanel.setMaximumSize(new Dimension(150, 260));
            
            JPanel card = null;
            if (found != null) {
                String start = found.getStartTime();
                String end = found.getEndTime();
                String time = "-";
                if (start != null && end != null) {
                    if (start.length() >= 5 && end.length() >= 5) {
                        time = start.substring(0,5) + " - " + end.substring(0,5);
                    } else {
                        time = start + " - " + end;
                    }
                }
                String note = found.getDescription() != null ? found.getDescription() : "";
                card = createDayCard(days[i], dayNumFormat.format(date), found.getShift(), found.getShiftName(), time, true, note);
            } else {
                card = createDayCard(days[i], dayNumFormat.format(date), "OFF", "Nghỉ", "-", false, "");
            }
            
            if (i == 0) {
                JButton btnPrev = new JButton("<");
                btnPrev.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btnPrev.setFocusPainted(false);
                btnPrev.setPreferredSize(new Dimension(28, 28));
                btnPrev.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                btnPrev.addActionListener(e -> onPrev.run());
                dayPanel.add(btnPrev, BorderLayout.WEST);
            }
            dayPanel.add(card, BorderLayout.CENTER);
            if (i == 6) {
                JButton btnNext = new JButton(">");
                btnNext.setFont(new Font("Segoe UI", Font.BOLD, 16));
                btnNext.setFocusPainted(false);
                btnNext.setPreferredSize(new Dimension(28, 28));
                btnNext.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
                btnNext.addActionListener(e -> onNext.run());
                dayPanel.add(btnNext, BorderLayout.EAST);
            }
            weekPanel.add(dayPanel);
        }
        return weekPanel;
    }

    // PHẦN 2: Tạo panel tìm kiếm theo ngày
    private JPanel createSearchPanel(String manv) {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(new Color(248, 249, 250));
        searchPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        
        // Thanh công cụ tìm kiếm
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolPanel.setOpaque(false);
        
        JLabel lblFind = new JLabel("Tìm lịch:");
        lblFind.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        SpinnerNumberModel dayModel = new SpinnerNumberModel(LocalDate.now().getDayOfMonth(), 1, 31, 1);
        JSpinner spDay = new JSpinner(dayModel);
        spDay.setPreferredSize(new Dimension(50, 25));
        
        SpinnerNumberModel monthModel = new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1);
        JSpinner spMonth = new JSpinner(monthModel);
        spMonth.setPreferredSize(new Dimension(50, 25));
        
        SpinnerNumberModel yearModel = new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2050, 1);
        JSpinner spYear = new JSpinner(yearModel);
        spYear.setPreferredSize(new Dimension(70, 25));
        
        JButton btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSearch.setPreferredSize(new Dimension(90, 25));
        
        JTable resultTable = new JTable();
        resultTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultTable.setRowHeight(40);
        resultTable.setBackground(Color.WHITE);
        resultTable.setGridColor(new Color(220, 220, 220));
        resultTable.setShowGrid(true);
        resultTable.setIntercellSpacing(new Dimension(1, 1));
        
        // Căn giữa nội dung bảng
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Ngày", "Thứ", "Ca làm", "Tên ca", "Giờ làm việc", "Ghi chú"}, 0);
        resultTable.setModel(tableModel);
        
        // Apply center renderer to all columns
        for (int i = 0; i < tableModel.getColumnCount(); i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 100));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        btnSearch.addActionListener(e -> {
            try {
                int day = (Integer) spDay.getValue();
                int month = (Integer) spMonth.getValue();
                int year = (Integer) spYear.getValue();
                
                loadScheduleForDate(LocalDate.of(year, month, day), tableModel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        toolPanel.add(lblFind);
        toolPanel.add(new JLabel("Ngày:"));
        toolPanel.add(spDay);
        toolPanel.add(new JLabel("Tháng:"));
        toolPanel.add(spMonth);
        toolPanel.add(new JLabel("Năm:"));
        toolPanel.add(spYear);
        toolPanel.add(btnSearch);
        
        searchPanel.add(toolPanel);
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(scrollPane);
        
        // Hiển thị lịch làm việc hôm nay mặc định
        loadScheduleForDate(LocalDate.now(), tableModel);
        
        return searchPanel;
    }

    private void loadScheduleForDate(LocalDate searchDate, DefaultTableModel tableModel) {
        ScheduleDTO schedule = dao.getScheduleByEmployeeAndDate(manv, searchDate);
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = Date.from(searchDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        String dateStr = sdf.format(date);
        String dayOfWeek = getDayOfWeekVN(searchDate.getDayOfWeek());

        if (schedule != null) {
            String start = schedule.getStartTime() != null && schedule.getStartTime().length() >= 5 
                ? schedule.getStartTime().substring(0, 5) : "-";
            String end = schedule.getEndTime() != null && schedule.getEndTime().length() >= 5 
                ? schedule.getEndTime().substring(0, 5) : "-";
            String time = start + " - " + end;
            String note = schedule.getDescription() != null ? schedule.getDescription() : "";
            tableModel.addRow(new Object[]{dateStr, dayOfWeek, schedule.getShift(), schedule.getShiftName(), time, note});
        } else {
            tableModel.addRow(new Object[]{dateStr, dayOfWeek, "-", "Không có lịch làm việc", "-", ""});
        }
    }

    private String getDayOfWeekVN(DayOfWeek dow) {
        switch (dow) {
            case MONDAY: return "Thứ Hai";
            case TUESDAY: return "Thứ Ba";
            case WEDNESDAY: return "Thứ Tư";
            case THURSDAY: return "Thứ Năm";
            case FRIDAY: return "Thứ Sáu";
            case SATURDAY: return "Thứ Bảy";
            case SUNDAY: return "Chủ Nhật";
            default: return "";
        }
    }

    private boolean sameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
            && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)
            && c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private JPanel createDayCard(String dayName, String dayNum, String code, String shiftName, String time, boolean isWorkDay, String note) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235)));
        card.setMaximumSize(new Dimension(140, 260));
        card.setPreferredSize(new Dimension(140, 260));

        JLabel lblDay = new JLabel(dayName);
        lblDay.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDay.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDay.setForeground(Color.GRAY);
        lblDay.setBorder(new EmptyBorder(10, 0, 4, 0));

        JLabel lblNum = new JLabel(dayNum);
        lblNum.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblNum.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblNum.setBorder(new EmptyBorder(0, 0, 10, 0));

        Color[] bgColors = {new Color(255, 251, 235), new Color(239, 246, 255), new Color(245, 243, 255), new Color(236, 253, 245), new Color(255, 237, 237), new Color(237, 255, 245), new Color(237, 241, 255)};
        Color[] fgColors = {new Color(245, 158, 11), new Color(59, 130, 246), new Color(139, 92, 246), new Color(16, 185, 129), new Color(220, 38, 38), new Color(13, 148, 136), new Color(59, 130, 246)};
        int colorIdx = 0;
        if (isWorkDay) {
            if (code != null && !code.equals("")) {
                colorIdx = Math.abs(code.hashCode()) % bgColors.length;
            }
        }

        JPanel shiftInfo = new JPanel();
        shiftInfo.setLayout(new BoxLayout(shiftInfo, BoxLayout.Y_AXIS));
        shiftInfo.setPreferredSize(new Dimension(140, 120));
        shiftInfo.setMaximumSize(new Dimension(140, 120));
        if (isWorkDay) {
            shiftInfo.setBackground(bgColors[colorIdx]);
            JLabel lblCode = new JLabel(code, SwingConstants.CENTER);
            lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblCode.setForeground(fgColors[colorIdx]);
            lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblCode.setBorder(new EmptyBorder(0, 0, 4, 0));
            JLabel lblName = new JLabel(shiftName, SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblName.setBorder(new EmptyBorder(0, 0, 4, 0));
            JLabel lblTime = new JLabel(time, SwingConstants.CENTER);
            lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblTime.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblTime.setVisible(true);
            shiftInfo.add(lblCode);
            shiftInfo.add(lblName);
            shiftInfo.add(lblTime);

            JLabel lblNote = new JLabel();
            lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            lblNote.setForeground(new Color(120, 120, 120));
            lblNote.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblNote.setText("<html><body style='width:130px;word-wrap:break-word;'>" + note.replace("\n", "<br>") + "</body></html>");
            lblNote.setBorder(new EmptyBorder(0, 6, 0, 10));
            shiftInfo.add(lblNote);
        } else {
            shiftInfo.setBackground(new Color(243, 244, 246));
            JLabel lblCode = new JLabel(code, SwingConstants.CENTER);
            lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lblCode.setForeground(Color.GRAY);
            lblCode.setAlignmentX(Component.CENTER_ALIGNMENT);
            lblCode.setBorder(new EmptyBorder(0, 0, 2, 0));
            JLabel lblName = new JLabel("Nghỉ", SwingConstants.CENTER);
            lblName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
            shiftInfo.add(lblCode);
            shiftInfo.add(lblName);
            shiftInfo.add(Box.createVerticalStrut(10));
        }

        card.add(lblDay);
        card.add(lblNum);
        card.add(shiftInfo);
        return card;
    }
}