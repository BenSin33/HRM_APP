package com.hrm.DTO;

public class AccountManagerDTO {
    public String maNV;
    public String hoTen;
    public String phongBan;
    public String roleId;
    public String roleName;
    public int status;
    public String email;
    public String dienThoai;

    public AccountManagerDTO() {}

    public AccountManagerDTO(String maNV, String hoTen, String phongBan,
                             String roleId, String roleName, int status, String email, String dienThoai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.phongBan = phongBan;
        this.roleId = roleId;
        this.roleName = roleName;
        this.status = status;
        this.email = email;
        this.dienThoai = dienThoai;
    }

    public String getStatusText() {
        return status == 1 ? "Kích hoạt" : "Vô hiệu";
    }
}
