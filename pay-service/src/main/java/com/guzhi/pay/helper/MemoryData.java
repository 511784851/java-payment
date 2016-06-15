package com.guzhi.pay.helper;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * JVM缓存,支持数据过期失效,最多支持1000条记录
 * 
 * 特别注意: 如果数据在其他地方被修改了,那么缓存里也被修改了...(目前此处貌似不用担心数据被改动)
 * 
 * @author administrator
 * 
 */
public final class MemoryData {
    private MemoryData() {
    }

    // 最多保持1000条数据
    private static final int MAX_ELEMENT = 1000;
    private static final Map<String, MemoryValue> memoryData = Collections
            .synchronizedMap(new LinkedHashMap<String, MemoryValue>(16, 0.75f, true) {
                private static final long serialVersionUID = 712400042044865785L;

                @Override
                protected boolean removeEldestEntry(Map.Entry<String, MemoryValue> entry) {
                    return this.size() >= MAX_ELEMENT;
                }
            });

    /**
     * 添加会过期的数据，如果已有该键数据，则覆盖
     * 
     * @param key
     * @param value
     * @param expriedDate
     */
    public static void put(String key, Object value, Date expiredDate) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        memoryData.put(key, new MemoryValueExpire(value, expiredDate));
    }

    /**
     * 设置一个值，该值经过指定毫秒后失效
     * 
     * @param key
     * @param value
     * @param expiredAfterMilSecs 经过多少毫秒后过期
     * @author jgnan
     */
    public static void put(String key, Object value, long expiredAfterMilSecs) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        memoryData.put(key, new MemoryValueExpire(value, expiredAfterMilSecs));

    }

    /**
     * 添加不会过期的数据，如果已有该键数据，则覆盖
     * 
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        memoryData.put(key, new MemoryValueNotExpire(value));
    }

    /**
     * 查询数据
     * 
     * @param key
     * @return 查不到或者过期返回null，如果key为null也返回null
     */
    public static Object get(String key) {
        MemoryValue val = memoryData.get(key);
        if (val == null || val.isExpired()) {
            remove(key);
            return null;
        }
        return val.getValue();
    }

    /**
     * 查询是否包含该key
     * 
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return memoryData.containsKey(key);
    }

    /**
     * 移除某key
     * 
     * @param key
     * @return
     */
    public static void remove(String key) {
        if (key != null)
            memoryData.remove(key);
    }

    private static interface MemoryValue {
        boolean isExpired();

        Object getValue();
    }

    private static class MemoryValueExpire implements MemoryValue {
        public Object value;
        public long expireDate;

        public MemoryValueExpire(Object value, Date expireDate) {
            this.value = value;
            this.expireDate = expireDate.getTime();
        }

        public MemoryValueExpire(Object value, long expireAfterMilSecs) {
            this.value = value;
            this.expireDate = System.currentTimeMillis() + expireAfterMilSecs;
        }

        @Override
        public boolean isExpired() {
            return expireDate < System.currentTimeMillis();
        }

        @Override
        public Object getValue() {
            return value;
        }
    }

    private static class MemoryValueNotExpire implements MemoryValue {
        public Object value;

        public MemoryValueNotExpire(Object value) {
            this.value = value;
        }

        @Override
        public boolean isExpired() {
            return false;
        }

        @Override
        public Object getValue() {
            return value;
        }
    }
}
