package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.VoiceMaterialEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.material.VoiceMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialListReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialStatusReq;
import com.zhufuyu.bless.model.request.material.VoiceMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.VoiceMaterialResp;
import com.zhufuyu.bless.repository.VoiceMaterialRepository;
import com.zhufuyu.bless.service.VoiceMaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 音色素材服务实现
 */
@Service
public class VoiceMaterialServiceImpl implements VoiceMaterialService {

    private final VoiceMaterialRepository voiceMaterialRepository;
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VoiceMaterialServiceImpl(VoiceMaterialRepository voiceMaterialRepository) {
        this.voiceMaterialRepository = voiceMaterialRepository;
    }

    @Override
    public Long createVoiceMaterial(VoiceMaterialCreateReq request) {
        // 校验名称唯一性
        if (!StringUtils.hasText(request.getName())) {
            throw new BizException(20001, "名称不能为空");
        }
        if (voiceMaterialRepository.findByName(request.getName()) != null) {
            throw new BizException(20001, "音色名称已存在");
        }

        // 校验必填字段
        if (!StringUtils.hasText(request.getGender())) {
            throw new BizException(20001, "音色性别不能为空");
        }
        if (!StringUtils.hasText(request.getLanguage())) {
            throw new BizException(20001, "音色语言不能为空");
        }
        if (!StringUtils.hasText(request.getType())) {
            throw new BizException(20001, "音色类型不能为空");
        }

        // 保存记录
        VoiceMaterialEntity entity = new VoiceMaterialEntity();
        entity.setName(request.getName());
        entity.setGender(request.getGender());
        entity.setLanguage(request.getLanguage());
        entity.setAgeGroup(request.getAgeGroup());
        entity.setType(request.getType());
        entity.setStatus(1);

        VoiceMaterialEntity saved = voiceMaterialRepository.save(entity);
        return saved.getId();
    }

    @Override
    public PageResponse<VoiceMaterialResp> listVoiceMaterials(VoiceMaterialListReq request) {
        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                request.getPageNo() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Page<VoiceMaterialEntity> page = voiceMaterialRepository.findByConditions(
                request.getName(),
                request.getGender(),
                request.getLanguage(),
                pageRequest
        );

        // 转换为响应对象
        List<VoiceMaterialResp> list = page.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(
                request.getPageNo(),
                request.getPageSize(),
                page.getTotalElements(),
                list
        );
    }

    @Override
    public VoiceMaterialResp getVoiceMaterialById(Long id) {
        VoiceMaterialEntity entity = voiceMaterialRepository.findById(id)
                .orElseThrow(() -> new BizException(20002, "音色素材不存在"));
        return toResponse(entity);
    }

    @Override
    public void updateVoiceMaterial(VoiceMaterialUpdateReq request) {
        VoiceMaterialEntity entity = voiceMaterialRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "音色素材不存在"));

        // 更新名称(检查唯一性)
        if (StringUtils.hasText(request.getName()) && !request.getName().equals(entity.getName())) {
            if (voiceMaterialRepository.findByName(request.getName()) != null) {
                throw new BizException(20001, "音色名称已存在");
            }
            entity.setName(request.getName());
        }

        // 更新其他字段
        if (StringUtils.hasText(request.getGender())) {
            entity.setGender(request.getGender());
        }
        if (StringUtils.hasText(request.getLanguage())) {
            entity.setLanguage(request.getLanguage());
        }
        if (request.getAgeGroup() != null) {
            entity.setAgeGroup(request.getAgeGroup());
        }
        if (StringUtils.hasText(request.getType())) {
            entity.setType(request.getType());
        }

        voiceMaterialRepository.save(entity);
    }

    @Override
    public void toggleVoiceMaterialStatus(VoiceMaterialStatusReq request) {
        VoiceMaterialEntity entity = voiceMaterialRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "音色素材不存在"));

        entity.setStatus(request.getStatus());
        voiceMaterialRepository.save(entity);
    }

    private VoiceMaterialResp toResponse(VoiceMaterialEntity entity) {
        VoiceMaterialResp resp = new VoiceMaterialResp();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setGender(entity.getGender());
        resp.setLanguage(entity.getLanguage());
        resp.setAgeGroup(entity.getAgeGroup());
        resp.setType(entity.getType());
        resp.setStatus(entity.getStatus());
        resp.setCreatedTime(entity.getCreatedTime().format(FORMATTER));
        return resp;
    }
}
