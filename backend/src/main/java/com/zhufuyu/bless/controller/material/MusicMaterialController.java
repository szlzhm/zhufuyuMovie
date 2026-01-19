package com.zhufuyu.bless.controller.material;

import com.zhufuyu.bless.model.common.BaseResponse;
import com.zhufuyu.bless.model.request.material.MusicMaterialCreateReq;
import com.zhufuyu.bless.model.request.material.MusicMaterialListReq;
import com.zhufuyu.bless.model.request.material.MusicMaterialUpdateReq;
import com.zhufuyu.bless.model.response.common.PageResponse;
import com.zhufuyu.bless.model.response.material.MusicMaterialResp;
import com.zhufuyu.bless.service.MusicMaterialService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 背景音乐素材管理
 */
@RestController
@RequestMapping("/api/material/music")
public class MusicMaterialController {

    private final MusicMaterialService musicMaterialService;

    public MusicMaterialController(MusicMaterialService musicMaterialService) {
        this.musicMaterialService = musicMaterialService;
    }

    /**
     * 上传背景音乐文件
     */
    @PostMapping("/upload/v1")
    public BaseResponse<Long> uploadMusicMaterial(
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "emotion", required = false) Long emotion,
            @RequestParam("file") MultipartFile file) {
        
        MusicMaterialCreateReq request = new MusicMaterialCreateReq();
        request.setName(name);
        request.setDescription(description);
        request.setEmotion(emotion);

        Long id = musicMaterialService.uploadMusicMaterial(request, file);
        return BaseResponse.success(id);
    }

    /**
     * 创建背景音乐
     */
    @PostMapping("/create/v1")
    public BaseResponse<Long> createMusicMaterial(@RequestBody MusicMaterialCreateReq request) {
        Long id = musicMaterialService.createMusicMaterial(request);
        return BaseResponse.success(id);
    }

    /**
     * 分页查询背景音乐列表
     */
    @PostMapping("/list/query/v1")
    public BaseResponse<PageResponse<MusicMaterialResp>> listMusicMaterials(@RequestBody MusicMaterialListReq request) {
        PageResponse<MusicMaterialResp> response = musicMaterialService.listMusicMaterials(request);
        return BaseResponse.success(response);
    }

    /**
     * 获取背景音乐详情
     */
    @PostMapping("/detail/query/v1")
    public BaseResponse<MusicMaterialResp> getMusicMaterialDetail(@RequestBody Long id) {
        MusicMaterialResp response = musicMaterialService.getMusicMaterialById(id);
        return BaseResponse.success(response);
    }

    /**
     * 更新背景音乐
     */
    @PostMapping("/update/v1")
    public BaseResponse<Void> updateMusicMaterial(@RequestBody MusicMaterialUpdateReq request) {
        musicMaterialService.updateMusicMaterial(request);
        return BaseResponse.success(null);
    }

    /**
     * 删除背景音乐
     */
    @PostMapping("/delete/v1")
    public BaseResponse<Void> deleteMusicMaterial(@RequestBody Long id) {
        musicMaterialService.deleteMusicMaterial(id);
        return BaseResponse.success(null);
    }
}
