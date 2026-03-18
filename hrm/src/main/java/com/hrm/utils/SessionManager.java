package com.hrm.utils;

import com.hrm.DTO.UserDTO;

/**
 * Quản lý session người dùng hiện tại
 * Lưu thông tin user sau khi đăng nhập để sử dụng ở các nơi khác trong ứng dụng
 */
public class SessionManager {

    private static SessionManager instance;
    private UserDTO currentUser;

    private SessionManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserDTO currentUser) {
        this.currentUser = currentUser;
    }

    public void clearSession() {
        this.currentUser = null;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
