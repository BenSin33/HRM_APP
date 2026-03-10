package com.hrm.UI.Manager.evaluation;
import com.hrm.UI.Manager.color.ColorScheme;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EvaluationList extends JPanel {
    private JPanel listPanel;
    private JLabel lblQuarter;
    private List<EvaluationRow> rows;

    public EvaluationList() {
        rows = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true));

        // Header của list
        JPanel listHeader = new JPanel(new BorderLayout());
        listHeader.setBackground(Color.WHITE);
        listHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(18, 25, 18, 25)
        ));

        lblQuarter = new JLabel("Danh sách nhân viên");
        lblQuarter.setFont(new Font("Segoe UI", Font.BOLD, 18));
        listHeader.add(lblQuarter, BorderLayout.WEST);

        // Danh sách nhân viên
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
         scrollPane.getVerticalScrollBar().setUnitIncrement(16);   // Tốc độ khi scroll bằng chuột (mặc định: 1)
        scrollPane.getVerticalScrollBar().setBlockIncrement(100);

        add(listHeader, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setQuarter(String quarter) {
        lblQuarter.setText("Danh sách nhân viên - " + quarter);
    }

    public void setData(Object[][] data) {
        listPanel.removeAll();
        rows.clear();

        for (Object[] row : data) {
            EvaluationRow evalRow = new EvaluationRow(row);
            rows.add(evalRow);
            listPanel.add(evalRow);
            listPanel.add(createDivider());
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JSeparator createDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(240, 240, 240));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public int getDaHoanThanhCount() {
        int count = 0;
        for (EvaluationRow row : rows) {
            if ("Đã hoàn thành".equals(row.getTrangThai())) {
                count++;
            }
        }
        return count;
    }

    public int getChuaDanhGiaCount() {
        int count = 0;
        for (EvaluationRow row : rows) {
            if ("Chưa đánh giá".equals(row.getTrangThai())) {
                count++;
            }
        }
        return count;
    }
}