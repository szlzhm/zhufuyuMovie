package com.zhufuyu.bless.service;

import com.zhufuyu.bless.entity.ImagePromptTemplateEntity;
import com.zhufuyu.bless.model.request.ImagePromptSubmitReq;
import com.zhufuyu.bless.repository.ImagePromptTemplateRepository;
import com.zhufuyu.bless.repository.SysConfigRepository;
import com.zhufuyu.bless.repository.VideoTaskRepository;
import com.zhufuyu.bless.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ImageCreationServiceTest {

    @Autowired
    private ImageCreationService imageCreationService;

    @Autowired
    private ImagePromptTemplateRepository templateRepository;

    @MockBean
    private QwenImageGenerationService qwenImageGenerationService;

    @MockBean
    private SysConfigRepository sysConfigRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testSubmitTask() {
        // Mock static resource config
        when(sysConfigRepository.findByConfigKey(anyString())).thenReturn(Optional.empty());

        // 1. Create a template
        ImagePromptTemplateEntity template = new ImagePromptTemplateEntity();
        template.setTemplateContent("Content with {{VAR}}");
        template.setPlaceholderKeywords("VAR");
        template.setTemplateStatus(1);
        template = templateRepository.save(template);

        // 2. Submit task
        ImagePromptSubmitReq req = new ImagePromptSubmitReq();
        req.setTemplateId(template.getId());
        req.setPromptContent("Content with Value");
        req.setResolution("1024*1024");
        req.setNumImages(1);
        
        Long taskId = imageCreationService.submitImageTask(req);
        assertNotNull(taskId);
    }
}
