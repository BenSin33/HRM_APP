package com.hrm.DAO.HR;

import java.util.ArrayList;
import java.util.List;

import com.hrm.DTO.HR.TaskDTO;

/**
 * Simple in-memory DAO for managing tasks on the HR dashboard.
 */
public class TaskDAO {
    private List<TaskDTO> tasks;

    public TaskDAO() {
        tasks = new ArrayList<>();
        // Initialize with default tasks
        tasks.add(new TaskDTO("Duyệt 5 đơn xin nghỉ phép"));
        tasks.add(new TaskDTO("Chốt bảng lương tháng 1"));
        tasks.add(new TaskDTO("Tạo đợt đánh giá Q1 2025"));
        tasks.add(new TaskDTO("Cập nhật thông tin hợp đồng"));
    }

    public List<TaskDTO> getAll() {
        return new ArrayList<>(tasks);
    }

    public void add(TaskDTO task) {
        if (task.getId() == null) {
            task.setId(java.util.UUID.randomUUID().toString());
        }
        tasks.add(task);
    }

    public void delete(String id) {
        tasks.removeIf(t -> t.getId().equals(id));
    }

    public void updateCompleted(String id, boolean completed) {
        tasks.stream()
            .filter(t -> t.getId().equals(id))
            .findFirst()
            .ifPresent(t -> t.setCompleted(completed));
    }
}
