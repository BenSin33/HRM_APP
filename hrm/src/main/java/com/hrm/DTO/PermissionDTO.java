package com.hrm.DTO;

public class PermissionDTO {
    private String roleId;
    private String manv;
    private String machucNang;
    private String tenChucNang;
    private boolean quyenXem;
    private boolean quyenThem;
    private boolean quyenSua;
    private boolean quyenXoa;
    private boolean quyenDuyet;
    private boolean quyenXuatBaoCao;
    private boolean userOverride;

    // Constructor đầy đủ
    public PermissionDTO(String roleId, String machucNang, String tenChucNang, 
                         boolean quyenXem, boolean quyenThem, boolean quyenSua, boolean quyenXoa) {
        this.roleId = roleId;
        this.machucNang = machucNang;
        this.tenChucNang = tenChucNang;
        this.quyenXem = quyenXem;
        this.quyenThem = quyenThem;
        this.quyenSua = quyenSua;
        this.quyenXoa = quyenXoa;
    }

    public PermissionDTO(String roleId, String manv, String machucNang, String tenChucNang,
                         boolean quyenXem, boolean quyenThem, boolean quyenSua, boolean quyenXoa,
                         boolean quyenDuyet, boolean quyenXuatBaoCao, boolean userOverride) {
        this.roleId = roleId;
        this.manv = manv;
        this.machucNang = machucNang;
        this.tenChucNang = tenChucNang;
        this.quyenXem = quyenXem;
        this.quyenThem = quyenThem;
        this.quyenSua = quyenSua;
        this.quyenXoa = quyenXoa;
        this.quyenDuyet = quyenDuyet;
        this.quyenXuatBaoCao = quyenXuatBaoCao;
        this.userOverride = userOverride;
    }

    // Constructor rỗng
    public PermissionDTO() {
    }

    // Getters và Setters
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getMachucNang() {
        return machucNang;
    }

    public String getManv() {
        return manv;
    }

    public void setManv(String manv) {
        this.manv = manv;
    }

    public void setMachucNang(String machucNang) {
        this.machucNang = machucNang;
    }

    public String getTenChucNang() {
        return tenChucNang;
    }

    public void setTenChucNang(String tenChucNang) {
        this.tenChucNang = tenChucNang;
    }

    public boolean isQuyenXem() {
        return quyenXem;
    }

    public void setQuyenXem(boolean quyenXem) {
        this.quyenXem = quyenXem;
    }

    public boolean isQuyenThem() {
        return quyenThem;
    }

    public void setQuyenThem(boolean quyenThem) {
        this.quyenThem = quyenThem;
    }

    public boolean isQuyenSua() {
        return quyenSua;
    }

    public void setQuyenSua(boolean quyenSua) {
        this.quyenSua = quyenSua;
    }

    public boolean isQuyenXoa() {
        return quyenXoa;
    }

    public void setQuyenXoa(boolean quyenXoa) {
        this.quyenXoa = quyenXoa;
    }

    public boolean isQuyenDuyet() {
        return quyenDuyet;
    }

    public void setQuyenDuyet(boolean quyenDuyet) {
        this.quyenDuyet = quyenDuyet;
    }

    public boolean isQuyenXuatBaoCao() {
        return quyenXuatBaoCao;
    }

    public void setQuyenXuatBaoCao(boolean quyenXuatBaoCao) {
        this.quyenXuatBaoCao = quyenXuatBaoCao;
    }

    public boolean isUserOverride() {
        return userOverride;
    }

    public void setUserOverride(boolean userOverride) {
        this.userOverride = userOverride;
    }

    @Override
    public String toString() {
        return "PermissionDTO{" +
                "roleId='" + roleId + '\'' +
            ", manv='" + manv + '\'' +
                ", machucNang='" + machucNang + '\'' +
                ", tenChucNang='" + tenChucNang + '\'' +
                ", quyenXem=" + quyenXem +
                ", quyenThem=" + quyenThem +
                ", quyenSua=" + quyenSua +
            ", quyenXoa=" + quyenXoa +
            ", quyenDuyet=" + quyenDuyet +
            ", quyenXuatBaoCao=" + quyenXuatBaoCao +
            ", userOverride=" + userOverride +
                '}';
    }
}
