package com.zhufuyu.bless.service.impl;

import com.zhufuyu.bless.service.ImageStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 图片存储服务实现
 */
@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    @Value("${app.image.storage.path:./uploads/images}")
    private String storagePath;

    @Override
    public String downloadAndSaveImage(String imageUrl, String fileName) throws IOException {
        // 创建存储目录（使用2级目录结构：年/月）
        String datePath = LocalDate.now().getYear() + "/" + String.format("%02d", LocalDate.now().getMonthValue());
        String fullDirPath = Paths.get(storagePath, datePath).toString();
        Path dirPath = Paths.get(fullDirPath);
        
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 如果没有指定文件名，则生成唯一文件名
        if (fileName == null || fileName.isEmpty()) {
            fileName = UUID.randomUUID().toString() + ".png";
        }

        // 构建完整文件路径
        String filePath = Paths.get(fullDirPath, fileName).toString();

        // 下载并保存图片
        URL url = new URL(imageUrl);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

        try (InputStream inputStream = connection.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(filePath)) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        }

        return filePath;
    }

    @Override
    public List<String> downloadAndSaveImages(List<String> imageUrls) throws IOException {
        List<String> savedPaths = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            String fileName = "generated_" + System.currentTimeMillis() + "_" + i + ".png";
            String savedPath = downloadAndSaveImage(imageUrl, fileName);
            savedPaths.add(savedPath);
        }
        return savedPaths;
    }

    @Override
    public String createTaskDirectory(String taskId) {
        // 创建基于任务ID的目录结构
        String taskDir = taskId.replace("task_", "").substring(0, 2); // 取任务ID前两位作为一级目录
        String subDir = taskId.replace("task_", "").substring(2, 4);  // 取接下来两位作为二级目录
        String fullDirPath = Paths.get(storagePath, taskDir, subDir, taskId).toString();
        
        Path dirPath = Paths.get(fullDirPath);
        try {
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("创建任务目录失败: " + e.getMessage(), e);
        }
        
        return fullDirPath;
    }
}