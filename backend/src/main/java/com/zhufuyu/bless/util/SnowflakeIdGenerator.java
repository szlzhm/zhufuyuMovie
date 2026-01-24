package com.zhufuyu.bless.util;

import org.springframework.stereotype.Component;

/**
 * 雪花算法 ID 生成器
 */
@Component
public class SnowflakeIdGenerator {

    // 起始时间戳 (2025-01-01)
    private final long twepoch = 1735689600000L;

    // 机器标识占用的位数
    private final long workerIdBits = 5L;

    // 数据中心标识占用的位数
    private final long datacenterIdBits = 5L;

    // 支持的最大机器标识 id，结果是 31
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    // 支持的最大数据中心标识 id，结果是 31
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    // 序列在 id 中占用的位数
    private final long sequenceBits = 12L;

    // 机器 ID 向左移 12 位
    private final long workerIdShift = sequenceBits;

    // 数据中心 id 向左移 17 位 (12+5)
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    // 时间截向左移 22 位 (12+5+5)
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    // 生成序列的掩码，这里为 4095 (0b111111111111=0xfff=4095)
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // 工作机器 ID(0~31)
    private long workerId = 1L;

    // 数据中心 ID(0~31)
    private long datacenterId = 1L;

    // 毫秒内序列(0~4095)
    private long sequence = 0L;

    // 上次生成 ID 的时间截
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator() {
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过，这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 上次生成 ID 的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 64 位的 ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
