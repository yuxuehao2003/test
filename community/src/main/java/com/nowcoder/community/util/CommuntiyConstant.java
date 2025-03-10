package com.nowcoder.community.util;

/**
 * @author 勋谦
 * @ClassName: CommuntiyConstant
 * @date: 2025/2/28 13:46
 * @Description:
 */
public interface CommuntiyConstant {

    /**
     * 激活成功
     */
    int ACTIVATION_SUCCESS = 0;
    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;
    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;
    /**
     * 默认状态的登录凭证的超时时间
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    /**
     * 记住状态的登录凭证的超时时间
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 100;

    // 帖子
    int ENTITY_TYPE_POST = 1;
    // 评论
    int ENTITY_TYPE_COMMENT = 2;

    // 用户
    int ENTITY_TYPE_USER = 3;

    // 主题
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";

    int SYSTEM_USER_ID = 1;

    // 发帖
    String TOPIC_PUBLISH = "publish";
}
