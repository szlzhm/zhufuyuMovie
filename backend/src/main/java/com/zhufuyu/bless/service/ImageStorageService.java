package com.zhufuyu.bless.service;

import java.io.IOException;
import java.util.List;

/**
 * 图片存储服务接口
 */
public interface ImageStorageService {
    
    /**
     * 从URL下载图片并保存到本地
     * @param imageUrl 图片URL
     * @param fileName 文件名
     * @return 本地存储路径
     */
    String downloadAndSaveImage(String imageUrl, String fileName) throws IOException;
    
    /**
     * 批量下载并保存图片
     * @param imageUrls 图片URL列表
     * @return 本地存储路径列表
     */
    List<String> downloadAndSaveImages(List<String> imageUrls) throws IOException;
    
    /**
     * 根据任务ID创建存储目录
     * @param taskId 任务ID
     * @return 目录路径
     */
    String createTaskDirectory(String taskId);
}