package com.hrm.UI.Manager.evaluation;

import com.hrm.DAO.HR.EvaluationDAO;
import com.hrm.DTO.HR.EvaluationPeriodDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EvaluationHeader extends JPanel {
    private JComboBox<String> quarterCombo;
    private List<EvaluationPeriodDTO> periods;
    private Runnable onQuarterChanged;
    
    public EvaluationHeader() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("Đánh giá nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Chọn nhân viên để bắt đầu đánh giá");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitle);

        // Panel phải: ComboBox chọn kỳ đánh giá
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(Color.WHITE);
        
        JLabel quarterLabel = new JLabel("Kỳ đánh giá:");
        quarterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        quarterCombo = new JComboBox<>();
        quarterCombo.setPreferredSize(new Dimension(180, 32));
        quarterCombo.addActionListener(e -> {
            if (onQuarterChanged != null) {
                onQuarterChanged.run();
            }
        });
        
        // Load các kỳ từ database
        loadQuarters();
        
        rightPanel.add(quarterLabel);
        rightPanel.add(quarterCombo);

        add(titlePanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }
    
    private void loadQuarters() {
        try {
            EvaluationDAO periodDAO = new EvaluationDAO();
            periods = periodDAO.getAllPeriods();
            
            for (EvaluationPeriodDTO period : periods) {
                quarterCombo.addItem(period.getLabel());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public String getSelectedMaDot() {
        int idx = quarterCombo.getSelectedIndex();
        if (idx >= 0 && idx < periods.size()) {
            return periods.get(idx).getMaDot();
        }
        return "";
    }
    
    public void setOnQuarterChanged(Runnable callback) {
        this.onQuarterChanged = callback;
    }
}
