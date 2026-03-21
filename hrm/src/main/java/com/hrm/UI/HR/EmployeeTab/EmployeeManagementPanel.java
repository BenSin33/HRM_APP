package com.hrm.UI.HR.EmployeeTab;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.SwingUtilities;

import com.hrm.DAO.PositionDAO;
import com.hrm.DAO.TrinhDoDAO;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.PositionDTO;
import com.hrm.DTO.TrinhDoDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.DepartmentService;
import com.hrm.Service.PermissionService;
import com.hrm.utils.EmployeeExcelHelper;
import com.hrm.utils.SessionManager;

public class EmployeeManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private List<Object[]> masterData = new ArrayList<>();
    private JComboBox<String> filterBox;
    private JTextField searchField;
    private String currentKeyword = "";
    private String currentDeptFilter = null; // null = "Tất cả phòng ban", else mã phòng ban (PB01, PB02...)

    private static final String SEARCH_PLACEHOLDER = "Tìm kiếm nhân viên theo tên hoặc mã nhân viên";

    private Icon viewIcon;
    private Icon editIcon;
    private Icon deleteIcon;

    private com.hrm.DAO.HR.NhanVienHRDAO nhanVienHRDAO = new com.hrm.DAO.HR.NhanVienHRDAO();
    private com.hrm.DAO.HR.ChucVuHRDAO chucVuDAO = new com.hrm.DAO.HR.ChucVuHRDAO();
    private TrinhDoDAO trinhDoDAO = new TrinhDoDAO();
    private PositionDAO positionDAO = new PositionDAO();
    private DepartmentService departmentService = new DepartmentService();
    private PermissionService permissionService = new PermissionService();

    public EmployeeManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        viewIcon = loadIcon("/icons/view_button.png");
        editIcon = loadIcon("/icons/edit_button.png");
        deleteIcon = loadIcon("/icons/delete_button.png");

        add(createHeader(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);

        // Mỗi khi chuyển sang tab Quản lý nhân viên, tải lại toàn bộ dữ liệu từ bảng nhanvien trong database.
        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                    loadMasterDataFromDb();
                    currentDeptFilter = getSelectedDeptCode();
                    currentKeyword = "";
                    refreshTableFromMaster(currentKeyword, currentDeptFilter);
                }
            }
        });
    }

    private Icon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            return null;
        }
        ImageIcon icon = new ImageIcon(url);
        Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // ================= HEADER =================
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Quản lý nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel sub = new JLabel("Toàn quyền CRUD");
        sub.setForeground(Color.GRAY);

        left.add(title);
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        searchField = new JTextField(35);
        searchField.setText(SEARCH_PLACEHOLDER);
        searchField.setForeground(Color.GRAY);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (SEARCH_PLACEHOLDER.equals(searchField.getText())) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText(SEARCH_PLACEHOLDER);
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText().trim();
                if (SEARCH_PLACEHOLDER.equals(searchField.getText())) {
                    currentKeyword = "";
                } else {
                    currentKeyword = text.toLowerCase();
                }
                refreshTableFromMaster(currentKeyword, currentDeptFilter);
            }
        });

        filterBox = new JComboBox<>();
        filterBox.addItem("Tất cả phòng ban");
        for (DepartmentDTO d : departmentService.getAllDepartments()) {
            filterBox.addItem(d.getMaPhongBan() + " - " + d.getTenPhongBan());
        }
        JButton addBtn = new JButton("+ Thêm nhân viên");
        addBtn.setBackground(new Color(88, 63, 191));
        addBtn.setForeground(Color.WHITE);

        JButton exportExcelBtn = new JButton("Xuất Excel");
        exportExcelBtn.setBackground(new Color(56, 142, 60));
        exportExcelBtn.setForeground(Color.WHITE);

        JButton importExcelBtn = new JButton("Nhập Excel");
        importExcelBtn.setBackground(new Color(25, 118, 210));
        importExcelBtn.setForeground(Color.WHITE);

        // Kiểm tra quyền ADD cho chức năng nhân viên
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canAdd(currentUser, "CN02_EMPLOYEE")) {
            addBtn.setEnabled(false);
            addBtn.setToolTipText("Bạn không có quyền thêm nhân viên");
            importExcelBtn.setEnabled(false);
            importExcelBtn.setToolTipText("Bạn không có quyền nhập nhân viên");
        }
        if (currentUser == null || !permissionService.canExport(currentUser, "CN02_EMPLOYEE")) {
            exportExcelBtn.setEnabled(false);
            exportExcelBtn.setToolTipText("Bạn không có quyền xuất Excel");
        }

        // Action mở form thêm nhân viên
        addBtn.addActionListener(e -> openAddEmployeeForm());
        exportExcelBtn.addActionListener(e -> EmployeeExcelHelper.handleEmployeeExport(table, this));
        importExcelBtn.addActionListener(e -> EmployeeExcelHelper.handleEmployeeImport(this, this::refreshDataFromImport));

        // Lọc phòng ban: khi chọn phòng ban thì chỉ hiện nhân viên thuộc phòng đó
        filterBox.addActionListener(e -> {
            currentDeptFilter = getSelectedDeptCode();
            currentKeyword = "";
            searchField.setText(SEARCH_PLACEHOLDER);
            searchField.setForeground(Color.GRAY);
            refreshTableFromMaster(currentKeyword, currentDeptFilter);
        });

        right.add(searchField);
        right.add(filterBox);
        right.add(exportExcelBtn);
        right.add(importExcelBtn);
        right.add(addBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ================= TABLE =================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] columns = {"MÃ NV", "HỌ VÀ TÊN", "GIỚI TÍNH", "EMAIL", "ĐIỆN THOẠI", "ĐỊA CHỈ", "PHÒNG BAN", "CHỨC VỤ", "TRÌNH ĐỘ", "NGÀY VÀO LÀM", "SỐ NGÀY PHÉP", "TRẠNG THÁI", "THAO TÁC"};

        // Đổ toàn bộ dữ liệu từ bảng nhanvien trong database vào bảng quản lý nhân viên.
        masterData.clear();
        java.util.List<com.hrm.DTO.Manager.NhanVienDTO> dsNhanVien = nhanVienHRDAO.getAll();
        for (com.hrm.DTO.Manager.NhanVienDTO nv : dsNhanVien) {
            String tenChucVu = chucVuDAO.getTenChucVu(nv.getMachucvu());
            String tenPhongBan = departmentService.findDepartmentById(nv.getMaphongban()) != null
                    ? departmentService.findDepartmentById(nv.getMaphongban()).getTenPhongBan() : "";
            String tenTrinhDo = "";
            if (nv.getMatrinhdo() != null) {
                var td = trinhDoDAO.getTrinhDoById(nv.getMatrinhdo());
                tenTrinhDo = td != null && td.getTrinhDo() != null ? td.getTrinhDo() : nv.getMatrinhdo();
            }
            Object[] row = {
                str(nv.getManv()),
                str(nv.getHoten()),
                str(nv.getGioitinh()),
                str(nv.getEmail()),
                str(nv.getDienthoai()),
                str(nv.getDiachi()),
                tenPhongBan,
                str(tenChucVu),
                tenTrinhDo,
                str(nv.getNgayvaolam()),
                nv.getSongayphep(),
                str(nv.getTrangthai()) != null && !str(nv.getTrangthai()).isEmpty() ? str(nv.getTrangthai()) : "Đang làm việc"
            };
            masterData.add(row);
        }

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 12; // chỉ cột thao tác có thể chứa nút
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Tắt tự co cột để cột không bị ép nhỏ gây cắt chữ; dùng thanh kéo ngang xem hết
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // initial fill (Tất cả phòng ban)
        refreshTableFromMaster("", null);

        // Độ rộng ưu tiên từng cột để nội dung đủ hiển thị, kéo ngang xem hết
        setTableColumnPreferredWidths();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /** Trả về chuỗi hiển thị, NULL trong DB hiển thị thành rỗng. */
    private static String str(Object o) {
        return o != null ? o.toString() : "";
    }

    /** Gọi sau khi nhập Excel để refresh bảng. */
    public void refreshDataFromImport() {
        loadMasterDataFromDb();
        refreshTableFromMaster(currentKeyword, currentDeptFilter);
    }

    /** Tải lại toàn bộ dữ liệu từ bảng nhanvien trong database vào quản lý nhân viên. */
    private void loadMasterDataFromDb() {
        masterData.clear();
        java.util.List<NhanVienDTO> dsNhanVien = nhanVienHRDAO.getAll();
        for (NhanVienDTO nv : dsNhanVien) {
            String tenChucVu = chucVuDAO.getTenChucVu(nv.getMachucvu());
            String tenPhongBan = departmentService.findDepartmentById(nv.getMaphongban()) != null
                    ? departmentService.findDepartmentById(nv.getMaphongban()).getTenPhongBan() : "";
            String tenTrinhDo = "";
            if (nv.getMatrinhdo() != null) {
                var td = trinhDoDAO.getTrinhDoById(nv.getMatrinhdo());
                tenTrinhDo = td != null && td.getTrinhDo() != null ? td.getTrinhDo() : nv.getMatrinhdo();
            }
            Object[] row = {
                    str(nv.getManv()),
                    str(nv.getHoten()),
                    str(nv.getGioitinh()),
                    str(nv.getEmail()),
                    str(nv.getDienthoai()),
                    str(nv.getDiachi()),
                    tenPhongBan,
                    str(tenChucVu),
                    tenTrinhDo,
                    str(nv.getNgayvaolam()),
                    nv.getSongayphep(),
                    str(nv.getTrangthai()) != null && !str(nv.getTrangthai()).isEmpty() ? str(nv.getTrangthai()) : "Đang làm việc"
            };
            masterData.add(row);
        }
    }

    /** Chuẩn hóa trạng thái từ form sang DB. */
    private String normalizeTrangThai(String formStatus) {
        if (formStatus == null) return "Đang làm việc";
        if (formStatus.startsWith("Đang làm") || formStatus.equals("Đang làm việc")) return "Đang làm việc";
        return formStatus;
    }

    /** Lấy mã phòng ban từ item đã chọn: "Tất cả phòng ban" -> null, "PB01 - Nhân sự" -> "PB01". */
    private String getSelectedDeptCode() {
        Object sel = filterBox.getSelectedItem();
        if (sel == null || "Tất cả phòng ban".equals(sel)) return null;
        String s = sel.toString();
        if (s.contains(" - ")) return s.substring(0, s.indexOf(" - ")).trim();
        return s;
    }

    private void refreshTableFromMaster(String keyword, String deptCode) {
        String[] columns = {"MÃ NV", "HỌ VÀ TÊN", "GIỚI TÍNH", "EMAIL", "ĐIỆN THOẠI", "ĐỊA CHỈ", "PHÒNG BAN", "CHỨC VỤ", "TRÌNH ĐỘ", "NGÀY VÀO LÀM", "SỐ NGÀY PHÉP", "TRẠNG THÁI", "THAO TÁC"};
        DefaultTableModel newModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 12; // chỉ cột THAO TÁC
            }
        };

        String filterDeptName = (deptCode != null && !deptCode.isEmpty())
                && departmentService.findDepartmentById(deptCode) != null
                ? departmentService.findDepartmentById(deptCode).getTenPhongBan() : null;

        for (Object[] r : masterData) {
            String id = String.valueOf(r[0]).toLowerCase();
            String name = String.valueOf(r[1]).toLowerCase();
            String email = String.valueOf(r[3]).toLowerCase();
            String tenPhongBan = r.length > 6 ? String.valueOf(r[6]) : "";

            boolean matchKeyword = (keyword == null || keyword.isEmpty()) || id.contains(keyword) || name.contains(keyword) || email.contains(keyword);
            boolean matchDept = (deptCode == null || deptCode.isEmpty()) || (filterDeptName != null && filterDeptName.equals(tenPhongBan));

            if (matchKeyword && matchDept) {
                Object[] row = new Object[13];
                System.arraycopy(r, 0, row, 0, Math.min(r.length, 12));
                row[12] = ""; // placeholder for action column
                newModel.addRow(row);
            }
        }

        this.model = newModel;
        table.setModel(newModel);
        table.getColumn("THAO TÁC").setCellRenderer(new ButtonRenderer());
        table.getColumn("THAO TÁC").setCellEditor(new ButtonEditor(new JCheckBox()));
        setTableColumnPreferredWidths();
    }

    /** Đặt độ rộng ưu tiên từng cột để nội dung không bị cắt, kéo ngang xem hết. */
    private void setTableColumnPreferredWidths() {
        try {
            table.getColumn("MÃ NV").setPreferredWidth(70);
            table.getColumn("HỌ VÀ TÊN").setPreferredWidth(160);
            table.getColumn("GIỚI TÍNH").setPreferredWidth(75);
            table.getColumn("EMAIL").setPreferredWidth(200);
            table.getColumn("ĐIỆN THOẠI").setPreferredWidth(110);
            table.getColumn("ĐỊA CHỈ").setPreferredWidth(220);
            table.getColumn("PHÒNG BAN").setPreferredWidth(100);
            table.getColumn("CHỨC VỤ").setPreferredWidth(110);
            table.getColumn("TRÌNH ĐỘ").setPreferredWidth(90);
            table.getColumn("NGÀY VÀO LÀM").setPreferredWidth(110);
            table.getColumn("SỐ NGÀY PHÉP").setPreferredWidth(90);
            table.getColumn("TRẠNG THÁI").setPreferredWidth(120);
            table.getColumn("THAO TÁC").setMinWidth(110);
            table.getColumn("THAO TÁC").setPreferredWidth(130);
        } catch (Exception ignored) { }
    }

    // ================= FORM THÊM NHÂN VIÊN =================
    private void openAddEmployeeForm() {
        // Kiểm tra quyền trước khi mở form
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canAdd(currentUser, "CN02_EMPLOYEE")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền thêm nhân viên!", "Từ chối quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mã NV tự động tăng
        String nextManv = nhanVienHRDAO.generateNextManv();
        JTextField idField = new JTextField(nextManv, 20);
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));

        JTextField nameField = new JTextField(20);
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Nam", "Nữ"});
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField addressField = new JTextField(20);

        JComboBox<String> deptBox = new JComboBox<>();
        deptBox.addItem("-- Chọn phòng ban --");
        for (DepartmentDTO d : departmentService.getAllDepartments()) {
            deptBox.addItem(d.getMaPhongBan() + " - " + d.getTenPhongBan());
        }

        List<PositionDTO> positions = positionDAO.getAllPositions();
        JComboBox<String> posBox = new JComboBox<>();
        posBox.addItem("-- Chọn chức vụ --");
        for (PositionDTO p : positions) {
            posBox.addItem(p.getTenViTri() != null ? p.getTenViTri() : p.getMaChucVu());
        }

        List<TrinhDoDTO> trinhDoList = trinhDoDAO.getAllTrinhDo();
        JComboBox<String> trinhDoBox = new JComboBox<>();
        trinhDoBox.addItem("-- Chọn trình độ --");
        for (TrinhDoDTO td : trinhDoList) {
            trinhDoBox.addItem(td.getTrinhDo() != null ? td.getTrinhDo() : td.getMaTrinhDo());
        }

        JTextField ngayVaoLamField = new JTextField(20);
        ngayVaoLamField.setToolTipText("yyyy-MM-dd (ví dụ: 2024-01-15)");
        JTextField soNgayPhepField = new JTextField("12", 10);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang làm", "Nghỉ việc"});

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setBorder(new EmptyBorder(24, 28, 20, 28));
        form.add(new JLabel("Mã NV:"));
        form.add(idField);
        form.add(new JLabel("Họ và tên:"));
        form.add(nameField);
        form.add(new JLabel("Giới tính:"));
        form.add(genderBox);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Điện thoại:"));
        form.add(phoneField);
        form.add(new JLabel("Địa chỉ:"));
        form.add(addressField);
        form.add(new JLabel("Phòng ban:"));
        form.add(deptBox);
        form.add(new JLabel("Chức vụ:"));
        form.add(posBox);
        form.add(new JLabel("Trình độ:"));
        form.add(trinhDoBox);
        form.add(new JLabel("Ngày vào làm:"));
        form.add(ngayVaoLamField);
        form.add(new JLabel("Số ngày phép:"));
        form.add(soNgayPhepField);
        form.add(new JLabel("Trạng thái:"));
        form.add(statusBox);

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Thêm nhân viên mới", true);
        dialog.setLayout(new BorderLayout());

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnPanel.setBorder(new EmptyBorder(0, 28, 20, 28));
        JButton okBtn = new JButton("OK");
        okBtn.setBackground(new Color(88, 63, 191));
        okBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.addActionListener(e -> dialog.dispose());
        okBtn.addActionListener(e -> {
            String manv = idField.getText().trim();
            String hoten = nameField.getText().trim();
            String gioitinh = String.valueOf(genderBox.getSelectedItem());
            String email = emailField.getText().trim();
            String dienthoai = phoneField.getText().trim();
            String diachi = addressField.getText().trim();

            Object deptSel = deptBox.getSelectedItem();
            String maphongban = null;
            if (deptSel != null && !"-- Chọn phòng ban --".equals(deptSel.toString())) {
                String s = deptSel.toString();
                maphongban = s.contains(" - ") ? s.substring(0, s.indexOf(" - ")).trim() : s;
            }

            int posIdx = posBox.getSelectedIndex();
            int tdIdx = trinhDoBox.getSelectedIndex();

            // ========== Kiểm tra dữ liệu và focus vào trường sai ==========
            if (!manv.toUpperCase().startsWith("NV")) {
                JOptionPane.showMessageDialog(dialog, "Mã NV phải bắt đầu bằng 'NV'!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> idField.requestFocusInWindow());
                return;
            }
            if (hoten.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Họ tên không được trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> { nameField.requestFocusInWindow(); nameField.selectAll(); });
                return;
            }
            if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._-]+@company\\.com$")) {
                JOptionPane.showMessageDialog(dialog, "Email phải đúng format: chữ@company.com (ví dụ: abc.xyz@company.com)", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> { emailField.requestFocusInWindow(); emailField.selectAll(); });
                return;
            }
            if (!dienthoai.isEmpty() && !dienthoai.matches("^\\d{10}$")) {
                JOptionPane.showMessageDialog(dialog, "Điện thoại phải là đúng 10 chữ số!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> { phoneField.requestFocusInWindow(); phoneField.selectAll(); });
                return;
            }
            if (maphongban == null || maphongban.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Phải chọn Phòng ban!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> deptBox.requestFocusInWindow());
                return;
            }
            if (posIdx <= 0) {
                JOptionPane.showMessageDialog(dialog, "Phải chọn Chức vụ!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> posBox.requestFocusInWindow());
                return;
            }
            if (tdIdx <= 0) {
                JOptionPane.showMessageDialog(dialog, "Phải chọn Trình độ!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> trinhDoBox.requestFocusInWindow());
                return;
            }

            LocalDate ngayvaolam = null;
            String ngayStr = ngayVaoLamField.getText().trim();
            if (!ngayStr.isEmpty()) {
                try {
                    ngayvaolam = LocalDate.parse(ngayStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    try {
                        ngayvaolam = LocalDate.parse(ngayStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    } catch (DateTimeParseException ex2) {
                        JOptionPane.showMessageDialog(dialog, "Ngày vào làm không hợp lệ. Dùng định dạng yyyy-MM-dd hoặc dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                        SwingUtilities.invokeLater(() -> { ngayVaoLamField.requestFocusInWindow(); ngayVaoLamField.selectAll(); });
                        return;
                    }
                }
            }

            int songayphep = 12;
            try {
                String sp = soNgayPhepField.getText().trim();
                if (!sp.isEmpty()) {
                    songayphep = Integer.parseInt(sp);
                    if (songayphep < 0) songayphep = 12;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số ngày phép phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                SwingUtilities.invokeLater(() -> { soNgayPhepField.requestFocusInWindow(); soNgayPhepField.selectAll(); });
                return;
            }

            String machucvu = (posIdx > 0 && posIdx <= positions.size()) ? positions.get(posIdx - 1).getMaChucVu() : "CV02";
            String matrinhdo = (tdIdx > 0 && tdIdx <= trinhDoList.size()) ? trinhDoList.get(tdIdx - 1).getMaTrinhDo() : "TD01";
            String trangthai = normalizeTrangThai(String.valueOf(statusBox.getSelectedItem()));

            NhanVienDTO dto = new NhanVienDTO(manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi, dienthoai, email, ngayvaolam, songayphep, trangthai);
            if (nhanVienHRDAO.insert(dto)) {
                loadMasterDataFromDb();
                refreshTableFromMaster(currentKeyword, currentDeptFilter);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu vào database.");
            }
        });
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setMinimumSize(new Dimension(480, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    // ================= BUTTON RENDERER =================
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        }

        private JButton makeIconButton(Icon icon, String fallbackText) {
            JButton b = new JButton(fallbackText);
            if (icon != null) {
                b.setIcon(icon);
                b.setText("");
            }
            b.setMargin(new java.awt.Insets(0, 0, 0, 0));
            b.setPreferredSize(new java.awt.Dimension(28, 28));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            return b;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            removeAll();
            
            UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
            
            JButton viewBtn = makeIconButton(viewIcon, "V");
            viewBtn.setEnabled(permissionService.canView(currentUser, "CN02_EMPLOYEE"));
            add(viewBtn);

            JButton editBtn = makeIconButton(editIcon, "E");
            editBtn.setEnabled(permissionService.canEdit(currentUser, "CN02_EMPLOYEE"));
            add(editBtn);

            JButton deleteBtn = makeIconButton(deleteIcon, "D");
            deleteBtn.setEnabled(permissionService.canDelete(currentUser, "CN02_EMPLOYEE"));
            add(deleteBtn);

            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    // ================= BUTTON EDITOR =================
    class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewBtn, editBtn, deleteBtn;
        private int currentRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));

            viewBtn = createEditorButton(viewIcon, "V");
            editBtn = createEditorButton(editIcon, "E");
            deleteBtn = createEditorButton(deleteIcon, "D");

            viewBtn.addActionListener(e -> {
                fireEditingStopped();
                showEmployeeDetailsByViewRow(currentRow);
            });

            editBtn.addActionListener(e -> {
                fireEditingStopped();
                openEditEmployeeFormByViewRow(currentRow);
            });

            deleteBtn.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Bạn có chắc muốn xóa nhân viên này?",
                        "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteEmployeeByViewRow(currentRow);
                }
                fireEditingStopped();
            });

            panel.add(viewBtn);
            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private JButton createEditorButton(Icon icon, String fallbackText) {
            JButton b = new JButton(fallbackText);
            if (icon != null) {
                b.setIcon(icon);
                b.setText("");
            }
            b.setMargin(new java.awt.Insets(0, 0, 0, 0));
            b.setPreferredSize(new java.awt.Dimension(28, 28));
            b.setFocusPainted(false);
            b.setBorderPainted(false);
            b.setContentAreaFilled(false);
            return b;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.currentRow = row;
            
            UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
            viewBtn.setEnabled(permissionService.canView(currentUser, "CN02_EMPLOYEE"));
            editBtn.setEnabled(permissionService.canEdit(currentUser, "CN02_EMPLOYEE"));
            deleteBtn.setEnabled(permissionService.canDelete(currentUser, "CN02_EMPLOYEE"));

            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // Helpers that operate on masterData by finding matching IDs from view rows
    private void showEmployeeDetailsByViewRow(int viewRow) {
        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        String id = String.valueOf(table.getModel().getValueAt(viewRow, 0));
        int idx = findMasterIndexById(id);
        if (idx >= 0) showEmployeeDetails(idx);
    }

    private void openEditEmployeeFormByViewRow(int viewRow) {
        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        String id = String.valueOf(table.getModel().getValueAt(viewRow, 0));
        int idx = findMasterIndexById(id);
        if (idx >= 0) openEditEmployeeForm(idx);
    }

    private void deleteEmployeeByViewRow(int viewRow) {
        // Kiểm tra quyền DELETE
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canDelete(currentUser, "CN02_EMPLOYEE")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa nhân viên!", "Từ chối quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (viewRow < 0 || viewRow >= table.getRowCount()) return;
        String manv = String.valueOf(table.getModel().getValueAt(viewRow, 0));
        if (nhanVienHRDAO.delete(manv)) {
            loadMasterDataFromDb();
            refreshTableFromMaster(currentKeyword, currentDeptFilter);
            JOptionPane.showMessageDialog(null, "Đã xóa nhân viên.");
        } else {
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa (có thể nhân viên đã được tham chiếu ở bảng khác).");
        }
    }

    private int findMasterIndexById(String id) {
        for (int i = 0; i < masterData.size(); i++) {
            Object[] r = masterData.get(i);
            if (r.length > 0 && String.valueOf(r[0]).equals(id)) return i;
        }
        return -1;
    }

    // ================= VIEW / EDIT HELPERS =================
    private void showEmployeeDetails(int masterIndex) {
        // Kiểm tra quyền VIEW
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canView(currentUser, "CN02_EMPLOYEE")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xem thông tin nhân viên!", "Từ chối quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (masterIndex < 0 || masterIndex >= masterData.size()) return;
        Object[] r = masterData.get(masterIndex);
        String id = String.valueOf(r[0]);
        String name = String.valueOf(r[1]);
        String email = r.length > 3 ? String.valueOf(r[3]) : "";
        String dept = r.length > 6 ? String.valueOf(r[6]) : "";
        String pos = r.length > 7 ? String.valueOf(r[7]) : "";
        String trinhDo = r.length > 8 ? String.valueOf(r[8]) : "";
        String ngayVaoLam = r.length > 9 ? String.valueOf(r[9]) : "";
        String soNgayPhep = r.length > 10 ? String.valueOf(r[10]) : "";
        String status = r.length > 11 ? String.valueOf(r[11]) : "";

        String message = String.format(
                "Mã NV: %s\nHọ và tên: %s\nEmail: %s\nPhòng ban: %s\nChức vụ: %s\nTrình độ: %s\nNgày vào làm: %s\nSố ngày phép: %s\nTrạng thái: %s",
                id, name, email, dept, pos, trinhDo, ngayVaoLam, soNgayPhep, status);
        JOptionPane.showMessageDialog(null, message, "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
    }

    private void openEditEmployeeForm(int masterIndex) {
        // Kiểm tra quyền EDIT
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canEdit(currentUser, "CN02_EMPLOYEE")) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền sửa thông tin nhân viên!", "Từ chối quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (masterIndex < 0 || masterIndex >= masterData.size()) return;
        Object[] r = masterData.get(masterIndex);
        String manv = String.valueOf(r[0]);
        NhanVienDTO nv = nhanVienHRDAO.findById(manv);
        if (nv == null) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy nhân viên trong database.");
            return;
        }

        JTextField idField = new JTextField(nv.getManv() != null ? nv.getManv() : "");
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));
        JTextField nameField = new JTextField(nv.getHoten() != null ? nv.getHoten() : "");
        JComboBox<String> genderBox = new JComboBox<>(new String[]{"Nam", "Nữ"});
        if (nv.getGioitinh() != null) {
            if (nv.getGioitinh().equals("Nữ")) genderBox.setSelectedItem("Nữ");
            else genderBox.setSelectedItem("Nam");
        }
        JTextField emailField = new JTextField(nv.getEmail() != null ? nv.getEmail() : "");
        JTextField phoneField = new JTextField(nv.getDienthoai() != null ? nv.getDienthoai() : "");
        JTextField addressField = new JTextField(nv.getDiachi() != null ? nv.getDiachi() : "");

        JComboBox<String> deptBox = new JComboBox<>();
        deptBox.addItem("-- Chọn phòng ban --");
        List<DepartmentDTO> departments = departmentService.getAllDepartments();
        int deptSelectIdx = 0;
        for (int i = 0; i < departments.size(); i++) {
            DepartmentDTO d = departments.get(i);
            String item = d.getMaPhongBan() + " - " + d.getTenPhongBan();
            deptBox.addItem(item);
            if (d.getMaPhongBan() != null && d.getMaPhongBan().equals(nv.getMaphongban())) deptSelectIdx = i + 1;
        }
        deptBox.setSelectedIndex(deptSelectIdx);

        List<PositionDTO> positions = positionDAO.getAllPositions();
        JComboBox<String> posBox = new JComboBox<>();
        posBox.addItem("-- Chọn chức vụ --");
        int posSelectIdx = 0;
        for (int i = 0; i < positions.size(); i++) {
            PositionDTO p = positions.get(i);
            posBox.addItem(p.getTenViTri() != null ? p.getTenViTri() : p.getMaChucVu());
            if (p.getMaChucVu() != null && p.getMaChucVu().equals(nv.getMachucvu())) posSelectIdx = i + 1;
        }
        posBox.setSelectedIndex(posSelectIdx);

        List<TrinhDoDTO> trinhDoList = trinhDoDAO.getAllTrinhDo();
        JComboBox<String> trinhDoBox = new JComboBox<>();
        trinhDoBox.addItem("-- Chọn trình độ --");
        int tdSelectIdx = 0;
        for (int i = 0; i < trinhDoList.size(); i++) {
            TrinhDoDTO td = trinhDoList.get(i);
            trinhDoBox.addItem(td.getTrinhDo() != null ? td.getTrinhDo() : td.getMaTrinhDo());
            if (td.getMaTrinhDo() != null && td.getMaTrinhDo().equals(nv.getMatrinhdo())) tdSelectIdx = i + 1;
        }
        trinhDoBox.setSelectedIndex(tdSelectIdx);

        JTextField ngayVaoLamField = new JTextField(20);
        ngayVaoLamField.setToolTipText("yyyy-MM-dd (ví dụ: 2024-01-15)");
        if (nv.getNgayvaolam() != null) {
            ngayVaoLamField.setText(nv.getNgayvaolam().format(DateTimeFormatter.ISO_LOCAL_DATE));
        }
        JTextField soNgayPhepField = new JTextField(String.valueOf(nv.getSongayphep()), 10);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc"});
        statusBox.setSelectedItem(nv.getTrangthai() != null ? nv.getTrangthai() : "Đang làm việc");

        JPanel form = new JPanel(new GridLayout(0, 2, 12, 10));
        form.setBorder(new EmptyBorder(24, 28, 20, 28));
        form.add(new JLabel("Mã NV:"));
        form.add(idField);
        form.add(new JLabel("Họ và tên:"));
        form.add(nameField);
        form.add(new JLabel("Giới tính:"));
        form.add(genderBox);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Điện thoại:"));
        form.add(phoneField);
        form.add(new JLabel("Địa chỉ:"));
        form.add(addressField);
        form.add(new JLabel("Phòng ban:"));
        form.add(deptBox);
        form.add(new JLabel("Chức vụ:"));
        form.add(posBox);
        form.add(new JLabel("Trình độ:"));
        form.add(trinhDoBox);
        form.add(new JLabel("Ngày vào làm:"));
        form.add(ngayVaoLamField);
        form.add(new JLabel("Số ngày phép:"));
        form.add(soNgayPhepField);
        form.add(new JLabel("Trạng thái:"));
        form.add(statusBox);

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentFrame, "Sửa nhân viên", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnPanel.setBorder(new EmptyBorder(0, 28, 20, 28));
        JButton okBtn = new JButton("OK");
        okBtn.setBackground(new Color(88, 63, 191));
        okBtn.setForeground(Color.WHITE);
        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.addActionListener(e -> dialog.dispose());
        okBtn.addActionListener(e -> {
            String hoten = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dienthoai = phoneField.getText().trim();

            // Kiểm tra dữ liệu và focus vào trường sai
            if (hoten.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Họ tên không được trống!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                nameField.requestFocusInWindow();
                return;
            }
            if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._-]+@company\\.com$")) {
                JOptionPane.showMessageDialog(dialog, "Email phải đúng format: chữ@company.com (ví dụ: abc.xyz@company.com)", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                emailField.requestFocusInWindow();
                return;
            }
            if (!dienthoai.isEmpty() && !dienthoai.matches("^\\d{10}$")) {
                JOptionPane.showMessageDialog(dialog, "Điện thoại phải là đúng 10 chữ số!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                phoneField.requestFocusInWindow();
                return;
            }

            Object deptSel = deptBox.getSelectedItem();
            if (deptSel == null || "-- Chọn phòng ban --".equals(deptSel.toString())) {
                JOptionPane.showMessageDialog(dialog, "Phải chọn Phòng ban!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                deptBox.requestFocusInWindow();
                return;
            }
            String maphongban;
            String s = deptSel.toString();
            maphongban = s.contains(" - ") ? s.substring(0, s.indexOf(" - ")).trim() : s;
            nv.setMaphongban(maphongban);

            int posIdx = posBox.getSelectedIndex();
            if (posIdx <= 0) {
                JOptionPane.showMessageDialog(dialog, "Phải chọn Chức vụ!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                posBox.requestFocusInWindow();
                return;
            }
            nv.setMachucvu(positions.get(posIdx - 1).getMaChucVu());

            int tdIdx = trinhDoBox.getSelectedIndex();
            if (tdIdx <= 0) {
                JOptionPane.showMessageDialog(dialog, "Phải chọn Trình độ!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                trinhDoBox.requestFocusInWindow();
                return;
            }
            nv.setMatrinhdo(trinhDoList.get(tdIdx - 1).getMaTrinhDo());

            String ngayStr = ngayVaoLamField.getText().trim();
            if (!ngayStr.isEmpty()) {
                try {
                    nv.setNgayvaolam(LocalDate.parse(ngayStr, DateTimeFormatter.ISO_LOCAL_DATE));
                } catch (DateTimeParseException ex) {
                    try {
                        nv.setNgayvaolam(LocalDate.parse(ngayStr, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    } catch (DateTimeParseException ex2) {
                        JOptionPane.showMessageDialog(dialog, "Ngày vào làm không hợp lệ. Dùng định dạng yyyy-MM-dd hoặc dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                        ngayVaoLamField.requestFocusInWindow();
                        return;
                    }
                }
            } else {
                nv.setNgayvaolam(null);
            }

            try {
                String sp = soNgayPhepField.getText().trim();
                nv.setSongayphep(sp.isEmpty() ? 0 : Integer.parseInt(sp));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Số ngày phép phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
                soNgayPhepField.requestFocusInWindow();
                return;
            }

            nv.setHoten(hoten);
            nv.setGioitinh(String.valueOf(genderBox.getSelectedItem()));
            nv.setEmail(email);
            nv.setDienthoai(dienthoai);
            nv.setDiachi(addressField.getText().trim());
            nv.setTrangthai(normalizeTrangThai(String.valueOf(statusBox.getSelectedItem())));

            if (nhanVienHRDAO.update(nv)) {
                loadMasterDataFromDb();
                refreshTableFromMaster(currentKeyword, currentDeptFilter);
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi lưu vào database.");
            }
        });
        btnPanel.add(okBtn);
        btnPanel.add(cancelBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setMinimumSize(new Dimension(480, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

}