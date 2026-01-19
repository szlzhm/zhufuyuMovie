package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.entity.ImageMaterialEntity;
import com.zhufuyu.bless.entity.SysImageCategoryEntity;
import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.model.request.material.ImageMaterialListReq;
import com.zhufuyu.bless.model.request.material.ImageMaterialUpdateReq;
import com.zhufuyu.bless.model.request.material.ImageMaterialUploadReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.ImageMaterialResp;
import com.zhufuyu.bless.repository.ImageMaterialRepository;
import com.zhufuyu.bless.repository.SysImageCategoryRepository;
import com.zhufuyu.bless.service.FileConfigService;
import com.zhufuyu.bless.service.ImageMaterialService;
import com.zhufuyu.bless.util.FileUploadUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片素材服务实现
 */
@Service
public class ImageMaterialServiceImpl implements ImageMaterialService {

    private final ImageMaterialRepository imageMaterialRepository;
    private final SysImageCategoryRepository imageCategoryRepository;
    private final FileConfigService fileConfigService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ImageMaterialServiceImpl(ImageMaterialRepository imageMaterialRepository,
                                   SysImageCategoryRepository imageCategoryRepository,
                                   FileConfigService fileConfigService) {
        this.imageMaterialRepository = imageMaterialRepository;
        this.imageCategoryRepository = imageCategoryRepository;
        this.fileConfigService = fileConfigService;
    }

    @Override
    public Long uploadImageMaterial(ImageMaterialUploadReq request, MultipartFile file) {
        // 校验标题唯一性
        if (!StringUtils.hasText(request.getTitle())) {
            throw new BizException(20001, "标题不能为空");
        }
        if (imageMaterialRepository.existsByTitle(request.getTitle())) {
            throw new BizException(20001, "标题已存在");
        }

        // 校验分类
        if (request.getCategoryId() == null) {
            throw new BizException(20001, "类别不能为空");
        }
        SysImageCategoryEntity category = imageCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BizException(20002, "图片分类不存在"));

        // 上传文件
        String rootPath = fileConfigService.getFileRootPath().getRootPath();
        String relativePath = FileUploadUtil.uploadImage(file, rootPath);

        // 保存记录
        ImageMaterialEntity entity = new ImageMaterialEntity();
        entity.setTitle(request.getTitle());
        entity.setCategoryId(request.getCategoryId());
        entity.setDescription(request.getDescription());
        entity.setImagePath(relativePath);

        ImageMaterialEntity saved = imageMaterialRepository.save(entity);
        return saved.getId();
    }

    @Override
    public PageResponse<ImageMaterialResp> listImageMaterials(ImageMaterialListReq request) {
        // 构建查询条件
        Specification<ImageMaterialEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 标题模糊查询
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(cb.like(root.get("title"), "%" + request.getTitle() + "%"));
            }

            // 分类筛选
            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), request.getCategoryId()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 分页查询
        // 构建排序对象
        Sort sort;
        if (StringUtils.hasText(request.getSortField())) {
            // 字段名映射（前端使用name，后端实体使用title）
            String sortField = "name".equals(request.getSortField()) ? "title" : request.getSortField();
            Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortOrder()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
            sort = Sort.by(direction, sortField);
        } else {
            // 默认按创建时间倒序
            sort = Sort.by(Sort.Direction.DESC, "createdTime");
        }
        
        PageRequest pageRequest = PageRequest.of(
                request.getPageNo() - 1,
                request.getPageSize(),
                sort
        );

        Page<ImageMaterialEntity> page = imageMaterialRepository.findAll(spec, pageRequest);

        // 转换为响应对象
        List<ImageMaterialResp> list = page.getContent().stream()
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
    public ImageMaterialResp getImageMaterialById(Long id) {
        ImageMaterialEntity entity = imageMaterialRepository.findById(id)
                .orElseThrow(() -> new BizException(20002, "图片素材不存在"));
        return toResponse(entity);
    }

    @Override
    public void updateImageMaterial(ImageMaterialUpdateReq request) {
        ImageMaterialEntity entity = imageMaterialRepository.findById(request.getId())
                .orElseThrow(() -> new BizException(20002, "图片素材不存在"));

        // 更新标题(检查唯一性)
        if (StringUtils.hasText(request.getTitle()) && !request.getTitle().equals(entity.getTitle())) {
            if (imageMaterialRepository.existsByTitle(request.getTitle())) {
                throw new BizException(20001, "标题已存在");
            }
            entity.setTitle(request.getTitle());
        }

        // 更新分类
        if (request.getCategoryId() != null && !request.getCategoryId().equals(entity.getCategoryId())) {
            imageCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new BizException(20002, "图片分类不存在"));
            entity.setCategoryId(request.getCategoryId());
        }

        // 更新简介
        if (request.getDescription() != null) {
            entity.setDescription(request.getDescription());
        }

        imageMaterialRepository.save(entity);
    }

    private ImageMaterialResp toResponse(ImageMaterialEntity entity) {
        ImageMaterialResp resp = new ImageMaterialResp();
        resp.setId(entity.getId());
        resp.setTitle(entity.getTitle());
        resp.setCategoryId(entity.getCategoryId());
        resp.setDescription(entity.getDescription());
        resp.setImagePath(entity.getImagePath());
        resp.setCreatedTime(entity.getCreatedTime().format(FORMATTER));

        // 获取分类名称，如果分类ID存在
        if (entity.getCategoryId() != null) {
            imageCategoryRepository.findById(entity.getCategoryId())
                    .ifPresent(category -> resp.setCategoryName(category.getCategoryName()));
        }

        return resp;
    }
}
