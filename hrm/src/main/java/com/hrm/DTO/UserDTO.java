package com.hrm.DTO;

public class UserDTO {
    private String manv;
    private String roleId;
    private String username;

    // Constructor rỗng
    public UserDTO() {}

    // Constructor đầy đủ
    public UserDTO(String manv, String roleId, String username) {
        this.manv = manv;
        this.roleId = roleId;
        this.username = username;
    }

    // Constructor cho xác thực (username + password)
    public UserDTO(String username, String password) {
        this.username = username;
    }

    // Getters và Setters
    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
