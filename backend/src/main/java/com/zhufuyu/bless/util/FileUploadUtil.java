package com.zhufuyu.bless.util;

import com.zhufuyu.bless.exception.BizException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    /**
     * 上传图片文件
     * @param file 文件
     * @param rootPath 根路径
     * @return 相对路径
     */
    public static String uploadImage(MultipartFile file, String rootPath) {
        if (file == null || file.isEmpty()) {
            throw new BizException(20001, "图片文件不能为空");
        }

        // 校验文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isImageFile(originalFilename)) {
            throw new BizException(20001, "只支持jpg、png、jpeg格式的图片");
        }

        // 生成相对路径: uploads/images/2025/01/15/uuid.jpg
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;
        String relativePath = "uploads/images/" + datePath + "/" + filename;

        // 完整路径
        String fullPath = rootPath + relativePath;

        try {
            // 创建目录
            Path path = Paths.get(fullPath);
            Files.createDirectories(path.getParent());

            // 保存文件
            file.transferTo(new File(fullPath));

            return relativePath;
        } catch (IOException e) {
            throw new BizException(50000, "文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传音频文件
     * @param file 文件
     * @param rootPath 根路径
     * @return 相对路径
     */
    public static String uploadAudio(MultipartFile file, String rootPath) {
        if (file == null || file.isEmpty()) {
            throw new BizException(20001, "音频文件不能为空");
        }

        // 校验文件格式
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isAudioFile(originalFilename)) {
            throw new BizException(20001, "只支持mp3、wav、m4a、aac格式的音频文件");
        }

        // 生成相对路径: uploads/audios/bgm/2025/01/15/uuid.mp3
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String extension = getFileExtension(originalFilename);
        String filename = UUID.randomUUID().toString() + extension;
        String relativePath = "uploads/audios/bgm/" + datePath + "/" + filename;

        // 完整路径
        String fullPath = rootPath + relativePath;

        try {
            // 创建目录
            Path path = Paths.get(fullPath);
            Files.createDirectories(path.getParent());

            // 保存文件
            file.transferTo(new File(fullPath));

            return relativePath;
        } catch (IOException e) {
            throw new BizException(50000, "文件上传失败: " + e.getMessage());
        }
    }

    private static boolean isImageFile(String filename) {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".jpg") 
            || lowerFilename.endsWith(".jpeg") 
            || lowerFilename.endsWith(".png");
    }

    private static boolean isAudioFile(String filename) {
        String lowerFilename = filename.toLowerCase();
        return lowerFilename.endsWith(".mp3") 
            || lowerFilename.endsWith(".wav") 
            || lowerFilename.endsWith(".m4a")
            || lowerFilename.endsWith(".aac");
    }

    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }
}
