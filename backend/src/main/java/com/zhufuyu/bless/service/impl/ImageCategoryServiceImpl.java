package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.SysImageCategoryEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.common.PageResult;
import com.zhufuyu.bless.model.request.config.ImageCategoryCreateReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryListReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryToggleStatusReq;
import com.zhufuyu.bless.model.request.config.ImageCategoryUpdateReq;
import com.zhufuyu.bless.model.response.config.ImageCategoryListItemResp;
import com.zhufuyu.bless.repository.SysImageCategoryRepository;
import com.zhufuyu.bless.security.LoginUserContext;
import com.zhufuyu.bless.service.ImageCategoryService;
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
public class ImageCategoryServiceImpl implements ImageCategoryService {

    private final SysImageCategoryRepository sysImageCategoryRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ImageCategoryServiceImpl(SysImageCategoryRepository sysImageCategoryRepository) {
        this.sysImageCategoryRepository = sysImageCategoryRepository;
    }

    private void checkAdmin() {
        LoginUserContext.LoginUserInfo info = LoginUserContext.get();
        if (info == null || !"ADMIN".equals(info.getRole())) {
            throw new BizException(10004, "无权限访问");
        }
    }

    @Override
    public PageResult<ImageCategoryListItemResp> queryImageCategoryList(ImageCategoryListReq request) {
        checkAdmin();

        int pageNo = request.getPageNo() != null ? request.getPageNo() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize,
                Sort.by(Sort.Direction.DESC, "createdTime"));

        Specification<SysImageCategoryEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(request.getCategoryName())) {
                predicates.add(cb.like(root.get("categoryName"), "%" + request.getCategoryName() + "%"));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SysImageCategoryEntity> page = sysImageCategoryRepository.findAll(spec, pageable);

        List<ImageCategoryListItemResp> list = page.getContent().stream()
                .map(entity -> {
                    ImageCategoryListItemResp resp = new ImageCategoryListItemResp();
                    resp.setId(entity.getId());
                    resp.setCategoryCode(entity.getCategoryCode());
                    resp.setCategoryName(entity.getCategoryName());
                    resp.setCategoryDesc(entity.getCategoryDesc());
                    resp.setStatus(entity.getStatus());
                    resp.setCreatedTime(entity.getCreatedTime().format(formatter));
                    return resp;
                })
                .collect(Collectors.toList());

        PageResult<ImageCategoryListItemResp> result = new PageResult<>();
        result.setList(list);
        result.setTotal(page.getTotalElements());
        result.setPageNo(pageNo);
        result.setPageSize(pageSize);

        return result;
    }

    @Override
    public Long createImageCategory(ImageCategoryCreateReq request) {
        checkAdmin();

        if (!StringUtils.hasText(request.getCategoryCode())) {
            throw new BizException(20001, "分类编码不能为空");
        }

        if (sysImageCategoryRepository.existsByCategoryCode(request.getCategoryCode())) {
            throw new BizException(20001, "分类编码已存在");
        }

        SysImageCategoryEntity entity = new SysImageCategoryEntity();
        entity.setCategoryCode(request.getCategoryCode());
        entity.setCategoryName(request.getCategoryName());
        entity.setCategoryDesc(request.getCategoryDesc());
        entity.setSortOrder(0); // 默认排序为0
        entity.setStatus(1);

        SysImageCategoryEntity saved = sysImageCategoryRepository.save(entity);
        return saved.getId();
    }

    @Override
    public void updateImageCategory(ImageCategoryUpdateReq request) {
        checkAdmin();

        SysImageCategoryEntity entity = sysImageCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "图片分类不存在"));

        if (StringUtils.hasText(request.getCategoryName())) {
            entity.setCategoryName(request.getCategoryName());
        }
        if (request.getCategoryDesc() != null) {
            entity.setCategoryDesc(request.getCategoryDesc());
        }

        sysImageCategoryRepository.save(entity);
    }

    @Override
    public void toggleStatus(ImageCategoryToggleStatusReq request) {
        checkAdmin();

        SysImageCategoryEntity entity = sysImageCategoryRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "图片分类不存在"));

        entity.setStatus(request.getStatus());
        sysImageCategoryRepository.save(entity);
    }
}
