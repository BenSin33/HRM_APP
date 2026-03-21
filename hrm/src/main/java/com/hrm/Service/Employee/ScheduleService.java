package com.hrm.Service.Employee;

import com.hrm.DAO.Employee.ScheduleDAO;
import com.hrm.DTO.Employee.ScheduleDTO;

import java.time.LocalDate;
import java.util.List;

public class ScheduleService {
    private ScheduleDAO scheduleDAO;

    public ScheduleService() {
        this.scheduleDAO = new ScheduleDAO();
    }

    public List<ScheduleDTO> getAllShifts() {
        return scheduleDAO.getAllShifts();
    }

    public List<ScheduleDTO> getSchedulesForEmployeeAndWeek(String manv, LocalDate weekStart) {
        return scheduleDAO.getSchedulesForEmployeeAndWeek(manv, weekStart);
    }

    public ScheduleDTO getScheduleByEmployeeAndDate(String manv, LocalDate date) {
        return scheduleDAO.getScheduleByEmployeeAndDate(manv, date);
    }
}
