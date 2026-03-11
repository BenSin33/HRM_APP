package com.hrm.DTO.HR;

import java.time.LocalDateTime;

/**
 * DTO đơn giản cho các hoạt động gần đây trong bảng điều khiển HR.
 */
public class ActivityDTO {
    private String id;
    private String content;
    private LocalDateTime createdAt;

    public ActivityDTO() {
    }

    public ActivityDTO(String id, String content, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
    }

    public ActivityDTO(String content) {
        this.id = java.util.UUID.randomUUID().toString();
        this.content = content;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
