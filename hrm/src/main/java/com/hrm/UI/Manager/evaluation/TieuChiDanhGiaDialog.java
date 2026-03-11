package com.hrm.UI.Manager.evaluation;

import com.hrm.DAO.PhieuDanhGiaDAO;
import com.hrm.DAO.TieuChiDanhGiaDAO;
import com.hrm.DTO.Manager.TieuChiDanhGiaDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TieuChiDanhGiaDialog extends JDialog {

    private final String maNV;
    private final String hoTen;
    private final String maDot;

    private final TieuChiDanhGiaDAO tieuChiDAO = new TieuChiDanhGiaDAO();
    private final PhieuDanhGiaDAO phieuDAO = new PhieuDanhGiaDAO();

    private JTable table;
    private JTextArea txtNhanXet;
    private CriteriaTableModel model;
    private boolean locked;

    public interface OnSavedListener {
        void onSaved();
    }

    public TieuChiDanhGiaDialog(Window owner, String maNV, String hoTen, String maDot, OnSavedListener onSaved) {
        super(owner, "Chấm điểm - " + hoTen + " (" + maNV + ")", ModalityType.APPLICATION_MODAL);
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.maDot = maDot;

        initUI(onSaved);
    }

    private void initUI(OnSavedListener onSaved) {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        locked = phieuDAO.hasEvaluation(maNV, maDot);
        JLabel lblTop = new JLabel("Kỳ đánh giá: " + maDot + " • " + hoTen + (locked ? "  (Đã lưu)" : ""));
        lblTop.setFont(new Font("Segoe UI", Font.BOLD, 14));
        root.add(lblTop, BorderLayout.NORTH);

        List<TieuChiDanhGiaDTO> criteria = tieuChiDAO.getAll();
        model = new CriteriaTableModel(criteria);
        table = new JTable(model);
        table.setRowHeight(34);
        table.setFillsViewportHeight(true);

        // Spinner editor (0..10)
        table.getColumnModel().getColumn(2).setCellEditor(new SpinnerCellEditor(0, 10));
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(360);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);

        JScrollPane spTable = new JScrollPane(table);
        root.add(spTable, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(false);

        JPanel nhanXetPanel = new JPanel(new BorderLayout(6, 6));
        nhanXetPanel.setOpaque(false);
        JLabel lblNhanXet = new JLabel("Nhận xét");
        lblNhanXet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtNhanXet = new JTextArea(4, 40);
        txtNhanXet.setLineWrap(true);
        txtNhanXet.setWrapStyleWord(true);
        JScrollPane spNhanXet = new JScrollPane(txtNhanXet);
        nhanXetPanel.add(lblNhanXet, BorderLayout.NORTH);
        nhanXetPanel.add(spNhanXet, BorderLayout.CENTER);

        bottom.add(nhanXetPanel, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        JButton btnCancel = new JButton("Hủy");
        JButton btnSave = new JButton("Lưu điểm");
        JButton btnReset = new JButton("Reset");

        btnCancel.addActionListener(e -> dispose());
        btnSave.addActionListener(e -> {
            if (table.isEditing()) table.getCellEditor().stopCellEditing();
            Map<String, Integer> diem = model.getScoresByCriteria();
            boolean ok = phieuDAO.upsertEvaluation(maNV, maDot, diem, txtNhanXet.getText());
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã lưu phiếu đánh giá.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSaved != null) onSaved.onSaved();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu phiếu đánh giá thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReset.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Reset sẽ xóa phiếu đánh giá đã lưu và cho phép chấm lại. Bạn chắc chắn?",
                    "Xác nhận reset",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = phieuDAO.resetEvaluation(maNV, maDot);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Đã reset phiếu đánh giá.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                if (onSaved != null) onSaved.onSaved();
            } else {
                JOptionPane.showMessageDialog(this, "Reset thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        if (locked) {
            table.setEnabled(false);
            txtNhanXet.setEditable(false);
            btnSave.setEnabled(false);
            btnReset.setEnabled(true);
        } else {
            btnReset.setEnabled(false);
        }

        actions.add(btnCancel);
        actions.add(btnReset);
        actions.add(btnSave);
        bottom.add(actions, BorderLayout.SOUTH);

        root.add(bottom, BorderLayout.SOUTH);

        setSize(650, 520);
        setLocationRelativeTo(getOwner());
    }

    public static String defaultMaDotNow() {
        LocalDate now = LocalDate.now();
        int q = ((now.getMonthValue() - 1) / 3) + 1;
        return "Q" + q + "-" + now.getYear();
    }

    private static class CriteriaTableModel extends AbstractTableModel {
        private final List<TieuChiDanhGiaDTO> criteria;
        private final int[] scores;
        private final String[] cols = {"Mã", "Tiêu chí", "Điểm (0-10)"};

        CriteriaTableModel(List<TieuChiDanhGiaDTO> criteria) {
            this.criteria = criteria;
            this.scores = new int[criteria != null ? criteria.size() : 0];
        }

        @Override public int getRowCount() { return scores.length; }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int column) { return cols[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TieuChiDanhGiaDTO tc = criteria.get(rowIndex);
            switch (columnIndex) {
                case 0: return tc.getMaTieuChi();
                case 1: return tc.getTenTieuChi();
                case 2: return scores[rowIndex];
                default: return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 2;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex != 2) return;
            int v = 0;
            if (aValue instanceof Number) v = ((Number) aValue).intValue();
            else if (aValue != null) {
                try { v = Integer.parseInt(aValue.toString()); } catch (NumberFormatException ignored) {}
            }
            if (v < 0) v = 0;
            if (v > 10) v = 10;
            scores[rowIndex] = v;
            fireTableCellUpdated(rowIndex, columnIndex);
        }

        Map<String, Integer> getScoresByCriteria() {
            Map<String, Integer> map = new LinkedHashMap<>();
            for (int i = 0; i < scores.length; i++) {
                map.put(criteria.get(i).getMaTieuChi(), scores[i]);
            }
            return map;
        }
    }

    private static class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner;

        SpinnerCellEditor(int min, int max) {
            this.spinner = new JSpinner(new SpinnerNumberModel(min, min, max, 1));
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Object getCellEditorValue() {
            Object v = spinner.getValue();
            if (v instanceof Number) return ((Number) v).intValue();
            return 0;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            if (value instanceof Number) spinner.setValue(((Number) value).intValue());
            else spinner.setValue(0);
            return spinner;
        }
    }
}

