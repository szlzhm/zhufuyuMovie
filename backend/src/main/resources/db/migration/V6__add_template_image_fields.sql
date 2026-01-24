-- 为提示语模板表添加图片相关字段

ALTER TABLE image_prompt_templates 
    ADD COLUMN template_image_path VARCHAR(500) COMMENT '模板图片文件路径（可选）',
    ADD COLUMN template_image_url VARCHAR(500) COMMENT '模板图片访问URL（可选）';
