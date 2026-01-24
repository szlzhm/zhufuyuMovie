package com.zhufuyu.bless.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SnowflakeIdGeneratorTest {

    @Test
    public void testNextId() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        
        assertTrue(id1 > 0);
        assertTrue(id2 > id1);
    }

    @Test
    public void testUniqueIds() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator();
        int count = 1000;
        java.util.Set<Long> ids = new java.util.HashSet<>();
        
        for (int i = 0; i < count; i++) {
            ids.add(generator.nextId());
        }
        
        assertEquals(count, ids.size());
    }
}
