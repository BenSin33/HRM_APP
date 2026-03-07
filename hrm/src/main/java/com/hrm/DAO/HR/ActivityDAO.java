package com.hrm.DAO.HR;

import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.HR.ActivityDTO;

/**
 * Simple in-memory DAO for managing recent activities on the HR dashboard.
 */
public class ActivityDAO {
    private List<ActivityDTO> activities;

    public ActivityDAO() {
        activities = new ArrayList<>();
        // Initialize with default activities
        activities.add(new ActivityDTO("Nguyễn Văn A đã được tuyển dụng vào phòng IT"));
        activities.add(new ActivityDTO("Trần Thị B đã gửi đơn xin nghỉ phép"));
        activities.add(new ActivityDTO("Đã chốt bảng lương tháng 12"));
        activities.add(new ActivityDTO("Hoàn thành đánh giá quý 4"));
    }

    public List<ActivityDTO> getAll() {
        return new ArrayList<>(activities);
    }

    public void add(ActivityDTO activity) {
        if (activity.getId() == null) {
            activity.setId(java.util.UUID.randomUUID().toString());
        }
        activities.add(activity);
    }

    public void delete(String id) {
        activities.removeIf(a -> a.getId().equals(id));
    }
}
