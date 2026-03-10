package com.hrm.DTO.HR;

import java.time.LocalDateTime;

/**
 * DTO đơn giản cho các nhiệm vụ trong bảng điều khiển HR.
 */
public class TaskDTO {
    private String id;
    private String content;
    private boolean completed;
    private LocalDateTime createdAt;

    public TaskDTO() {
    }

    public TaskDTO(String id, String content, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public TaskDTO(String content) {
        this.id = java.util.UUID.randomUUID().toString();
        this.content = content;
        this.completed = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
