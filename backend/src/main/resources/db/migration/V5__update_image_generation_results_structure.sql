-- 修改图像生成结果表结构：改为一个任务一条记录，支持多图存储

-- 1. 删除旧的单数字段
ALTER TABLE image_generation_results 
    DROP COLUMN IF EXISTS image_path,
    DROP COLUMN IF EXISTS image_url;

-- 2. 添加新的复数字段（存储JSON数组）
ALTER TABLE image_generation_results 
    ADD COLUMN IF NOT EXISTS image_paths TEXT COMMENT '生成的图片文件路径列表（JSON数组）',
    ADD COLUMN IF NOT EXISTS image_urls TEXT COMMENT '图片访问URL列表（JSON数组）',
    ADD COLUMN IF NOT EXISTS generation_time DOUBLE COMMENT '生成耗时（秒）';

-- 3. 为task_id添加唯一索引（一个任务只能有一条结果记录）
ALTER TABLE image_generation_results 
    ADD UNIQUE INDEX IF NOT EXISTS uk_task_id (task_id);
