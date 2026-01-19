package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysTextCategoryEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.TextCategoryCreateReq;
import com.zhufuyu.bless.model.request.config.TextCategoryListReq;
import com.zhufuyu.bless.model.request.config.TextCategoryToggleStatusReq;
import com.zhufuyu.bless.model.request.config.TextCategoryUpdateReq;
import com.zhufuyu.bless.model.response.config.TextCategoryListItemResp;
import com.zhufuyu.bless.repository.SysTextCategoryRepository;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.TextCategoryService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TextCategoryServiceImpl implements TextCategoryService {

    private final SysTextCategoryRepository sysTextCategoryRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TextCategoryServiceImpl(SysTextCategoryRepository sysTextCategoryRepository) {
        this.sysTextCategoryRepository = sysTextCategoryRepository;
    }

    private void checkAdmin() {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null || !"ADMIN".equals(info.getRole())) {
            throw new BizException(10004, "无权限访问");
        }
    }

    @Override
    public PageResult<TextCategoryListItemResp> queryTextCategoryList(TextCategoryListReq request) {
        checkAdmin();

        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdTime"));

        Specification<SysTextCategoryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getCategoryName())) {
                predicates.add(cb.like(root.get("categoryName"), "%" + request.getCategoryName() + "%"));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysTextCategoryEntity> page = sysTextCategoryRepository.findAll(spec, pageable);

        List<TextCategoryListItemResp> list = page.getContent().stream()
                .map(entity -> {
                    TextCategoryListItemResp resp = new TextCategoryListItemResp();
                    resp.setId(entity.getId());
                    resp.setCategoryCode(entity.getCategoryCode());
                    resp.setCategoryName(entity.getCategoryName());
                    resp.setCategoryDesc(entity.getCategoryDesc());
                    resp.setStatus(entity.getStatus());
                    resp.setCreatedTime(entity.getCreatedTime().format(formatter));
                    return resp;
                })
                .collect(Collectors.toList());

        PageResult<TextCategoryListItemResp> result = new PageResult<>();
        result.setList(list);
        result.setTotal(page.getTotalElements());
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public Long createTextCategory(TextCategoryCreateReq request) {
        checkAdmin();

        if (!StringUtils.hasText(request.getCategoryCode())) {
            throw new BizException(20001, "分类编码不能为空");
        }

        if (sysTextCategoryRepository.existsByCategoryCode(request.getCategoryCode())) {
            throw new BizException(20001, "分类编码已存在");
        }

        SysTextCategoryEntity entity = new SysTextCategoryEntity();
        entity.setCategoryCode(request.getCategoryCode());
        entity.setCategoryName(request.getCategoryName());
        entity.setCategoryDesc(request.getCategoryDesc());
        entity.setSortOrder(0); // 默认排序为0
        entity.setStatus(1);

        SysTextCategoryEntity saved = sysTextCategoryRepository.save(entity);
        return saved.getId();
    }

    @Override
    public void updateTextCategory(TextCategoryUpdateReq request) {
        checkAdmin();

        SysTextCategoryEntity entity = sysTextCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "文案分类不存在"));

        if (StringUtils.hasText(request.getCategoryName())) {
            entity.setCategoryName(request.getCategoryName());
        }
        if (request.getCategoryDesc() != null) {
            entity.setCategoryDesc(request.getCategoryDesc());
        }

        sysTextCategoryRepository.save(entity);
    }

    @Override
    public void toggleStatus(TextCategoryToggleStatusReq request) {
        checkAdmin();

        SysTextCategoryEntity entity = sysTextCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "文案分类不存在"));

        entity.setStatus(request.getStatus());
        sysTextCategoryRepository.save(entity);
    }
}
