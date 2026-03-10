package com.hrm.Service;
import com.hrm.DTO.Manager.NghiPhepDTO;
import com.hrm.DAO.NghiPhepDAO;
import java.util.List;

public class NghiPhepService {
    private NghiPhepDAO dao;
    
    public NghiPhepService() {
        dao = new NghiPhepDAO();
    }
    
    // Lấy data cho UI (format Object[][])
    // Thứ tự theo .sql: [0]=MANGHIPHEP, [1]=MANV, [2]=LOAINGHI, [3]=LYDONGHI, 
    // [4]=NGAYNGHI, [5]=NGAYLAMLAI, [6]=NGUOIDUYET, [7]=NGAYDUYET, [8]=TRANGTHAI, [9]=LYDOTUCHOI, [10]=TENNV
    public Object[][] getTableDataForLeave() {
        List<NghiPhepDTO> list = dao.getAll();
        System.out.println("[NghiPhepService] Received " + list.size() + " records from DAO");
        Object[][] data = new Object[list.size()][11];
        
        for (int i = 0; i < list.size(); i++) {
            NghiPhepDTO np = list.get(i);
            data[i][0] = np.getManghiphep();                                      // MANGHIPHEP
            data[i][1] = np.getManv();                                             // MANV
            data[i][2] = np.getLoainghi();                                         // LOAINGHI
            data[i][3] = np.getLydonghi();                                         // LYDONGHI
            data[i][4] = np.getNgaynghi() != null ? np.getNgaynghi().toString() : "";  // NGAYNGHI
            data[i][5] = np.getNgaylamlai() != null ? np.getNgaylamlai().toString() : ""; // NGAYLAMLAI
            data[i][6] = np.getNguoiduyet() != null ? np.getNguoiduyet() : "";    // NGUOIDUYET
            data[i][7] = np.getNgayduyet() != null ? np.getNgayduyet().toString() : ""; // NGAYDUYET
            data[i][8] = np.getTrangthai();                                        // TRANGTHAI
            data[i][9] = np.getLydotuchoi() != null ? np.getLydotuchoi() : "";    // LYDOTUCHOI
            data[i][10] = np.getTennv();                                           // TENNV (for display)
        }
        return data;
    }
    
    // Duyệt đơn
    public boolean duyetDon(String manghiphep, String nguoiduyet) {
        return dao.duyetDon(manghiphep, nguoiduyet);
    }
    
    // Từ chối đơn
    public boolean tuChoiDon(String manghiphep, String nguoiduyet, String lydo) {
        return dao.tuChoiDon(manghiphep, nguoiduyet, lydo);
    }
    
    // Thống kê
    public int countChoDuyet() {
        return dao.countByTrangThai("Chờ duyệt");
    }
    
    public int countDaDuyet() {
        return dao.countByTrangThai("Đã duyệt");
    }
    
    public int countTuChoi() {
        return dao.countByTrangThai("Từ chối");
    }

    public int countOnLeaveToday() {
        return dao.countOnLeaveToday();
    }
}
