package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysTextCategoryEntity;
import com.zhufuyu.bless.entity.TextMaterialEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.material.TextMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.TextMaterialListReq;
import com.zhufuyu.bless.model.request.material.TextMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.TextMaterialResp;
import com.zhufuyu.bless.repository.SysTextCategoryRepository;
import com.zhufuyu.bless.repository.TextMaterialRepository;
import com.zhufuyu.bless.service.TextMaterialService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextMaterialServiceImpl implements TextMaterialService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TextMaterialRepository textMaterialRepository;
    private final SysTextCategoryRepository textCategoryRepository;

    public TextMaterialServiceImpl(TextMaterialRepository textMaterialRepository,
                                   SysTextCategoryRepository textCategoryRepository) {
        this.textMaterialRepository = textMaterialRepository;
        this.textCategoryRepository = textCategoryRepository;
    }

    @Override
    public Long createTextMaterial(TextMaterialCreateReq request) {
        // 校验名称唯一性
        if (!StringUtils.hasText(request.getName())) {
            throw new BizException(20001, "文案名称不能为空");
        }
        if (textMaterialRepository.existsByName(request.getName())) {
            throw new BizException(20001, "文案名称已存在");
        }

        // 校验正文
        if (!StringUtils.hasText(request.getContent())) {
            throw new BizException(20001, "文案正文不能为空");
        }

        // 校验分类
        if (request.getCategoryId() == null) {
            throw new BizException(20001, "分类不能为空");
        }
        SysTextCategoryEntity category = textCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BizException(20002, "文案分类不存在"));

        // 保存记录
        TextMaterialEntity entity = new TextMaterialEntity();
        entity.setName(request.getName());
        entity.setContent(request.getContent());
        entity.setDescription(request.getDescription());
        entity.setCategoryId(request.getCategoryId());

        TextMaterialEntity saved = textMaterialRepository.save(entity);
        return saved.getId();
    }

    @Override
    public PageResponse<TextMaterialResp> listTextMaterials(TextMaterialListReq request) {
        // 构建查询条件
        Specification<TextMaterialEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 名称模糊查询
            if (StringUtils.hasText(request.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + request.getName() + "%"));
            }

            // 分类筛选
            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), request.getCategoryId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        PageRequest pageRequest = PageRequest.of(
                request.getPageNo() - 1,
                request.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdTime")
        );

        Page<TextMaterialEntity> page = textMaterialRepository.findAll(spec, pageRequest);

        // 转换为响应对象
        List<TextMaterialResp> list = page.getContent().stream()
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
    public TextMaterialResp getTextMaterialById(Long id) {
        TextMaterialEntity entity = textMaterialRepository.findById(id)
                .orElseThrow(() -> new BizException(20002, "文案素材不存在"));
        return toResponse(entity);
    }

    @Override
    public void updateTextMaterial(TextMaterialUpdateReq request) {
        if (request.getId() == null) {
            throw new BizException(20001, "ID不能为空");
        }

        TextMaterialEntity entity = textMaterialRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "文案素材不存在"));

        // 校验名称唯一性(排除自己)
        if (StringUtils.hasText(request.getName()) && !request.getName().equals(entity.getName())) {
            if (textMaterialRepository.existsByName(request.getName())) {
                throw new BizException(20001, "文案名称已存在");
            }
            entity.setName(request.getName());
        }

        // 校验正文
        if (StringUtils.hasText(request.getContent())) {
            entity.setContent(request.getContent());
        }

        // 更新描述
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        // 校验分类
        if (request.getCategoryId() != null && !request.getCategoryId().equals(entity.getCategoryId())) {
            textCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BizException(20002, "文案分类不存在"));
            entity.setCategoryId(request.getCategoryId());
        }

        textMaterialRepository.save(entity);
    }

    private TextMaterialResp toResponse(TextMaterialEntity entity) {
        TextMaterialResp resp = new TextMaterialResp();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setContent(entity.getContent());
        resp.setDescription(entity.getDescription());
        resp.setCategoryId(entity.getCategoryId());
        resp.setCreatedTime(entity.getCreatedTime().format(DATE_FORMATTER));

        // 获取分类名称
        textCategoryRepository.findById(entity.getCategoryId()).ifPresent(category -> {
            resp.setCategoryName(category.getCategoryName());
        });

        return resp;
    }
}
