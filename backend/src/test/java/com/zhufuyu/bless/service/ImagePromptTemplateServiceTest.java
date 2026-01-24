package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.ImagePromptTemplateQueryReq;
import com.zhufuyu.bless.model.request.ImagePromptTemplateReq;
import com.zhufuyu.bless.model.response.ImagePromptTemplateResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.repository.SysConfigRepository;
import com.zhufuyu.bless.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ImagePromptTemplateServiceTest {

    @Autowired
    private ImagePromptTemplateService templateService;

    @MockBean
    private SysConfigRepository sysConfigRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    public void testSaveAndQuery() {
        // Prepare mock for StaticResourceConfig initialization
        when(sysConfigRepository.findByConfigKey(anyString())).thenReturn(Optional.empty());

        // 1. Save
        ImagePromptTemplateReq req = new ImagePromptTemplateReq();
        req.setTemplateContent("Content with {{VAR}}");
        req.setPlaceholderKeywords("VAR");
        req.setTemplateStatus(1);
        
        templateService.saveTemplate(req);

        // 2. Query
        ImagePromptTemplateQueryReq queryReq = new ImagePromptTemplateQueryReq();
        queryReq.setPage(1);
        queryReq.setSize(10);
        
        PageResponse<ImagePromptTemplateResp> page = templateService.queryTemplates(queryReq);
        assertTrue(page.getTotal() >= 1);
        
        boolean found = page.getList().stream()
                .anyMatch(t -> t.getTemplateContent().equals("Content with {{VAR}}"));
        assertTrue(found);
    }
}
