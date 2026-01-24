package com.zhufuyu.bless.controller.imagecreation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhufuyu.bless.model.request.ImagePromptTemplateReq;
import com.zhufuyu.bless.model.response.ImagePromptTemplateResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.repository.SysConfigRepository;
import com.zhufuyu.bless.security.JwtUtil;
import com.zhufuyu.bless.service.ImageCreationService;
import com.zhufuyu.bless.service.ImagePromptTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageCreationController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for simple unit test
public class ImageCreationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImagePromptTemplateService templateService;

    @MockBean
    private ImageCreationService imageCreationService;

    @MockBean
    private SysConfigRepository sysConfigRepository;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        when(sysConfigRepository.findByConfigKey(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void testSaveTemplate() throws Exception {
        ImagePromptTemplateReq req = new ImagePromptTemplateReq();
        req.setTemplateContent("Test content {{KEY}}");
        req.setPlaceholderKeywords("KEY");
        req.setTemplateStatus(1);

        mockMvc.perform(post("/api/image-creation/template/save/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    public void testQueryTemplates() throws Exception {
        ImagePromptTemplateResp resp = new ImagePromptTemplateResp();
        resp.setId(1000L);
        resp.setTemplateContent("Test content");

        PageResponse<ImagePromptTemplateResp> pageResp = new PageResponse<>(1, 10, 1L, Collections.singletonList(resp));

        when(templateService.queryTemplates(any())).thenReturn(pageResp);

        mockMvc.perform(post("/api/image-creation/template/query/v1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.list[0].id").value(1000));
    }
}
