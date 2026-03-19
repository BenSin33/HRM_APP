package com.hrm.UI.HR.Attendancetab;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * AttenDanceFilterDialog – dialog lọc nâng cao cho bảng chấm công.
 *
 * Các tiêu chí:
 *   - Phòng ban (combo: Tất cả | Nhân sự | Kỹ thuật | Kinh doanh | Kế toán)
 *   - Tình trạng  (combo: Tất cả | Có đi muộn | Có vắng mặt | Đầy đủ)
 *   - Ngày công từ–đến (spinner int)
 *
 * Callback onApply nhận FilterCriteria khi người dùng bấm "Áp dụng".
 */
public class AttenDanceFilterDialog extends JDialog {

    private static final Color PURPLE  = new Color(124, 58, 237);
    private static final Color GRAY900 = new Color( 17, 24,  39);
    private static final Color GRAY500 = new Color(107,114, 128);
    private static final Color GRAY200 = new Color(229,231, 235);

    // ── Bộ lọc ──────────────────────────────────────────────────
    public static class FilterCriteria {
        public String phongBan     = "Tất cả";
        public String tinhTrang    = "Tất cả";
        public int    minWorkDays  = 0;
        public int    maxWorkDays  = 31;

        public boolean isEmpty() {
            return "Tất cả".equals(phongBan)
                && "Tất cả".equals(tinhTrang)
                && minWorkDays == 0
                && maxWorkDays == 31;
        }
    }

    private JComboBox<String>  cmbPhongBan;
    private JComboBox<String>  cmbTinhTrang;
    private JSpinner           spnMin;
    private JSpinner           spnMax;

    private final Consumer<FilterCriteria> onApply;
    private final List<String>             danhSachPhongBan;

    // ─────────────────────────────────────────────────────────────
    public AttenDanceFilterDialog(Frame parent,
                                   List<String> danhSachPhongBan,
                                   FilterCriteria current,
                                   Consumer<FilterCriteria> onApply) {
        super(parent, "Bộ lọc nâng cao", true);
        this.onApply          = onApply;
        this.danhSachPhongBan = danhSachPhongBan;
        setSize(380, 340);
        setLocationRelativeTo(parent);
        setResizable(false);
        initUI(current);
    }

    // ─────────────────────────────────────────────────────────────
    private void initUI(FilterCriteria current) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,GRAY200),
                BorderFactory.createEmptyBorder(16,20,14,20)));

        JLabel title = new JLabel("Bộ lọc nâng cao");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        title.setForeground(GRAY900);
        header.add(title, BorderLayout.WEST);
        root.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        // Phòng ban
        String[] pbItems = buildPhongBanItems();
        cmbPhongBan = new JComboBox<>(pbItems);
        cmbPhongBan.setSelectedItem(current.phongBan);
        cmbPhongBan.putClientProperty("FlatLaf.style", "arc:8");
        addRow(form, gc, 0, "Phòng ban:", cmbPhongBan);

        // Tình trạng
        cmbTinhTrang = new JComboBox<>(new String[]{
            "Tất cả", "Có đi muộn", "Có vắng mặt", "Đầy đủ (không muộn, không vắng)"
        });
        cmbTinhTrang.setSelectedItem(current.tinhTrang);
        cmbTinhTrang.putClientProperty("FlatLaf.style", "arc:8");
        addRow(form, gc, 1, "Tình trạng:", cmbTinhTrang);

        // Ngày công tối thiểu
        spnMin = new JSpinner(new SpinnerNumberModel(current.minWorkDays, 0, 31, 1));
        spnMin.putClientProperty("FlatLaf.style", "arc:8");
        addRow(form, gc, 2, "Ngày công tối thiểu:", spnMin);

        // Ngày công tối đa
        spnMax = new JSpinner(new SpinnerNumberModel(current.maxWorkDays, 0, 31, 1));
        spnMax.putClientProperty("FlatLaf.style", "arc:8");
        addRow(form, gc, 3, "Ngày công tối đa:", spnMax);

        root.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnBar.setBackground(Color.WHITE);
        btnBar.setBorder(new CompoundBorder(
                new MatteBorder(1,0,0,0,GRAY200),
                BorderFactory.createEmptyBorder(12,20,16,20)));

        JButton btnReset = makeOutlineBtn("Đặt lại");
        btnReset.addActionListener(e -> resetFields());

        JButton btnApply = makePrimaryBtn("Áp dụng");
        btnApply.addActionListener(e -> apply());

        btnBar.add(btnReset);
        btnBar.add(btnApply);
        root.add(btnBar, BorderLayout.SOUTH);
    }

    private String[] buildPhongBanItems() {
        String[] items = new String[danhSachPhongBan.size() + 1];
        items[0] = "Tất cả";
        for (int i = 0; i < danhSachPhongBan.size(); i++)
            items[i+1] = danhSachPhongBan.get(i);
        return items;
    }

    private void addRow(JPanel form, GridBagConstraints gc, int row,
                         String label, JComponent field) {
        gc.gridx=0; gc.gridy=row; gc.gridwidth=1; gc.weightx=0;
        gc.insets = new Insets(0,0,14,12);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(GRAY900);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 13f));
        form.add(lbl, gc);
        gc.gridx=1; gc.gridwidth=2; gc.weightx=1;
        gc.insets = new Insets(0,0,14,0);
        form.add(field, gc);
    }

    private void resetFields() {
        cmbPhongBan.setSelectedIndex(0);
        cmbTinhTrang.setSelectedIndex(0);
        spnMin.setValue(0);
        spnMax.setValue(31);
    }

    private void apply() {
        FilterCriteria fc = new FilterCriteria();
        fc.phongBan   = (String) cmbPhongBan.getSelectedItem();
        fc.tinhTrang  = (String) cmbTinhTrang.getSelectedItem();
        fc.minWorkDays = (Integer) spnMin.getValue();
        fc.maxWorkDays = (Integer) spnMax.getValue();
        if (onApply != null) onApply.accept(fc);
        dispose();
    }

    // ─────────────────────────────────────────────────────────────
    private JButton makePrimaryBtn(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PURPLE);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc:8; background:#7C3AED; borderWidth:0");
        btn.setBorder(BorderFactory.createEmptyBorder(9,20,9,20));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(109,40,217)); }
            @Override public void mouseExited (MouseEvent e) { btn.setBackground(PURPLE); }
        });
        return btn;
    }

    private JButton makeOutlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(GRAY500);
        btn.setBackground(Color.WHITE);
        btn.setFont(btn.getFont().deriveFont(Font.PLAIN, 13f));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new CompoundBorder(new LineBorder(GRAY200,1,true),
                BorderFactory.createEmptyBorder(8,20,8,20)));
        return btn;
    }
}