package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysEmotionEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.*;
import com.zhufuyu.bless.model.response.config.EmotionListItemResp;
import com.zhufuyu.bless.repository.SysEmotionRepository;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.EmotionService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmotionServiceImpl implements EmotionService {

    private final SysEmotionRepository sysEmotionRepository;

    public EmotionServiceImpl(SysEmotionRepository sysEmotionRepository) {
        this.sysEmotionRepository = sysEmotionRepository;
    }

    private void checkAdmin() {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null || !"ADMIN".equals(info.getRole())) {
            throw new BizException(10004, "无权限访问");
        }
    }

    @Override
    public PageResult<EmotionListItemResp> queryEmotionList(EmotionListReq request) {
        checkAdmin();

        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdTime"));

        Specification<SysEmotionEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(request.getEmotionName())) {
                predicates.add(cb.like(root.get("emotionName"), "%" + request.getEmotionName() + "%"));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysEmotionEntity> page = sysEmotionRepository.findAll(spec, pageable);

        List<EmotionListItemResp> items = page.getContent().stream().map(entity -> {
            EmotionListItemResp vo = new EmotionListItemResp();
            vo.setId(entity.getId());
            vo.setEmotionCode(entity.getEmotionCode());
            vo.setEmotionName(entity.getEmotionName());
            vo.setUsageDesc(entity.getUsageDesc());
            vo.setStatus(entity.getStatus());
            vo.setCreatedTime(entity.getCreatedTime());
            vo.setUpdatedTime(entity.getUpdatedTime());
            return vo;
        }).toList();

        PageResult<EmotionListItemResp> result = new PageResult<>();
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);
        result.setTotal(page.getTotalElements());
        result.setList(items);
        return result;
    }

    @Override
    public Long createEmotion(EmotionCreateReq request) {
        checkAdmin();

        if (sysEmotionRepository.existsByEmotionCode(request.getEmotionCode())) {
            throw new BizException(20001, "情绪编码已存在");
        }

        SysEmotionEntity entity = new SysEmotionEntity();
        entity.setEmotionCode(request.getEmotionCode());
        entity.setEmotionName(request.getEmotionName());
        entity.setUsageDesc(request.getUsageDesc());
        entity.setStatus(1);

        SysEmotionEntity saved = sysEmotionRepository.save(entity);
        return saved.getId();
    }

    @Override
    public void updateEmotion(EmotionUpdateReq request) {
        checkAdmin();

        SysEmotionEntity entity = sysEmotionRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "情绪不存在"));

        entity.setEmotionName(request.getEmotionName());
        entity.setUsageDesc(request.getUsageDesc());
        sysEmotionRepository.save(entity);
    }

    @Override
    public void toggleStatus(EmotionToggleStatusReq request) {
        checkAdmin();

        SysEmotionEntity entity = sysEmotionRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "情绪不存在"));

        entity.setStatus(request.getStatus());
        sysEmotionRepository.save(entity);
    }
}
