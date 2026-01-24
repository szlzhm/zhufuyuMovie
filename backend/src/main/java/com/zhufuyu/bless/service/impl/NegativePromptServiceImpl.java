package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.NegativePromptEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.NegativePromptQueryReq;
import com.zhufuyu.bless.model.request.NegativePromptReq;
import com.zhufuyu.bless.model.response.NegativePromptResp;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.repository.NegativePromptRepository;
import com.zhufuyu.bless.service.NegativePromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 负面提示语服务实现类
 */
@Service
@RequiredArgsConstructor
public class NegativePromptServiceImpl implements NegativePromptService {

    private final NegativePromptRepository negativePromptRepository;

    @Override
    @Transactional
    public void save(NegativePromptReq request) {
        NegativePromptEntity entity;
        if (request.getId() != null) {
            entity = negativePromptRepository.findById(request.getId())
                    .orElseThrow(() -> new BizException(500, "记录不存在"));
        } else {
            entity = new NegativePromptEntity();
        }
        BeanUtils.copyProperties(request, entity);
        negativePromptRepository.save(entity);
    }

    @Override
    public PageResponse<NegativePromptResp> query(NegativePromptQueryReq request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<NegativePromptEntity> page = negativePromptRepository.search(request.getContent(), pageable);
        
        List<NegativePromptResp> list = page.getContent().stream()
                .map(this::convertToResp)
                .collect(Collectors.toList());
        
        return new PageResponse<>(page.getNumber() + 1, page.getSize(), page.getTotalElements(), list);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        negativePromptRepository.deleteById(id);
    }

    @Override
    public List<NegativePromptResp> listAll() {
        return negativePromptRepository.findAll().stream()
                .map(this::convertToResp)
                .collect(Collectors.toList());
    }

    private NegativePromptResp convertToResp(NegativePromptEntity entity) {
        NegativePromptResp resp = new NegativePromptResp();
        BeanUtils.copyProperties(entity, resp);
        return resp;
    }
}
