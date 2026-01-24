package com.zhufuyu.bless.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.entity.ImagePromptTemplateEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.ImagePromptTemplateQueryReq;
import com.zhufuyu.bless.model.request.ImagePromptTemplateReq;
import com.zhufuyu.bless.model.request.SaveTemplateFromResultReq;
import com.zhufuyu.bless.model.response.ImagePromptTemplateResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.repository.ImagePromptTemplateRepository;
import com.zhufuyu.bless.service.FileConfigService;
import com.zhufuyu.bless.service.ImagePromptTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提示语模板服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImagePromptTemplateServiceImpl implements ImagePromptTemplateService {

    private final ImagePromptTemplateRepository templateRepository;
    private final ObjectMapper objectMapper;
    private final FileConfigService fileConfigService;

    @Override
    @Transactional
    public void saveTemplate(ImagePromptTemplateReq request) {
        ImagePromptTemplateEntity entity;
        if (request.getId() != null) {
            entity = templateRepository.findById(request.getId())
                    .orElseThrow(() -> new BizException(500, "模板不存在"));
            
            // 如果状态发生变化，更新状态变更时间
            if (request.getTemplateStatus() != null && !request.getTemplateStatus().equals(entity.getTemplateStatus())) {
                entity.setStatusChangedTime(LocalDateTime.now());
            }
        } else {
            entity = new ImagePromptTemplateEntity();
            entity.setStatusChangedTime(LocalDateTime.now());
        }

        BeanUtils.copyProperties(request, entity, "id", "parameters");
        
        if (request.getParameters() != null) {
            try {
                entity.setTemplateParameters(objectMapper.writeValueAsString(request.getParameters()));
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize template parameters", e);
            }
        }
        
        templateRepository.save(entity);
    }

    @Override
    public PageResponse<ImagePromptTemplateResp> queryTemplates(ImagePromptTemplateQueryReq request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, "createdTime"));
        Page<ImagePromptTemplateEntity> page;

        if (StringUtils.hasText(request.getTemplateContent())) {
            page = templateRepository.findByIsDeletedAndTemplateContentContaining(0, request.getTemplateContent(), pageable);
        } else {
            page = templateRepository.findByIsDeleted(0, pageable);
        }

        List<ImagePromptTemplateResp> list = page.getContent().stream().map(this::convertToResp).collect(Collectors.toList());
        return new PageResponse<>(page.getNumber() + 1, page.getSize(), page.getTotalElements(), list);
    }

    @Override
    @Transactional
    public void deleteTemplate(Long id) {
        templateRepository.findById(id).ifPresent(entity -> {
            templateRepository.softDeleteById(id);
        });
    }

    @Override
    public List<ImagePromptTemplateResp> getAllActiveTemplates() {
        // 这里简单处理，不分页获取所有启用的
        return templateRepository.findAll().stream()
                .filter(t -> t.getIsDeleted() == 0 && t.getTemplateStatus() == 1)
                .map(this::convertToResp)
                .collect(Collectors.toList());
    }

    @Override
    public ImagePromptTemplateResp getTemplateById(Long id) {
        return templateRepository.findById(id)
                .map(this::convertToResp)
                .orElseThrow(() -> new BizException(500, "模板不存在"));
    }

    @Override
    @Transactional
    public void saveTemplateFromResult(SaveTemplateFromResultReq request) {
        log.info("开始从结果保存为模板, resultId={}, placeholderKeywords={}", 
                 request.getResultId(), request.getPlaceholderKeywords());
        
        // 1. 创建模板实体
        ImagePromptTemplateEntity entity = new ImagePromptTemplateEntity();
        entity.setTemplateContent(request.getTemplateContent());
        entity.setPlaceholderKeywords(request.getPlaceholderKeywords());
        entity.setTemplateStatus(request.getTemplateStatus());
        entity.setStatusChangedTime(LocalDateTime.now());
        
        log.info("设置占位符关键字到实体: placeholderKeywords=[{}], isEmpty={}, isBlank={}", 
                 entity.getPlaceholderKeywords(),
                 entity.getPlaceholderKeywords() == null || entity.getPlaceholderKeywords().isEmpty(),
                 entity.getPlaceholderKeywords() == null || entity.getPlaceholderKeywords().isBlank());
        
        // 2. 处理参数
        if (request.getParameters() != null) {
            try {
                entity.setTemplateParameters(objectMapper.writeValueAsString(request.getParameters()));
            } catch (JsonProcessingException e) {
                log.error("序列化模板参数失败", e);
                throw new BizException(500, "保存模板参数失败");
            }
        }
        
        // 3. 先保存模板（获取自增ID）
        entity = templateRepository.save(entity);
        Long templateId = entity.getId();
        log.info("模板保存成功, templateId={}, 保存后的placeholderKeywords=[{}]", 
                 templateId, entity.getPlaceholderKeywords());
        
        // 4. 复制图片到模板目录
        try {
            String firstImageUrl = request.getFirstImageUrl();
            
            // 从URL提取相对路径（去掉 /bless/web/img/ 前缀）
            String relativePath = firstImageUrl.replace("/bless/web/img/", "").replace("/", "\\");
            
            // 获取文件根路径
            String fileRootPath = fileConfigService.getFileRootPathValue();
            Path sourceFile = Paths.get(fileRootPath, relativePath);
            
            if (!Files.exists(sourceFile)) {
                log.error("源图片文件不存在: {}", sourceFile);
                throw new BizException(500, "源图片文件不存在");
            }
            
            // 生成目标路径: templates/images/{date}/template_{id}_{timestamp}.png
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = String.format("template_%d_%d.png", templateId, System.currentTimeMillis());
            
            Path targetDir = Paths.get(fileRootPath, "templates", "images", dateStr);
            Files.createDirectories(targetDir);
            
            Path targetFile = targetDir.resolve(fileName);
            Files.copy(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            
            // 生成相对路径和URL
            String templateImagePath = String.format("./templates/images/%s/%s", dateStr, fileName);
            String templateImageUrl = String.format("/bless/web/img/./templates/images/%s/%s", dateStr, fileName);
            
            // 更新模板的图片字段
            entity.setTemplateImagePath(templateImagePath);
            entity.setTemplateImageUrl(templateImageUrl);
            entity = templateRepository.save(entity);
            
            log.info("模板图片复制成功: {} -> {}, 最终placeholderKeywords=[{}]", 
                     sourceFile, targetFile, entity.getPlaceholderKeywords());
            
        } catch (IOException e) {
            log.error("复制模板图片失败", e);
            throw new BizException(500, "保存模板图片失败");
        }
        
        log.info("从结果保存为模板完成, templateId={}", templateId);
    }

    private ImagePromptTemplateResp convertToResp(ImagePromptTemplateEntity entity) {
        ImagePromptTemplateResp resp = new ImagePromptTemplateResp();
        BeanUtils.copyProperties(entity, resp);
        
        if (StringUtils.hasText(entity.getTemplateParameters())) {
            try {
                resp.setParameters(objectMapper.readValue(entity.getTemplateParameters(), new TypeReference<java.util.Map<String, Object>>() {}));
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize template parameters", e);
            }
        }
        
        return resp;
    }
}
