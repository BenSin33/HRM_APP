package com.hrm.DTO;

/**
 * DTO cho Phòng ban (Department)
 */
public class DepartmentDTO {
    private String maphongban;
    private String tenphongban;
    private String mota;

    public DepartmentDTO() {}

    public DepartmentDTO(String maphongban, String tenphongban, String mota) {
        this.maphongban = maphongban;
        this.tenphongban = tenphongban;
        this.mota = mota;
    }

    public String getMaphongban() {
        return maphongban;
    }

    public void setMaphongban(String maphongban) {
        this.maphongban = maphongban;
    }

    public String getTenphongban() {
        return tenphongban;
    }

    public void setTenphongban(String tenphongban) {
        this.tenphongban = tenphongban;
    }

    public String getMota() {
        return mota;
    }

    public void setMota(String mota) {
        this.mota = mota;
    }
}
