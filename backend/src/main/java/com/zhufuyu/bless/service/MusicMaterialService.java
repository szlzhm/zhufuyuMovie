package com.zhufuyu.bless.service;

import com.zhufuyu.bless.model.request.material.MusicMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.MusicMaterialListReq;
import com.zhufuyu.bless.model.request.material.MusicMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.MusicMaterialResp;
import org.springframework.web.multipart.MultipartFile;

/**
 * 背景音乐素材服务接口
 */
public interface MusicMaterialService {

    /**
     * 上传背景音乐文件
     */
    Long uploadMusicMaterial(MusicMaterialCreateReq request, MultipartFile file);

    /**
     * 创建背景音乐
     */
    Long createMusicMaterial(MusicMaterialCreateReq request);

    /**
     * 分页查询背景音乐列表
     */
    PageResponse<MusicMaterialResp> listMusicMaterials(MusicMaterialListReq request);

    /**
     * 获取背景音乐详情
     */
    MusicMaterialResp getMusicMaterialById(Long id);

    /**
     * 更新背景音乐
     */
    void updateMusicMaterial(MusicMaterialUpdateReq request);

    /**
     * 删除背景音乐
     */
    void deleteMusicMaterial(Long id);
}
