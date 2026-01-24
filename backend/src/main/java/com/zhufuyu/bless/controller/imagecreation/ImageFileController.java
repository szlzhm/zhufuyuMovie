package com.zhufuyu.bless.controller.imagecreation;

import com.zhufuyu.bless.exception.BizException;
import com.zhufuyu.bless.service.FileConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 图片文件访问控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/image-creation")
@RequiredArgsConstructor
public class ImageFileController {

    private final FileConfigService fileConfigService;

    /**
     * 获取图片内容
     * @param path 相对于文件根目录的路径
     * @return 图片文件流
     */
    @GetMapping("/image")
    public ResponseEntity<Resource> getImage(@RequestParam("path") String path) {
        try {
            // 获取文件根目录
            String rootPath = fileConfigService.getFileRootPathValue();
            
            // 构建完整文件路径
            Path fullPath = Paths.get(rootPath, path).normalize();
            File file = fullPath.toFile();
            
            // 安全检查：确保文件在根目录下
            if (!fullPath.startsWith(Paths.get(rootPath).normalize())) {
                throw new BizException(40003, "非法的文件路径");
            }
            
            // 检查文件是否存在
            if (!file.exists() || !file.isFile()) {
                throw new BizException(40004, "文件不存在");
            }
            
            // 返回文件流
            Resource resource = new FileSystemResource(file);
            
            // 根据文件扩展名设置Content-Type
            String contentType = determineContentType(file.getName());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                    .body(resource);
                    
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            log.error("读取图片文件失败: path={}", path, e);
            throw new BizException(50000, "读取图片文件失败");
        }
    }
    
    /**
     * 根据文件名确定Content-Type
     */
    private String determineContentType(String fileName) {
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".webp")) {
            return "image/webp";
        } else if (lowerName.endsWith(".bmp")) {
            return "image/bmp";
        } else {
            return "application/octet-stream";
        }
    }
}
