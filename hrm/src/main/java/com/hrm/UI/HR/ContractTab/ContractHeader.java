package com.hrm.UI.HR.ContractTab;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.hrm.DAO.ContractDAO;
import com.hrm.DTO.ContractDTO;

public class ContractHeader extends JPanel {
    private ContractStatsCard totalCard;
    private ContractStatsCard activeCard;
    private ContractStatsCard soonCard;
    private ContractStatsCard expiredCard;
    private JButton addButton;
    private Runnable onAddCallback;
    private ContractDAO contractDAO;

    public ContractHeader() {
        contractDAO = new ContractDAO();
        initComponent();
        loadStats();
    }

    public void setOnAddCallback(Runnable callback) {
        this.onAddCallback = callback;
    }

    private void initComponent() {
        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        
        // Top: Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản lý hợp đồng");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        
        JLabel subtitleLabel = new JLabel("Quản lý hợp đồng lao động nhân viên");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);
        
        titlePanel.add(textPanel, BorderLayout.WEST);
        
        // Nút Thêm hợp đồng
        addButton = new JButton("+ Thêm hợp đồng");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(156, 39, 176)); // Purple
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(180, 40));
        addButton.addActionListener(e -> {
            if (onAddCallback != null) {
                onAddCallback.run();
            }
        });
        
        titlePanel.add(addButton, BorderLayout.EAST);
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Stats Cards
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        totalCard = new ContractStatsCard("Tổng hợp đồng", 0, "icons/document.svg", new Color(156, 39, 176));
        activeCard = new ContractStatsCard("Đang hiệu lực", 0, "icons/check.svg", new Color(76, 175, 80));
        soonCard = new ContractStatsCard("Sắp hết hạn", 0, "icons/warning.svg", new Color(255, 193, 7));
        expiredCard = new ContractStatsCard("Đã hết hạn", 0, "icons/error.svg", new Color(244, 67, 54));
        
        statsPanel.add(totalCard);
        statsPanel.add(activeCard);
        statsPanel.add(soonCard);
        statsPanel.add(expiredCard);
        
        add(statsPanel, BorderLayout.CENTER);
    }

    public void loadStats() {
        List<ContractDTO> contracts = contractDAO.getAllContracts();
        
        int total = contracts.size();
        int active = 0;
        int soon = 0;
        int expired = 0;
        
        for (ContractDTO contract : contracts) {
            String status = contract.trangThai;
            if (status.equals("Đang hiệu lực")) {
                active++;
            } else if (status.equals("Sắp hết hạn")) {
                soon++;
            } else if (status.equals("Hết hạn")) {
                expired++;
            }
        }
        
        // Update cards with new values
        updateStatsCard(totalCard, total);
        updateStatsCard(activeCard, active);
        updateStatsCard(soonCard, soon);
        updateStatsCard(expiredCard, expired);
    }

    private void updateStatsCard(ContractStatsCard card, int value) {
        // Re-create the card with new value
        JPanel parent = (JPanel) card.getParent();
        int index = -1;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == card) {
                index = i;
                break;
            }
        }
        
        if (index >= 0) {
            ContractStatsCard oldCard = card;
            if (card == totalCard) {
                card = new ContractStatsCard("Tổng hợp đồng", value, "icons/document.svg", new Color(156, 39, 176));
                totalCard = card;
            } else if (card == activeCard) {
                card = new ContractStatsCard("Đang hiệu lực", value, "icons/check.svg", new Color(76, 175, 80));
                activeCard = card;
            } else if (card == soonCard) {
                card = new ContractStatsCard("Sắp hết hạn", value, "icons/warning.svg", new Color(255, 193, 7));
                soonCard = card;
            } else if (card == expiredCard) {
                card = new ContractStatsCard("Đã hết hạn", value, "icons/error.svg", new Color(244, 67, 54));
                expiredCard = card;
            }
            parent.remove(index);
            parent.add(card, index);
            parent.revalidate();
            parent.repaint();
        }
    }
}
