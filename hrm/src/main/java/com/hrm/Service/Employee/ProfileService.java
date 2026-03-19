package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.ProfileDAO;
import com.hrm.DTO.Employee.ProfileDTO;

public class ProfileService {
    private ProfileDAO profileDAO;

    public ProfileService() {
        this.profileDAO = new ProfileDAO();
    }

    public ProfileDTO getFullProfile(String manv) {
        return profileDAO.getFullProfile(manv);
    }
}
