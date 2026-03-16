package com.hrm.UI.Employee.ScheduleEmp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import com.hrm.DAO.Employee.ScheduleDAO;
import com.hrm.DTO.Employee.ScheduleDTO;

public class ScheduleFooter extends JPanel {
	public ScheduleFooter() {
		setLayout(new BorderLayout());
		setBackground(new Color(248, 249, 250));
		setBorder(new EmptyBorder(10, 25, 30, 25));

		JPanel mainFooter = new JPanel(new BorderLayout());
		mainFooter.setBackground(Color.WHITE);
		mainFooter.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(230, 230, 230)),
			new EmptyBorder(15, 20, 15, 20)
		));

		JLabel lblTitle = new JLabel("Chú thích ca làm việc");
		lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
		lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

		JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
		legendPanel.setOpaque(false);

		// Lấy danh sách ca làm từ database
		ScheduleDAO dao = new ScheduleDAO();
		List<ScheduleDTO> shifts = dao.getAllShifts();
		Color[] bgColors = {new Color(255, 251, 235), new Color(239, 246, 255), new Color(245, 243, 255), new Color(236, 253, 245), new Color(255, 237, 237), new Color(237, 255, 245), new Color(237, 241, 255)};
		Color[] fgColors = {new Color(245, 158, 11), new Color(59, 130, 246), new Color(139, 92, 246), new Color(16, 185, 129), new Color(220, 38, 38), new Color(13, 148, 136), new Color(59, 130, 246)};
		for (int i = 0; i < shifts.size(); i++) {
			ScheduleDTO shift = shifts.get(i);
			String code = shift.getShift();
			String name = shift.getShiftName();
			String time = (shift.getStartTime() != null && shift.getEndTime() != null) ? shift.getStartTime() + " - " + shift.getEndTime() : "";
			Color bg = bgColors[i % bgColors.length];
			Color fg = fgColors[i % fgColors.length];
			legendPanel.add(createLegendItem(code, name, time, bg, fg));
		}

		mainFooter.add(lblTitle, BorderLayout.NORTH);
		mainFooter.add(legendPanel, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane(mainFooter);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		add(scrollPane, BorderLayout.CENTER);
	}

	private JPanel createLegendItem(String code, String name, String time, Color bg, Color text) {
		JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		item.setOpaque(false);

		JLabel lblBox = new JLabel(code, SwingConstants.CENTER);
		lblBox.setPreferredSize(new Dimension(35, 35));
		lblBox.setOpaque(true);
		lblBox.setBackground(bg);
		lblBox.setForeground(text);
		lblBox.setFont(new Font("Segoe UI", Font.BOLD, 12));

		JPanel info = new JPanel(new GridLayout(2, 1));
		info.setOpaque(false);
		JLabel lblName = new JLabel(name);
		lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
		JLabel lblTime = new JLabel(time);
		lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		lblTime.setForeground(Color.GRAY);

		info.add(lblName);
		info.add(lblTime);

		item.add(lblBox);
		item.add(info);
		return item;
	}
}