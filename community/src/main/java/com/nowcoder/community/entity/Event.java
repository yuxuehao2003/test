package com.nowcoder.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 勋谦
 * @ClassName: Event
 * @date: 2025/3/8 20:05
 * @Description:
 */

// 事件对象
public class Event {

    // 张三给李四点赞
    private String topic;

    // 张三
    private int userId;
    private int entityType;
    private int entityId;

    // 李四
    private int entityUserId;
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key,Object value) {
        this.data.put(key,value);
        return this;
    }
}
