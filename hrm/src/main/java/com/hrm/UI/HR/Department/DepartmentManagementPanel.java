package com.hrm.UI.HR.Department;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.hrm.DAO.HR.DepartmentDAO.EmployeeOption;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.Service.DepartmentService;
import com.hrm.utils.JDBCConection;

public class DepartmentManagementPanel extends JPanel {

    private JPanel cardsContainer;
    private JTextField searchField;
    private DepartmentService departmentService = new DepartmentService();

    public DepartmentManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(createTitleSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createStatsSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createSearchSection());

        add(topSection, BorderLayout.NORTH);
        add(createCardsArea(), BorderLayout.CENTER);
    }

    // ============== TITLE ==============
    private JPanel createTitleSection() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        JLabel title = new JLabel("Quản lý phòng ban");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitle = new JLabel("Quản lý tổ chức và phòng ban công ty");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(130, 130, 130));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        panel.add(textPanel);
        return panel;
    }

    // ============== STATS CARDS (4 ô phía trên) ==============
    private JPanel createStatsSection() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 18, 0));
        stats.setOpaque(false);

        // Calculate values from database
        List<DepartmentDTO> departments = departmentService.getAllDepartmentsWithEmployeeCount();
        int totalDepartments = departments.size();

        int totalEmployees = 0;
        int maxEmployees = 0;
        for (DepartmentDTO dept : departments) {
            int empCount = dept.getSoNhanVien();
            totalEmployees += empCount;
            if (empCount > maxEmployees) {
                maxEmployees = empCount;
            }
        }

        int avgEmployees = totalDepartments > 0 ? totalEmployees / totalDepartments : 0;

        stats.add(createStatCard("Tổng phòng ban", String.valueOf(totalDepartments), new Color(180, 81, 255)));
        stats.add(createStatCard("Tổng nhân viên", String.valueOf(totalEmployees), new Color(80, 140, 255)));
        stats.add(createStatCard("TB NV/Phòng ban", String.valueOf(avgEmployees), new Color(56, 180, 130)));
        stats.add(createStatCard("PB lớn nhất", maxEmployees + " NV", new Color(120, 120, 255)));

        return stats;
    }

    private JPanel createStatCard(String label, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 242), 1, true),
                new EmptyBorder(16, 18, 10, 18)
        ));

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLabel.setForeground(new Color(140, 144, 153));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblValue.setForeground(accent);

        // Gạch màu sắc bên dưới số, giống mockup
        JPanel colorLine = new JPanel();
        colorLine.setBackground(accent);
        colorLine.setMaximumSize(new Dimension(Integer.MAX_VALUE, 3));
        colorLine.setPreferredSize(new Dimension(1, 3));

        card.add(lblLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);
        card.add(Box.createVerticalStrut(10));
        card.add(colorLine);

        return card;
    }

    // ============== SEARCH BAR + ADD BUTTON ==============
    private JPanel createSearchSection() {
        JPanel wrapper = new JPanel();
        wrapper.setOpaque(false);
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));

        // Ô tìm kiếm lớn bo tròn
        final String SEARCH_PLACEHOLDER = "Tìm kiếm theo tên phòng ban, mã, trưởng phòng...";
        searchField = new JTextField(SEARCH_PLACEHOLDER);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setBackground(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(225, 229, 237), 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));
        searchField.setPreferredSize(new Dimension(500, 42));
        searchField.setMaximumSize(new Dimension(Short.MAX_VALUE, 42));

        // Placeholder: khi nhấp vào thì xóa text gợi ý, hiện ô trắng để nhập
        searchField.setForeground(new Color(150, 150, 150));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(searchField.getText())) {
                    searchField.setText("");
                    searchField.setForeground(new Color(33, 37, 41));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });

        // Lọc danh sách phòng ban theo nội dung tìm kiếm
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                String text = searchField.getText();
                if (SEARCH_PLACEHOLDER.equals(text)) {
                    filterDepartments("");
                } else {
                    filterDepartments(text);
                }
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
        });

        wrapper.add(searchField);
        wrapper.add(Box.createHorizontalStrut(16));

        // Nút thêm phòng ban màu tím ở góc phải
        JButton addBtn = new JButton("+ Thêm phòng ban");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setForeground(Color.WHITE);
        addBtn.setBackground(new Color(151, 71, 255));
        addBtn.setFocusPainted(false);
        addBtn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
        // Rộng hơn để luôn hiển thị hết chữ
        addBtn.setPreferredSize(new Dimension(170, 42));

        addBtn.addActionListener(e -> openAddDepartmentForm());

        wrapper.add(addBtn);
        return wrapper;
    }

    // ============== CARDS GRID ==============
    private JScrollPane createCardsArea() {
        cardsContainer = new JPanel(new GridLayout(0, 3, 20, 20));
        cardsContainer.setOpaque(false);
        cardsContainer.setBorder(new EmptyBorder(24, 0, 0, 0));

        // Load departments from HR service (đã có tổng số nhân viên)
        List<DepartmentDTO> departments = departmentService.getAllDepartmentsWithEmployeeCount();
        for (DepartmentDTO dept : departments) {
            ManagerInfo info = getManagerInfo(dept.getMaPhongBan());

            String managerName = info != null && info.name != null && !info.name.isEmpty()
                    ? info.name
                    : "Chưa cập nhật";
            String description = "Mô tả phòng ban sẽ được cập nhật sau";
            String email = info != null && info.email != null && !info.email.isEmpty() ? info.email : "N/A";
            String phone = info != null && info.phone != null && !info.phone.isEmpty() ? info.phone : "N/A";

            cardsContainer.add(createDepartmentCard(
                    dept.getTenPhongBan(),
                    dept.getMaPhongBan(),
                    dept.getSoNhanVien(),
                    managerName,
                    description,
                    email,
                    phone
            ));
        }

        // Bọc grid trong JScrollPane để có thể kéo xuống xem các phòng bên dưới
        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(new Color(245, 247, 250));
        // Cho phép kéo ngang khi không đủ chỗ hiển thị hết các phòng ban
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scroll;
    }

    // ============== MỘT CARD PHÒNG BAN ==============
    private JPanel createDepartmentCard(String name, String code, int employees,
                                        String manager, String description,
                                        String email, String phone) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 234, 242), 1, true),
                new EmptyBorder(0, 0, 0, 0)
        ));
        // Lưu thông tin phục vụ tìm kiếm
        String searchText = (name + " " + code + " " + manager).toLowerCase();
        card.putClientProperty("searchText", searchText);

        // Header tím
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(new Color(151, 71, 255));
        header.setBorder(new EmptyBorder(16, 18, 16, 18));

        JPanel headerTop = new JPanel(new BorderLayout());
        headerTop.setOpaque(false);

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(Color.WHITE);

        // Không dùng icon tòa nhà, chỉ hiển thị tên phòng
        headerTop.add(lblName, BorderLayout.WEST);

        JLabel lblCode = new JLabel("Mã: " + code);
        lblCode.setForeground(Color.WHITE);
        lblCode.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel employeesRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        employeesRow.setOpaque(false);
        JLabel lblEmployees = new JLabel(employees + " nhân viên");
        lblEmployees.setForeground(Color.WHITE);
        lblEmployees.setFont(new Font("Segoe UI", Font.BOLD, 15));
        employeesRow.add(lblEmployees);

        header.add(headerTop);
        header.add(Box.createVerticalStrut(8));
        header.add(lblCode);
        header.add(Box.createVerticalStrut(10));
        header.add(employeesRow);

        // Nội dung trắng bên dưới
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(16, 18, 10, 18));

        addInfoBlock(body, "Trưởng phòng", manager, null);
        addInfoBlock(body, "Mô tả", "<html>" + description + "</html>", null);

        // Hàng email / điện thoại
        JPanel contactRow = new JPanel(new GridLayout(1, 2, 40, 0));
        contactRow.setOpaque(false);
        JPanel emailPanel = createLabeledValue("Email", email);
        JPanel phonePanel = createLabeledValue("Điện thoại", phone);
        contactRow.add(emailPanel);
        contactRow.add(phonePanel);
        body.add(contactRow);
        body.add(Box.createVerticalStrut(10));

        // Thanh action dưới cùng
        JPanel actionsBar = new JPanel(new BorderLayout());
        actionsBar.setOpaque(false);
        actionsBar.setBorder(new EmptyBorder(12, 18, 16, 18));

        JButton viewBtn = new JButton("Xem chi tiết");
        viewBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        viewBtn.setForeground(new Color(120, 49, 255));
        viewBtn.setBackground(new Color(238, 228, 255));
        viewBtn.setFocusPainted(false);
        viewBtn.setBorder(BorderFactory.createEmptyBorder(8, 26, 8, 26));

        viewBtn.addActionListener(e -> JOptionPane.showMessageDialog(
                null,
                "<html><b>" + name + " (" + code + ")</b><br/>"
                        + "Trưởng phòng: " + manager + "<br/>"
                        + "Số nhân viên: " + employees + "<br/><br/>"
                        + "Mô tả: " + description + "<br/>"
                        + "Email: " + email + "<br/>"
                        + "Điện thoại: " + phone + "</html>",
                "Chi tiết phòng ban",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JPanel iconButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        iconButtons.setOpaque(false);

        // Nút "Sửa" dạng text, không dùng icon
        JButton editBtn = new JButton("Sửa");
        editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        editBtn.setForeground(new Color(86, 125, 255));
        editBtn.setFocusPainted(false);
        editBtn.setContentAreaFilled(false);
        editBtn.setBorder(BorderFactory.createEmptyBorder());
        editBtn.setToolTipText("Sửa phòng ban");
        editBtn.addActionListener(e -> openEditDepartmentForm(code, name, manager, email, phone));

        // Nút "Xóa" dạng text, không dùng icon
        JButton deleteBtn = new JButton("Xóa");
        deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deleteBtn.setForeground(new Color(234, 84, 77));
        deleteBtn.setFocusPainted(false);
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorder(BorderFactory.createEmptyBorder());
        deleteBtn.setToolTipText("Xóa phòng ban");
        deleteBtn.addActionListener(e -> handleDeleteDepartment(code, name, card));

        iconButtons.add(editBtn);
        iconButtons.add(deleteBtn);

        actionsBar.add(viewBtn, BorderLayout.WEST);
        actionsBar.add(iconButtons, BorderLayout.EAST);

        card.add(header);
        card.add(body);

        // đường kẻ mờ phía trên thanh action
        JPanel bottomWrapper = new JPanel();
        bottomWrapper.setLayout(new BorderLayout());
        bottomWrapper.setOpaque(false);
        bottomWrapper.add(new JLabel() {{
            setOpaque(true);
            setBackground(new Color(238, 240, 245));
            setPreferredSize(new Dimension(1, 1));
        }}, BorderLayout.NORTH);
        bottomWrapper.add(actionsBar, BorderLayout.CENTER);

        card.add(bottomWrapper);

        return card;
    }

    // Thông tin trưởng phòng
    private static class ManagerInfo {
        String name;
        String email;
        String phone;
    }

    /**
     * Lấy thông tin trưởng phòng (tên, email, điện thoại) theo mã phòng ban.
     * Ưu tiên trưởng phòng có MACHUCVU = 'CV01'; nếu không có thì lấy bất kỳ nhân viên đầu tiên của phòng.
     */
    private ManagerInfo getManagerInfo(String maPhongBan) {
        ManagerInfo info = new ManagerInfo();
        String sql = "SELECT hoten, email, dienthoai " +
                "FROM nhanvien " +
                "WHERE maphongban = ? " +
                "ORDER BY CASE WHEN machucvu = 'CV01' THEN 0 ELSE 1 END, manv LIMIT 1";
        try (java.sql.Connection conn = JDBCConection.getConnection();
             java.sql.PreparedStatement ps = conn != null ? conn.prepareStatement(sql) : null) {
            if (ps == null) {
                return info;
            }
            ps.setString(1, maPhongBan);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    info.name = rs.getString("hoten");
                    info.email = rs.getString("email");
                    info.phone = rs.getString("dienthoai");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return info;
    }

    private void addInfoBlock(JPanel parent, String label, String value1, String value2) {
        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(140, 144, 153));

        JLabel lblMain = new JLabel(value1);
        lblMain.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMain.setForeground(new Color(33, 37, 41));

        parent.add(lblTitle);
        parent.add(lblMain);
        if (value2 != null && !value2.isEmpty()) {
            JLabel lblSub = new JLabel(value2);
            lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblSub.setForeground(new Color(140, 144, 153));
            parent.add(lblSub);
        }
        parent.add(Box.createVerticalStrut(10));
    }

    private JPanel createLabeledValue(String label, String value) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTitle.setForeground(new Color(140, 144, 153));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblValue.setForeground(new Color(33, 37, 41));

        panel.add(lblTitle);
        panel.add(lblValue);
        return panel;
    }

    // ============== LỌC DANH SÁCH PHÒNG BAN THEO TỪ KHÓA ==============
    private void filterDepartments(String query) {
        if (cardsContainer == null) return;

        String q = query == null ? "" : query.trim().toLowerCase();

        for (java.awt.Component c : cardsContainer.getComponents()) {
            if (!(c instanceof JPanel)) continue;
            JPanel card = (JPanel) c;
            Object prop = card.getClientProperty("searchText");
            String s = prop == null ? "" : prop.toString();
            boolean match = q.isEmpty() || s.contains(q);
            card.setVisible(match);
        }

        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    /**
     * Xử lý xóa phòng ban: xóa trong database rồi refresh giao diện.
     */
    private void handleDeleteDepartment(String maPhongBan, String tenPhongBan, JPanel card) {
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Bạn có chắc muốn xóa phòng \"" + tenPhongBan + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Kiểm tra phòng ban có nhân viên không
        int empCount = departmentService.countEmployeesInDepartment(maPhongBan);
        if (empCount > 0) {
            JOptionPane.showMessageDialog(
                    null,
                    "Không thể xóa phòng ban vì còn " + empCount + " nhân viên. Vui lòng chuyển nhân viên sang phòng khác trước.",
                    "Không thể xóa",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        departmentService.deleteDepartment(maPhongBan);
        refreshPanel();
        JOptionPane.showMessageDialog(null, "Đã xóa phòng ban thành công!");
    }

    /**
     * Mở form sửa phòng ban. Sửa được: Tên phòng ban, Trưởng phòng, Email, Điện thoại. Không sửa Mô tả.
     */
    private void openEditDepartmentForm(String maPhongBan, String tenPhongBanHienTai,
                                        String managerName, String emailHienTai, String phoneHienTai) {
        JTextField nameField = new JTextField(tenPhongBanHienTai, 25);
        JLabel codeLabel = new JLabel(maPhongBan);

        List<EmployeeOption> employees = departmentService.getEmployeesInDepartment(maPhongBan);
        JComboBox<EmployeeOption> managerCombo = new JComboBox<>();
        managerCombo.addItem(new EmployeeOption("", "-- Chọn trưởng phòng --", "", ""));
        EmployeeOption currentHead = null;
        for (EmployeeOption emp : employees) {
            managerCombo.addItem(emp);
            if (emp.hoten.equals(managerName)) {
                currentHead = emp;
            }
        }
        if (currentHead != null) {
            managerCombo.setSelectedItem(currentHead);
        }

        String emailVal = (emailHienTai != null && !"N/A".equals(emailHienTai)) ? emailHienTai : "";
        String phoneVal = (phoneHienTai != null && !"N/A".equals(phoneHienTai)) ? phoneHienTai : "";
        JTextField emailField = new JTextField(emailVal, 25);
        JTextField phoneField = new JTextField(phoneVal, 25);

        managerCombo.addActionListener(ev -> {
            Object sel = managerCombo.getSelectedItem();
            if (sel instanceof EmployeeOption) {
                EmployeeOption emp = (EmployeeOption) sel;
                if (!emp.manv.isEmpty()) {
                    emailField.setText(emp.email);
                    phoneField.setText(emp.dienthoai);
                }
            }
        });

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Mã phòng ban:"));
        form.add(codeLabel);
        form.add(new JLabel("Tên phòng ban:"));
        form.add(nameField);
        form.add(new JLabel("Trưởng phòng:"));
        form.add(managerCombo);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Điện thoại:"));
        form.add(phoneField);

        int result = JOptionPane.showConfirmDialog(
                null,
                form,
                "Sửa phòng ban (trừ Mô tả)",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String tenPhongBanMoi = nameField.getText().trim();
            if (tenPhongBanMoi.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên phòng ban!");
                return;
            }

            departmentService.updateDepartment(new DepartmentDTO(maPhongBan, tenPhongBanMoi));

            Object sel = managerCombo.getSelectedItem();
            if (sel instanceof EmployeeOption) {
                EmployeeOption emp = (EmployeeOption) sel;
                if (!emp.manv.isEmpty()) {
                    departmentService.setDepartmentHead(maPhongBan, emp.manv);
                    departmentService.updateEmployeeContact(emp.manv, emailField.getText().trim(), phoneField.getText().trim());
                }
            }

            refreshPanel();
            JOptionPane.showMessageDialog(null, "Cập nhật phòng ban thành công!");
        }
    }

    // ============== FORM THÊM PHÒNG BAN ==============
    private void openAddDepartmentForm() {
        JTextField nameField = new JTextField();
        JTextField codeField = new JTextField();
        JTextField employeesField = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Tên phòng ban:"));
        form.add(nameField);
        form.add(new JLabel("Mã phòng ban:"));
        form.add(codeField);
        form.add(new JLabel("Số nhân viên:"));
        form.add(employeesField);

        int result = JOptionPane.showConfirmDialog(
                null,
                form,
                "Thêm phòng ban mới",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String tenPhongBan = nameField.getText().trim();
            String maPhongBan = codeField.getText().trim();
            String soNhanVienStr = employeesField.getText().trim();

            if (tenPhongBan.isEmpty() || maPhongBan.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ tên và mã phòng ban!");
                return;
            }

            int soNhanVien = 0;
            if (!soNhanVienStr.isEmpty()) {
                try {
                    soNhanVien = Integer.parseInt(soNhanVienStr);
                    if (soNhanVien < 0) {
                        JOptionPane.showMessageDialog(null, "Số nhân viên phải là số không âm!");
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Số nhân viên phải là số nguyên!");
                    return;
                }
            }

            // Kiểm tra mã phòng ban đã tồn tại chưa
            DepartmentDTO existing = departmentService.findDepartmentById(maPhongBan);
            if (existing != null) {
                JOptionPane.showMessageDialog(null, "Mã phòng ban đã tồn tại!");
                return;
            }

            // Thêm vào database
            DepartmentDTO newDept = new DepartmentDTO(maPhongBan, tenPhongBan, soNhanVien);
            departmentService.addDepartment(newDept);

            // Refresh toàn bộ panel
            refreshPanel();

            JOptionPane.showMessageDialog(null, "Thêm phòng ban thành công!");
        }
    }

    /**
     * Refresh the entire panel to update stats and department cards
     */
    private void refreshPanel() {
        // Remove all components
        removeAll();

        // Reinitialize the panel
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));

        topSection.add(createTitleSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createStatsSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createSearchSection());

        add(topSection, BorderLayout.NORTH);
        add(createCardsArea(), BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}