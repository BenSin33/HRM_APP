package com.hrm.utils;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class để validate các form input
 */
public class FormValidator {

    /**
     * Validate email
     * @param email Email string
     * @return true nếu hợp lệ
     */
    public static boolean isValidEmail(String email) {
        email = email.trim();
        if (email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    /**
     * Validate số điện thoại
     * @param phone Số điện thoại
     * @return true nếu hợp lệ (ít nhất 9 chữ số)
     */
    public static boolean isValidPhone(String phone) {
        if (phone.trim().isEmpty()) {
            return true; // Optional field
        }
        // Chỉ cho phép số, dấu +, dấu gạch ngang, khoảng trắng, và dấu ngoặc
        if (!phone.matches("[0-9+\\-\\s()]*")) {
            return false;
        }
        // Phải có ít nhất 9 chữ số
        long digitCount = phone.replaceAll("[^0-9]", "").length();
        return digitCount >= 9;
    }

    /**
     * Validate số tiền (positive integer)
     * @param amount Số tiền dạng string
     * @return true nếu hợp lệ
     */
    public static boolean isValidAmount(String amount) {
        if (amount.trim().isEmpty()) {
            return false;
        }
        try {
            BigDecimal value = new BigDecimal(amount.replaceAll("[^0-9]", ""));
            return value.compareTo(BigDecimal.ZERO) > 0 &&
                   value.compareTo(new BigDecimal("1000000000")) <= 0; // Max 1 tỷ
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate ngày theo định dạng dd/MM/yyyy
     * @param dateString Chuỗi ngày
     * @return LocalDate nếu hợp lệ, null nếu không
     */
    public static LocalDate parseDate(String dateString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(dateString.trim(), formatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Validate hai ngày (từ <= đến)
     * @param fromDate Ngày từ
     * @param toDate Ngày đến
     * @return true nếu hợp lệ
     */
    public static boolean isValidDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate == null || toDate == null) {
            return false;
        }
        return !toDate.isBefore(fromDate);
    }

    /**
     * Validate mã (chỉ chứa chữ hoa, số, gạch chân)
     * @param code Mã
     * @return true nếu hợp lệ
     */
    public static boolean isValidCode(String code) {
        code = code.trim();
        if (code.isEmpty()) {
            return false;
        }
        return code.matches("[A-Z0-9_]+");
    }

    /**
     * Validate tên (chứa chữ, số, khoảng trắng, không chứa ký tự đặc biệt)
     * @param name Tên
     * @return true nếu hợp lệ
     */
    public static boolean isValidName(String name) {
        name = name.trim();
        if (name.isEmpty()) {
            return false;
        }
        // Cho phép chữ (Latin + Unicode), số, khoảng trắng, dấu gạch ngang
        return name.matches("^[\\p{L}0-9\\s\\-]+$");
    }

    /**
     * Hiện thông báo lỗi
     * @param parent Parent component
     * @param message Thông báo
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Hiện thông báo cảnh báo
     * @param parent Parent component
     * @param message Thông báo
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Hiện thông báo thành công
     * @param parent Parent component
     * @param message Thông báo
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}
