package com.hrm.UI.HR.Evaluationtab;

import com.hrm.DAO.HR.EvaluationDAO;
import com.hrm.DTO.HR.EvaluationPeriodDTO;

import javax.swing.*;
import java.awt.*;

public class EvaluationHeader extends JPanel {

    private final EvaluationDAO dao = new EvaluationDAO();

    // Callback để báo EvaluationTable refresh khi tạo đợt mới xong
    private Runnable onPeriodCreated;

    public EvaluationHeader() {
        this(null);
    }

    public EvaluationHeader(Runnable onPeriodCreated) {
        this.onPeriodCreated = onPeriodCreated;

        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // ── Tiêu đề trái ─────────────────────────────────────────
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Quản lý đánh giá hiệu suất");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setForeground(new Color(17, 24, 39));

        JLabel subtitle = new JLabel("Xem & Xét duyệt thưởng/phạt");
        subtitle.setForeground(new Color(107, 114, 128));
        subtitle.setFont(subtitle.getFont().deriveFont(Font.PLAIN, 13f));

        titlePanel.add(title);
        titlePanel.add(subtitle);

        // ── Nút tạo mới phải ─────────────────────────────────────
        JButton createBtn = new JButton("＋  Tạo đợt đánh giá mới");
        createBtn.setFont(createBtn.getFont().deriveFont(Font.BOLD, 13f));
        createBtn.setForeground(Color.WHITE);
        createBtn.setBackground(new Color(124, 58, 237));
        createBtn.setFocusPainted(false);
        createBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createBtn.putClientProperty("FlatLaf.style",
                "arc:10; background:#7C3AED; borderWidth:0");
        createBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effect
        createBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                createBtn.putClientProperty("FlatLaf.style",
                        "arc:10; background:#6D28D9; borderWidth:0");
                createBtn.repaint();
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                createBtn.putClientProperty("FlatLaf.style",
                        "arc:10; background:#7C3AED; borderWidth:0");
                createBtn.repaint();
            }
        });

        // ── Action: mở form tạo đợt mới ──────────────────────────
        createBtn.addActionListener(e -> showCreateDialog());

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        rightWrapper.add(createBtn);

        add(titlePanel,   BorderLayout.WEST);
        add(rightWrapper, BorderLayout.EAST);
    }

    // ─────────────────────────────────────────────────────────────
    // DIALOG TẠO ĐỢT ĐÁNH GIÁ MỚI
    // ─────────────────────────────────────────────────────────────
    private void showCreateDialog() {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Tạo đợt đánh giá mới",
                Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(420, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // ── Form panel ───────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.weightx = 1.0;

        // Tên đợt
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Tên đợt đánh giá:"), gbc);
        gbc.gridy = 1;
        JTextField tenDotField = new JTextField();
        tenDotField.setPreferredSize(new Dimension(350, 34));
        tenDotField.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        form.add(tenDotField, gbc);

        // Kỳ
        gbc.gridy = 2;
        form.add(new JLabel("Kỳ:"), gbc);
        gbc.gridy = 3;
        JComboBox<String> kyBox = new JComboBox<>(new String[]{"Q1", "Q2", "Q3", "Q4"});
        kyBox.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        kyBox.setPreferredSize(new Dimension(350, 34));
        form.add(kyBox, gbc);

        // Năm
        gbc.gridy = 4;
        form.add(new JLabel("Năm:"), gbc);
        gbc.gridy = 5;
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        JSpinner namSpinner = new JSpinner(new SpinnerNumberModel(currentYear, 2020, 2099, 1));
        namSpinner.setPreferredSize(new Dimension(350, 34));
        namSpinner.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        form.add(namSpinner, gbc);

        // Người đánh giá
        gbc.gridy = 6;
        form.add(new JLabel("Người đánh giá:"), gbc);
        gbc.gridy = 7;
        JTextField nguoiDGField = new JTextField();
        nguoiDGField.setPreferredSize(new Dimension(350, 34));
        nguoiDGField.putClientProperty("FlatLaf.style", "arc:8; borderColor:#E5E7EB");
        form.add(nguoiDGField, gbc);

        // ── Button panel ─────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        btnPanel.setBackground(Color.WHITE);

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setPreferredSize(new Dimension(90, 36));
        cancelBtn.putClientProperty("FlatLaf.style",
                "arc:8; background:#F3F4F6; borderColor:#E5E7EB");
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton saveBtn = new JButton("Tạo mới");
        saveBtn.setPreferredSize(new Dimension(100, 36));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBackground(new Color(124, 58, 237));
        saveBtn.putClientProperty("FlatLaf.style",
                "arc:8; background:#7C3AED; borderWidth:0");
        saveBtn.addActionListener(e -> {
            String tenDot   = tenDotField.getText().trim();
            String kyKy     = (String) kyBox.getSelectedItem();
            int    nam      = (Integer) namSpinner.getValue();
            String nguoiDG  = nguoiDGField.getText().trim();

            // Validate
            if (tenDot.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập tên đợt đánh giá!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nguoiDG.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Vui lòng nhập tên người đánh giá!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Build DTO
            EvaluationPeriodDTO dto = new EvaluationPeriodDTO();
            dto.setMaDot(dao.generateMaDot(kyKy, nam));
            dto.setTenDot(tenDot);
            dto.setKyKy(kyKy);
            dto.setNam(nam);
            dto.setNguoiDanhGia(nguoiDG);
            dto.setTrangThai("Đang mở");

            boolean ok = dao.insertPeriod(dto);
            if (ok) {
                JOptionPane.showMessageDialog(dialog,
                        "Tạo đợt đánh giá thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                // Báo parent refresh ComboBox
                if (onPeriodCreated != null) onPeriodCreated.run();
            } else {
                JOptionPane.showMessageDialog(dialog,
                        "Tạo thất bại! Vui lòng thử lại.", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setVisible(true);
    }
}