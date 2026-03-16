package com.hrm.UI.HR.CategoryTab;

import java.awt.*;
import javax.swing.*;
import com.formdev.flatlaf.FlatClientProperties;

/**
 * Tab quản lý danh mục chính
 * Cho phép quản lý các loại danh mục (phòng ban, chức vụ, v.v...)
 */
public class CategoryPanel extends JPanel {
    private JTabbedPane tabbedPane;

    public CategoryPanel() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background: #f8f9fa");
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "arrowType: chevron");

        // Thêm các tab cho các danh mục khác nhau
        tabbedPane.addTab("Phòng ban", new DepartmentTab());
        tabbedPane.addTab("Chức vụ", new PositionTab());
        tabbedPane.addTab("Trình độ", new TrinhDoTab());
        tabbedPane.addTab("Tiêu chí đánh giá", new EvaluationCriteriaTab());
        tabbedPane.addTab("Phụ cấp", new AllowanceTab());
        tabbedPane.addTab("Khấu trừ", new DeductionTab());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
