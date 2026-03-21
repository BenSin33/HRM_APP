package com.hrm.utils;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }

        if (isBcryptHash(storedPassword)) {
            try {
                return BCrypt.checkpw(rawPassword, storedPassword);
            } catch (IllegalArgumentException ex) {
                return false;
            }
        }

        // Hỗ trợ tài khoản cũ đang lưu plain text
        return rawPassword.equals(storedPassword);
    }

    public static boolean isBcryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]?\\$\\d{2}\\$.{53}$");
    }
}
