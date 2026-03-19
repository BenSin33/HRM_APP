package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.HomeDAO;
import com.hrm.DAO.Employee.HomeReportDAO;
import com.hrm.DTO.Employee.HomeDTO;
import com.hrm.DTO.Employee.HomeReportDTO;

public class HomeService {
    private HomeDAO homeDAO;
    private HomeReportDAO homeReportDAO;

    public HomeService() {
        this.homeDAO = new HomeDAO();
        this.homeReportDAO = new HomeReportDAO();
    }

    public HomeDTO getHomeHeaderData(String manv) {
        return homeDAO.getHomeHeaderData(manv);
    }

    public HomeReportDTO getReportData(String manv) {
        return homeReportDAO.getReportData(manv);
    }
}
