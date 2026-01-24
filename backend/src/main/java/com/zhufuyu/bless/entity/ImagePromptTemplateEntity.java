package com.zhufuyu.bless.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "image_prompt_templates")
public class ImagePromptTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "template_content", nullable = false, length = 5000)
    private String templateContent;

    @Column(name = "placeholder_keywords", length = 500)
    private String placeholderKeywords;

    @Column(name = "template_status")
    private Integer templateStatus = 1; // 1-启用, 0-禁用

    @Column(name = "template_parameters", columnDefinition = "TEXT")
    private String templateParameters;

    @Column(name = "template_image_path", length = 500)
    private String templateImagePath;

    @Column(name = "template_image_url", length = 500)
    private String templateImageUrl;

    @Column(name = "is_deleted")
    private Integer isDeleted = 0; // 0-未删除, 1-已删除

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Column(name = "status_changed_time", nullable = false)
    private LocalDateTime statusChangedTime;

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        statusChangedTime = LocalDateTime.now();
        if (isDeleted == null) isDeleted = 0;
        if (templateStatus == null) templateStatus = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
