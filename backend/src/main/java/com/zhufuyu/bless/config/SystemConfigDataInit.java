package com.zhufuyu.bless.config;

import com.zhufuyu.bless.entity.*;
import com.zhufuyu.bless.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@Order(2)
public class SystemConfigDataInit implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigDataInit.class);

    private final SysConfigRepository sysConfigRepository;
    private final SysEmotionRepository sysEmotionRepository;
    private final SysImageCategoryRepository sysImageCategoryRepository;
    private final SysTextCategoryRepository sysTextCategoryRepository;

    public SystemConfigDataInit(SysConfigRepository sysConfigRepository,
                                SysEmotionRepository sysEmotionRepository,
                                SysImageCategoryRepository sysImageCategoryRepository,
                                SysTextCategoryRepository sysTextCategoryRepository) {
        this.sysConfigRepository = sysConfigRepository;
        this.sysEmotionRepository = sysEmotionRepository;
        this.sysImageCategoryRepository = sysImageCategoryRepository;
        this.sysTextCategoryRepository = sysTextCategoryRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("开始初始化系统配置数据...");
        
        cleanTestData();
        initSystemConfig();
        initEmotions();
        initImageCategories();
        initTextCategories();
        
        log.info("系统配置数据初始化完成");
    }

    private void cleanTestData() {
        // 删除测试情绪数据
        sysEmotionRepository.findAll().stream()
            .filter(e -> e.getEmotionCode() != null && 
                   (e.getEmotionCode().startsWith("TEST") || 
                    e.getEmotionCode().equals("哈哈哈哈") ||
                    e.getEmotionCode().equals("ROMANTIC")))
            .forEach(e -> {
                sysEmotionRepository.delete(e);
                log.info("删除测试情绪: {}", e.getEmotionCode());
            });

        // Note: No longer clearing image/text categories to prevent duplicate key errors on restart
        log.info("清理测试数据完成");
    }

    private void initSystemConfig() {
        if (!sysConfigRepository.findByConfigKey("file.root.path").isPresent()) {
            SysConfigEntity config = new SysConfigEntity();
            config.setConfigKey("file.root.path");
            config.setConfigValue("E:/data/bless/");
            config.setConfigDesc("文件存储根目录");
            sysConfigRepository.save(config);
            log.info("初始化文件根目录配置: E:/data/bless/");
        }
    }

    private void initEmotions() {
        if (sysEmotionRepository.count() > 0) {
            log.info("情绪数据已存在，跳过初始化");
            return;
        }

        List<EmotionData> emotions = Arrays.asList(
            new EmotionData("CHEERFUL", "喜庆", "适合春节、婚礼、庆典等欢乐场景,氛围热闹、喜气洋洋"),
            new EmotionData("WARM", "温馨", "适合家庭、亲子、朋友、感恩等温暖场景,氛围柔和亲切"),
            new EmotionData("TOUCHING", "感动", "适合告白、回忆、感谢、纪念等真情流露的场景"),
            new EmotionData("INSPIRING", "励志", "适合鼓励、打气、升学、升职等积极向上的场景"),
            new EmotionData("SOOTHING", "舒缓", "适合晚安、治愈、宁静、放松等慢节奏场景"),
            new EmotionData("LIVELY", "活泼", "适合儿童、日常搞怪、轻松幽默的场景,节奏轻快"),
            new EmotionData("SOLEMN", "庄重", "适合正式祝贺、长辈祝福、纪念类等较为严肃的场景"),
            new EmotionData("DAILY", "日常", "适合不强调明显情绪的普通祝福场景,氛围中性")
        );

        for (EmotionData data : emotions) {
            SysEmotionEntity entity = new SysEmotionEntity();
            entity.setEmotionCode(data.code);
            entity.setEmotionName(data.name);
            entity.setUsageDesc(data.desc);
            entity.setStatus(1);
            sysEmotionRepository.save(entity);
        }
        log.info("初始化情绪数据: {} 条", emotions.size());
    }

    private void initImageCategories() {
        if (sysImageCategoryRepository.count() > 0) {
            log.info("图片分类数据已存在，跳过初始化");
            return;
        }
        
        // 一级分类
        SysImageCategoryEntity scene = createImageCategory("SCENE", "场景", "各种场景类图片", null, 1);
        SysImageCategoryEntity festival = createImageCategory("FESTIVAL", "节日", "各种节日类图片", null, 2);
        SysImageCategoryEntity emotion = createImageCategory("EMOTION", "情感", "各种情感类图片", null, 3);

        // 场景-二级分类
        createImageCategory("SCENE_BIRTHDAY", "生日", "生日场景图片", scene.getId(), 1);
        createImageCategory("SCENE_WEDDING", "婚礼", "婚礼场景图片", scene.getId(), 2);
        createImageCategory("SCENE_BUSINESS", "商务", "商务场景图片", scene.getId(), 3);

        // 节日-二级分类
        createImageCategory("FESTIVAL_SPRING", "春节", "春节主题图片", festival.getId(), 1);
        createImageCategory("FESTIVAL_MID_AUTUMN", "中秋", "中秋主题图片", festival.getId(), 2);
        createImageCategory("FESTIVAL_NATIONAL", "国庆", "国庆主题图片", festival.getId(), 3);

        // 情感-二级分类
        createImageCategory("EMOTION_WARM", "温馨", "温馨情感图片", emotion.getId(), 1);
        createImageCategory("EMOTION_INSPIRING", "励志", "励志情感图片", emotion.getId(), 2);
        createImageCategory("EMOTION_GRATEFUL", "感恩", "感恩情感图片", emotion.getId(), 3);

        log.info("初始化图片分类数据完成");
    }

    private void initTextCategories() {
        if (sysTextCategoryRepository.count() > 0) {
            log.info("文案分类数据已存在，跳过初始化");
            return;
        }
        
        // 一级分类
        SysTextCategoryEntity blessType = createTextCategory("BLESS_TYPE", "祝福类型", "各种祝福类型文案", null, 1);
        SysTextCategoryEntity useScene = createTextCategory("USE_SCENE", "使用场景", "各种使用场景文案", null, 2);
        SysTextCategoryEntity festivalTheme = createTextCategory("FESTIVAL_THEME", "节日主题", "各种节日主题文案", null, 3);

        // 祝福类型-二级分类
        createTextCategory("BLESS_BIRTHDAY", "生日祝福", "生日祝福文案", blessType.getId(), 1);
        createTextCategory("BLESS_FESTIVAL", "节日祝福", "节日祝福文案", blessType.getId(), 2);
        createTextCategory("BLESS_DAILY", "日常问候", "日常问候文案", blessType.getId(), 3);

        // 使用场景-二级分类
        createTextCategory("SCENE_FRIEND", "朋友", "适用于朋友的文案", useScene.getId(), 1);
        createTextCategory("SCENE_FAMILY", "家人", "适用于家人的文案", useScene.getId(), 2);
        createTextCategory("SCENE_COLLEAGUE", "同事", "适用于同事的文案", useScene.getId(), 3);

        // 节日主题-二级分类
        createTextCategory("FESTIVAL_SPRING", "春节", "春节主题文案", festivalTheme.getId(), 1);
        createTextCategory("FESTIVAL_MID_AUTUMN", "中秋", "中秋主题文案", festivalTheme.getId(), 2);
        createTextCategory("FESTIVAL_NEW_YEAR", "元旦", "元旦主题文案", festivalTheme.getId(), 3);

        log.info("初始化文案分类数据完成");
    }

    private SysImageCategoryEntity createImageCategory(String code, String name, String desc, Long parentId, int sortOrder) {
        SysImageCategoryEntity entity = new SysImageCategoryEntity();
        entity.setCategoryCode(code);
        entity.setCategoryName(name);
        entity.setCategoryDesc(desc);
        entity.setParentId(parentId);
        entity.setSortOrder(sortOrder);
        entity.setStatus(1);
        return sysImageCategoryRepository.save(entity);
    }

    private SysTextCategoryEntity createTextCategory(String code, String name, String desc, Long parentId, int sortOrder) {
        SysTextCategoryEntity entity = new SysTextCategoryEntity();
        entity.setCategoryCode(code);
        entity.setCategoryName(name);
        entity.setCategoryDesc(desc);
        entity.setParentId(parentId);
        entity.setSortOrder(sortOrder);
        entity.setStatus(1);
        return sysTextCategoryRepository.save(entity);
    }

    private static class EmotionData {
        String code;
        String name;
        String desc;

        EmotionData(String code, String name, String desc) {
            this.code = code;
            this.name = name;
            this.desc = desc;
        }
    }
}
