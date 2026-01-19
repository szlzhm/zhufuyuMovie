package com.zhufuyu.bless.repository;

import com.zhufuyu.bless.entity.VideoPublishRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoPublishRecordRepository extends JpaRepository<VideoPublishRecordEntity, Long> {

    /**
     * 根据视频ID查询所有发布记录
     */
    List<VideoPublishRecordEntity> findByVideoIdOrderByPublishTimeDesc(Long videoId);

    /**
     * 检查同一视频的同一视频号是否已存在发布记录
     */
    boolean existsByVideoIdAndChannelName(Long videoId, String channelName);

    /**
     * 根据视频ID列表批量查询发布记录
     */
    List<VideoPublishRecordEntity> findByVideoIdIn(List<Long> videoIds);
}
