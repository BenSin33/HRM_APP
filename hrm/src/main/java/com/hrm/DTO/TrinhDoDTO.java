package com.hrm.DTO;

import java.math.BigDecimal;

/**
 * DTO cho danh mục trình độ (bảng trinhdo)
 */
public class TrinhDoDTO {
    private String maTrinhDo;
    private String trinhDo;
    private BigDecimal heSoTrinhDo;

    public String getMaTrinhDo() {
        return maTrinhDo;
    }

    public void setMaTrinhDo(String maTrinhDo) {
        this.maTrinhDo = maTrinhDo;
    }

    public String getTrinhDo() {
        return trinhDo;
    }

    public void setTrinhDo(String trinhDo) {
        this.trinhDo = trinhDo;
    }

    public BigDecimal getHeSoTrinhDo() {
        return heSoTrinhDo;
    }

    public void setHeSoTrinhDo(BigDecimal heSoTrinhDo) {
        this.heSoTrinhDo = heSoTrinhDo;
    }
}
