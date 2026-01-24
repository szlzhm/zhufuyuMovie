package com.zhufuyu.bless.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "image_generation_tasks")
public class ImageGenerationTaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prompt_id", nullable = false)
    private Long promptId;

    @Column(name = "resolution", nullable = false, length = 50)
    private String resolution;

    @Column(name = "num_images")
    private Integer numImages = 1;

    @Column(name = "seed")
    private Long seed = -1L;

    @Column(name = "smart_optimization")
    private Integer smartOptimization = 0; // 0-否, 1-是

    @Column(name = "inference_steps")
    private Integer inferenceSteps = 4;

    @Column(name = "cfg_scale")
    private Double cfgScale = 1.0;

    @Column(name = "enable_custom_params")
    private Boolean enableCustomParams = false;

    @Column(name = "custom_params", columnDefinition = "TEXT")
    private String customParams;

    @Column(name = "negative_prompt", columnDefinition = "TEXT")
    private String negativePrompt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "task_status", nullable = false, length = 20)
    private String taskStatus = "WAITING"; // WAITING, PROCESSING, COMPLETED, FAILED, CANCELED, PAUSED

    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime;

    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;

    @Column(name = "status_changed_time", nullable = false)
    private LocalDateTime statusChangedTime;

    @PrePersist
    protected void onCreate() {
        createdTime = LocalDateTime.now();
        updatedTime = LocalDateTime.now();
        statusChangedTime = LocalDateTime.now();
        if (numImages == null) numImages = 1;
        if (seed == null) seed = -1L;
        if (smartOptimization == null) smartOptimization = 0;
        if (inferenceSteps == null) inferenceSteps = 4;
        if (cfgScale == null) cfgScale = 1.0;
        if (enableCustomParams == null) enableCustomParams = false;
        if (taskStatus == null) taskStatus = "WAITING";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedTime = LocalDateTime.now();
    }
}
