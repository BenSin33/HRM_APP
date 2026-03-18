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

import com.hrm.DAO.PositionDAO;
import com.hrm.DAO.TrinhDoDAO;
import com.hrm.DTO.HR.DepartmentDTO;
import com.hrm.DTO.Manager.NhanVienDTO;
import com.hrm.DTO.PositionDTO;
import com.hrm.DTO.TrinhDoDTO;
import com.hrm.DTO.UserDTO;
import com.hrm.Service.DepartmentService;
import com.hrm.Service.PermissionService;
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

        // Kiểm tra quyền ADD cho chức năng nhân viên
        UserDTO currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null || !permissionService.canAdd(currentUser, "CN02_EMPLOYEE")) {
            addBtn.setEnabled(false);
            addBtn.setToolTipText("Bạn không có quyền thêm nhân viên");
        }

        // Action mở form thêm nhân viên
        addBtn.addActionListener(e -> openAddEmployeeForm());

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

        JTextField idField = new JTextField(20);
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

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
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

        int result = JOptionPane.showConfirmDialog(null, form,
                "Thêm nhân viên mới", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
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
            String machucvu = (posIdx > 0 && posIdx <= positions.size()) ? positions.get(posIdx - 1).getMaChucVu() : "CV02";

            int tdIdx = trinhDoBox.getSelectedIndex();
            String matrinhdo = (tdIdx > 0 && tdIdx <= trinhDoList.size()) ? trinhDoList.get(tdIdx - 1).getMaTrinhDo() : "TD01";

            LocalDate ngayvaolam = null;
            String ngayStr = ngayVaoLamField.getText().trim();
            if (!ngayStr.isEmpty()) {
                try {
                    ngayvaolam = LocalDate.parse(ngayStr, DateTimeFormatter.ISO_LOCAL_DATE);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(null, "Ngày vào làm không hợp lệ. Dùng định dạng yyyy-MM-dd.");
                    return;
                }
            }

            int songayphep = 12;
            try {
                String sp = soNgayPhepField.getText().trim();
                if (!sp.isEmpty()) songayphep = Integer.parseInt(sp);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Số ngày phép phải là số nguyên.");
                return;
            }

            String trangthai = normalizeTrangThai(String.valueOf(statusBox.getSelectedItem()));

            // ========== Kiểm tra dữ liệu đầu vào ==========
            if (manv.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Mã NV không được trống!");
                return;
            }
            if (nhanVienHRDAO.findById(manv) != null) {
                JOptionPane.showMessageDialog(null, "Mã NV đã tồn tại (không trùng)!");
                return;
            }
            if (hoten.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Họ tên không được trống!");
                return;
            }
            if (!email.isEmpty() && !email.matches("^[a-zA-Z0-9._-]+@company\\.com$")) {
                JOptionPane.showMessageDialog(null, "Email phải đúng format: chữ@company.com (ví dụ: abc.xyz@company.com)");
                return;
            }
            if (!dienthoai.isEmpty() && !dienthoai.matches("^\\d{10}$")) {
                JOptionPane.showMessageDialog(null, "Điện thoại phải là đúng 10 chữ số!");
                return;
            }
            if (maphongban == null || maphongban.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Phải chọn Phòng ban!");
                return;
            }
            if (posIdx <= 0) {
                JOptionPane.showMessageDialog(null, "Phải chọn Chức vụ!");
                return;
            }
            if (tdIdx <= 0) {
                JOptionPane.showMessageDialog(null, "Phải chọn Trình độ!");
                return;
            }

            NhanVienDTO dto = new NhanVienDTO(manv, maphongban, machucvu, matrinhdo, hoten, gioitinh, diachi, dienthoai, email, ngayvaolam, songayphep, trangthai);
            if (nhanVienHRDAO.insert(dto)) {
                loadMasterDataFromDb();
                refreshTableFromMaster(currentKeyword, currentDeptFilter);
                JOptionPane.showMessageDialog(null, "Thêm nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(null, "Lỗi khi lưu vào database.");
            }
        }
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

        JTextField idField = new JTextField(nv.getManv());
        idField.setEditable(false);
        JTextField nameField = new JTextField(nv.getHoten());
        JTextField emailField = new JTextField(nv.getEmail());
        JTextField deptField = new JTextField(nv.getMaphongban() != null ? nv.getMaphongban() : "");
        String tenChucVu = chucVuDAO.getTenChucVu(nv.getMachucvu());
        JTextField posField = new JTextField(tenChucVu != null ? tenChucVu : nv.getMachucvu());
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Đang làm việc", "Nghỉ việc"});
        statusBox.setSelectedItem(nv.getTrangthai() != null ? nv.getTrangthai() : "Đang làm việc");

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Mã NV:"));
        form.add(idField);
        form.add(new JLabel("Họ và tên:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Phòng ban:"));
        form.add(deptField);
        form.add(new JLabel("Chức vụ:"));
        form.add(posField);
        form.add(new JLabel("Trạng thái:"));
        form.add(statusBox);

        int result = JOptionPane.showConfirmDialog(null, form,
                "Sửa nhân viên", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            nv.setHoten(nameField.getText().trim());
            nv.setEmail(emailField.getText().trim());
            nv.setMaphongban(deptField.getText().trim());
            nv.setMachucvu(chucVuDAO.getMaChucVuByTen(posField.getText().trim()));
            nv.setTrangthai(normalizeTrangThai(String.valueOf(statusBox.getSelectedItem())));

            if (nhanVienHRDAO.update(nv)) {
                loadMasterDataFromDb();
                refreshTableFromMaster(currentKeyword, currentDeptFilter);
                JOptionPane.showMessageDialog(null, "Cập nhật nhân viên thành công!");
            } else {
                JOptionPane.showMessageDialog(null, "Lỗi khi lưu vào database.");
            }
        }
    }

}