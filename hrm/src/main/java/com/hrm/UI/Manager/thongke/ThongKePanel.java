package com.hrm.UI.Manager.thongke;

import com.hrm.DTO.ContractDTO;
import com.hrm.Service.ThongKeService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends JPanel {
    private final ThongKeService thongKeService;
    private JTable contractTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JLabel countLabel;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,###");

    public ThongKePanel() {
        this.thongKeService = new ThongKeService();
        setLayout(new BorderLayout(0, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Top Section: Charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsPanel.setBackground(new Color(245, 245, 245));
        chartsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        chartsPanel.add(createChartWrapper("Tỉ lệ chênh lệch", createDiscrepancyChart()));
        chartsPanel.add(createChartWrapper("Tỉ lệ các loại hợp đồng", createTypeDistributionChart()));

        add(chartsPanel, BorderLayout.NORTH);

        // 2. Center Section: Table and Filters
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setBackground(Color.WHITE);

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        filterBar.setBackground(Color.WHITE);

        filterCombo = new JComboBox<>(new String[]{"Tất cả hợp đồng", "Đã ký trong năm", "Hết hạn trong năm"});
        filterCombo.setPreferredSize(new Dimension(200, 30));
        filterCombo.addActionListener(e -> refreshTable());
        filterBar.add(filterCombo);

        countLabel = new JLabel("Số lượng: 0");
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterBar.add(countLabel);

        tableContainer.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"STT", "Mã - Tên nhân viên", "Phòng ban", "Từ ngày", "Đến ngày", "Loại hợp đồng", "Lương cơ bản"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contractTable = new JTable(tableModel);
        contractTable.setRowHeight(35);
        contractTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        contractTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contractTable.setShowVerticalLines(false);
        contractTable.setGridColor(new Color(240, 240, 240));

        // Center align STT column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        contractTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        contractTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        // Right align Salary column
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        contractTable.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(contractTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);

        // Initial load
        refreshTable();
    }

    private JPanel createChartWrapper(String title, JFreeChart chart) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        wrapper.add(lblTitle, BorderLayout.NORTH);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBackground(new Color(245, 245, 245));
        chartPanel.setPreferredSize(new Dimension(400, 250));
        wrapper.add(chartPanel, BorderLayout.CENTER);

        return wrapper;
    }

    private JFreeChart createTypeDistributionChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> data = thongKeService.getContractTypeDistribution();
        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        stylePieChart(chart);
        
        // Set specific colors for type distribution
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("1 - 2 năm", new Color(33, 150, 243));  // Blue
        plot.setSectionPaint("3 - 5 năm", new Color(76, 175, 80));   // Green
        plot.setSectionPaint("6 - 8 năm", new Color(255, 193, 7));   // Yellow
        plot.setSectionPaint("9 - 10 năm", new Color(255, 152, 0));  // Orange
        plot.setSectionPaint("trên 10 năm", new Color(244, 67, 54)); // Red
        
        return chart;
    }

    private JFreeChart createDiscrepancyChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> data = thongKeService.getContractDiscrepancy();
        data.forEach(dataset::setValue);

        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        stylePieChart(chart);
        
        // Set colors for discrepancy
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Hợp đồng đã kí trong năm", new Color(33, 150, 243));
        plot.setSectionPaint("Hợp đồng hết hạn trong năm", new Color(76, 175, 80));
        
        return chart;
    }

    private void stylePieChart(JFreeChart chart) {
        chart.setBackgroundPaint(new Color(245, 245, 245));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(245, 245, 245));
        plot.setOutlineVisible(false);
        
        // Hiển thị nhãn theo định dạng "Tên: 00%" thay vì chỉ "00%"
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {2}")); 
        
        plot.setLabelBackgroundPaint(new Color(255, 255, 255, 180));
        plot.setLabelOutlinePaint(new Color(200, 200, 200));
        plot.setLabelShadowPaint(null);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setLabelPaint(Color.DARK_GRAY);
        
        // Tắt SimpleLabels để có đường dẫn trỏ vào biểu đồ, giúp dễ nhìn hơn khi dữ liệu nhỏ
        plot.setSimpleLabels(false); 

        if (chart.getLegend() != null) {
            chart.getLegend().setBackgroundPaint(new Color(245, 245, 245));
            chart.getLegend().setFrame(org.jfree.chart.block.BlockBorder.NONE);
            chart.getLegend().setItemFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
    }

    private void refreshTable() {
        String filter = (String) filterCombo.getSelectedItem();
        List<ContractDTO> list = thongKeService.getContractDetails(filter);
        tableModel.setRowCount(0);

        int stt = 1;
        for (ContractDTO dto : list) {
            tableModel.addRow(new Object[]{
                    stt++,
                    dto.maNV + " - " + dto.hoTen,
                    dto.phongBan != null ? dto.phongBan : "N/A",
                    dto.ngayLamHopDong != null ? dto.ngayLamHopDong.format(DATE_FORMATTER) : "",
                    dto.hanHopDong != null ? dto.hanHopDong.format(DATE_FORMATTER) : "",
                    dto.loaiHopDong,
                    dto.luongCoBan != null ? CURRENCY_FORMAT.format(dto.luongCoBan) : "0"
            });
        }
        countLabel.setText("Số lượng: " + list.size());
    }
}
